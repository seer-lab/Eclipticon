package ca.uoit.eclipticon.instrumentation;

import java.io.File;

/**
 * This class is used to generate an XML file that has the instrumentation points
 * to be used in automatic instrumentation, the input is the automatic instrumentation
 * configuration XML file.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class AutoInstrumentCreator {

	/**
	 * This method will end up creating an XML file that has the automatic
	 * instrumentation points to be used.
	 * 
	 * @param automaticConfig the XML configuration with information on how to instrument
	 * @return xmlFile the XML file with all the instrumentation points
	 */
	public File makeXmlFile( File automaticConfig ) {
		// TODO Need get the automatic configuration from the xml file
		// TODO Need to use the configuration information and find the instrument points in all .java files in the project
		// TODO The parsing of the .java files can be done using regexs, just look for the keywords (doesn't matter if they are in comments)
		// TODO Keep a running list of instrumentation points which will be converted to an XMLfile
		return null;
	}
}