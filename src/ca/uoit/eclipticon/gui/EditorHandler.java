package ca.uoit.eclipticon.gui;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * This class is used to allow the GUI to open files in the editor 
 * through a double mouse click action on the file displayed.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class EditorHandler {

	/**
	 * The file is opened in the editor.
	 * 
	 * @param pathOfFile the file to open in the editor
	 */
	public void openFile( Path pathOfFile ) {

		File fileToOpen = pathOfFile.toFile();

		// Check to ensure that this file actually is valid
		if( fileToOpen.exists() && fileToOpen.isFile() ) {

			IFileStore fileStore = EFS.getLocalFileSystem().getStore( fileToOpen.toURI() );
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			try {
				IDE.openEditorOnFileStore( page, fileStore );
			}
			catch( PartInitException e ) {
			}
		}
	}
}