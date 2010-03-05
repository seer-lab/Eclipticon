package ca.uoit.eclipticon.data;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class is used to create XML files for testing purposes, this class will
 * eventually be @deprecated when the plugin is implemented.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class XMLCreator {

	/**
	 * This method will create an automatic configuration XML file and an
	 * instrumentation points XML file all filled with dummy data. The instrument
	 * points are tied to the test1.java and test2.java file.
	 * 
	 * @param args not used
	 */
	public static void main( String[] args ) {

		// Creates an automatic configuration XML file
		AutomaticConfigurationHandler autoHandler = new AutomaticConfigurationHandler();

		autoHandler.setConfigurationData( 0, 150, 25, 75, 65, 70, 45, 60, 10 );

		// Creates an instrumentation XML file
		InstrumentationPointHandler idhHandler = new InstrumentationPointHandler();

		idhHandler.addInstrumentationPoint( 0, "/home/jalbert/projects/eclipticon/test/test1.java", 36, 4, 0, 50, 2, 10 );
		idhHandler.addInstrumentationPoint( 1, "/home/jalbert/projects/eclipticon/test/test1.java", 37, 5, 1, 50, 23, 100 );
		idhHandler.addInstrumentationPoint( 2, "/home/jalbert/projects/eclipticon/test/test2.java", 36, 4, 1, 50, 23, 100 );
		idhHandler.addInstrumentationPoint( 3, "/home/jalbert/projects/eclipticon/test/test2.java", 36, 25, 1, 50, 23, 100 );
		idhHandler.addInstrumentationPoint( 4, "/home/jalbert/projects/eclipticon/test/test1.java", 36, 4, 0, 50, 2, 10 );
		idhHandler.addInstrumentationPoint( 5, "/home/jalbert/projects/eclipticon/test/test1.java", 36, 4, 1, 50, 2, 10 );
		idhHandler.addInstrumentationPoint( 6, "/home/jalbert/projects/eclipticon/test/test1.java", 36, 4, 0, 50, 2, 10 );
		idhHandler.addInstrumentationPoint( 7, "/home/jalbert/projects/eclipticon/test/test1.java", 36, 4, 0, 50, 2, 10 );
		
		idhHandler.setXmlLocation( "/home/jalbert/projects/eclipticon/test/instrpoint.xml" );

		// Write the XML files
		try {
			idhHandler.writeXml();
			autoHandler.writeXml();
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}
		catch( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}