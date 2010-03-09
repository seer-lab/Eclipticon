package ca.uoit.eclipticon;

import java.io.File;
import java.util.InputMismatchException;
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

		boolean running = true;

		while( running ) {
			System.out.println( "Please enter the correct integer to make your choice..." );
			System.out.println( "0 - manual instrumentation" );
			System.out.println( "1 - automatic instrumentation" );
			System.out.println( "2 - manual instrumentation + automatic instrumentation" );
			System.out.println( "3 - quit application" );

			int mode = -1;

			// Check to make sure that the input being read is a valid Integer
			boolean validInput = false;
			while( !validInput ) {
				try {
					// Set up a scanner to read in from the console
					Scanner in = new Scanner( System.in );
					mode = in.nextInt();
					in.close();

					validInput = true;
				}
				catch( InputMismatchException e ) {
					System.out.println( "An error occured with the input, try again" );
					validInput = false;
				}
			}

			// Instrument using the correct mode
			if( mode == Constants.MANUAL ) {
				System.out.println( "Choose the manual instrumentation XML file" );
				File manualXmlFile = getXmlFile();

				System.out.println( "Performing the manual instrumentation" );
				engine.instrument( mode, manualXmlFile, null );
				running = false;
			}
			else if( mode == Constants.AUTOMATIC ) {
				System.out.println( "Choose the automatic configurations XML file" );
				File automaticConfig = getXmlFile();

				System.out.println( "Performing the automatic instrumentation" );
				engine.instrument( mode, null, automaticConfig );
				running = false;
			}
			else if( mode == Constants.BOTH ) {
				System.out.println( "Choose the manual instrumentation XML file" );
				File manualXmlFile = getXmlFile();

				System.out.println( "Choose the automatic configurations XML file" );
				File automaticConfig = getXmlFile();

				System.out.println( "Performing the manual instrumentation and automatic instrumentation" );
				engine.instrument( mode, manualXmlFile, automaticConfig );
				running = false;
			}
			else if( mode == 3 ) {
				System.out.println( "Quitting applicaiton" );
				running = false;
			}
			else {
				System.out.println( "Input was not valid, try again." );
			}
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