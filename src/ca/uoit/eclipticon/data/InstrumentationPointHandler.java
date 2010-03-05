package ca.uoit.eclipticon.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

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
 * This class is used to handle a collection of {@link InstrumentationPoint} by
 * allowing creation of instrumentation points and to read and write the collection
 * to an XML file.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class InstrumentationPointHandler {

	private ArrayList<InstrumentationPoint>	_instrumentationPoints	= null; // Collection of Instrumentation Points
	private String								_xmlLocation			= null; // The XML Location (Path)
	

	public InstrumentationPointHandler() {
		// When the plugin is running functionally this is the location our xml will be stored.
		_xmlLocation = Activator.getDefault().getStateLocation().addTrailingSeparator().toString() + "InstrumentationPoints.XML";
	}

	/**
	 * Add's a new instrumentation point to the collection.
	 * 
	 * @param id the ID of the instrumentation point
	 * @param source the source file the instrumentation point is located in
	 * @param line the line number the instrumentation point is in
	 * @param character the number of characters into the line the instrumentation point is located
	 * @param type the type of instrumentation point
	 * @param chance the chance of an instrumentation point activating
	 * @param low the low value for the instrumentation point
	 * @param high the high value for the instrumentation point
	 */
	public void addInstrumentationPoint( int id, String source, int line, int character, int type, int chance, int low, int high ) {

		// If the collections aren't initialized, do it now
		if( _instrumentationPoints == null ) {
			_instrumentationPoints = new ArrayList<InstrumentationPoint>();
		}

		// Create a new instrumentation point with the traits given
		InstrumentationPoint ipNew = new InstrumentationPoint();
		ipNew.setCharacter( character );
		ipNew.setHigh( high );
		ipNew.setId( id );
		ipNew.setLine( line );
		ipNew.setLow( low );
		ipNew.setSource( source );
		ipNew.setType( type );
		ipNew.setChance( chance );

		// Add it to the collection
		_instrumentationPoints.add( ipNew );
	}

	/**
	 * Read the XML file and replaces the collection with what it just read.
	 * 
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void readXml() throws FileNotFoundException {

		// If the collections aren't initialized, do it now
		if( _instrumentationPoints == null ) {
			_instrumentationPoints = new ArrayList<InstrumentationPoint>();
		}
		//TODO: Handle Re-Reading XML

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
			NodeList instrPointList = doc.getElementsByTagName( "InstrumentationPoint" );

			for( int s = 0; s < instrPointList.getLength(); s++ ) {

				Node currentNode = instrPointList.item( s );
				// Read in the values for the current Instrumentaion Point
				if( currentNode.getNodeType() == Node.ELEMENT_NODE ) {
					InstrumentationPoint newPoint = new InstrumentationPoint();
					Element instrPointElement = (Element)currentNode;
					newPoint.setId( Integer.valueOf( getNodeValue( instrPointElement, "Id" ) ) );
					newPoint.setSource( getNodeValue( instrPointElement, "Source" ) );
					newPoint.setLine( Integer.valueOf( getNodeValue( instrPointElement, "Line" ) ) );
					newPoint.setCharacter( Integer.valueOf( getNodeValue( instrPointElement, "Character" ) ) );
					newPoint.setType( Integer.valueOf( getNodeValue( instrPointElement, "Type" ) ) );
					newPoint.setChance( Integer.valueOf( getNodeValue( instrPointElement, "Chance" ) ) );
					newPoint.setLow( Integer.valueOf( getNodeValue( instrPointElement, "Low" ) ) );
					newPoint.setHigh( Integer.valueOf( getNodeValue( instrPointElement, "High" ) ) );
					_instrumentationPoints.add( newPoint );
				}
			}
		}
		catch( ParserConfigurationException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( SAXException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Get the value of the tag given, from within the Instrumentation Point given
	 * @param instrPointElement
	 * @param strTag
	 * @return
	 */
	private String getNodeValue( Element instrPointElement, String strTag ) {
		NodeList nodeList = instrPointElement.getElementsByTagName( strTag );
		Element idElement = (Element)nodeList.item( 0 );
		NodeList node = idElement.getChildNodes();
		return node.item( 0 ).getNodeValue();
	}

	/**
	 * Sets the Path of the XML file.
	 * 
	 * @param xmlLocation the new location of the XML files
	 */
	public void setXmlLocation( String xmlLocation ) {
		//_xmlLocation = xmlLocation;
	}

	/**
	 * Write what is in the collection to the XML file.
	 * @throws IOException 
	 */
	public void writeXml() throws IOException {
		// Check to see if there's something to write
		if( ( _instrumentationPoints != null ) && ( !_instrumentationPoints.isEmpty() ) ) {
			// Compile the XML for the Instrumentation Points
			String xml = "<list>\n";
			for( InstrumentationPoint a : _instrumentationPoints ) {
				xml = xml.concat( "<InstrumentationPoint>\n\r" );
				xml = xml.concat( createElement( "Id", a.getId() ) );
				xml = xml.concat( createElement( "Source", a.getSource() ) );
				xml = xml.concat( createElement( "Line", a.getLine() ) );
				xml = xml.concat( createElement( "Character", a.getCharacter() ) );
				xml = xml.concat( createElement( "Type", a.getType() ) );
				xml = xml.concat( createElement( "Chance", a.getChance() ) );
				xml = xml.concat( createElement( "Low", a.getLow() ) );
				xml = xml.concat( createElement( "High", a.getHigh() ) );
				xml = xml.concat( "</InstrumentationPoint>\n\r" );
			}

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
	 * Creates the XML tag and returns the String
	 * @param tag
	 * @param value
	 * @return
	 */
	private String createElement( String tag, String value ) {
		String markup = "<" + tag + ">" + value + "</" + tag + ">\n\r";
		return markup;
	}

	/**
	 * Creates the XML tag and returns the String
	 * @param tag
	 * @param value
	 * @return
	 */
	private String createElement( String tag, int value ) {
		String markup = createElement( tag, String.valueOf( value ) );
		return markup;
	}

	/**
	 * Gets the collection of instrumentation points.
	 * 
	 * @return the collection of instrumentation points
	 */
	public ArrayList<InstrumentationPoint> getInstrPoints() {

		// TODO Add a check to see if it's current version of instrumentation points to avoid reparsing when not needed.
		return _instrumentationPoints;
	}
}