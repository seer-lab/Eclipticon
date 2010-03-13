package ca.uoit.eclipticon.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	
	/**
	 * The constructor is used to acquire the automatic configuration's XML location 
	 * from the plugin's metadata folder to restore previous settings.
	 */
	public AutomaticConfigurationHandler() {
		_xmlLocation = Activator.getDefault().getStateLocation().addTrailingSeparator().toString() + "AutomaticConfig.XML";
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
	public void setConfigurationData( int lowDelayRange, int highDelayRange, int sleepProbability,
			int yieldProbability, int synchronizeProbability, int barrierProbability,
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
		
		// Open a new file at the plugin location
		// TODO: Handle file not found.
		File file = new File( _xmlLocation );
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse( file );
			doc.getDocumentElement().normalize();

			//Get a list of Instrumentation Points
			NodeList automicaticConfigTopNode = doc.getElementsByTagName( "AutomaticConfig" );
			Node currentNode = automicaticConfigTopNode.item( 0 );
			
			// Read in the values for the current Instrumentation Point
			if( currentNode.getNodeType() == Node.ELEMENT_NODE ) {

				Element autoConfigElement = (Element)currentNode;
				_configurationData.setBarrierProbability( Integer.valueOf( getNodeValue( autoConfigElement, "barrierProbability" ) ) );
				_configurationData.setLowDelayRange( Integer.valueOf( getNodeValue( autoConfigElement, "lowDelayRange" ) ) );
				_configurationData.setHighDelayRange( Integer.valueOf( getNodeValue( autoConfigElement, "highDelayRange" ) ) );
				_configurationData.setSleepProbability( Integer.valueOf( getNodeValue( autoConfigElement, "sleepProbability" ) ) );
				_configurationData.setYieldProbability( Integer.valueOf( getNodeValue( autoConfigElement, "yieldProbability" ) ) );
				_configurationData.setSynchronizeProbability( Integer.valueOf( getNodeValue( autoConfigElement, "synchronizeChance" ) ) );
				_configurationData.setLatchProbability( Integer.valueOf( getNodeValue( autoConfigElement, "latchChance" ) ) );
				_configurationData.setSemaphoreProbability( Integer.valueOf( getNodeValue( autoConfigElement, "semaphoreChance" ) ) );
			}

		}
		catch( ParserConfigurationException e ) {
			e.printStackTrace();
		}
		catch( SAXException e ) {
			e.printStackTrace();
		}
		catch( IOException e ) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the value of the tag given, from within the Automatic Configuration given
	 * @param autoConfigElement the element within the automatic configuration
	 * @param strTag the tag for the element to be found
	 * @return a node from an element using the tag
	 */
	private String getNodeValue( Element autoConfigElement, String strTag ) {
		NodeList nodeList = autoConfigElement.getElementsByTagName( strTag );
		Element idElement = (Element)nodeList.item( 0 );
		NodeList node = idElement.getChildNodes();
		return node.item( 0 ).getNodeValue();
	}

	/**
	 * Write the data object to the XML file.
	 * 
	 * @throws FileNotFoundException
	 */
	public void writeXml() throws IOException {

		if( _configurationData != null ) {
			// Compile the XML for the Instrumentation Points
			String xml = "<list>\n";

			xml = xml.concat( "<AutomaticConfig>\n\r" );
			xml = xml.concat( createElement( "barrierChance", _configurationData.getBarrierProbability() ) );
			xml = xml.concat( createElement( "lowDelayRange", _configurationData.getLowDelayRange() ) );
			xml = xml.concat( createElement( "highDelayRange", _configurationData.getHighDelayRange() ) );
			xml = xml.concat( createElement( "sleepChance", _configurationData.getSleepProbability() ) );
			xml = xml.concat( createElement( "yieldChance", _configurationData.getYieldProbability() ) );
			xml = xml.concat( createElement( "synchronizeChance", _configurationData.getSynchronizeProbability() ) );
			xml = xml.concat( createElement( "latchChance", _configurationData.getLatchProbability() ) );
			xml = xml.concat( createElement( "semaphoreChance", _configurationData.getSemaphoreProbability() ) );
			xml = xml.concat( "</AutomaticConfig>\n\r" );

			xml = xml.concat( "</list>" );
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
		String markup = "<" + tag + ">" + value + "</" + tag + ">\n\r";
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