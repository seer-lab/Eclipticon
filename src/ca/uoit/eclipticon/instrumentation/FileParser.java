package ca.uoit.eclipticon.instrumentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
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

	private ArrayList<SequenceOrdering>	_sequence	= new ArrayList<SequenceOrdering>();	// Sequence of constructs

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
	 * the current source file, these points are then turned into {@link InterestPoint}
	 * and are attached to the source file.
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

		// Create an annotation parser to parse the prevLine's annotations
		AnnotationParser annotationParser = new AnnotationParser();

		String curLine = ""; // The current line
		String prevLine = ""; // The previous line (has annotation possibly)
		String nextLine = ""; // The next line if needed to move forward
		int lineNum = 1; // The active line number
		int currentLineNum = 1; // The current line number
		int synchronizedPosition = -1; // The last found synchronized character position on the line
		boolean synchronizedOnSameLine = true; // If the synchronized is still on the same line

		// If bufferReader is ready start parsing the sourceFile
		try {
			if( bufReader.ready() ) {

				// For as long as there are lines left to read; acquire current one
				while( ( curLine = bufReader.readLine() ) != null ) {

					// Due to new line
					synchronizedOnSameLine = true;
					currentLineNum = lineNum;

					// Handle appropriate synchronize construct if they reside on current line
					handleFindingConstructs( curLine, currentLineNum );

					while( synchronizedOnSameLine ) {

						synchronizedPosition = findNextSynchronized( curLine, currentLineNum, synchronizedPosition );

						if( synchronizedPosition > -1 ) { // Synchronized is found

							// Check current line for a terminating character after the syntax
							int typeFound = determineSynchronizedType( curLine, synchronizedPosition
									+ Constants.SYNCHRONIZE_BLOCK.length() );

							// Keep looping till a delimiter for the synchronized is found
							while( typeFound == 0 ) {
								nextLine = bufReader.readLine();
								lineNum++;
								synchronizedPosition = 1; // Reset since new line
								typeFound = determineSynchronizedType( nextLine, synchronizedPosition );
							}

							// The synchronized type is a block
							if( typeFound == 1 ) {

								// If the lines are different then we stop checking
								if( currentLineNum != lineNum ) {
									synchronizedOnSameLine = false;
								}
							}
							else { // Method is found
								// Ignore
							}
						}
						else { // Synchronized is not found, exit
							synchronizedOnSameLine = false;
						}
					}

					// If there are points found then figure out order and add the points
					if( _sequence.size() > 0 ) {

						// Fix the ordering of the interest points
						ArrayList<SequenceOrdering> orderedPoints = correctSequenceOrdering();
						int sequenceNumber = 0;

						// For each point found, figure out if it is an instrumentation or interest point
						for( SequenceOrdering sequencePoint : orderedPoints ) {

							// Figure out if this interest point was already annotated to be an instrumentation point
							InstrumentationPoint instrumentationPoint = annotationParser.parseLineForAnnotations(
									prevLine, currentLineNum, sequenceNumber, sequencePoint.getInterestPoint()
											.getConstruct(), sequencePoint.getInterestPoint().getConstructSyntax() );
							sequenceNumber++;

							// Check for a null value, if so then instrumentation point wasn't there (it is an interest
							// point)
							if( instrumentationPoint == null ) {

								source.addInterestingPoint( sequencePoint.getInterestPoint() );
							}
							else {

								source.addInterestingPoint( instrumentationPoint );
							}
						}
						_sequence.clear();
					}

					// If both line numbers are the same then we didn't move due to the synchronized
					if( currentLineNum == lineNum ) {
						lineNum++;
					}
					else { // We moved due to the synchronized and the line needs not increment
						currentLineNum = lineNum;
					}
					prevLine = curLine; // Keep current line in case it has a PreemptionPoint annotation
				}
			}
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will find the next occurrence from the last synchronized position for a synchronized
	 * block (syntax) and will add this occurrence to the _sequence arraylist.
	 * 
	 * @param curLine the current line that is being checked
	 * @param currentLineNum the current line number
	 * @param synchronizedPosition the last position of a found synchronized block on this line
	 * @return the character position a found synchronized block, or -1 if none are found
	 */
	private int findNextSynchronized( String curLine, int currentLineNum, int synchronizedPosition ) {

		int pos = synchronizedPosition + 1; // The last character position
		int currentPos = 0; // The current character position
		boolean stillMore = true; // Flag if there are more relevant syntax

		// Loop for as long as there is relevant syntax
		while( stillMore ) {

			// Keep going unless no more constructs are found
			if( ( currentPos = curLine.indexOf( Constants.SYNCHRONIZE_BLOCK, pos ) ) != -1 ) {

				// A construct is found, create an interest point
				InterestPoint interestingPoint = new InterestPoint( currentLineNum, _sequence.size(),
						Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_BLOCK );
				_sequence.add( new SequenceOrdering( interestingPoint, currentPos ) );
				pos = currentPos + Constants.SYNCHRONIZE_BLOCK.length();
				break; // Exit since we found a synchronized block and need to handle it appropriately
			}
			else {
				stillMore = false;
			}
		}
		return currentPos;
	}

	/**
	 * Using the current line and the last synchronized syntax position, an attempt of finding
	 * the type of synchronized is carried out. If the next valid character is found to be a '('
	 * then this represents a synchronized block, other wise if any other non-whitespace character
	 * is found then a synchronized method was found. In the situation where nothing is found then 
	 * the next line needs to be examined.
	 * 
	 * @param curLine the current line that is being checked to determine the the found synchronized type
	 * @param synchronizedPosition the last position of the found synchronized syntax
	 * @return 0 = nothing found (try next line) | 1 = block synchronized found | 2 = method synchronized found
	 */
	private int determineSynchronizedType( String curLine, int synchronizedPosition ) {

		int sychronizedType = 0;

		// If there are characters on this line then iterate through it
		if( !curLine.isEmpty() ) {

			// Start a character iterator before the synchronized position
			StringCharacterIterator charIter = new StringCharacterIterator( curLine, synchronizedPosition - 1 );
			String nextChar = null;

			// Keep looping till the end of the line is hit, or a break is encountered
			while( charIter.next() != CharacterIterator.DONE ) {

				nextChar = Character.toString( charIter.current() );

				// If the next character is a '(', we have a block synchronized
				if( nextChar.equals( "(" ) ) {
					sychronizedType = 1;
					break;
				}
				else if( nextChar.matches( "\\s" ) ) { // Whitespace is found
					// Consume it
				}
				else { // Another character showed up, we have a method synchronized
					sychronizedType = 2;
					break;
				}
			}
		}
		return sychronizedType;
	}

	/**
	 * All the synchronized constructs are handled here for the current line that is being examined.
	 * The only exceptions are the synchronized block and method constructs since they have to be handled
	 * in a different manner, and are handled elsewhere.
	 * 
	 * @param curLine the current line for which the constructs are checked on
	 * @param lineNum the current line number
	 */
	private void handleFindingConstructs( String curLine, int lineNum ) {

		// Synchronize
		parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_LOCK );
		parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_LOCKINTERRUPTIBLY );
		parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_TRYLOCK );
		parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_UNLOCK );
		parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_NEWCONDITION );

		// Latch
		parseLineForConstructs( curLine, lineNum, Constants.LATCH, Constants.LATCH_COUNTDOWN );
		parseLineForConstructs( curLine, lineNum, Constants.LATCH, Constants.LATCH_AWAIT );

		// Barrier
		parseLineForConstructs( curLine, lineNum, Constants.BARRIER, Constants.BARRIER_RESET );
		parseLineForConstructs( curLine, lineNum, Constants.BARRIER, Constants.BARRIER_AWAIT );

		// Semaphore
		parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIRE );
		parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIREUNINTERRUPTIBLY );
		parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_TRYACQUIRE );
		parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_DRAIN );
		parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_RELEASE );
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
	private void parseLineForConstructs( String curLine, int lineNumber, String construct, String syntax ) {

		int pos = 0; // The last character position
		int currentPos = 0; // The current character position
		boolean stillMore = true; // Flag if there are more relevant syntax

		// Loop for as long as there is relevant syntax
		while( stillMore ) {

			// Keep going unless no more constructs are found
			if( ( currentPos = curLine.indexOf( syntax, pos ) ) != -1 ) {

				// A construct is found, create an interest point
				InterestPoint interestingPoint = new InterestPoint( lineNumber, _sequence.size(), construct, syntax );
				_sequence.add( new SequenceOrdering( interestingPoint, currentPos ) );
				pos = currentPos + syntax.length();
			}
			else {
				stillMore = false;
			}
		}
	}

	/**
	 * The sequence of interest points discovered is usually out of order, and thus needs 
	 * re-ordering. This method will correct the sequence's ordering while retaining all the
	 * additional metadata as found in the {@link SequenceOrdering} class.
	 * 
	 * @return the ordered arraylist of the sequence ordering
	 */
	private ArrayList<SequenceOrdering> correctSequenceOrdering() {

		ArrayList<SequenceOrdering> sortedOrder = new ArrayList<SequenceOrdering>(); // The sorted order

		// If more then one point then sort the points
		if( _sequence.size() > 0 ) {

			int lowestIndex = 0; // The lowest index of to be added (starts off higher then possible)
			int index = 0; // The selected index to be added to the sortOrder next
			int lowestChar = 0; // The lowest character position found

			// Keep looping till all the interest points are accounted for
			while( _sequence.size() > 0 ) {

				lowestChar = _sequence.get( 0 ).getCharacterPosition();
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
		}
		else { // If only one point found then add it
			sortedOrder.add( _sequence.get( 0 ) );
		}
		return sortedOrder;
	}

	/**
	 * This inner class is a data class used to hold additional metadata such as the character 
	 * position that the {@link InterestPoint} was found at in the source line.
	 * 
	 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
	 */
	private class SequenceOrdering {

		private InterestPoint	_interestPoint		= null; // An interest point
		private int				_characterPosition	= 0;	// The character position on the line

		/**
		 * Constructor use to instantiate a {@link SequenceOrdering} data class.
		 * 
		 * @param interestPoint the interest point
		 * @param characterPosition the character position on the line
		 * @param constructType the construct type
		 */
		public SequenceOrdering( InterestPoint interestPoint, int characterPosition ) {
			_interestPoint = interestPoint;
			_characterPosition = characterPosition;
		}

		/**
		 * Gets the {@link InterestPoint}.
		 * 
		 * @return the {@link InterestPoint}
		 */
		public InterestPoint getInterestPoint() {
			return _interestPoint;
		}

		/**
		 * Gets the character position.
		 * 
		 * @return the character position
		 */
		public int getCharacterPosition() {
			return _characterPosition;
		}
	}
	public Boolean checkIfBackupExists( Path root ) {
		// Make a file out of the path
		File file = root.toFile();

		// If the path is a directory
		if( file.isDirectory() ) {

			// Get all Files and Folders in it
			File[] allFiles = file.listFiles();

			// Go through each File/Folder
			for( File fileTemp : allFiles ) {

				Path currentPath = new Path( fileTemp.getPath() );
				
				// If it is a file
				if( fileTemp.isFile() ) {

					// and the file is a source file
					if( fileTemp.toString().endsWith( ".eclipticon" ) ) {
						return true;
						
					}
				}
				// If it is a folder then recursively call getFiles and add their returns to the current arraylist
				else if( fileTemp.isDirectory() ) {
					if( checkIfBackupExists( currentPath ) ) {
						return true;
					}
				}
				
			}
		}
		return false;
	}
}