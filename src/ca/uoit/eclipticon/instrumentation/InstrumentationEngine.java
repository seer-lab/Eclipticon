package ca.uoit.eclipticon.instrumentation;

/**
 * This class will perform the actual source instrumentation of files given an
 * XML file that contains all the instrumentation points.						  					  
 * 																					  
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc										       
 *																					  
 */
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JFileChooser;

import ca.uoit.eclipticon.data.InstrumentationDataHandler;
import ca.uoit.eclipticon.data.InstrumentationPoint;


public class InstrumentationEngine {

	/** A common buffer size for character I/O */
	public final int BUFFER_SIZE = 127;

	/** The chance of instrumentation points executing */
	public int CHANCE = 50;

	/**
	 * Instantiates a new instrumentation engine and acquire the XML file that
	 * contains the list of the instrumentation points. The file will be parsed
	 * and the actual instrumentation points will be acquired to be applied to
	 * the source files they pertain to.
	 */
	public InstrumentationEngine() {

		// Get the xml file of that has all the instrumentation points
		File xmlFile = getXmlFile();
		
		// Acquire the instrumentation points data
		ArrayList<InstrumentationPoint> instrPoints = parseXmlFile(xmlFile);

		// A set that will store the files that have been instrumented
		Set<String> usedSources = new HashSet<String>();

		// Loop through each of the source file
		for (InstrumentationPoint singlePoint : instrPoints) {

			// If this is a new source file
			if (!usedSources.contains(singlePoint.getSource())) {

				// Instrument this source file
				instrumentSourceFile(singlePoint, instrPoints);

				// Add this source file to the set of already used sources
				usedSources.add(singlePoint.getSource());
			}
		}
	}

