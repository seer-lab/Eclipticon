package ca.uoit.eclipticon.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Path;

import ca.uoit.eclipticon.data.SourceFile;

/**
 * This class manages the pre-parsing of the source files to establish the required
 * information to detect synchronized methods at a later parsing to find the interesting
 * points. In addition the source files will receive their corresponding package and imports.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class PreParser {

	// The array list of synchronized methods found
	static private ArrayList<SynchronizedMethods> _synchronizedMethods = new ArrayList<SynchronizedMethods>();

	/**
	 * This method will take the source files and end up finding and storing all the 
	 * synchronized method for future use during the pre-parse phase.
	 * 
	 * @param sources the array list of source files
	 * @return an array list of synchronized methods
	 */
	public void findSynchronizedMethods( ArrayList<SourceFile> sources ) {

		String contents = "";
		String methodRegex = "synchronized" + "[\\s]+?" + "[public|private|protector]+[\\s]*?" + "[\\w]+?" + "[\\s]+?"
				+ "(\\w+)\\(";
		Pattern methodPattern = Pattern.compile( methodRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL
				| Pattern.MULTILINE );
		Matcher matcher = null;

		// For each source file
		for( SourceFile source : sources ) {

			// Create this file
			BufferedReader bufReader = null;
			try {
				bufReader = new BufferedReader( new FileReader( source.getPath().toFile() ) );
			}
			catch( FileNotFoundException e ) {
				e.printStackTrace();
			}

			// Build the string up
			contents = "";
			String line = null;
			try {
				while( ( line = bufReader.readLine() ) != null ) {
					contents = contents.concat( line + "\n" );
				}
			}
			catch( IOException e ) {
				e.printStackTrace();
			}

			// Find the sync methods
			matcher = methodPattern.matcher( contents );
			while (matcher.find()){
				SynchronizedMethods synchMethod = new SynchronizedMethods( matcher.group( 1 ), source.getPath() );
				_synchronizedMethods.add( synchMethod );
			}

			// Stuff the headers
			String classRegex = "(public|private|protected|\\s)+[(\\s)+](class|interface|abstract class)[(\\s)+]([a-z][a-z0-9_])*[(\\s)+](.*?)(\\{)";
			Pattern classPattern = Pattern.compile( classRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL
					| Pattern.MULTILINE );

			matcher = classPattern.matcher( contents );

			// If a match is found store the package and import statements
			if( matcher.find() ) {
				source.clearPackageAndImports();
				source.setPackageAndImports( contents.substring( 0, matcher.start() ) );
			}
		}
	}
	
	/**
	 * Gets the arraylist of the found synchronized methods.
	 * 
	 * @return the arraylist of the synchronized methods
	 */
	public ArrayList<SynchronizedMethods> getSynchronizedMethods() {
		return _synchronizedMethods;
	}

	/**
	 * This class represents a data structure that holds the name and file
	 * path of any synchronized methods that were discovered in the pre-parse. 
	 * 
	 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
	 */
	public class SynchronizedMethods {

		private String	_name		= null; // The name of the method
		private Path	_filePath	= null; // The file path of the source file

		/**
		 * Instantiates a new synchronized method.
		 * 
		 * @param name the name of the method
		 * @param filePath the file path of the source file
		 */
		public SynchronizedMethods( String name, Path filePath ) {
			_name = name;
			_filePath = filePath;
		}
		
		/**
		 * Gets the name of this method.
		 * 
		 * @return the name
		 */
		public String getName() {
			return _name;
		}

		/**
		 * Gets the file path for this method.
		 * 
		 * @return the file path
		 */
		public Path getFilePath() {
			return _filePath;
		}
	}
}