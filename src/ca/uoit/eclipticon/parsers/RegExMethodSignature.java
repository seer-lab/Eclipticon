package ca.uoit.eclipticon.parsers;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

/**
 * The Class RegExMethodSignature will end up verifying that a file's path end up
 * being represented in the package or the import statements of a file. The reasoning 
 * for this is to verify that a synchronized method call is valid by checking the
 * correct import statement (a naive approach to object checking).
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class RegExMethodSignature {

	/**
	 * Checks if the file's method call is valid by checking the import 
	 * and package statements.
	 * 
	 * @param pathFileClass the path of the current file's method call
	 * @param importStatementsList the import and package string
	 * @return true, if the method call resides in the imported file
	 */
	public boolean isMethodImportedInFile( Path pathFileClass, String importStatementsList ) {

		// format the path
		String filePath = pathFileClass.toString();
		System.out.println( "path: " + filePath );
		filePath = filePath.replace( File.separatorChar, '.' ); // covert pathClass to have no periods!!
		if( filePath.indexOf( ".java" ) != -1 ) // remove the .java
			filePath = filePath.substring( 0, filePath.indexOf( ".java" ) );

		// get the workspace path
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Path workspacePath = (Path)root.getLocation();
		String workspace = workspacePath.toString();
		workspace = workspace.replace( File.separatorChar, '.' );

		// remove workspace path from path class
		filePath = filePath.substring( workspace.length() );
		if( filePath.indexOf( '.' ) == 0 ) // check if there is a period at the beginning of the string and get rid of
			// it.
			filePath = filePath.substring( 1 );

		if( importStatementsList == null ) {
			importStatementsList = "";
		}

		// Regex's to match on the package statements
		String packageRegex = "(package)(\\s+)((?:[a-z][a-z\\.\\d\\-]+)\\.(?:[a-z][a-z\\-]+))([\\.]*)([\\*]*)([\\s]*)(;)";
		Pattern packagePattern = Pattern.compile( packageRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher matcher = null;

		// Match on the import statement
		matcher = packagePattern.matcher( importStatementsList );
		if( matcher.find() ) {

			int packageExists = importStatementsList.indexOf( "package" );
			if( packageExists != -1 ) { // contains a package

				// grab the package statement
				String packageStatement = importStatementsList.substring( packageExists + "package ".length(),
						importStatementsList.indexOf( ';', packageExists ) );

				// is the package statement a substring of the path?
				if( filePath.indexOf( packageStatement ) != -1 ) {
					System.out.println( "Found a package that matches." );
					return true;
				}
			}
			else { // Package was empty, therefore default to workspace

				// TODO Need to figure out how to handle default package
				// For now just assume that it passes
				return true;
			}
		}

		// Regex's to match on the import statements
		String importRegex = "(import)(\\s+)((?:[a-z][a-z\\.\\d\\-]+)\\.(?:[a-z][a-z\\-]+))([\\.]*)([\\*]*)([\\s]*)(;)";
		Pattern importPattern = Pattern.compile( importRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher matcher1 = null;

		// Match on the import statement
		matcher1 = importPattern.matcher( importStatementsList );
		if( matcher1.find() ) {

			// Match was found now find the correct import
			boolean moreImports = true;
			while( moreImports ) {
				int importExists = importStatementsList.indexOf( "import" );
				if( importExists != -1 ) { // contains a import

					// grab the import statement
					String importStatement;
					try {
						importStatement = importStatementsList.substring( importExists + "import ".length(),
								importStatementsList.indexOf( ';', importExists ) );
					}
					catch( Exception e ) {
						// TODO Seems to be an index out of bound error here when there is not a proper formated import
						// (ie: comments)
						return true;
					}

					// is the import statement a substring of the path?
					if( filePath.indexOf( importStatement ) != -1 ) {
						System.out.println( "Found a import that matches." );
						moreImports = false;
						return true;
					}
					else {
						// delete the import statement from the import statements list, and loop again
						importStatementsList = importStatementsList.substring( importStatementsList.indexOf( ';' ) + 1 );
						// remove whitespace at the beginning of the line
					}
				}
				else
					moreImports = false;
			}
		}
		// no package or imports match this file, return false
		return false;
	}
}