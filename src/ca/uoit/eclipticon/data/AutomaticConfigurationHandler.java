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
	 * @param sleepChance the sleep chance
	 * @param yieldChance the yield chance
	 * @param instrumentChance the instrument chance
	 * @param synchronizeChance the synchronize chance
	 * @param barrierChance the barrier chance
	 * @param latchChance the latch chance
	 * @param semaphoreChance the semaphore chance
	 */
	public void setConfigurationData( int lowDelayRange, int highDelayRange, int sleepChance, int yieldChance, int instrumentChance, int synchronizeChance, int barrierChance, int latchChance, int semaphoreChance ) {

		// If the configuration data object is not initialized then do it now
		if( _configurationData == null ) {
			_configurationData = new AutomaticConfiguration();
		}

		// Sets the configuration data object with the selected values
		_configurationData.setLowDelayRange( lowDelayRange );
		_configurationData.setHighDelayRange( highDelayRange );
		_configurationData.setSleepChance( sleepChance );
		_configurationData.setYieldChance( yieldChance );
		_configurationData.setInstrumentChance( instrumentChance );
		_configurationData.setSynchronizeChance( synchronizeChance );
		_configurationData.setBarrierChance( barrierChance );
		_configurationData.setLatchChance( latchChance );
		_configurationData.setSemaphoreChance( semaphoreChance );
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
		_xStream.aliasField( "sleepChance", AutomaticConfiguration.class, "_sleepChance" );
		_xStream.aliasField( "yieldChance", AutomaticConfiguration.class, "_yieldChance" );
		_xStream.aliasField( "instrumentChance", AutomaticConfiguration.class, "_instrumentChance" );
		_xStream.aliasField( "synchronizeChance", AutomaticConfiguration.class, "_synchronizeChance" );
		_xStream.aliasField( "barrierChance", AutomaticConfiguration.class, "_barrierChance" );
		_xStream.aliasField( "latchChance", AutomaticConfiguration.class, "_latchChance" );
		_xStream.aliasField( "semaphoreChance", AutomaticConfiguration.class, "_semaphoreChance" );

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
			_xStream.aliasField( "sleepChance", AutomaticConfiguration.class, "_sleepChance" );
			_xStream.aliasField( "yieldChance", AutomaticConfiguration.class, "_yieldChance" );
			_xStream.aliasField( "instrumentChance", AutomaticConfiguration.class, "_instrumentChance" );
			_xStream.aliasField( "synchronizeChance", AutomaticConfiguration.class, "_synchronizeChance" );
			_xStream.aliasField( "barrierChance", AutomaticConfiguration.class, "_barrierChance" );
			_xStream.aliasField( "latchChance", AutomaticConfiguration.class, "_latchChance" );
			_xStream.aliasField( "semaphoreChance", AutomaticConfiguration.class, "_semaphoreChance" );

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