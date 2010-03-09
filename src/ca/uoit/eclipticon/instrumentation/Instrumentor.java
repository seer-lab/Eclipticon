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

import ca.uoit.eclipticon.data.InstrumentationPoint;

/**
 * This class will perform the actual instrumentation of source files
 * given the instrumentation points.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class Instrumentor {

	private final int	BUFFER_SIZE	= 127;				// A common buffer size for character I/O
	private NoiseMaker	_noiseMaker	= new NoiseMaker(); // The object to generate noise
	private int			_charMod	= 0;				// A counter of the modified characters on the current line

	/**
	 * Prints the file content to the same file path with the modification of
	 * .instr append to the original file.
	 * 
	 * @param fileContent the file's content
	 * @param filePath the file's path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void printFile( String fileContent, String filePath ) {

		// Create the file name for the instrumented file
		String fileName = ( filePath + ".instr" );

		// Write the fileContent to the new file
		FileWriter fw = null;
		try {
			fw = new FileWriter( fileName );
			fw.write( fileContent );
			fw.flush();
			fw.close();
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will perform the instrumentation on a source file given all
	 * the instrumentation points. The source file is interpreted one line at a
	 * time, and when an instrumentation point is on the current line then by a
	 * character biases. Multiple instrumentation are allowed and handled on a
	 * single line.
	 * 
	 * @param sourceFile the source file to be instrumented
	 * @param instrPoints the arraylist of instrumentation points to be considered
	 */
	public void instrument( File sourceFile, ArrayList<InstrumentationPoint> instrPoints ) {

		StringBuffer fileContents = new StringBuffer(); // The new file with the instrumentation
		BufferedReader bufReader = getBufferReader( sourceFile ); // The buffer reader of the original source file
		String curLine = ""; // The current line's value
		String modLine = ""; // The modified current line's value
		int lineNum = 1; // The current line number

		// If bufferReader is ready start parsing the sourceFile
		try {
			if( bufReader.ready() ) {
				String buffer = "";
				_charMod = 0;

				// For as long as there are lines left to read; acquire current one
				while( ( curLine = bufReader.readLine() ) != null ) {

					modLine = curLine;

					// Loop through all the instrumentation points
					for( int i = 0; i < instrPoints.size(); i++ ) {

						// Get a single instrumentation point
						InstrumentationPoint singlePoint = instrPoints.get( i );

						// If the instrumentation point resides in the current source file
						if( singlePoint.getSource() == sourceFile.toString() ) {

							// If this instrumentation point is on the current line
							if( singlePoint.getLine() == lineNum ) {

								// Evaluate this line based on the current information
								modLine = evaluateLine( singlePoint, curLine, modLine );
							}
						}
					}

					// Add modLine or curLine to the buffer depending if modLine changed
					if( !modLine.equals( curLine ) ) {
						buffer = buffer + modLine + "\n";
					}
					else {
						buffer = buffer + curLine + "\n";
					}

					// If the buffer's length is over the buffer size then dump it
					if( buffer.length() > this.BUFFER_SIZE ) {
						fileContents.append( buffer );
						buffer = "";
					}

					// Increase the line number, reset the modLine and charMod
					lineNum++;
					_charMod = 0;
				}

				// Append the rest of the buffer before exiting
				fileContents.append( buffer );
			}
		}
		catch( IOException e ) {
			e.printStackTrace();
		}

		// Print the fileContents to an instrumented source
		printFile( fileContents.toString(), sourceFile.toString() );
	}

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
	 * This method will evaluate a line and will insert noise if the current
	 * instrumentation point demands it. The method will also be aware of
	 * previous modifications that occurred on it and will take that into account.
	 * 
	 * @param currentPoint the current instrumentation point that is being serviced
	 * @param curLine the current line that is being serviced
	 * @return the modified (instrumented) string of the original line
	 */
	private String evaluateLine( InstrumentationPoint currentPoint, String curLine, String modLine ) {

		// If the modLine is not null then the line has been changed
		if( modLine != null ) {
			curLine = modLine; // Make the curLine equal to the modLine since that is our base now
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
			if( ( currentPoint.getCharacter() + _charMod - 1 ) == currentPosition ) {

				// Make the noise given the instrumentation information
				String noise = _noiseMaker.makeNoise( currentPoint.getChance(), currentPoint.getType(), currentPoint
						.getLow(), currentPoint.getHigh() );

				// Add the noise to the line
				modLine = modLine + noise;

				// Change the character modifier to reflect the added noise
				_charMod = _charMod + noise.length() - 1;
			}

			// If there are more characters add the current character to the mod line
			if( charIter.next() != CharacterIterator.DONE ) {
				modLine = modLine + charIter.current();
			}
		}

		return modLine;
	}
}