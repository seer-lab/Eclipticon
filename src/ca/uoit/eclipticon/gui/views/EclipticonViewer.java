package ca.uoit.eclipticon.gui.views;

import java.util.ArrayList;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.AutomaticConfiguration;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.data.InterestPoint;
import ca.uoit.eclipticon.data.SourceFile;
import ca.uoit.eclipticon.instrumentation.FileParser;

public class EclipticonViewer extends Viewer implements SelectionListener, ModifyListener, FocusListener {

	// The GUI components that need to be modified during run time

	Composite	_compositeParent	= null;
	Composite	_compositePoints	= null;
	CTabFolder	_folderTab			= null;

	Text		_txtLower			= null;
	Text		_txtHigher			= null;
	Text		_txtProb			= null;

	Combo		_cmbType			= null;
	Scale		_sleepYield			= null;
	Text		_txtAutoLower		= null;
	Text		_txtAutoHigher		= null;
	Scale		_scaleBarrier		= null;
	Scale		_scaleSync			= null;
	Scale		_scaleSemaphores	= null;
	Scale		_scaleLatches		= null;

	Tree		_tree				= null;

	// AutomaticConfigurationHandler _ach = null;

	public EclipticonViewer( Composite parent, int i ) {
		// TODO Auto-generated constructor stub
		super();
	}

	protected Control createControl( Composite compositeParent ) {

		_compositeParent = compositeParent;
		_compositePoints = new Composite( compositeParent, SWT.NULL );
		_compositePoints.setLayout( new FillLayout() );

		// Create new Tab Folder to hold Auto and Manual Tab
		_folderTab = new CTabFolder( _compositePoints, SWT.NULL );
		_folderTab.setTabPosition( SWT.TOP );

		// Create and populate the Two tabs
		createManualTab( _folderTab );
		createAutoTab( _folderTab );
		disableInfoLabels();
		fillTree();
		// Initialize the models

		// Select the Manual Tab
		_folderTab.setSelection( 0 );

		// Add listeners
		_folderTab.addSelectionListener( this );

		_cmbType.addSelectionListener( this );
		_scaleBarrier.addSelectionListener( this );
		_scaleLatches.addSelectionListener( this );
		_scaleSemaphores.addSelectionListener( this );
		_scaleSync.addSelectionListener( this );
		_sleepYield.addSelectionListener( this );

		_txtLower.addFocusListener( this );
		_txtProb.addFocusListener( this );
		_txtHigher.addFocusListener( this );
		_txtAutoLower.addFocusListener( this );
		_txtAutoHigher.addFocusListener( this );

		_txtLower.addModifyListener( this );
		_txtHigher.addModifyListener( this );
		_txtProb.addModifyListener( this );
		_txtAutoLower.addModifyListener( this );
		_txtAutoHigher.addModifyListener( this );

		return _compositePoints;
	}

