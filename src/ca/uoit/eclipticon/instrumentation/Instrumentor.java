package ca.uoit.eclipticon.instrumentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Random;

import ca.uoit.eclipticon.data.InstrumentationPoint;

/**
 * This class will perform the actual instrumentation of source files
 * given the instrumentation points.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class Instrumentor {

	private final int	BUFFER_SIZE	= 127;	// A common buffer size for character I/O

	/**
	 * Method will create a noise statement given the noise type and delay ranges.
	 * 
	 * @param type the noise type
	 * @param chance the chance of this noise activating out of 100
	 * @param low the lower bound of the sleep delay
	 * @param high the upper bound of the sleep delay
	 * @return the noise statement
	 */
	public String makeNoise( int chance, int type, int low, int high ) {

		String noise = null;

		// Based on the type form a noise statement using type and delay ranges
		switch( type ){
			case 0: // Type 0 = sleep
				noise = getIfChance( chance ) + createSleep( low, high );
				break;
			case 1: // Type 1 = yield
				noise = getIfChance( chance ) + createYield();
				break;
		}

		// Returns the noise statement
		return noise;
	}

	/**
	 * This method will create the sleep instrumentation statement by using the
	 * delay ranges, it also includes the appropriate try/catch syntax.
	 * 
	 * @param low the low bound of the sleep delay
	 * @param high the high bound of the sleep delay
	 * @return the sleep statement
	 */
	private String createSleep( int low, int high ) {

		Random rand = new Random();

		return "try {Thread.sleep(" + ( rand.nextInt( high - low ) + low ) + ");} catch (Exception e) {};";
	}

	/**
	 * This method will create the yield instrumentation statement,
	 * it also includes the appropriate try/catch syntax.
	 * 
	 * @return the yield statement
	 */
	private String createYield() {
		return "try {Thread.yield(" + ");} catch (Exception e) {};";
	}

	/**
	 * This method will create the if statement that controls the chance (out of
	 * 100) of an instrumentation point actually executing.
	 * 
	 * @param chance the value out of 100 that will decide the executing chance
	 * @return the if statement that will decide if the instrument will occur
	 */
	private String getIfChance( int chance ) {

		Random rand = new Random();
		String randomNumber = String.valueOf( ( rand.nextInt( 100 - 0 ) + 0 ) );

		return "if(" + randomNumber + " <= " + chance + ")";
	}

	/**
	 * Prints the file content to the same file path with the modification of
	 * .instr append to the original file.
	 * 
	 * @param fileContent the file's content
	 * @param filePath the file's path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void printFile( String fileContent, String filePath ) throws IOException {

		// Create the file name for the instrumented file
		String fileName = ( filePath + ".instr" );

		// Write the fileContent to the new file
		FileWriter fw = new FileWriter( fileName );
		fw.write( fileContent );
		fw.flush();
		fw.close();
	}

	/**
	 * This method will perform the instrumentation on a source file given all
	 * the instrumentation points. The source file is interpreted one line at a
	 * time, and when an instrumentation point is on the current line then by a
	 * character biases. Multiple instrumentation are allowed and handled on a
	 * single line.
	 * 
	 * @TODO Should probably refactor and split this algorithm up more.
	 * @param sourceFile the source file to be instrumented
	 * @param instrPoints the arraylist of instrumentation points to be considered
	 */
	public void instrument( File sourceFile, ArrayList<InstrumentationPoint> instrPoints ) {

		// Read the sourceFile and create the reading and file content objects
		FileReader fileReader = null;
		try {
			fileReader = new FileReader( sourceFile );
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}
		BufferedReader bufReader = new BufferedReader( fileReader );
		StringBuffer fileContents = new StringBuffer();

		// If bufferReader is ready start parsing the sourceFile
		String curLine = "";
		String modLine = "";
		String noise = "";
		int lastCharMod = 0;
		int lineNum = 1;
		try {
			if( bufReader.ready() ) {
				String buffer = "";
				int charMod = 0;

				// For as long as there are lines left to read; acquire current one
				while( ( curLine = bufReader.readLine() ) != null ) {

					// The modified line will be based off the current line
					modLine = curLine;

					// Loop through all the instrumentation points
					for( int i = 0; i < instrPoints.size(); i++ ) {

						// Get a single instrumentation point
						InstrumentationPoint singlePoint = instrPoints.get( i );

						// If the instrumentation point resides in the current source file
						if( singlePoint.getSource() == sourceFile.toString() ) {

							// If this instrumentation point is on the current line
							if( singlePoint.getLine() == lineNum ) {

								// If the modLine is not null then the line has been changed
								if( modLine != null ) {
									curLine = modLine;
								}

								// Reset the modLine to accommodate new instrumentation point
								modLine = "";

								// Create a characterIterator to navigate the line
								CharacterIterator charIter = new StringCharacterIterator( curLine );

								// For as long as there are characters left to read
								while( charIter.current() != CharacterIterator.DONE ) {

									// Get the current position of the current character
									int currentPosition = charIter.getIndex();

									// If the current position is where the instrumentation should occur
									if( ( singlePoint.getCharacter() + charMod - 1 ) == currentPosition ) {

										// Make the noise given the instrumentation information
										noise = makeNoise( singlePoint.getChance(), singlePoint.getType(), singlePoint.getLow(), singlePoint.getHigh() );

										// Add the noise to the line
										modLine = modLine + noise;

										// Change the character modifier to reflect the added noise
										lastCharMod = charMod + noise.length() - 1;
									}

									// If there are more characters add the current character to the mod line
									if( charIter.next() != CharacterIterator.DONE ) {
										modLine = modLine + charIter.current();
									}
								}

								// Update the character modifier
								charMod = lastCharMod;
							}
						}
					}

					// If the modLine is not null then append it to the fileContents
					if( modLine != null ) {

						// Add modLine to the buffer
						buffer = buffer + modLine + "\n";

						// If the buffer's length is over the buffer size then dump it
						if( buffer.length() > this.BUFFER_SIZE ) {
							fileContents.append( buffer );
							buffer = "";
						}

						// Increase the line number, reset the modLine and charMod
						lineNum++;
						modLine = null;
						charMod = 0;
					}
				}

				// Append the rest of the buffer before exiting
				fileContents.append( buffer );
			}
		}
		catch( IOException e ) {
			e.printStackTrace();
		}

		// Print the fileContents to an instrumented source
		try {
			printFile( fileContents.toString(), sourceFile.toString() );
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
	}
}