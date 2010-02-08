package ca.uoit.eclipticon;

import java.io.File;
import java.util.Scanner;

import javax.swing.JFileChooser;

import ca.uoit.eclipticon.instrumentation.InstrumentationEngine;

/**
 * This class is the main application that allows the user to instrument using a
 * selected mode of instrumentation and defined XML files. This is mainly used
 * to provide simple headless use, and might be @depreciate or refactored eventually.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class Application {

	/**
	 * The main method that will create the instrumentation engine and will prompt
	 * the user for information on how they wish to make use of the tool.
	 * TODO Need to make the input handling more robust
	 * 
	 * @param args not used
	 */
	public static void main( String[] args ) {

		InstrumentationEngine engine = new InstrumentationEngine();

		System.out.println( "Choose one... please ensure that the value is correct or boom~" );
		System.out.println( "0 - manual" );
		System.out.println( "1 - automatic" );
		System.out.println( "2 - manual + automatic" );

		// Acquire the mode that the user wants to use
		Scanner in = new Scanner( System.in );
		int mode = in.nextInt();
		in.close();

		// Instrument using the correct mode
		if( mode == 0 ) {
			System.out.println( "Choose the manual instrumentation XML file" );
			File manualXmlFile = getXmlFile();

			engine.instrument( mode, manualXmlFile, null );
		}
		else if( mode == 1 ) {
			System.out.println( "Choose the automatic configurations XML file" );
			File automaticConfig = getXmlFile();

			engine.instrument( mode, null, automaticConfig );
		}
		else if( mode == 2 ) {
			System.out.println( "Choose the manual instrumentation XML file" );
			File manualXmlFile = getXmlFile();

			System.out.println( "Choose the automatic configurations XML file" );
			File automaticConfig = getXmlFile();

			engine.instrument( mode, manualXmlFile, automaticConfig );
		}
	}

	/**
	 * This method will prompt the user to select the XML file to use.
	 * 
	 * @return xmlFile the XML file to be used
	 */
	private static File getXmlFile() {

		// TODO Somehow acquire the appropriate XML file based on the current project

		// A file chooser to specify the directory to save the file in
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );

		int returnValue = fc.showDialog( fc, "Use XML File" );

		// Acquire the root folder
		File xmlFile = fc.getSelectedFile();

		// Handle the user's actions in the file dialog chooser
		if( returnValue == JFileChooser.CANCEL_OPTION ) {
			System.exit( 0 );
		}

		return xmlFile;
	}
}