	/**
	 * Create's the Manual Instrumentation Points Tab
	 * 
	 * @param tabFolder
	 * @return
	 */
	private void createManualTab( CTabFolder tabFolder ) {

		// Create the Tab and insert it into the TabFolder
		CTabItem tabManual = new CTabItem( tabFolder, SWT.NULL );
		tabManual.setText( "Manual" );

		Composite composite = new Composite( tabFolder, SWT.NULL );
		GridData gridData = new GridData( GridData.FILL_BOTH );
		composite.setLayoutData( gridData );
		GridLayout gridLayout = new GridLayout();
		composite.setLayout( gridLayout );

		_tree = new Tree( composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK | SWT.BORDER );
		TreeColumn col1 = new TreeColumn( _tree, SWT.LEFT );

		col1.setText( "Instrumentation Point" );

		TreeColumn col2 = new TreeColumn( _tree, SWT.LEFT );
		col2.setText( "Type" );

		TreeColumn col3 = new TreeColumn( _tree, SWT.LEFT );
		col3.setText( "Delay" );
		_tree.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

		_tree.setLinesVisible( true );
		_tree.setHeaderVisible( true );
		_tree.addSelectionListener( this );
		_tree.addTreeListener( new TreeListener() {

			@Override
			public void treeCollapsed( TreeEvent e ) {
				// TODO Auto-generated method stub
				Tree parent = ( (TreeItem)e.item ).getParent();
				for( int i = 0; i < parent.getColumnCount(); i++ ) {
					parent.getColumn( i ).pack();
				}
			}

			@Override
			public void treeExpanded( TreeEvent e ) {
				// TODO Auto-generated method stub
				Tree parent = ( (TreeItem)e.item ).getParent();
				for( int i = 0; i < parent.getColumnCount(); i++ ) {
					parent.getColumn( i ).pack();
				}
			}

		} );
		for( int i = 0; i < _tree.getColumnCount(); i++ ) {
			_tree.getColumn( i ).pack();
		}

		// Create a Section for the properties
		Group groupProperties = new Group( composite, SWT.NULL );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.minimumHeight = 200;
		groupProperties.setLayoutData( gridData );

		groupProperties.setText( "Properties" );
		gridLayout = new GridLayout( 2, false );
		groupProperties.setLayout( gridLayout );

		// Instrumentation Type
		Label typeLbl = new Label( groupProperties, SWT.NULL );
		gridData = new GridData();
		typeLbl.setLayoutData( gridData );
		typeLbl.setText( "Type:" );

		_cmbType = new Combo( groupProperties, SWT.NULL );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		_cmbType.setLayoutData( gridData );
		_cmbType.add( "Sleep" );
		_cmbType.add( "Yield" );
		_cmbType.select( 1 );

		// Lower Bound
		Label lowerLbl = new Label( groupProperties, SWT.NULL );
		gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
		lowerLbl.setLayoutData( gridData );
		lowerLbl.setText( "Lower Bound:" );

		_txtLower = new Text( groupProperties, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		_txtLower.setLayoutData( gridData );

		// Higher Bound
		Label higherLbl = new Label( groupProperties, SWT.NULL );
		gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
		higherLbl.setLayoutData( gridData );
		higherLbl.setText( "Higher Bound:" );

		_txtHigher = new Text( groupProperties, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		_txtHigher.setLayoutData( gridData );

		// Probability
		Label probLbl = new Label( groupProperties, SWT.NULL );
		gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
		probLbl.setLayoutData( gridData );
		probLbl.setText( "Probability of Delay (%):" );

		_txtProb = new Text( groupProperties, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		_txtProb.setLayoutData( gridData );

		// Default Button
		Button but = new Button( groupProperties, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_END );
		gridData.horizontalSpan = 3;
		but.setLayoutData( gridData );
		but.setText( "Default" );

		but.addSelectionListener( this );

		tabManual.setControl( composite );

	}

	/**
	 * Creates the Automatic Instrumentation Points Tab.
	 * 
	 * @param tabFolder
	 * @return
	 */
	private void createAutoTab( CTabFolder tabFolder ) {

		// Setup the Tab Name
		CTabItem tabAuto = new CTabItem( tabFolder, SWT.NULL );
		tabAuto.setText( "Automatic" );

		Composite composite = new Composite( tabFolder, SWT.NULL );
		GridData gridData = new GridData( GridData.FILL_BOTH );
		composite.setLayoutData( gridData );
		GridLayout gridLayout = new GridLayout();
		composite.setLayout( gridLayout );

		// Create a Settings Group
		Group groupSettings = new Group( composite, SWT.NULL );
		gridData = new GridData( GridData.FILL_BOTH );
		groupSettings.setLayoutData( gridData );
		groupSettings.setText( "Settings" );
		gridLayout = new GridLayout( 3, false );
		groupSettings.setLayout( gridLayout );

		// Slider for probability of Typ: Yield or Sleep
		Label typeLbl = new Label( groupSettings, SWT.NULL );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		typeLbl.setLayoutData( gridData );
		typeLbl.setText( "Probability of Type" );

		Label sleepLbl = new Label( groupSettings, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		sleepLbl.setLayoutData( gridData );
		sleepLbl.setText( "Sleep" );

		_sleepYield = new Scale( groupSettings, SWT.NULL );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.verticalSpan = 2;
		_sleepYield.setLayoutData( gridData );

		_sleepYield.setMinimum( 0 );
		_sleepYield.setMaximum( 100 );
		_sleepYield.setSelection( 100 );

		Label yieldLbl = new Label( groupSettings, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		yieldLbl.setLayoutData( gridData );
		yieldLbl.setText( "Yield" );

		Label sleepLbl2 = new Label( groupSettings, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		sleepLbl2.setLayoutData( gridData );
		sleepLbl2.setText( "0" );

		Label yieldLbl2 = new Label( groupSettings, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		yieldLbl2.setLayoutData( gridData );
		yieldLbl2.setText( "100" );

		// The range text boxes if it happens to be a sleep
		Composite range = new Composite( groupSettings, SWT.NULL );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 3;
		range.setLayoutData( gridData );
		gridLayout = new GridLayout( 3, false );
		gridLayout.marginWidth = 0;
		range.setLayout( gridLayout );

		Label rangeLbl = new Label( range, SWT.NULL );
		gridData = new GridData();
		rangeLbl.setLayoutData( gridData );
		rangeLbl.setText( "Delay Range(ms):" );

		_txtAutoLower = new Text( range, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		_txtAutoLower.setLayoutData( gridData );

		_txtAutoHigher = new Text( range, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		_txtAutoHigher.setLayoutData( gridData );

		// Create a group for the mechanisms
		Group groupMech = new Group( groupSettings, SWT.NULL );
		gridData = new GridData( GridData.FILL_BOTH );
		gridData.minimumHeight = 280;
		gridData.horizontalSpan = 3;
		groupMech.setLayoutData( gridData );
		groupMech.setText( "Probability of delay at specified instrumentations" );
		gridLayout = new GridLayout( 3, false );
		groupMech.setLayout( gridLayout );

		// Barrier Slider
		Label typeMech = new Label( groupMech, SWT.NULL );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		typeMech.setLayoutData( gridData );
		typeMech.setText( "Barriers" );

		Label zeroLbl = new Label( groupMech, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		zeroLbl.setLayoutData( gridData );
		zeroLbl.setText( "0" );

		_scaleBarrier = new Scale( groupMech, SWT.NULL );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		_scaleBarrier.setLayoutData( gridData );
		_scaleBarrier.setMinimum( 0 );
		_scaleBarrier.setMaximum( 100 );

		Label hundredLbl = new Label( groupMech, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		hundredLbl.setLayoutData( gridData );
		hundredLbl.setText( "100" );

		// Latch Slider
		Label typeMech2 = new Label( groupMech, SWT.NULL );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		typeMech2.setLayoutData( gridData );
		typeMech2.setText( "Latches" );

		Label zeroLbl2 = new Label( groupMech, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		zeroLbl2.setLayoutData( gridData );
		zeroLbl2.setText( "0" );
		_scaleLatches = new Scale( groupMech, SWT.NULL );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		_scaleLatches.setLayoutData( gridData );
		_scaleLatches.setMinimum( 0 );
		_scaleLatches.setMaximum( 100 );

		Label hundredLbl2 = new Label( groupMech, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		hundredLbl2.setLayoutData( gridData );
		hundredLbl2.setText( "100" );

		// Semaphore Slider
		Label typeMech3 = new Label( groupMech, SWT.NULL );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		typeMech3.setLayoutData( gridData );
		typeMech3.setText( "Semaphores" );

		Label zeroLbl3 = new Label( groupMech, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		zeroLbl3.setLayoutData( gridData );
		zeroLbl3.setText( "0" );

		_scaleSemaphores = new Scale( groupMech, SWT.NULL );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		_scaleSemaphores.setLayoutData( gridData );
		_scaleSemaphores.setMinimum( 0 );
		_scaleSemaphores.setMaximum( 100 );

		Label hundredLbl3 = new Label( groupMech, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		hundredLbl3.setLayoutData( gridData );
		hundredLbl3.setText( "100" );

		// Synchronization Slider
		Label typeMech4 = new Label( groupMech, SWT.NULL );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		typeMech4.setLayoutData( gridData );
		typeMech4.setText( "Synchronizations" );

		Label zeroLbl4 = new Label( groupMech, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		zeroLbl4.setLayoutData( gridData );
		zeroLbl4.setText( "0" );

		_scaleSync = new Scale( groupMech, SWT.NULL );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		_scaleSync.setLayoutData( gridData );
		_scaleSync.setMinimum( 0 );
		_scaleSync.setMaximum( 100 );

		Label hundredLbl4 = new Label( groupMech, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER );
		hundredLbl4.setLayoutData( gridData );
		hundredLbl4.setText( "100" );

		// Default Button
		Button but = new Button( groupSettings, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_END );
		gridData.horizontalSpan = 3;
		but.setLayoutData( gridData );
		but.setText( "Default" );

		tabAuto.setControl( composite );

	}

	/**
	 * Adjust the sliders positions to mimic the XML
	 * 
	 * @param autoConfig
	 */
	public void populateAutoTab( AutomaticConfiguration autoConfig ) {
		_sleepYield.setSelection( autoConfig.getYieldProbability() );
		_txtAutoLower.setText( String.valueOf( autoConfig.getLowDelayRange() ) );
		_txtAutoHigher.setText( String.valueOf( autoConfig.getHighDelayRange() ) );
		_scaleBarrier.setSelection( autoConfig.getBarrierProbability() );
		_scaleLatches.setSelection( autoConfig.getLatchProbability() );
		_scaleSemaphores.setSelection( autoConfig.getSemaphoreProbability() );
		_scaleSync.setSelection( autoConfig.getSynchronizeProbability() );
	}

	/**
	 * Fills the table with the instrumentation points specified in the XML file
	 * 
	 * @param instrPoints
	 */
	public void fillTree() {
		FileParser newFP = new FileParser();
		ResourcesPlugin.getWorkspace();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		Path tmpPath = (Path)root.getLocation();
		ArrayList<SourceFile> sources = newFP.getFiles( tmpPath );

		for( SourceFile sf : sources ) {
			newFP.findInterestPoints( sf );
			boolean someChecked = false;
			TreeItem item = new TreeItem( _tree, SWT.NONE );
			item.setText( sf.getName() );
			for( InterestPoint ip : sf.getInterestingPoints() ) {
				TreeItem subItem = new TreeItem( item, SWT.NONE );
				String[] strings = { "Line: " + ip.getLine(), ip.getConstruct(), "" };
				subItem.setText( strings );
				subItem.setData( ip );
				if( ip instanceof InstrumentationPoint ) {
					subItem.setChecked( true );
					item.setChecked( true );
					InstrumentationPoint tempIP = (InstrumentationPoint)ip;
					if( tempIP.getType() == Constants.SLEEP )
						subItem.setText( 2, "Sleep" );
					else if( tempIP.getType() == Constants.YEILD )
						subItem.setText( 2, "Yield" );
					someChecked = true;
				}
				else {
					if( someChecked ) {
						item.setGrayed( true );
					}
				}
			}

		}

	}

	/**
	 * Fill the information below the tables on the manual tab.
	 * 
	 * @param instrPoint
	 */
	public void fillInfoLabels( InstrumentationPoint instrPoint ) {
		// Sets the the Combo box
		// TODO: CONSTANTS
		if( instrPoint.getType() == 0 )
			_cmbType.select( _cmbType.indexOf( "Sleep" ) );
		else if( instrPoint.getType() == 1 )
			_cmbType.select( _cmbType.indexOf( "Yield" ) );
		_txtLower.setText( String.valueOf( instrPoint.getLow() ) );
		_txtHigher.setText( String.valueOf( instrPoint.getHigh() ) );
		_txtProb.setText( String.valueOf( instrPoint.getProbability() ) );
		_cmbType.setEnabled( true );
		_txtLower.setEnabled( true );
		_txtHigher.setEnabled( true );
		_txtProb.setEnabled( true );

	}

	public void disableInfoLabels() {
		_cmbType.setText( "" );
		_txtLower.setText( "" );
		_txtHigher.setText( "" );
		_txtProb.setText( "" );
		_cmbType.setEnabled( false );
		_txtLower.setEnabled( false );
		_txtHigher.setEnabled( false );
		_txtProb.setEnabled( false );
	}

	/**
	 * Refreshes the table item to reflect the model
	 * 
	 */
	public void refreshSelectedTableItem() {

	}

	/**
	 * Clears the table and repopulates from the model
	 */
	public void refreshTable() {

	}

	@Override
	public Control getControl() {
		return _compositePoints;
	}

	@Override
	public Object getInput() {

		return null;
	}

	@Override
	public ISelection getSelection() {

		return null;
	}

	@Override
	public void refresh() {

	}

	@Override
	public void setInput( Object arg0 ) {

	}

	@Override
	public void setSelection( ISelection arg0, boolean arg1 ) {

	}

	@Override
	public void widgetDefaultSelected( SelectionEvent arg0 ) {
		// TODO

	}

	@Override
	/*
	 * Takes care of the widget selections
	 */
	public void widgetSelected( SelectionEvent arg0 ) {

		// The widget selected was an item in our table
		if( arg0.item instanceof TableItem ) {
			// Grab the model and populate the info
			TableItem selectedItem = (TableItem)arg0.item;
			InstrumentationPoint selectedPoint = (InstrumentationPoint)selectedItem.getData();
			fillInfoLabels( selectedPoint );
		}

		// Selected Item was something in our Tree
		else if( arg0.item instanceof TreeItem ) {

			TreeItem selectedItem = (TreeItem)arg0.item;

			// If the selection was checking a checkbox
			if( arg0.detail == SWT.CHECK ) {

				// and the selection was at the root (File Names)
				if( selectedItem.getParentItem() == null ) {
					if( selectedItem.getGrayed() ) {
						selectedItem.setChecked( true );

					}
					// Make all it's children the same check status
					if( selectedItem.getItemCount() > 0 ) {
						for( TreeItem i : selectedItem.getItems() ) {
							i.setChecked( selectedItem.getChecked() );
						}
					}

					selectedItem.setGrayed( false );
				}
				// but if the selection was not at the root (Line numbers)
				else {

					// Get it's root item (File Name)
					TreeItem parent = selectedItem.getParentItem();

					// Check the root
					selectedItem.getParentItem().setChecked( true );
					// Start it not grayed
					parent.setGrayed( false );

					// Check to see if it was checked or unchecked
					if( selectedItem.getChecked() ) {

						// Go through it's siblings
						for( TreeItem i : parent.getItems() ) {

							// If it comes to a sibling that isn't selected the
							// root is grayed
							if( !i.getChecked() ) {
								parent.setGrayed( true );
								break;
							}
						}
					}

					// It was unchecked
					else {
						parent.setChecked( false );
						// Go through all the siblings
						for( TreeItem i : parent.getItems() ) {

							// if comes to one that is checked make the root
							// grayed
							if( i.getChecked() ) {
								parent.setChecked( true );
								parent.setGrayed( true );
								break;
							}
						}
					}
				}
			}
			else {
				if( selectedItem.getParentItem() == null ) {
					disableInfoLabels();
				}
				else {
					if( selectedItem.getData() instanceof InstrumentationPoint ) {
						fillInfoLabels( (InstrumentationPoint)selectedItem.getData() );
					}
					else
						disableInfoLabels();
				}
			}

		}
		// The Widget was a button
		else if( arg0.widget instanceof Button ) {
			fillTree();
		}

		// The Widget selected was a Combo box
		else if( arg0.widget == _cmbType ) {

		}

	}

	@Override
	/*
	 * If the text is modified in a textbox set a flag
	 */
	public void modifyText( ModifyEvent e ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusGained( FocusEvent e ) {

	}

	@Override
	public void focusLost( FocusEvent e ) {

	}

}
