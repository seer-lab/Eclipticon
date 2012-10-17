package ca.sqrlab.eclipticon.gui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class InstrumentationView extends ViewPart {
	private EclipticonViewer	viewer;

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

	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}