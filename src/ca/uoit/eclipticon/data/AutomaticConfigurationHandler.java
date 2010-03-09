package ca.uoit.eclipticon.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.thoughtworks.xstream.XStream;

/**
 * This class is used to handle the {@link AutomaticConfiguration} data class by
 * allowing creation of the configuration data, and to read and write the data
 * to an XML file.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class AutomaticConfigurationHandler {

	private AutomaticConfiguration	_configurationData	= null; // The configuration data for automatic configuration
	private String					_xmlLocation		= null; // The XML Location (Path)
	private XStream					_xStream			= null; // The XML stream, to convert the object to and from XML

	/**
	 * Sets the configuration data for the automatic instrumentation.
	 * 
	 * @param lowDelayRange the low delay range
	 * @param highDelayRange the high delay range
	 * @param sleepProbability the sleep probability
	 * @param yieldProbability the yield probability
	 * @param instrumentProbability the instrument probability
	 * @param synchronizeProbability the synchronize probability
	 * @param barrierProbability the barrier probability
	 * @param latchProbability the latch probability
	 * @param semaphoreProbability the semaphore probability
	 */
	public void setConfigurationData( int lowDelayRange, int highDelayRange, int sleepProbability,
			int yieldProbability, int instrumentProbability, int synchronizeProbability, int barrierProbability,
			int latchProbability, int semaphoreProbability ) {

		// If the configuration data object is not initialized then do it now
		if( _configurationData == null ) {
			_configurationData = new AutomaticConfiguration();
		}

		// Sets the configuration data object with the selected values
		_configurationData.setLowDelayRange( lowDelayRange );
		_configurationData.setHighDelayRange( highDelayRange );
		_configurationData.setSleepProbability( sleepProbability );
		_configurationData.setYieldProbability( yieldProbability );
		_configurationData.setInstrumentProbability( instrumentProbability );
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

		// Stuff to ensure the XML file is readable
		_xStream = new XStream();
		_xStream.aliasField( "lowDelayRange", AutomaticConfiguration.class, "_lowDelayRange" );
		_xStream.aliasField( "highDelayRange", AutomaticConfiguration.class, "_highDelayRange" );
		_xStream.aliasField( "sleepProbability", AutomaticConfiguration.class, "_sleepProbability" );
		_xStream.aliasField( "yieldProbability", AutomaticConfiguration.class, "_yieldProbability" );
		_xStream.aliasField( "instrumentProbability", AutomaticConfiguration.class, "_instrumentProbability" );
		_xStream.aliasField( "synchronizeProbability", AutomaticConfiguration.class, "_synchronizeProbability" );
		_xStream.aliasField( "barrierProbability", AutomaticConfiguration.class, "_barrierProbability" );
		_xStream.aliasField( "latchProbability", AutomaticConfiguration.class, "_latchProbability" );
		_xStream.aliasField( "semaphoreProbability", AutomaticConfiguration.class, "_semaphoreProbability" );

		// Read the XML file
		FileInputStream inFileStream = new FileInputStream( _xmlLocation );
		_configurationData = (AutomaticConfiguration)_xStream.fromXML( inFileStream );
	}

	/**
	 * Sets the path of the XML file.
	 * 
	 * @param xmlLocation the new location of the XML files
	 */
	public void setXmlLocation( String xmlLocation ) {
		_xmlLocation = xmlLocation;
	}

	/**
	 * Write the data object to the XML file.
	 * 
	 * @throws FileNotFoundException
	 */
	public void writeXml() throws FileNotFoundException {

		if( _configurationData != null ) {

			// Stuff to ensure the XML file is readable
			_xStream = new XStream();
			_xStream.aliasField( "lowDelayRange", AutomaticConfiguration.class, "_lowDelayRange" );
			_xStream.aliasField( "highDelayRange", AutomaticConfiguration.class, "_highDelayRange" );
			_xStream.aliasField( "sleepProbability", AutomaticConfiguration.class, "_sleepProbability" );
			_xStream.aliasField( "yieldProbability", AutomaticConfiguration.class, "_yieldProbability" );
			_xStream.aliasField( "instrumentProbability", AutomaticConfiguration.class, "_instrumentProbability" );
			_xStream.aliasField( "synchronizeProbability", AutomaticConfiguration.class, "_synchronizeProbability" );
			_xStream.aliasField( "barrierProbability", AutomaticConfiguration.class, "_barrierProbability" );
			_xStream.aliasField( "latchProbability", AutomaticConfiguration.class, "_latchProbability" );
			_xStream.aliasField( "semaphoreProbability", AutomaticConfiguration.class, "_semaphoreProbability" );

			// Write the XML file
			FileOutputStream outFileStream = new FileOutputStream( _xmlLocation );
			_xStream.toXML( _configurationData, outFileStream );
		}
	}

	/**
	 * Gets the configuration data object.
	 * 
	 * @return the configuration data object
	 */
	public AutomaticConfiguration getConfiguration() {
		return _configurationData;
	}
}