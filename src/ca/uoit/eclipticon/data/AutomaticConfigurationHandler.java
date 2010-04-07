package ca.uoit.eclipticon.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import ca.uoit.eclipticon.gui.Activator;

/**
 * This class is used to handle the {@link AutomaticConfiguration} data class by
 * allowing creation of the configuration data, and to read and write the data
 * to an XML file.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class AutomaticConfigurationHandler {

	private AutomaticConfiguration	_configurationData	= null; // The configuration data for automatic configuration
	private String					_xmlLocation		= null; // The path of the XML file
	private int						_position			= 0;
	private String					_input				= "";

	/**
	 * The constructor is used to acquire the automatic configuration's XML location 
	 * from the plugin's metadata folder to restore previous settings.
	 */
	public AutomaticConfigurationHandler() {
		_xmlLocation = Activator.getDefault().getStateLocation().addTrailingSeparator().toString() + "AutomaticConfig.txt";
	}

	/**
	 * Sets the configuration data for the automatic instrumentation.
	 * 
	 * @param lowDelayRange the low delay range
	 * @param highDelayRange the high delay range
	 * @param sleepProbability the sleep probability
	 * @param yieldProbability the yield probability
	 * @param synchronizeProbability the synchronize probability
	 * @param barrierProbability the barrier probability
	 * @param latchProbability the latch probability
	 * @param semaphoreProbability the semaphore probability
	 */
	public void setConfigurationData( int lowDelayRange, int highDelayRange, int sleepProbability, int yieldProbability, int synchronizeProbability, int barrierProbability, int latchProbability, int semaphoreProbability ) {

		// If the configuration data object is not initialized then do it now
		if( _configurationData == null ) {
			_configurationData = new AutomaticConfiguration();
		}

		// Sets the configuration data object with the selected values
		_configurationData.setLowDelayRange( lowDelayRange );
		_configurationData.setHighDelayRange( highDelayRange );
		_configurationData.setSleepProbability( sleepProbability );
		_configurationData.setYieldProbability( yieldProbability );
		_configurationData.setSynchronizeProbability( synchronizeProbability );
		_configurationData.setBarrierProbability( barrierProbability );
		_configurationData.setLatchProbability( latchProbability );
		_configurationData.setSemaphoreProbability( semaphoreProbability );
	}

	/**
	 * Read the XML file and replaces the data object with what it just read.
	 * 
	 * @throws FileNotFoundException
	 */
	public void readXml() throws FileNotFoundException {
		// If the configuration data object is not initialized then do it now
		if( _configurationData == null ) {
			_configurationData = new AutomaticConfiguration();
		}
		_position = 0;
		// Open a new file at the plugin location
		// TODO: Handle file not found.
		try {
			File file = new File( _xmlLocation );
			BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
			_input = bufferedReader.readLine();
		}
		catch( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Get a list of Instrumentation Points

		// Read in the values for the current Instrumentation Point
		if( _input.length() > 0 ) {
			_configurationData.setBarrierProbability( Integer.valueOf( getNextValue() ) );
			_configurationData.setLowDelayRange( Integer.valueOf( getNextValue() ) );
			_configurationData.setHighDelayRange( Integer.valueOf( getNextValue() ) );
			_configurationData.setSleepProbability( Integer.valueOf( getNextValue() ) );
			_configurationData.setYieldProbability( Integer.valueOf( getNextValue() ) );
			_configurationData.setSynchronizeProbability( Integer.valueOf( getNextValue() ) );
			_configurationData.setLatchProbability( Integer.valueOf( getNextValue() ) );
			_configurationData.setSemaphoreProbability( Integer.valueOf( getNextValue() ) );
		}

	}

	/**
	 * Get the value of the tag given, from within the Automatic Configuration given
	 * @param autoConfigElement the element within the automatic configuration
	 * @param strTag the tag for the element to be found
	 * @return a node from an element using the tag
	 */
	private String getNextValue() {
		String val = "";
		if( _position < _input.length() ) {
			int i = _input.indexOf( ",", _position );
			val = _input.substring( _position, i );
			_position = i + 1;
		}

		return val;
	}

	/**
	 * Write the data object to the XML file.
	 * 
	 * @throws FileNotFoundException
	 */
	public void writeXml() throws IOException {

		if( _configurationData != null ) {
			// Compile the XML for the Instrumentation Points
			String xml = "";
			xml = xml.concat( createElement( "barrierChance", _configurationData.getBarrierProbability() ) );
			xml = xml.concat( createElement( "lowDelayRange", _configurationData.getLowDelayRange() ) );
			xml = xml.concat( createElement( "highDelayRange", _configurationData.getHighDelayRange() ) );
			xml = xml.concat( createElement( "sleepChance", _configurationData.getSleepProbability() ) );
			xml = xml.concat( createElement( "yieldChance", _configurationData.getYieldProbability() ) );
			xml = xml.concat( createElement( "synchronizeChance", _configurationData.getSynchronizeProbability() ) );
			xml = xml.concat( createElement( "latchChance", _configurationData.getLatchProbability() ) );
			xml = xml.concat( createElement( "semaphoreChance", _configurationData.getSemaphoreProbability() ) );

			File newFile = new File( _xmlLocation );

			// Create a new file if it doesn't exist
			newFile.createNewFile();

			Writer output = new BufferedWriter( new FileWriter( _xmlLocation ) );
			try {
				// Write to the file
				output.write( xml );
			}
			finally {
				output.close();
			}
		}
	}

	/**
	 * Creates an XML element given the tag and string value.
	 * @param tag the tag to be used
	 * @param value the value to be used
	 * @return the XML element
	 */
	private String createElement( String tag, String value ) {
		String markup = value + ",";
		return markup;
	}

	/**
	 * Creates an XML element given the tag and int value.
	 * @param tag the tag to be used
	 * @param value the value to be used
	 * @return the XML element
	 */
	private String createElement( String tag, int value ) {
		String markup = createElement( tag, String.valueOf( value ) );
		return markup;
	}

	/**
	 * Gets the automatic configuration data object.
	 * 
	 * @return the automatic configuration data object
	 */
	public AutomaticConfiguration getConfiguration() {
		return _configurationData;
	}
}