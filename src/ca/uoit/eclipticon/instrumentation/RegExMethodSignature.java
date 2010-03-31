package ca.uoit.eclipticon.instrumentation;

import java.io.File;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class RegExMethodSignature {

	public static void main( String[] args ) {

		/*
		 * String s = "sparring with a purple porpoise";
		 * s = s.replace('p', 't');
		 * //returns "starring with a turtle tortoise"
		 * System.out.println(s);
		 * Path sampleClass = (Path)Path.fromPortableString(
		 * "C:\\Users\\Chris\\workspace\\eclipticon\\src\\ca\\uoit\\eclipticon\\test\\InstrumentationPointTest.java");
		 * String stmt =
		 * "import ca.uoit.eclipticon.instrumentation; import ca.uoit.eclipticon.tesat; import ca.uoit.eclipticon.test;"
		 * ;
		 * boolean bool = isMethodImportedInFile(sampleClass, stmt);
		 * System.out.println("Answer: " + bool);
		 */
	}

	// import path, a string of import statements
	// TODO in version 2.0: inner classes are not taken into account since we assume file name = only class
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

		// check for same package, cause we don't need imports for classes in the same package
		int packageExists = importStatementsList.indexOf( "package" );
		if( packageExists != -1 ) { // contains a package

			// grab the package statement
			String packageStatement = importStatementsList.substring( packageExists + "package ".length(),
					importStatementsList.indexOf( ';', packageExists ) );

			// is the package statement a substring of the path?
			if( filePath.indexOf( packageStatement ) != -1 ){				
				System.out.println( "Found a package that matches." );
				return true;
			}
		}
		else{ // Package was empty, therefore default to workspace
			
			// TODO Need to figure out how to handle default package
			// For now just assume that it passes
			return true;
		}

		// check imports
		boolean moreImports = true;
		while( moreImports ) {
			int importExists = importStatementsList.indexOf( "import" );
			if( importExists != -1 ) { // contains a import

				// grab the import statement
				String importStatement = importStatementsList.substring( importExists + "import ".length(),
						importStatementsList.indexOf( ';', importExists ) );

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

		// no package or imports match this file, return false
		return false;
	}
}