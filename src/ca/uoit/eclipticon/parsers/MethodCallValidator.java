package ca.uoit.eclipticon.parsers;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

/**
 * The Class MethodCallValidator will end up verifying that a file's path end up
 * being represented in the package or the import statements of a file. The reasoning 
 * for this is to verify that a synchronized method call is valid by checking the
 * correct import statement (a naive approach to object checking).
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class MethodCallValidator {
	
	// Regex's to match on the package statements
	private String _packageRegex = "([package]+[\\s]+[a-z][a-z\\.\\d\\-\\_\\s]*[\\*]*[\\s]*;)";
	private Pattern _packagePattern = Pattern.compile( _packageRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
	private String _importRegex = "([import]+[\\s]+[a-z][a-z\\.\\d\\-\\_\\s]*[\\s]*;)";
	private Pattern _importPattern = Pattern.compile( _importRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
	private Matcher _matcher = null;
	
	/**
	 * Checks if the file's method call is valid by checking the import 
	 * and package statements.
	 * 
	 * @param pathFileClass the path of the current file's method call
	 * @param importsAndPackage the import and package string
	 * @return true, if the method call resides in the imported file
	 */
	public boolean isMethodImportedInFile( Path pathFileClass, String importsAndPackage ) {

		// Take the path of the method class and format the path
		String filePath = pathFileClass.toString();
		System.out.println( "path: " + filePath );
		if( filePath.indexOf( ".java" ) != -1 ) // Remove the file extension
			filePath = filePath.substring( 0, filePath.indexOf( ".java" ) );
		filePath = filePath.replace( '\\', '.' ); // Replace the windows separators with dots
		filePath = filePath.replace( '/', '.' ); // Replace the unix separators with dots

		// Acquire the workspace path
		Path workspacePath;
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			workspacePath = (Path)root.getLocation();
		}
		catch( Exception e ) {
			workspacePath = new Path("");
		}

		String workspace = workspacePath.toString();
		workspace = workspace.replace( '\\', '.' ); // Replace the windows separators with dots
		workspace = workspace.replace( '/', '.' ); // Replace the unix separators with dots

		// Remove the workspace path from path of the method class
		filePath = filePath.substring( workspace.length() );
//		if( filePath.indexOf( '.' ) == 0 ) // If there is a dot at the beginning get rid of it
//			filePath = filePath.substring( 1 );
//		if( workspace.indexOf( '.' ) == 0 ) // If there is a dot at the beginning get rid of it
//			workspace = workspace.substring( 1 );

		// If the imports and package string is null then set it an empty string
		if( importsAndPackage == null ) {
			importsAndPackage = "";
		}
		else if (importsAndPackage.isEmpty()){ // Package was empty, therefore default to workspace
			importsAndPackage = "package " + workspace + ";";
		}

		_matcher = null;

		// Match on the package statement
		_matcher = _packagePattern.matcher( importsAndPackage );
		if( _matcher.find() ) {
			
			// Acquire the package statement
			String packageStatement = _matcher.group();
			packageStatement = packageStatement.replace( "package", "" );
			packageStatement = packageStatement.replace( ";", "" );
			packageStatement = packageStatement.replaceAll( "[\\s]*" , "" );

			if (filePath.indexOf( packageStatement ) != -1){
				System.out.println( "Found a package that matches." );
				return true;
			}
		}

		// Match on the import statement
		_matcher = _importPattern.matcher( importsAndPackage );
		while( true ) {
			if( _matcher.find() ) {

				// Acquire the import statement
				String importStatement = _matcher.group();
				importStatement = importStatement.replace( "import", "" );
				importStatement = importStatement.replace( ";", "" );
				importStatement = importStatement.replaceAll( "[\\s]*" , "" );

				if (filePath.indexOf( importStatement ) != -1){
					System.out.println( "Found an import that matches." );
					return true;
				}
			}
			else{ // No import found
				return false;
			}
		}
	}
}