	/**
	 * This method will prompt the user to select the xml file that they
	 * wish to use for the instrumentation. 
	 * 
	 * @TODO Might change it so it's more transparent to the user.
	 * 
	 * @return xmlFile the xml file that has all the instrumentation data
	 */
	private File getXmlFile() {
		
		// A file chooser to specify the directory to save the file in
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		int returnValue = fc.showDialog(fc, "Use XML File");

		// Acquire the root folder
		File xmlFile = fc.getSelectedFile();

		// Handle the user's actions in the file dialog chooser
		if (returnValue == JFileChooser.CANCEL_OPTION) {
			System.exit(0);
		}

		return xmlFile;
	}

	
	/**
	 * This class will perform the instrumentation on a source file given all
	 * the instrumentation points. The source file is interpreted one line at a
	 * time, and when an instrumentation point is on the current line then by a
	 * character biases. Multiple instrumentation are allowed and handled on a
	 * single line.
	 * 
	 * @param sourceFileInstr
	 *            the source file to be instrumented
	 * @param instrPoints
	 *            the arraylist of instrumentation points to be considered
	 */
	private void instrumentSourceFile(InstrumentationPoint sourceFileInstr,
			ArrayList<InstrumentationPoint> instrPoints) {

		// Make a file object of the source file being used
		File sourceFile = null;
		sourceFile = new File(sourceFileInstr.getSource());

		// Read the sourceFile and create the reading and file content objects
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(sourceFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader bufReader = new BufferedReader(fileReader);
		StringBuffer fileContents = new StringBuffer();

		// If bufferReader is ready start parsing the sourceFile
		String curLine = "";
		String modLine = "";
		String noise = "";
		int lastCharMod = 0;
		int lineNum = 1;
		try {
			if (bufReader.ready()) {
				String buffer = "";
				int charMod = 0;

				// For as long as there are lines left to read; acquire current
				// one
				while ((curLine = bufReader.readLine()) != null) {

					// The modified line will be based off the current line
					modLine = curLine;

					// Loop through all the instrumentation points
					for (int i = 0; i < instrPoints.size(); i++) {

						// Get a single instrumentation point
						InstrumentationPoint singlePoint = instrPoints.get(i);

						// If the instrumentation point resides in the current
						// source file
						if (singlePoint.getSource() == sourceFile.toString()) {

							// If this instrumentation point is on the current
							// line
							if (singlePoint.getLine() == lineNum) {

								// If the modLine is not null then the line has
								// been changed
								if (modLine != null) {
									curLine = modLine;
								}

								// Reset the modLine to accommodate new
								// instrumentation point
								modLine = "";

								// Create a characterIterator to navigate the
								// line
								CharacterIterator charIter = new StringCharacterIterator(
										curLine);

								// For as long as there are characters left to
								// read
								while (charIter.current() != CharacterIterator.DONE) {

									// Get the current position of the current
									// character
									int currentPosition = charIter.getIndex();

									// If the current position is where the
									// instrumentation should occur
									if ((singlePoint.getChar() + charMod - 1) == currentPosition) {

										// Make the noise given the
										// instrumentation information
										noise = getNoiseType(singlePoint
												.getType(), singlePoint
												.getLow(), singlePoint
												.getHigh());

										// Add the noise to the line
										modLine = modLine + noise;

										// Change the character modifier to
										// reflect the added noise
										lastCharMod = charMod + noise.length()
												- 1;
									}

									// If there are more characters add the
									// current character to the mod line
									if (charIter.next() != CharacterIterator.DONE) {
										modLine = modLine + charIter.current();
									}
								}

								// Update the character modifier
								charMod = lastCharMod;
							}
						}
					}

					// If the modLine is not null then append it to the
					// fileContents
					if (modLine != null) {

						// Add modLine to the buffer
						buffer = buffer + modLine + "\n";

						// If the buffer's length is over the buffer size then
						// dump it
						if (buffer.length() > this.BUFFER_SIZE) {
							fileContents.append(buffer);
							buffer = "";
						}

						// Increase the line number, reset the modLine and
						// charMod
						lineNum++;
						modLine = null;
						charMod = 0;
					}
				}

				// Append the rest of the buffer before exiting
				fileContents.append(buffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Print the fileContents to an instrumented source
		try {
			printFile(fileContents.toString(), sourceFile.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Prints the file content to the same file path with the modification of
	 * .instr append to the original file.
	 * 
	 * @param fileContent
	 *            the file's content
	 * @param filePath
	 *            the file's path
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void printFile(String fileContent, String filePath)
			throws IOException {

		// Create the file name for the instrumented file
		String fileName = (filePath + ".instr");

		// Write the fileContent to the new file
		FileWriter fw = new FileWriter(fileName);
		fw.write(fileContent);
		fw.flush();
		fw.close();
	}

	/**
	 * Makes a noise statement given the noise type and delay ranges.
	 * 
	 * @param type
	 *            the noise type
	 * @param low
	 *            the lower bound of the sleep delay
	 * @param high
	 *            the upper bound of the sleep delay
	 * @return the noise statement
	 */
	private String getNoiseType(int type, int low, int high) {

		String noise = null;

		// Based on the type form a noise statement using type and delay ranges
		switch (type) {
		// Type 1 = sleep
		case 1:
			noise = getIfChance() + createSleep(low, high);
			break;
		// Type 2 = yield
		case 2:
			noise = getIfChance() + createYield();
			break;
		}

		// Returns the noise statement
		return noise;
	}

	/**
	 * This method will create the Thread.sleep() instrumentation statement,
	 * with the appropriate try/catch syntax.
	 * 
	 * @param low
	 *            the low bound of the sleep delay
	 * @param high
	 *            the high bound of the sleep delay
	 * @return the Thread.sleep() statement
	 */
	private String createSleep(int low, int high) {

		Random rand = new Random();

		return "try {Thread.sleep(" + (rand.nextInt(high - low) + low)
				+ ")}; catch (Exception e) {};";
	}

	/**
	 * This method will create the Thread.yield() instrumentation statement,
	 * with the appropriate try/catch syntax.
	 * 
	 * @return the Thread.yield() statement
	 */
	private String createYield() {

		return "try {Thread.yield(" + ");} catch (Exception e) {};";
	}

	/**
	 * This method will create the if statement of the chance of an
	 * instrumentation point actually executing.
	 * 
	 * @return the if statement that will decide if the instrument will occur.
	 */
	private String getIfChance() {

		Random rand = new Random();
		String randomNumber = String.valueOf((rand.nextInt(100 - 0) + 0));

		return "if(" + randomNumber + " >= " + CHANCE + ")";
	}

	/**
	 * Parses the instrumentation XML file and acquire the list of
	 * instrumentation points that are contained within it.
	 * 
	 * @param instrFile
	 *            the instrumentation XML file
	 * 
	 * @return an array list of instrumentation points
	 */
	private ArrayList<InstrumentationPoint> parseXmlFile(File instrFile) {

		// Make a data class that will hold the instrumentation points
		InstrumentationDataHandler instrPoints = new InstrumentationDataHandler();

		// Read the XML file for the instrumentation points
		try {
			instrPoints.setXmlLocation(instrFile.getAbsolutePath());
			instrPoints.readXml();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Return an array list of the instrumentation points
		return instrPoints.getInstrPoints();
	}
}