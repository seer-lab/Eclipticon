package ca.uoit.eclipticon.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;

import org.eclipse.core.runtime.Path;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.data.InterestPoint;
import ca.uoit.eclipticon.data.SourceFile;
import ca.uoit.eclipticon.parsers.PreParser.SynchronizedMethods;

/**
 * This class is concerned with the acquisition of the files found in a workspace, as well as parsing
 * for {@link InterestPoint} locations given a source file.
 *
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class FileParser {

	private ArrayList<InterestPoint>	_interestPointsOnLine	= new ArrayList<InterestPoint>();

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

					// Handle appropriate synchronized method calls if they reside on current line
					handleFindingMethods( curLine, currentLineNum, source );

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

								// Ensure that an empty line doesn't go through
								if( nextLine != null ) {
									synchronizedPosition = 1; // Reset since new line
									typeFound = determineSynchronizedType( nextLine, synchronizedPosition );
								}
								else {
									break;
								}
							}

							// The synchronized type is a block
							if( typeFound == 1 ) {

								// If the lines are different then we stop checking
								if( currentLineNum != lineNum ) {
									synchronizedOnSameLine = false;
								}
							}
							else { // Method is found

								// Remove the two last interest point that was added (synchronized and the method)
								_interestPointsOnLine.remove( _interestPointsOnLine.size() - 1 );
								
								// If null then remove last interest point in the sourcefile (ie: has moved lines)
								if (_interestPointsOnLine.size() == 0){
									
									// If the source has more then one point remove it, otherwise skip
									if (source.getInterestingPoints().size() > 0){										
										source.getInterestingPoints().remove( source.getInterestingPoints().size()-1 );
									}
								}
								else{									
									_interestPointsOnLine.remove( _interestPointsOnLine.size() - 1 );
								}
							}
						}
						else { // Synchronized is not found, exit
							synchronizedOnSameLine = false;
						}
					}

					// If there are points found then figure out order and add the points
					if( _interestPointsOnLine.size() > 0 ) {

						// For each point found, figure out if it is an instrumentation or interest point
						for( InterestPoint interestPoint : _interestPointsOnLine ) {

							// Figure out if this interest point was already annotated to be an instrumentation point
							InstrumentationPoint instrumentationPoint = annotationParser.parseLineForAnnotations(
									prevLine, currentLineNum, interestPoint.getSequence(), interestPoint
											.getConstruct(), interestPoint.getConstructSyntax() );

							// Check for a null, if so then instrumentation point wasn't there (it is an interest point)
							if( instrumentationPoint == null ) {
								source.addInterestingPoint( interestPoint );
							}
							else {
								source.addInterestingPoint( instrumentationPoint );
							}
						}
						_interestPointsOnLine.clear();
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
	 * This method will find all the instances of any synchronized method calls that fall on this
	 * line. The methods are acquired from the pre-parsed instance that previously acquires a collection
	 * of all the synchronized methods. In addition the package scope is check to reduce the false
	 * positives found (though it still is not checking on the object type, and is still naive).
	 *
	 * @param curLine the current line represented as a string
	 * @param lineNum the line number
	 * @param source the source file
	 */
	private void handleFindingMethods( String curLine, int lineNum, SourceFile source ) {

		// Acquire a reference to the PreParser to get the synchronized methods
		PreParser preParser = new PreParser();
		ArrayList<SynchronizedMethods> synchronizedMethods = preParser.getSynchronizedMethods();

		// Create method signature checker
		MethodCallValidator methodSignatureCheck = new MethodCallValidator();

		int pos = 0; // The last character position
		int currentPos = 0; // The current character position
		boolean stillMore = true; // Flag if there are more relevant syntax

		// For this source file find all synchronized method calls that matches
		for( SynchronizedMethods singleMethod : synchronizedMethods ) {

			stillMore = true;
			int sequenceNumber = 0; // Sequence number of the current method call

			// Loop for as long as there is relevant syntax
			while( stillMore ) {

				// Keep going unless no more method calls are found
				if( ( currentPos = curLine.indexOf( singleMethod.getName(), pos ) ) != -1 ) {

					// Construct was possibly found, now to verify if previous character is a non-word
					if( Character.toString( curLine.charAt( currentPos - 1 ) ).matches( "[\\W]" ) ) {

						if( currentPos + singleMethod.getName().length() < curLine.length() ) {

							// Next character is a non-word
							if( Character.toString( curLine.charAt( currentPos + singleMethod.getName().length() ) )
									.matches( "[\\W]" ) ) {

								// A method call is found, check to see if it is valid, if so add it
								if( methodSignatureCheck.isMethodImportedInFile( singleMethod.getFilePath(), source
										.getPackageAndImports() ) ) {
																		
									_interestPointsOnLine.add( new InterestPoint( lineNum, sequenceNumber,
											Constants.SYNCHRONIZE, singleMethod.getName() ) );
								}
							}
						}
					}

					pos = currentPos + singleMethod.getName().length();
					sequenceNumber++;
				}
				else {
					stillMore = false;
				}
			}
		}
	}

	/**
	 * This method will find the next occurrence from the last synchronized position for a synchronized
	 * block (syntax) and will add this occurrence to the _sequence arraylist (which might be removed
	 * depending on if the occurrence turns out to be a synchronized method.
	 *
	 * @param curLine the current line that is being checked
	 * @param currentLineNum the current line number
	 * @param synchronizedPosition the last position of a found synchronized block on this line
	 * @return the character position a found synchronized block, or -1 if none are found
	 */
	private int findNextSynchronized( String curLine, int currentLineNum, int synchronizedPosition ) {

		int pos = synchronizedPosition + 1; // The last character position
		int currentPos = 0; // The current character position
		
		// Look for the next synchronized keyword
		if( ( currentPos = curLine.indexOf( Constants.SYNCHRONIZE_BLOCK, pos ) ) != -1 ) {

			// Find the sequence number to use
			int sequenceNumber = 0;
			int tempPos = 0;
			String subLine = curLine.substring( 0, pos+Constants.SYNCHRONIZE_BLOCK.length() );		
			while( (tempPos = subLine.indexOf( Constants.SYNCHRONIZE_BLOCK, tempPos)) != -1 ){
				sequenceNumber++;
				tempPos = subLine.indexOf( Constants.SYNCHRONIZE_BLOCK, tempPos+1);
				if(tempPos == -1){
					break;
				}
			}
			
			// A construct is found, create an interest point (might have to remove it, if a method synchronized
			_interestPointsOnLine.add( new InterestPoint( currentLineNum, sequenceNumber,
					Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_BLOCK ) );
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
		parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_NEWCONDITION );
		parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_TRYLOCK );
		parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_UNLOCK );

		// Barrier
		parseLineForConstructs( curLine, lineNum, Constants.BARRIER, Constants.BARRIER_AWAIT );
		parseLineForConstructs( curLine, lineNum, Constants.BARRIER, Constants.BARRIER_RESET );

		// Latch
		parseLineForConstructs( curLine, lineNum, Constants.LATCH, Constants.LATCH_AWAIT );
		parseLineForConstructs( curLine, lineNum, Constants.LATCH, Constants.LATCH_COUNTDOWN );

		// Semaphore
		parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIRE );
		parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIREUNINTERRUPTIBLY );
		parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_DRAIN );
		parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_RELEASE );
		parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORE, Constants.SEMAPHORE_TRYACQUIRE );
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
		int sequenceNumber = 0; // The sequence number of the current constructs on the line

		// Loop for as long as there is relevant syntax
		while( stillMore ) {

			// Keep going unless no more constructs are found
			if( ( currentPos = curLine.indexOf( syntax, pos ) ) != -1 ) {

				// Construct was possibly found, now to verify based on previous character
				if( Character.toString( curLine.charAt( currentPos ) ).matches( "[\\.]|[\\s]|[\\)]|[\\(]|[;]|[}]|[{]" ) ) {

					if( currentPos + syntax.length() < curLine.length() ) {

						// Now to compare to the next character
						if( Character.toString( curLine.charAt( currentPos + syntax.length() ) )
								.matches( "[\\s]|[\\(]" ) ) {

							// A construct is found, create an interest point
							_interestPointsOnLine.add( new InterestPoint( lineNumber, sequenceNumber,
									construct, syntax ));
							sequenceNumber++;
						}
					}
				}
				pos = currentPos + syntax.length();
			}
			else {
				stillMore = false;
			}
		}
	}

	public Boolean checkIfBackupExists( Path root ) {

		boolean backupExists = false;

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

					// The file is a source file
					if( fileTemp.toString().endsWith( ".eclipticon" ) ) {
						backupExists = true;

					}
				}
				// If it is a folder then recursively call getFiles and add their returns to the current arraylist
				else if( fileTemp.isDirectory() ) {
					if( checkIfBackupExists( currentPath ) ) {
						backupExists = true;
					}
				}
			}
		}
		return backupExists;
	}

	public void manipulateAnnotation( SourceFile sourceFile, InstrumentationPoint instrumentationPoint, int deleteUpdateAdd ) throws IOException {
		
		StringBuffer fileContents = new StringBuffer(); // The new file with the instrumentation
		FileReader fileReader = null;
		AnnotationParser annotationParser = new AnnotationParser();
		
		try {
			fileReader = new FileReader( sourceFile.getPath().toFile() );
		}
		catch( FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BufferedReader bufReader = new BufferedReader( fileReader );
		
		// If bufferReader is ready start reading the sourceFile
		if( bufReader.ready() ) {
			String buffer = "";
			int lineNum = 0;
			String currentLine;
			// For as long as there are lines left to read; acquire current one
			while( ( currentLine = bufReader.readLine() ) != null ) {
				// When it reaches the line with the Interest Point on it
				if( lineNum == ( instrumentationPoint.getLine() - 2 ) ) {
					
					// Delete annotation
					if (deleteUpdateAdd == 0){
						currentLine = "";
					}
					else if (deleteUpdateAdd == 1){ // Update
						currentLine =  annotationParser.updateAnnotationComment( instrumentationPoint, currentLine );
						buffer = buffer + currentLine + "\n";
					}
					else if (deleteUpdateAdd == 2){ // Add
						currentLine =  currentLine + "\n" + annotationParser.createAnnotationComment( instrumentationPoint );
						buffer = buffer + currentLine + "\n";
					}
				}
				else{
					buffer = buffer + currentLine + "\n";
				}

				// If the buffer's length is over the buffer size then dump it
				if( buffer.length() > 127 ) {
					fileContents.append( buffer );
					buffer = "";
				}
				// Increase the line number
				lineNum++;
			}

			// Append the rest of the buffer before exiting
			fileContents.append( buffer );
		}

		// Write the fileContent to the new file
		FileWriter fw = null;
		try {
			fw = new FileWriter( sourceFile.getPath().toString() );
			fw.write( fileContents.toString() );
		}
		finally { // Close the writer
			if( fw != null ) {
				fw.flush();
				fw.close();
			}
		}

	}
}