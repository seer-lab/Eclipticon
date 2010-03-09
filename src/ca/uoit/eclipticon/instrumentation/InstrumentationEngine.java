package ca.uoit.eclipticon.instrumentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.InstrumentationPointHandler;
import ca.uoit.eclipticon.data.InstrumentationPoint;

/**
 * This class will handle all the instrumentation requests, by using the automatic
 * configuration XML file and the manual instrumentation XML file.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class InstrumentationEngine {

	private Instrumentor			instrumentor			= null; // Instrumentor to make changes to the files
	private AutoInstrumentCreator	autoInstrumentCreator	= null; // Auto instrument creator which makes an XML

	/**
	 * Instantiates a new instrumentation engine which sets up the instrumentor
	 * and the auto instrument creator to make an XML file of automatic
	 * instrumentation points.
	 */
	public InstrumentationEngine() {
		instrumentor = new Instrumentor();
		autoInstrumentCreator = new AutoInstrumentCreator();
	}

	/**
	 * Is used to service a request to instrument a project either through
	 * manual, automatic or both-types of instrumentation.
	 * 
	 * @param mode manual = 0, auto = 1, both = 2
	 * @param manualXmlFile the XML file that has the manual instrumentation points
	 * @param automaticConfig the XML file that has the automatic configurations
	 */
	public void instrument( int mode, File manualXmlFile, File automaticConfig ) {

		// A set that will store the files that have been instrumented
		Set<String> usedSources = new HashSet<String>();

		// Acquire the instrumentation points data for the appropriate mode of instrumentation
		ArrayList<InstrumentationPoint> instrPoints = null;
		if( mode == Constants.MANUAL ) {
			instrPoints = parseXmlFiles( manualXmlFile, null );
		}
		else if( mode == Constants.AUTOMATIC ) {
			instrPoints = parseXmlFiles( null, autoInstrumentCreator.makeXmlFile( automaticConfig ) );
		}
		else if( mode == Constants.BOTH ) {
			instrPoints = parseXmlFiles( manualXmlFile, autoInstrumentCreator.makeXmlFile( automaticConfig ) );
		}

		// Loop through each of the source file
		for( InstrumentationPoint singlePoint : instrPoints ) {

			// If this is a new source file
			if( !usedSources.contains( singlePoint.getSource() ) ) {

				// Make a file object of the source file being used
				File sourceFile = new File( singlePoint.getSource() );

				// Instrument the source file using the instrumentation points collection
				instrumentor.instrument( sourceFile, instrPoints );

				// Add this source file to the set of already used sources
				usedSources.add( singlePoint.getSource() );
			}
		}
	}

	/**
	 * Parses the XML files and acquires the instrumentation points for it, if
	 * there is a null passed in the place of an XML file then it is not considered.
	 * 
	 * @param manualXmlFile the manual instrumentation points XML file
	 * @param automaticXmlFile the automatic instrumentation points XML file
	 * @return an arraylist of instrumentation points
	 */
	private ArrayList<InstrumentationPoint> parseXmlFiles( File manualXmlFile, File automaticXmlFile ) {

		ArrayList<InstrumentationPoint> allInstrPoints = null;

		// Make a data class that will hold the instrumentation points
		InstrumentationPointHandler instrPoints = new InstrumentationPointHandler();

		// Read the XML file for the instrumentation points
		try {
			// Acquire the manual instrumentation points if XML file is present
			if( manualXmlFile != null ) {
				instrPoints.setXmlLocation( manualXmlFile.getAbsolutePath() );
				instrPoints.readXml();
				allInstrPoints = instrPoints.getInstrPoints();
			}

			// Acquire the automatic instrumentation points if XML file is present
			if( automaticXmlFile != null ) {
				instrPoints.setXmlLocation( automaticXmlFile.getAbsolutePath() );
				instrPoints.readXml();

				// If the allInstrPoints does not exist then make it, otherwise add to it
				if( allInstrPoints == null ) {
					allInstrPoints = instrPoints.getInstrPoints();
				}
				else {
					// Add only non-duplicates to the arraylist of all instrumentation points
					addAutoInstrPoints( allInstrPoints, instrPoints.getInstrPoints() );
				}
			}
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}

		/*
		 * TODO Sort the instrumentation points to ease the instrumentation process,
		 * 	might not be needed if points are added in order. 
		 */

		return allInstrPoints;
	}

	/**
	 * This method will add the auto instrumentation points to the all instrumentation
	 * points collection, without adding duplicated points to the collection.
	 * 
	 * @param allInstrPoints the all instrumentation points collection
	 * @param autoInstrPoints the auto instrumentation points collection
	 * @return an arraylist of all the instrumentation points
	 */
	private ArrayList<InstrumentationPoint> addAutoInstrPoints( ArrayList<InstrumentationPoint> allInstrPoints,
			ArrayList<InstrumentationPoint> autoInstrPoints ) {

		// An arraylist to keep track of the items to add to add from the autoInstrPoints collection
		ArrayList<Integer> itemsToAdd = new ArrayList<Integer>();

		boolean ignorePoint = false; // A flag to indicate whether to ignore the instrumentation point

		// Loop through the autoInstrPoints collection
		for( int i = 0; i < autoInstrPoints.size(); i++ ) {

			// Loop through the allInstrPoints collection
			for( int j = 0; j < allInstrPoints.size(); j++ ) {

				// If there is a match between both instrumentation points then flag to ignore it
				if( !autoInstrPoints.get( i ).equals( allInstrPoints.get( j ) ) ) {
					ignorePoint = true;
				}
			}

			// If flag is toggled, then ignore this point, otherwise add it
			if( !ignorePoint ) {
				itemsToAdd.add( i );
			}

			ignorePoint = false;
		}

		// Now go through the itemsToAdd collection and add them to the allInstrPoints collection
		for( int i = 0; i < itemsToAdd.size(); i++ ) {
			allInstrPoints.add( autoInstrPoints.get( itemsToAdd.get( i ) ) );
		}

		return allInstrPoints;
	}
}