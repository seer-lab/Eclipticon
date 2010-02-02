package ca.uoit.eclipticon.data;
/***********************************************************************************************************
 ********************  ____  ____  _     _____  ____  _____  _____  ____  ____  ____  **********************
 ******************** |  __||  __|| |   |_   _||  . ||_   _||_   _||  __||    ||  | | **********************
 ******************** |  __|| |__ | |__  _| |_ |  __|  | |   _| |_ | |__ | || || || | **********************
 ******************** |____||____||____||_____||_|     |_|  |_____||____||____||_|__| **********************
 ******************** 													 **********************
 ***********************************************************************************************************
 * 																					  *
 * This class will perform the XML writing of the instrumentation points.		  					  *
 * 																					  *
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc										       *
 *																					  *
 **********************************************************************************************************/

import java.io.FileNotFoundException;

public class XMLCreator {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		InstrumentationDataHandler idhHandler = new InstrumentationDataHandler();
		
		idhHandler.addInstrumentationPoint( 0, "C:/Users/Cody/Workspaces/Capstone Workspace/eclipticon/test/test1.java", 36, 4, 1, 2, 10);
		idhHandler.addInstrumentationPoint( 1, "C:/Users/Cody/Workspaces/Capstone Workspace/eclipticon/test/test1.java", 37, 5, 2, 23, 100);
		idhHandler.addInstrumentationPoint( 2, "C:/Users/Cody/Workspaces/Capstone Workspace/eclipticon/test/test2.java", 36, 4, 2, 23, 100);
		idhHandler.addInstrumentationPoint( 3, "C:/Users/Cody/Workspaces/Capstone Workspace/eclipticon/test/test2.java", 36, 25, 2, 23, 100);


		idhHandler.setXmlLocation( "C:/Users/Cody/Workspaces/Capstone Workspace/eclipticon/test/instrpoint.xml" );
		try {
			idhHandler.writeXml();
			
		}
		catch( FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
