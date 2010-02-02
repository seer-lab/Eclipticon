package ca.uoit.eclipticon.data;
/***********************************************************************************************************
 ********************  ____  ____  _     _____  ____  _____  _____  ____  ____  ____  **********************
 ******************** |  __||  __|| |   |_   _||  . ||_   _||_   _||  __||    ||  | | **********************
 ******************** |  __|| |__ | |__  _| |_ |  __|  | |   _| |_ | |__ | || || || | **********************
 ******************** |____||____||____||_____||_|     |_|  |_____||____||____||_|__| **********************
 ******************** 													 **********************
 ***********************************************************************************************************
 * 																					  *
 * This class handles all of the instrumentation points								  		  *
 * 																					  *
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc										       *
 *																					  *
 **********************************************************************************************************/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import com.thoughtworks.xstream.XStream;

public class InstrumentationDataHandler {

	private ArrayList<InstrumentationPoint>	_ipaPoints		= null; //Collection of Instrumentation Points
	private String								_sXmlLocation	= null; //The XML Location (Path)
	private XStream							_xsXmlStream	= null; //The XML stream, to convert the collection to XML

	/**
	 * Add's New instrumentation point to the collection
	 * @param iId the ID of the instrumentation point
	 * @param sSource the source file the instrumentation point is located in
	 * @param iLine	 the line number the instrumentation point is in
	 * @param iChar	 the number of characters into the line the instrumentation point is located
	 * @param iType the type of instrumentation point
	 * @param iLow the low value for the instrumentation point
	 * @param iHigh the high value for the instrumentation point
	 */
	public void addInstrumentationPoint( int iId, String sSource, int iLine, int iChar, int iType, int iLow, int iHigh ) {

		// If the collections isn't initialized, do it now
		//------------------------------------------------
		if( _ipaPoints == null )
			_ipaPoints = new ArrayList<InstrumentationPoint>();

		// Create a new instrumentation point with the traits given
		//---------------------------------------------------------
		InstrumentationPoint ipNew = new InstrumentationPoint();
		ipNew.setChar( iChar );
		ipNew.setHigh( iHigh );
		ipNew.setId( iId );
		ipNew.setLine( iLine );
		ipNew.setLow( iLow );
		ipNew.setSource( sSource );
		ipNew.setType( iType );
		
		// Add it to the Collection
		//-------------------------
		_ipaPoints.add( ipNew );
	}

	/**
	 * Read the XML file and replaces the collection with that it just read
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void readXml() throws FileNotFoundException {
		
		// Stuff to ensure the XML file is readable
		//-----------------------------------------
		_xsXmlStream = new XStream();
		_xsXmlStream.aliasField( "Id", InstrumentationPoint.class, "_iId" );
		_xsXmlStream.aliasField( "Source", InstrumentationPoint.class, "_sSource" );
		_xsXmlStream.aliasField( "Line", InstrumentationPoint.class, "_iLine" );
		_xsXmlStream.aliasField( "Char", InstrumentationPoint.class, "_iChar" );
		_xsXmlStream.aliasField( "Type", InstrumentationPoint.class, "_iType" );
		_xsXmlStream.aliasField( "Low", InstrumentationPoint.class, "_iLow" );
		_xsXmlStream.aliasField( "High", InstrumentationPoint.class, "_iHigh" );
		
		// Read the XML file
		//------------------
		FileInputStream fisStream = new FileInputStream( _sXmlLocation );
		_ipaPoints = (ArrayList<InstrumentationPoint>)_xsXmlStream.fromXML( fisStream );
	}
	
	/**
	 * Sets the Path of the XML file.
	 * @param sXmlLocation the new location of the XML files
	 */
	public void setXmlLocation( String sXmlLocation ) {
		_sXmlLocation = sXmlLocation;
	}

	/**
	 * Write what's in the Collection to the XML file
	 * @throws FileNotFoundException
	 */
	public void writeXml() throws FileNotFoundException {
		
		if( ( _ipaPoints != null ) && ( !_ipaPoints.isEmpty() ) ) {
			
			// Stuff to ensure the XML file is readable
			//-----------------------------------------
			_xsXmlStream = new XStream();
			_xsXmlStream.aliasField( "Id", InstrumentationPoint.class, "_iId" );
			_xsXmlStream.aliasField( "Source", InstrumentationPoint.class, "_sSource" );
			_xsXmlStream.aliasField( "Line", InstrumentationPoint.class, "_iLine" );
			_xsXmlStream.aliasField( "Char", InstrumentationPoint.class, "_iChar" );
			_xsXmlStream.aliasField( "Type", InstrumentationPoint.class, "_iType" );
			_xsXmlStream.aliasField( "Low", InstrumentationPoint.class, "_iLow" );
			_xsXmlStream.aliasField( "High", InstrumentationPoint.class, "_iHigh" );

			// Write the XML file
			//-------------------
			FileOutputStream fosStream = new FileOutputStream( _sXmlLocation );
			_xsXmlStream.toXML( _ipaPoints, fosStream );

		}
	}

	public ArrayList<InstrumentationPoint> getInstrPoints() {
		// TODO Add a check to see if it's current version of instrumentation points
		// to avoid reparsing when not needed.
	
		return _ipaPoints;
	}
}
