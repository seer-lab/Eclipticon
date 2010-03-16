package ca.uoit.eclipticon.instrumentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.Path;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.data.InterestPoint;
import ca.uoit.eclipticon.data.SourceFile;

/**
 * This class is concerned with the acquisition of the files found in a workspace, as well as parsing
 * for {@link InterestPoint} locations given a source file.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class FileParser {

	ArrayList<SequenceOrdering>	_sequence	= null;	// The sequence of constructs

	/**
	 * Will recursively acquire all the files under the root path, and return an arraylist
	 * of {@link SourceFile}.
	 * 
	 * @param root the root path of the workplace
	 * @return an arraylist of {@link SourceFiles}
	 */
	public ArrayList<SourceFile> getFiles( Path root ) {
		// Make a file out of the path
		File file = root.toFile();

		// Create an arraylist to hold the source files
		ArrayList<SourceFile> allSourceFiles = new ArrayList<SourceFile>();

		// If the path is a directory
		if( file.isDirectory() ) {

			// Get all Files and Folders in it
			File[] allFiles = file.listFiles();

			// Go through each File/Folder
			for( File fileTemp : allFiles ) {

				Path currentPath = new Path( fileTemp.getPath() );

				// If it is a folder then recursively call getFiles and add their returns to the current arraylist
				if( fileTemp.isDirectory() ) {
					allSourceFiles.addAll( getFiles( currentPath ) );
				}

				// If it is a file
				else if( fileTemp.isFile() ) {

					// and the file is a source file
					if( fileTemp.toString().endsWith( ".java" ) ) {

						allSourceFiles.add( new SourceFile( currentPath ) );
					}
				}
			}
		}
		return allSourceFiles;
	}

	/**
	 * This method will find all the potential synchronization constructs within
	 * the current source file, these points are then turned into {@link InterestPoint}.
	 * 
	 * @param source the {@link SourceFile} of interest
	 */
	public void findInterestPoints( SourceFile source ) {

		// Read the sourceFile and create the reading and file content objects
		FileReader fileReader = null;
		try {
			fileReader = new FileReader( source.getPath().toString() );
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}
		BufferedReader bufReader = new BufferedReader( fileReader );

		// If bufferReader is ready start parsing the sourceFile
		String curLine = "";
		String prevLine = "";
		int lineNum = 1;
		int sequenceNum = 0;
		try {
			if( bufReader.ready() ) {
				_sequence = new ArrayList<SequenceOrdering>();
				// For as long as there are lines left to read; acquire current one
				while( ( curLine = bufReader.readLine() ) != null ) {
					
					
					// Handle appropriate synchronize construct if they reside on current line
					// Synchronize
					parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_BLOCK, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_LOCK, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_LOCKINTERRUPTIBLY, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_TRYLOCK, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_UNLOCK, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_NEWCONDITION, sequenceNum );
					
					// Latch
					parseLineForConstructs( curLine, lineNum, Constants.LATCH, Constants.LATCH_COUNTDOWN, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.LATCH, Constants.LATCH_AWAIT, sequenceNum );
										
					// Barrier
					parseLineForConstructs( curLine, lineNum, Constants.BARRIER, Constants.BARRIER_RESET, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.BARRIER, Constants.BARRIER_AWAIT, sequenceNum );
					
					// Semaphore
					parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIRE, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIREUNINTERRUPTIBLY, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_TRYACQUIRE, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_DRAIN, sequenceNum );
					parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_RELEASE, sequenceNum );

					lineNum++;
					prevLine = curLine; // Keep current line in case it has a PreemptionPoint annotation
				}

				// Fix the ordering of the interest points
				ArrayList<SequenceOrdering> orderedPoints = correctSequenceOrdering();

				// Create an annotation parser to parse the prevLine's annotations (if any)
				AnnotationParser annotationParser = new AnnotationParser();

				// For each point found, figure out if it is an instrumentation or interest point
				for( SequenceOrdering singlePoint : orderedPoints ) {

					// Figure out if this interest point was already annotated to be an instrumentation point
					InstrumentationPoint instrumentationPoint = annotationParser.parseLineForAnnotations( prevLine,
							lineNum, singlePoint.getCharacterPosition(), singlePoint.getConstructType() );

					// Check for a null value, if so then instrumentation point wasn't there (it is an interest point)
					if( instrumentationPoint == null ) {

						// Add the interesting points to the source file
						source.addInterestingPoint( singlePoint.getInterestPoint() );
					}
					else {

						// Add the instrumentation point to the source file
						source.addInterestingPoint( instrumentationPoint );
					}
				}
			}
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The sequence of interest points discovered is usually out of order, and thus needs 
	 * re-ordering. This method will correct the sequence's ordering while retainning all the
	 * additional metadata as found in the {@link SequenceOrdering} class.
	 * 
	 * @return the ordered arraylist of the sequence ordering
	 */
	private ArrayList<SequenceOrdering> correctSequenceOrdering() {

		ArrayList<SequenceOrdering> sortedOrder = new ArrayList<SequenceOrdering>(); // The sorted order
		int lowestIndex = 0; // The lowest index of to be added (starts off higher then possible)
		int index = 0; // The selected index to be added to the sortOrder next
		int lowestChar = 0;
		// Keep looping till all the interest points are accounted for
		while( _sequence.size() > 0 ) {
			
			lowestChar = _sequence.get(0).getCharacterPosition();
			// Look at a single sequence object
			for( SequenceOrdering singleSequence : _sequence ) {
				
				if( singleSequence.getCharacterPosition() < lowestChar ) {
					lowestIndex = index;
				}
				index++;
			}

			// Add the next lowest interest point in order of sequence
			sortedOrder.add( _sequence.get( lowestIndex ) );
			_sequence.remove( lowestIndex );
		}

		return sortedOrder;
	}

	/**
	 * The passed line will be parsed for the syntax of a synchronization construct, when one is found an
	 * interest point is formed using all the data around it and placed within the inner class {@link SequenceOrdering}
	 * so that it can hold additional data that is not concerned with an {@link InterestPoint}. The order of found points
	 * is represented as a sequence number, this number is used since the ordering of points on the line can be non
	 * sequential and will be ordered later.
	 * 
	 * @param curLine the current line 
	 * @param lineNumber the current line's number
	 * @param construct the synchronization construct 
	 * @param syntax the syntax of the synchronization construct
	 * @param sequenceNum the sequence number
	 */
	private void parseLineForConstructs( String curLine, int lineNumber, String construct, String syntax, int sequenceNum ) {

		int pos = 0; // The last character position
		int currentPos = 0; // The current character position
		boolean stillMore = true; // Flag if there are more relevant syntax

		// Loop for as long as there is relevant syntax
		while( stillMore ) {

			// Keep going unless no more constructs are found
			if( ( currentPos = curLine.indexOf( syntax, pos ) ) != -1 ) {

				// A construct is found, create an interest point
				InterestPoint interestingPoint = new InterestPoint( lineNumber, sequenceNum, syntax );

				// Add to the _sequence, the point and the character position
				_sequence.add( new SequenceOrdering( interestingPoint, currentPos, construct ) );
				sequenceNum++;

				pos = currentPos + syntax.length();
			}
			else {
				stillMore = false;
			}
		}
	}

	/**
	 * This inner class is a data class used to hold additional metadata with an {@link InterestPoint}.
	 * 
	 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
	 */
	private class SequenceOrdering {

		private InterestPoint	interestPoint		= null; // An interest point
		private int				characterPosition	= 0;	// The character position on the line
		private String			constructType		= null; // The construct type

		/**
		 * Constructor use to instantiate a {@link SequenceOrdering} data class.
		 * 
		 * @param interestPoint the interest point
		 * @param characterPosition the character position on the line
		 * @param constructType the construct type
		 */
		public SequenceOrdering( InterestPoint interestPoint, int characterPosition, String constructType ) {
			this.interestPoint = interestPoint;
			this.characterPosition = characterPosition;
			this.constructType = constructType;
		}

		/**
		 * Gets the {@link InterestPoint}.
		 * 
		 * @return the {@link InterestPoint}
		 */
		public InterestPoint getInterestPoint() {
			return interestPoint;
		}

		/**
		 * Gets the character position.
		 * 
		 * @return the character position
		 */
		public int getCharacterPosition() {
			return characterPosition;
		}

		/**
		 * Gets the construct type.
		 * 
		 * @return the construct type
		 */
		public String getConstructType() {
			return constructType;
		}
	}
}