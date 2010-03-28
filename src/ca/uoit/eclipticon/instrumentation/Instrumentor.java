package ca.uoit.eclipticon.instrumentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.AutomaticConfiguration;
import ca.uoit.eclipticon.data.AutomaticConfigurationHandler;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.data.InterestPoint;
import ca.uoit.eclipticon.data.SourceFile;

/**
 * This class will perform the actual instrumentation of source files by inserting 
 * noise based on the instrumentation points that each source file has.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class Instrumentor {

	private final int	BUFFER_SIZE	= 127;				// A common buffer size for character I/O
	private NoiseMaker	_noiseMaker	= new NoiseMaker(); // The object to generate noise

	/**
	 * This method will take a source file and will produce a buffer reader
	 * from it that allows for line-by-line parsing of the file's contents.
	 * 
	 * @param sourceFile the source file to be used for the buffer reader
	 * @return the buffer reader of the file that was used
	 */
	private BufferedReader getBufferReader( File sourceFile ) {

		// Read the sourceFile and create the reading and file content objects
		FileReader fileReader = null;
		try {
			fileReader = new FileReader( sourceFile );
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}

		return new BufferedReader( fileReader );
	}

	/**
	 * Makes a backup of the source file with the file extension of .eclipticon
	 * 
	 * @param sourceFile file to backup
	 * @throws IOException 
	 */
	private void makeBackupFile( File sourceFile ) throws IOException {

		File copyFile = new File( sourceFile.getPath() + ".eclipticon" );
		
		//Create the file if it doesn't exist
		if( !copyFile.exists() ) {
			copyFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		
		//Copy the original to the copy
		try {
			source = new FileInputStream( sourceFile ).getChannel();
			destination = new FileOutputStream( copyFile ).getChannel();
			destination.transferFrom( source, 0, source.size() );
		}
		//Then close the stream
		finally {
			if( source != null ) {
				source.close();
			}
			if( destination != null ) {
				destination.close();
			}
		}

	}

	/**
	 * Prints the file content to the same file path with the modification of
	 * .instr append to the original file.
	 * 
	 * @param instrumentedCode the file's content
	 * @param filePath the file's path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void printFile( String instrumentedCode, String filePath ) {

		// Create the file name for the instrumented file
		String fileName = ( filePath );

		// Write the fileContent to the new file
		FileWriter fw = null;
		try {
			fw = new FileWriter( fileName );
			fw.write( instrumentedCode );
			fw.flush();
			fw.close();
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will revert the source file after the instrumentation back 
	 * to the original state before the instrumentation.
	 * 
	 * @param sourceFile The source file to be reverted
	 * @throws IOException 
	 */
	public void revertToOriginalState( SourceFile sourceFile ) throws IOException {
		//This is the path to the backup file and original file
		File backupFile = new File( sourceFile.getPath() + ".eclipticon" );
		File originalFile = sourceFile.getPath().toFile();
		
		//Only revert if the backup file exists
		if( backupFile.exists() ) {
			// Clear the instrumented file
			originalFile.delete();
			originalFile.createNewFile();
			FileChannel source = null;
			FileChannel destination = null;
			
			// Put back the original file
			try {
				source = new FileInputStream( backupFile ).getChannel();
				destination = new FileOutputStream( sourceFile.getPath().toString() ).getChannel();
				destination.transferFrom( source, 0, source.size() );
			}
			//Then close the stream
			finally {
				if( source != null ) {
					source.close();
				}
				if( destination != null ) {
					destination.close();
				}
				backupFile.delete();
			}
			
		}

	}

	/**
	 * This method will perform the instrumentation on a source file using all
	 * the instrumentation points. The source file is interpreted one line at a
	 * time.
	 * 
	 * @param sourceFile the source file to be instrumented
	 */
	public void instrument( SourceFile sourceFile, boolean automaticMode ) {

		StringBuffer fileContents = new StringBuffer(); // The new file with the instrumentation
		BufferedReader bufReader = getBufferReader( sourceFile.getPath().toFile() ); // Get a buffer reader of this file

		try {
			makeBackupFile( sourceFile.getPath().toFile() );
		}
		catch( IOException e1 ) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String currentLine = ""; // The current line's value
		int lineNum = 1; // The current line number

		// Make the arraylists to hold the points
		ArrayList<InterestPoint> interestingPoints = sourceFile.getInterestingPoints();
		ArrayList<InstrumentationPoint> instrPoints = new ArrayList<InstrumentationPoint>();

		// If automatic mode is used get the instrumentation Points using automatic configuration (automatic overwrites
		// manual)
		if( automaticMode ) {
			instrPoints = getAutomaticInstrumentationPoints( interestingPoints );
		}
		else { // Manual instrumentation is occurring

			// Go through all the interesting points and find the instrumentation points
			for( InterestPoint point : interestingPoints ) {
				// If this interesting point is an instrumentation point then store it
				if( point instanceof InstrumentationPoint ) {
					instrPoints.add( (InstrumentationPoint)point );
				}
			}
		}

		// If bufferReader is ready start parsing the sourceFile
		try {

			if( bufReader.ready() ) {
				String buffer = "";

				// For as long as there are lines left to read; acquire current one
				while( ( currentLine = bufReader.readLine() ) != null ) {

					// Loop through all the instrumentation points
					for( InstrumentationPoint point : instrPoints ) {

						// If this instrumentation point is on the current line
						if( point.getLine() == lineNum ) {

							// Evaluate this line based on the current information
							currentLine = evaluateLine( point, currentLine );
						}
					}

					// Add the currentLine to the buffer
					buffer = buffer + currentLine + "\n";

					// If the buffer's length is over the buffer size then dump it
					if( buffer.length() > this.BUFFER_SIZE ) {
						fileContents.append( buffer );
						buffer = "";
					}

					// Increase the line number
					lineNum++;
				}

				// Append the rest of the buffer before exiting
				fileContents.append( buffer );
			}
		}
		catch( IOException e ) {
			e.printStackTrace();
		}

		// The final instrumented code
		String instrumentedCode = fileContents.toString();

		// If there were some instrumentation points then add the random import and variable
		if( !instrPoints.isEmpty() ) {

			// Add random import and variable to source file
			instrumentedCode = addRandImportAndVariable( instrumentedCode );
		}

		// Print the fileContents to an instrumented source
		printFile( instrumentedCode, sourceFile.getPath().toString() );
	}

	/**
	 * This method will use the automatic configurations to transform every interesting point
	 * into an instrumentation point using the randomized values given in the configuration.
	 * 
	 * @param interestingPoints arraylist of interesting points for a sourcefile
	 * @return arraylist of instrumentation points
	 */
	private ArrayList<InstrumentationPoint> getAutomaticInstrumentationPoints( ArrayList<InterestPoint> interestingPoints ) {

		AutomaticConfigurationHandler configurationHandler = new AutomaticConfigurationHandler();
		try {
			configurationHandler.readXml();
		}
		catch( FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AutomaticConfiguration configuration = configurationHandler.getConfiguration();

		int lowDelayRange = configuration.getLowDelayRange();
		int highDelayRange = configuration.getHighDelayRange();
		int sleepProbability = configuration.getSleepProbability();
		int yieldProbability = configuration.getYieldProbability();
		int synchronizeProbability = configuration.getSynchronizeProbability();
		int barrierProbability = configuration.getBarrierProbability();
		int latchProbability = configuration.getLatchProbability();
		int semaphoreProbability = configuration.getSemaphoreProbability();

		ArrayList<InstrumentationPoint> instrPoints = new ArrayList<InstrumentationPoint>();

		Random rand = new Random();

		for( InterestPoint interestPoint : interestingPoints ) {

			// Figure out the type of noise to use
			int type = 0;
			if( rand.nextInt( 100 ) <= sleepProbability ) {
				type = Constants.SLEEP;
			}
			else {
				type = Constants.YIELD;
			}

			// Figure out the probability of instrumenting given the type of the construct
			int probability = 0;
			String construct = interestPoint.getConstruct();

			if( construct.equals( Constants.SYNCHRONIZE ) ) {
				probability = synchronizeProbability;
			}
			else if( construct.equals( Constants.BARRIER ) ) {
				probability = barrierProbability;
			}
			else if( construct.equals( Constants.LATCH ) ) {
				probability = latchProbability;
			}
			else if( construct.equals( Constants.SEMAPHORE ) ) {
				probability = semaphoreProbability;
			}

			// Add the newly made instrumentation point
			instrPoints.add( new InstrumentationPoint( interestPoint.getLine(), interestPoint.getSequence(), interestPoint.getConstruct(), interestPoint.getConstructSyntax(), type, probability, lowDelayRange, highDelayRange ) );

		}

		return instrPoints;
	}

	/**
	 * Adds the random import statement and a global random variable to the class
	 * which will be used by the instrumentation noise statements.
	 * 
	 * @param instrumentedCode the instrumented code
	 * @return the final instrumented code with the import and variable added
	 */
	private String addRandImportAndVariable( String instrumentedCode ) {

		// Regex's to match on the import and class statements to allow for the injection of the random variable
		String importRegex = "(import)(\\s+)((?:[a-z][a-z\\.\\d\\-]+)\\.(?:[a-z][a-z\\-]+))([\\.]*)([\\*]*)([\\s]*)(;)";
		String classRegex = "(public|private|protected|\\s)+[(\\s)+](class)[(\\s)+]([a-z][a-z0-9_])*[(\\s)+](.*?)(\\{)";
		Pattern importPattern = Pattern.compile( importRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Pattern classPattern = Pattern.compile( classRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE );
		Matcher matcher = null;
		String finalInstrumentedCode = "";

		// Match on the import statement
		matcher = importPattern.matcher( instrumentedCode );
		matcher.find();

		// Take the ending position of the match
		int importEndPos = matcher.end();

		// Append from the start till the end of the matched import and add the import statement
		finalInstrumentedCode = finalInstrumentedCode.concat( instrumentedCode.substring( 0, importEndPos ) + _noiseMaker.makeRandImport() );

		// Match on the class statement
		matcher = classPattern.matcher( instrumentedCode );
		matcher.find();

		// Take the ending position of the match
		int classEndPos = matcher.end();

		// Append from the ending of the import match till the end of the class match, and add the random variable
		finalInstrumentedCode = finalInstrumentedCode.concat( instrumentedCode.substring( importEndPos, classEndPos ) + _noiseMaker.makeRandVariable() );

		// Fill in the rest of the instrumented code
		finalInstrumentedCode = finalInstrumentedCode.concat( instrumentedCode.substring( classEndPos ) );

		return finalInstrumentedCode;
	}

	/**
	 * This method will evaluate a line and will insert noise if the current
	 * instrumentation point demands it.
	 * 
	 * @param point the current instrumentation point that is being serviced
	 * @param currentLine the current line that is being instrumented
	 * @return the modified (instrumented) string of the original line
	 */
	private String evaluateLine( InstrumentationPoint point, String currentLine ) {

		int injectionPosition = -1;

		// Skip to the correct instrumentation point based on the sequence number and the construct's syntax
		for( int i = 0; i <= point.getSequence(); i++ ) {

			injectionPosition = currentLine.indexOf( point.getConstructSyntax(), injectionPosition + 1 );

			if( i == point.getSequence() ) {

				// Point is found, now to backtrack from this point till a valid statement delimiter is found
				injectionPosition = currentLine.indexOf( point.getConstructSyntax(), injectionPosition );

				// Loop backwards from injectionPosition till a valid delimiter is found
				// TODO Not sure this works on a x.call that is split on two lines
				// TODO Need to instrument afterwards as well (need to take into account scope) // Need to avoid comments and strings
				// If in a synchronized block need to check for the correct closing } // Need to avoid comments and strings
				for( int j = injectionPosition; j != 0; j-- ) {

					// If the character matches a delimiter
					if( Character.toString( currentLine.charAt( j ) ).matches( "[(\\s+)]|[;]|[}]|[{]|[\\)]" ) ) {

						// Adjust to the new position
						injectionPosition = j + 1;
						break;
					}
				}
			}
		}

		// Make the noise given the instrumentation information
		String noise = _noiseMaker.makeNoise( point.getProbability(), point.getType(), point.getLow(), point.getHigh() );

		// Add the noise to the line
		String firstHalf = currentLine.substring( 0, injectionPosition );
		String otherHalf = currentLine.substring( injectionPosition );
		currentLine = firstHalf + noise + otherHalf;

		return currentLine;
	}
}