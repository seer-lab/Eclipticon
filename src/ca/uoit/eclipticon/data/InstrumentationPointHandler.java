package ca.uoit.eclipticon.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import com.thoughtworks.xstream.XStream;

/**
 * This class is used to handle a collection of {@link InstrumentationPoint} by
 * allowing creation of instrumentation points and to read and write the collection
 * to an XML file.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class InstrumentationPointHandler {

	private ArrayList<InstrumentationPoint>	_instrumentationPoints	= null; // Collection of Instrumentation Points
	private String							_xmlLocation			= null; // The XML Location (Path)
	private XStream							_xStream				= null; // The XML stream, to convert the collection to XML

	public InstrumentationPointHandler() {
		// When the plugin is running functionally this is the location our xml will be stored.
		//_xmlLocation = Activator.getDefault().getStateLocation().addTrailingSeparator().toString() + "InstrumentaionPoints.XML";
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

		// If the collections isn't initialized, do it now
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

		// Stuff to ensure the XML file is readable
		_xStream = new XStream();
		_xStream.aliasField( "Id", InstrumentationPoint.class, "_id" );
		_xStream.aliasField( "Source", InstrumentationPoint.class, "_source" );
		_xStream.aliasField( "Line", InstrumentationPoint.class, "_line" );
		_xStream.aliasField( "Character", InstrumentationPoint.class, "_character" );
		_xStream.aliasField( "Type", InstrumentationPoint.class, "_type" );
		_xStream.aliasField( "Chance", InstrumentationPoint.class, "_chance" );
		_xStream.aliasField( "Low", InstrumentationPoint.class, "_low" );
		_xStream.aliasField( "High", InstrumentationPoint.class, "_high" );

		// Read the XML file
		FileInputStream inFileStream = new FileInputStream( _xmlLocation );
		_instrumentationPoints = (ArrayList<InstrumentationPoint>)_xStream.fromXML( inFileStream );
	}

	/**
	 * Sets the Path of the XML file.
	 * 
	 * @param xmlLocation the new location of the XML files
	 */
	public void setXmlLocation( String xmlLocation ) {
		_xmlLocation = xmlLocation;
	}

	/**
	 * Write what is in the collection to the XML file.
	 * 
	 * @throws FileNotFoundException
	 */
	public void writeXml() throws FileNotFoundException {

		if( ( _instrumentationPoints != null ) && ( !_instrumentationPoints.isEmpty() ) ) {

			// Stuff to ensure the XML file is readable
			_xStream = new XStream();
			_xStream.aliasField( "Id", InstrumentationPoint.class, "_id" );
			_xStream.aliasField( "Source", InstrumentationPoint.class, "_source" );
			_xStream.aliasField( "Line", InstrumentationPoint.class, "_line" );
			_xStream.aliasField( "Character", InstrumentationPoint.class, "_character" );
			_xStream.aliasField( "Type", InstrumentationPoint.class, "_type" );
			_xStream.aliasField( "Chance", InstrumentationPoint.class, "_chance" );
			_xStream.aliasField( "Low", InstrumentationPoint.class, "_low" );
			_xStream.aliasField( "High", InstrumentationPoint.class, "_high" );

			// Write the XML file
			FileOutputStream outFileStream = new FileOutputStream( _xmlLocation );
			_xStream.toXML( _instrumentationPoints, outFileStream );
		}
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