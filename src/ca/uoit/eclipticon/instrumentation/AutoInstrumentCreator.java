package ca.uoit.eclipticon.instrumentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.AutomaticConfiguration;
import ca.uoit.eclipticon.data.AutomaticConfigurationHandler;
import ca.uoit.eclipticon.data.InstrumentationPointHandler;

/**
 * This class is used to generate an XML file that has the instrumentation points
 * to be used in automatic instrumentation, the input is the automatic instrumentation
 * configuration XML file.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class AutoInstrumentCreator {

	private int							_numberOfPoints		= 0;
	private String						_currentSourceFile	= null;
	private AutomaticConfiguration		_config				= null;
	private InstrumentationPointHandler	_automaticPoints	= new InstrumentationPointHandler();

	/**
	 * This method will end up creating an XML file that has the automatic
	 * instrumentation points to be used.
	 * 
	 * @param automaticConfig the XML configuration with information on how to instrument
	 * @return xmlFile the XML file with all the instrumentation points
	 */
	public File makeXmlFile( File automaticConfig ) {

		// Acquire the data object for the automatic configuration
		this._config = getConfigObject( automaticConfig );

		// TODO Figure out the source files we are looking at

		// Find points for instrumentation for the specified files in the project that have been tagged
		findInstrumentationPoints( "/home/jalbert/projects/eclipticon/test/test1.java" );

		return getXmlFile();
	}

	/**
	 * Gets the XML file that represents the collection of automatic
	 * instrumentation points that were just created.
	 * 
	 * @return the XML file of automatic instrumentation points
	 */
	private File getXmlFile() {

		this._automaticPoints.setXmlLocation( "/home/jalbert/projects/eclipticon/test/autoinstrpoints.xml" );

		try {
			this._automaticPoints.writeXml();
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}

		File xmlFile = new File( "/home/jalbert/projects/eclipticon/test/autoinstrpoints.xml" );

		return xmlFile;
	}

	/**
	 * This method will find all the potential synchronization constructs within
	 * the current source file, when one is found then based on the probability
	 * the construct will be instrumented or not.
	 * 
	 * @param sourceFile the source file being examined
	 */
	private void findInstrumentationPoints( String sourceFile ) {

		// Set the source file being used
		this._currentSourceFile = sourceFile;

		// Read the sourceFile and create the reading and file content objects
		FileReader fileReader = null;
		try {
			fileReader = new FileReader( sourceFile );
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}
		BufferedReader bufReader = new BufferedReader( fileReader );

		// If bufferReader is ready start parsing the sourceFile
		String curLine = "";
		int lineNum = 1;
		try {
			if( bufReader.ready() ) {

				// For as long as there are lines left to read; acquire current one
				while( ( curLine = bufReader.readLine() ) != null ) {

					// Handle appropriate synchronize construct if they reside on current line
					parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZESYNTAX );
					parseLineForConstructs( curLine, lineNum, Constants.BARRIERSYNTAX );
					parseLineForConstructs( curLine, lineNum, Constants.LATCHSYNTAX );
					parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORESYNTAX );

					lineNum++;
				}
			}
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will parse the current line and look for any appearance of
	 * the appropriate synchronization construct. If one is found it will might
	 * be added to the collection of instrumentation point, depending on the
	 * probability for this construct.
	 * 
	 * @param curLine the current line being parsed
	 * @param lineNumber the line number that is being parsed
	 * @param construct the synchronization construct that is trying to be matched
	 */
	private void parseLineForConstructs( String curLine, int lineNumber, String construct ) {

		int pos = 0;
		int currentPos = 0;
		boolean stillMore = true;

		while( stillMore ) {

			// Keep going unless no more constructs are found
			if( ( currentPos = curLine.indexOf( construct, pos ) ) != -1 ) {

				// A construct is found at currentPos
				addInstrumentationPoint( construct, lineNumber, currentPos );

				pos = currentPos + 1;
			}
			else {
				stillMore = false;
			}
		}
	}

	/**
	 * This method will add an instrumentation point for the found synchronization
	 * construct only if the automatic configuration probability for this
	 * construct allows it.
	 * 
	 * @param construct the synchronization construct that will be added
	 * @param lineNumber the line number that the construct was found on
	 * @param characterPos the character position that the construct was found at
	 */
	private void addInstrumentationPoint( String construct, int lineNumber, int characterPos ) {

		int typeOfConstruct = -1;
		int probability = 0;

		// Figure out the corresponding construct that was used
		if( construct.equals( Constants.SYNCHRONIZESYNTAX ) ) {
			typeOfConstruct = Constants.SYNCHRONIZE;
			probability = this._config.getSynchronizeProbability();
		}
		else if( construct.equals( Constants.BARRIERSYNTAX ) ) {
			typeOfConstruct = Constants.BARRIER;
			probability = this._config.getBarrierProbability();

		}
		else if( construct.equals( Constants.LATCHSYNTAX ) ) {
			typeOfConstruct = Constants.LATCH;
			probability = this._config.getLatchProbability();

		}
		else if( construct.equals( Constants.SEMAPHORESYNTAX ) ) {
			typeOfConstruct = Constants.SEMAPHORE;
			probability = this._config.getSemaphoreProbability();
		}

		Random rand = new Random();
		int randomNumber = ( rand.nextInt( 100 - 0 ) + 0 );

		// If the probability to instrument this construct passes then instrument
		if( randomNumber <= probability ) {
			// Add this instrumentation point to the collection
			this._automaticPoints.addInstrumentationPoint( this._numberOfPoints, this._currentSourceFile, lineNumber,
					characterPos, typeOfConstruct, this._config.getInstrumentProbability(), this._config
							.getLowDelayRange(), this._config.getHighDelayRange() );
		}

		// Increase the number of attempted instrumentation points
		this._numberOfPoints++;
	}

	/**
	 * Gets the automatic configuration object from the automatic configuration
	 * XML file that contains all the information.
	 * 
	 * @param automaticConfig the automatic configuration XML file
	 * @return the automatic configuration data object
	 */
	private AutomaticConfiguration getConfigObject( File automaticConfig ) {
		AutomaticConfigurationHandler configHandler = new AutomaticConfigurationHandler();

		// Read the automatic configuration file and acquire the configuration object
		try {
			configHandler.setXmlLocation( automaticConfig.getAbsolutePath() );
			configHandler.readXml();
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}

		return configHandler.getConfiguration();
	}
}