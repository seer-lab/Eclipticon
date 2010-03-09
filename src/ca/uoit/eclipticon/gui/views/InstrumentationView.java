package ca.uoit.eclipticon.gui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

public class InstrumentationView extends ViewPart {
	private EclipticonViewer	viewer;
	private Action				action1;
	private Action				action2;
	@SuppressWarnings("unused")
	private Action				doubleClickAction;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged( Viewer v, Object oldInput, Object newInput ) {
		}

		public void dispose() {
		}

		public Object[] getElements( Object parent ) {
			return new String[] { "One", "Two", "Three" };
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText( Object obj, int index ) {
			return getText( obj );
		}

		public Image getColumnImage( Object obj, int index ) {
			return getImage( obj );
		}

		public Image getImage( Object obj ) {
			return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_ELEMENT );
		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public InstrumentationView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl( Composite parent ) {

		viewer = new EclipticonViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );

		viewer.createControl( parent );

		makeActions();
		hookContextMenu();

		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager( "#PopupMenu" );
		menuMgr.setRemoveAllWhenShown( true );
		menuMgr.addMenuListener( new IMenuListener() {
			public void menuAboutToShow( IMenuManager manager ) {
				InstrumentationView.this.fillContextMenu( manager );
			}
		} );
		Menu menu = menuMgr.createContextMenu( viewer.getControl() );
		viewer.getControl().setMenu( menu );
		getSite().registerContextMenu( menuMgr, viewer );
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown( bars.getMenuManager() );
		fillLocalToolBar( bars.getToolBarManager() );
	}

	private void fillLocalPullDown( IMenuManager manager ) {
		manager.add( action1 );
		manager.add( new Separator() );
		manager.add( action2 );
	}

	private void fillContextMenu( IMenuManager manager ) {
		manager.add( action1 );
		manager.add( action2 );
		// Other plug-ins can contribute there actions here
		manager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
	}

	private void fillLocalToolBar( IToolBarManager manager ) {
		manager.add( action1 );
		manager.add( action2 );
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage( "Action 1 executed" );
			}
		};
		action1.setText( "Action 1" );
		action1.setToolTipText( "Action 1 tooltip" );
		action1.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_INFO_TSK ) );

		action2 = new Action() {
			public void run() {
				showMessage( "Action 2 executed" );
			}
		};
		action2.setText( "Action 2" );
		action2.setToolTipText( "Action 2 tooltip" );
		action2.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_INFO_TSK ) );
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ( (IStructuredSelection)selection ).getFirstElement();
				showMessage( "Double-click detected on " + obj.toString() );
			}
		};
	}

	private void showMessage( String message ) {
		MessageDialog.openInformation( viewer.getControl().getShell(), "Instrumentation Points", message );
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}