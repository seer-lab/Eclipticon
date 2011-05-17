package ca.uoit.eclipticon.gui.views;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.AutomaticConfiguration;
import ca.uoit.eclipticon.data.AutomaticConfigurationHandler;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.data.InterestPoint;
import ca.uoit.eclipticon.data.SourceFile;
import ca.uoit.eclipticon.gui.EditorHandler;
import ca.uoit.eclipticon.instrumentation.Instrumentor;
import ca.uoit.eclipticon.parsers.FileParser;
import ca.uoit.eclipticon.parsers.PreParser;
import ca.uoit.eclipticon.util.TreeCursor;

public class EclipticonViewer extends Viewer implements SelectionListener, ModifyListener, FocusListener {

	// The GUI components that need to be modified during run time

	Composite						_compositeParent	= null;
	Composite						_compositePoints	= null;
	CTabFolder						_folderTab			= null;

	Text							_txtLower			= null;
	Text							_txtHigher			= null;
	Text							_txtProb			= null;

	Button							_manualButton		= null;
	Button							_autoButton			= null;

	Combo							_cmbType			= null;
	Scale							_sleepYield			= null;
	Text							_txtAutoLower		= null;
	Text							_txtAutoHigher		= null;
	Scale							_scaleBarrier		= null;
	Scale							_scaleSync			= null;
	Scale							_scaleSemaphores	= null;
	Scale							_scaleLatches		= null;
	Boolean							_test				= false;
	Tree							_treeManual			= null;
	Tree							_treeAuto			= null;
	Boolean							_modified			= false;
	AutomaticConfigurationHandler	_ach				= null;
	FileParser						_newFP				= null;
	Path							_workspacePath		= null;

	public EclipticonViewer( Composite parent, int i ) {
		super();
		_newFP = new FileParser();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		_workspacePath = (Path)root.getLocation();
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
		disableManualInfoLabels();
		fillTree();
		// Initialize the models

		_ach = new AutomaticConfigurationHandler();

		try {
			// Read in their XML's
			_ach.readXml();
			
			// Populate the tree and the Auto Tab
			populateAutoTab( _ach.getConfiguration() );

			// Initialize the trees selection to the first item and fill the info at the bottom

		}
		catch( FileNotFoundException e ) {
			
			e.printStackTrace();
		}

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

		checkButtons();
		
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

		_treeManual = new Tree( composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK | SWT.BORDER );
		TreeColumn col1 = new TreeColumn( _treeManual, SWT.LEFT );

		col1.setText( "Instrumentation Point" );

		TreeColumn col2 = new TreeColumn( _treeManual, SWT.LEFT );
		col2.setText( "Type" );
		col2.setWidth( 100 );
		
		TreeColumn col3 = new TreeColumn( _treeManual, SWT.LEFT );
		col3.setText( "Syntax" );
		col3.setWidth( 100 );

		TreeColumn col4 = new TreeColumn( _treeManual, SWT.LEFT );
		col4.setText( "Noise" );
		col4.setWidth( 60 );
		_treeManual.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

		_treeManual.setLinesVisible( true );
		_treeManual.setHeaderVisible( true );
		_treeManual.addSelectionListener( this );
		_treeManual.addTreeListener( new TreeListener() {

			@Override
			public void treeCollapsed( TreeEvent e ) {
		
				_treeManual.getColumn( 0 ).pack();
			}

			@Override
			public void treeExpanded( TreeEvent e ) {
				
				_treeManual.getColumn( 0 ).pack();
			}

		} );
		_treeManual.getColumn( 0 ).pack();

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
		lowerLbl.setText( "Lower Bound (ms):" );

		_txtLower = new Text( groupProperties, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		_txtLower.setLayoutData( gridData );

		// Higher Bound
		Label higherLbl = new Label( groupProperties, SWT.NULL );
		gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
		higherLbl.setLayoutData( gridData );
		higherLbl.setText( "Higher Bound (ms):" );

		_txtHigher = new Text( groupProperties, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		_txtHigher.setLayoutData( gridData );

		// Probability
		Label probLbl = new Label( groupProperties, SWT.NULL );
		gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
		probLbl.setLayoutData( gridData );
		probLbl.setText( "Probability of Delay Activating (%):" );

		_txtProb = new Text( groupProperties, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		_txtProb.setLayoutData( gridData );

		// Default Button
		_manualButton = new Button( groupProperties, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_END );
		gridData.horizontalSpan = 3;

		_manualButton.setLayoutData( gridData );
		if( _newFP.checkIfBackupExists( _workspacePath ) )
			_manualButton.setText( "Revert Files" );
		else
			_manualButton.setText( "Instrument Files" );

		_manualButton.addSelectionListener( this );

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
		rangeLbl.setText( "Delay Range (ms):" );

		_txtAutoLower = new Text( range, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		_txtAutoLower.setLayoutData( gridData );

		_txtAutoHigher = new Text( range, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		_txtAutoHigher.setLayoutData( gridData );

		// Create a group for the mechanisms
		Group groupMech = new Group( groupSettings, SWT.NULL );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
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

		_treeAuto = new Tree( groupSettings, SWT.MULTI | SWT.FULL_SELECTION | SWT.CHECK | SWT.BORDER );
		gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
		gridData.horizontalSpan = 3;
		_treeAuto.setLayoutData( gridData );

		TreeColumn col1 = new TreeColumn( _treeAuto, SWT.LEFT );
		TreeColumn col2 = new TreeColumn( _treeAuto, SWT.LEFT );
		TreeColumn col3 = new TreeColumn( _treeAuto, SWT.LEFT );
		
		col1.setText( "Files to be Instrumented" );
		col1.setWidth(300 );
		
		col2.setText( "Lower Bound" );
		col2.setWidth(100 );
		
		col3.setText( "Upper Bound" );
		col3.setWidth( 100 );
		
		_treeAuto.setLinesVisible( true );
		_treeAuto.setHeaderVisible( true );
		_treeAuto.addSelectionListener( this );
		
		// create a TreeCursor to navigate around the tree
 		final TreeCursor cursor = new TreeCursor(_treeAuto, SWT.NONE);
 		// create an editor to edit the cell when the user hits "ENTER" 
 		// while over a cell in the tree
 		final ControlEditor editor = new ControlEditor(cursor);
 		editor.grabHorizontal = true;
 		editor.grabVertical = true;
 
 		// Activate the 
 		cursor.addSelectionListener(new SelectionAdapter() {
 			// when the TreeEditor is over a cell, select the corresponding row in 
 			// the tree
 			public void widgetSelected(SelectionEvent e) {
 				int column = cursor.getColumn();
 				if (column > 0){
 					cursor.setVisible(true);
 					_treeAuto.setSelection(new TreeItem[] {cursor.getRow()});
 				}
 				else{
 					cursor.setVisible(false);
 				}
 			}
 			// when the user hits "ENTER" in the TreeCursor, pop up a text editor so that 
 			// they can change the text of the cell
 			public void widgetDefaultSelected(SelectionEvent e){
 				int column = cursor.getColumn();
 				
 				if (column > 0){
	 				final Text text = new Text(cursor, SWT.NONE);
	 				TreeItem row = cursor.getRow();
	 				
	 				text.setText(row.getText(column));
	 				text.addKeyListener(new KeyAdapter() {
	 					public void keyPressed(KeyEvent e) {
	 						// close the text editor and copy the data over 
	 						// when the user hits "ENTER"
	 						if (e.character == SWT.CR) {
	 							TreeItem row = cursor.getRow();
	 							int column = cursor.getColumn();
	 							row.setText(column, text.getText());
	 							text.dispose();
	 						}
	 						// close the text editor when the user hits "ESC"
	 						if (e.character == SWT.ESC) {
	 							text.dispose();
	 						}
	 					}
	 				});
	 				editor.setEditor(text);
	 				text.setFocus();
	 			}
 			}
 		});
 		
 		//
 		
 		cursor.addFocusListener( new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				int column = cursor.getColumn();
				e.getSource();
				if (column == 0){
					cursor.setVisible(true);
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
 		// Hide the TreeCursor when the user hits the "MOD1" or "MOD2" key.
 		// This alows the user to select multiple items in the tree.
 		cursor.addKeyListener(new KeyAdapter() {
 			public void keyPressed(KeyEvent e) {
 				if (e.keyCode == SWT.MOD1 || 
 				    e.keyCode == SWT.MOD2 || 
 				    (e.stateMask & SWT.MOD1) != 0 || 
 				    (e.stateMask & SWT.MOD2) != 0) {
 					cursor.setVisible(false);
 				}
 			}
 		});
 		// Show the TreeCursor when the user releases the "MOD2" or "MOD1" key.
 		// This signals the end of the multiple selection task.
 		_treeAuto.addKeyListener(new KeyAdapter() {
 			public void keyReleased(KeyEvent e) {
 				if (e.keyCode == SWT.MOD1 && (e.stateMask & SWT.MOD2) != 0) return;
 				if (e.keyCode == SWT.MOD2 && (e.stateMask & SWT.MOD1) != 0) return;
 				if (e.keyCode != SWT.MOD1 && (e.stateMask & SWT.MOD1) != 0) return;
 				if (e.keyCode != SWT.MOD2 && (e.stateMask & SWT.MOD2) != 0) return;
 			
 				TreeItem[] selection = _treeAuto.getSelection();
 				TreeItem row = (selection.length == 0) ? _treeAuto.getItem(_treeAuto.indexOf(_treeAuto.getTopItem())) : selection[0];
 				_treeAuto.showItem(row);
 				cursor.setSelection(row, 0);
 				cursor.setVisible(true);
 				cursor.setFocus();
 			}
 		});
		
 		
		
		// Default Button
		_autoButton = new Button( groupSettings, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_END );
		gridData.horizontalSpan = 3;
		_autoButton.setLayoutData( gridData );
		
		if( _newFP.checkIfBackupExists( _workspacePath ) )
			_autoButton.setText( "Revert Files" );
		else
			_autoButton.setText( "Instrument Files" );
		
		_autoButton.addSelectionListener( this );
		tabAuto.setControl( composite );
	}

	
	
	
	/*
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
		ArrayList<SourceFile> sources = _newFP.getFiles( _workspacePath );

		// First go through and create all of the directory trees
		// ------------------------------------------------------
		for( SourceFile sf : sources ) {
			
			// Get the path from the workspace
			IPath folderPath =  sf.getPath().makeRelativeTo(_workspacePath);
			folderPath = folderPath.removeLastSegments(1);
			//Get the tree items and find the path in the tree

			Object parentItem = _treeAuto;
			
			// Iterate all of the path folders
			for (String currentSegment:folderPath.segments()){

				// Go through Children of current folder and try to match to an item 
				Boolean found = false;
				TreeItem[] items = new TreeItem[0];
				
				if (parentItem instanceof TreeItem){
					items = ((TreeItem) parentItem).getItems();
					
				}
				else if (parentItem instanceof Tree){
					items = ((Tree) parentItem).getItems();
					
				}
				
				for (TreeItem currentItem: items){
										
					if (currentItem.getText(0).compareTo(currentSegment) == 0){
						found = true;
						parentItem = currentItem;
						break;
						
					}
					
				}
				
				// Check if matched, make one if not
				
				if (!found){
					if (parentItem instanceof TreeItem){
						TreeItem item = new TreeItem((TreeItem) parentItem,SWT.NONE );
						String[] tempStr = {currentSegment, "",""};
						item.setText(tempStr);
						parentItem = item;
						item.setChecked( true );
					}
					else if (parentItem instanceof Tree){
						TreeItem item = new TreeItem((Tree) parentItem,SWT.NONE );
						String[] tempStr = {currentSegment, "",""};
						item.setText(tempStr);
						parentItem = item;
						item.setChecked( true );
					}
					
				}	
				
			}
		}
		
		// Place the files in now
		// ----------------------
		for( SourceFile sf : sources ) {
			
			// Get the path from the workspace
			IPath folderPath =  sf.getPath().makeRelativeTo(_workspacePath);
			folderPath = folderPath.removeLastSegments(1);
			//Get the tree items and find the path in the tree

			Object parentItem = _treeAuto;
			
			// Iterate all of the path folders
			for (String currentSegment:folderPath.segments()){

				// Go through Children of current folder and try to match to an item 
				TreeItem[] items = new TreeItem[0];
				
				if (parentItem instanceof TreeItem){
					items = ((TreeItem) parentItem).getItems();
					
				}
				else if (parentItem instanceof Tree){
					items = ((Tree) parentItem).getItems();
					
				}
				
				for (TreeItem currentItem: items){
										
					if (currentItem.getText(0).compareTo(currentSegment) == 0){
						parentItem = currentItem;
						break;
					}
				}
			}
			
			
			// Have the final Location of the SF now add it to the tree
			if (parentItem instanceof TreeItem){
				TreeItem item = new TreeItem( (TreeItem) parentItem, SWT.NONE );
				String[] tempStr = {sf.getName(), "0","100"};
				item.setText(tempStr);
				item.setData( sf );
				item.setChecked( true );
				
			}
			else if (parentItem instanceof Tree){
				TreeItem item = new TreeItem((Tree) parentItem, SWT.NONE );
				String[] tempStr = {sf.getName(), "0","100"};
				item.setText(tempStr);
				item.setData( sf );
				item.setChecked( true );
			}
			

		}


	}

	/**
	 * Fills the Manual Tabs tree with the instrumentation points specified in the XML file
	 * 
	 * @param instrPoints
	 */
	public void fillTree() {

		ArrayList<SourceFile> sources = _newFP.getFiles( _workspacePath );	
		
		// Perform pre-parse
		PreParser pp = new PreParser(); 
		pp.findSynchronizedMethods( sources );
		
		for( SourceFile sf : sources ) {
			sf.clearInterestingPoints();
			_newFP.findInterestPoints( sf );
			boolean someChecked = false;
			if( sf.getInterestingPoints().size() > 0 || _test ) {
				TreeItem item = new TreeItem( _treeManual, SWT.NONE );
				item.setText( sf.getName() );
				item.setData( sf );
				for( InterestPoint ip : sf.getInterestingPoints() ) {
					TreeItem subItem = new TreeItem( item, SWT.NONE );
					String[] strings = { "Line: " + ip.getLine(), ip.getConstruct(), ip.getConstructSyntax(),"" };
					subItem.setText( strings );
					subItem.setData( ip );
					if( ip instanceof InstrumentationPoint ) {
						subItem.setChecked( true );
						item.setChecked( true );
						InstrumentationPoint tempIP = (InstrumentationPoint)ip;
						if( tempIP.getType() == Constants.NOISE_SLEEP )
							subItem.setText( 3, "Sleep" );
						else if( tempIP.getType() == Constants.NOISE_YIELD )
							subItem.setText( 3, "Yield" );
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
	}

	/**
	 * Fill the information below the trees on the manual tab.
	 * 
	 * @param instrPoint
	 */
	public void fillManualInfoLabels( InstrumentationPoint instrPoint ) {
		// Sets the the Combo box
		if( instrPoint.getType() == Constants.NOISE_SLEEP )
			_cmbType.select( _cmbType.indexOf( "Sleep" ) );
		else if( instrPoint.getType() == Constants.NOISE_YIELD )
			_cmbType.select( _cmbType.indexOf( "Yield" ) );
		_txtLower.setText( String.valueOf( instrPoint.getLow() ) );
		_txtHigher.setText( String.valueOf( instrPoint.getHigh() ) );
		_txtProb.setText( String.valueOf( instrPoint.getProbability() ) );
		_cmbType.setEnabled( true );
		_txtLower.setEnabled( true );
		_txtHigher.setEnabled( true );
		_txtProb.setEnabled( true );
	}
	
	/**
	 * Disable the Manual Tabs Info Labels
	 */
	public void disableManualInfoLabels() {
		_cmbType.setText( "" );
		_txtLower.setText( "" );
		_txtHigher.setText( "" );
		_txtProb.setText( "" );
		_cmbType.setEnabled( false );
		_txtLower.setEnabled( false );
		_txtHigher.setEnabled( false );
		_txtProb.setEnabled( false );
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
	
	/**
	 * Instrument the Tree Item in the Manual Tab specified
	 * @param item The Tree Item to Instrument
	 * @param refreshWorkspace Refresh the workspace after uninstrumentation
	 */
	private void instrumentManualTreeItem(TreeItem item, boolean refreshWorkspace){
		refreshManualTreeItem( item.getParentItem() );
		
		// Create annotation
		InterestPoint ip = (InterestPoint)item.getData();
		InstrumentationPoint instr = new InstrumentationPoint(ip.getLine(), ip.getSequence(),ip.getConstruct(), ip.getConstructSyntax(),Constants.NOISE_YIELD, 100, 0, 1000);
		SourceFile sf = (SourceFile)item.getParentItem().getData();
		try {
			_newFP.manipulateAnnotation( sf, instr, Constants.ANNOTATION_ADD, refreshWorkspace);
		}
		catch( IOException e ) {
	
			e.printStackTrace();
		}		
		refreshManualTreeItem(item.getParentItem() );
	}
	
	/**
	 * Uninstrument the Tree Item in the Manual Tab specified
	 * @param item The Tree Item to Uninstrument
	 * @param refreshWorkspace Refresh the workspace after uninstrumentation
	 */
	private void uninstrumentManualTreeItem(TreeItem item, boolean refreshWorkspace){
		refreshManualTreeItem( item.getParentItem() );
		
		InstrumentationPoint ip = (InstrumentationPoint)item.getData();
		SourceFile sf = (SourceFile) item.getParentItem().getData();
		try {
			_newFP.manipulateAnnotation( sf, ip, Constants.ANNOTATION_DELETE, refreshWorkspace);
		}
		catch( IOException e ) {
			
			e.printStackTrace();
		}
		
		refreshManualTreeItem( item.getParentItem() );
	}
	
	/**
	 * Updates the Tree Item Parent 
	 * @param parent
	 */
	private void refreshManualTreeItem(TreeItem parent){
		
		SourceFile sf = (SourceFile) parent.getData();
		sf.clearInterestingPoints();
		_newFP.findInterestPoints( sf );
		ArrayList<InterestPoint> ips = sf.getInterestingPoints();
		int count = 0;
		// Go through it's siblings
		for( TreeItem i : parent.getItems() ) {
			InterestPoint ipCurrent = ips.get( count );
			count++;
			i.setData( ipCurrent );
			
			String[] strings = { "Line: " + ipCurrent.getLine(), ipCurrent.getConstruct(), ipCurrent.getConstructSyntax(), "" };
			i.setText( strings );
			
			if( ipCurrent instanceof InstrumentationPoint ) {
				
				InstrumentationPoint tempIP = (InstrumentationPoint)ipCurrent;
				if( tempIP.getType() == Constants.NOISE_SLEEP )
					i.setText( 3, "Sleep" );
				else if( tempIP.getType() == Constants.NOISE_YIELD )
					i.setText( 3, "Yield" );
				
				i.setChecked(true);
			}
			else{
				i.setChecked(false);
			}
			
		}
	}

	@Override
	public void widgetDefaultSelected( SelectionEvent arg0 ) {
		// Double Clicked
		// The selection was a file in the tree
		if( arg0.item instanceof TreeItem ) {
			TreeItem selectedItem = (TreeItem)arg0.item;
			Object data = selectedItem.getData();
			// Open it in the editor
			if( data instanceof SourceFile ) {
				EditorHandler eH = new EditorHandler();
				SourceFile sFSelected = (SourceFile)data;
				eH.openFile( sFSelected.getPath() );
			}
		}
	}
	
	/**
	 * Update the children Tree Items
	 * @param item
	 */
	void updateChildrenItems(TreeItem item){

		// Make all it's children the same check status
		if( item.getItemCount() > 0 ) {
			for( TreeItem i : item.getItems() ) {
				i.setChecked( item.getChecked() );
				i.setGrayed(false);
				updateChildrenItems(i);
			}
		}
		
	}
	
	
	@Override
	/*
	 * Takes care of the widget selections
	 */
	public void widgetSelected( SelectionEvent arg0 ) {
	

		// Selected Item was something in our Tree
		if( arg0.item instanceof TreeItem ) {

			TreeItem selectedItem = (TreeItem)arg0.item;
			
			// If the selection was checking a checkbox
			if( arg0.detail == SWT.CHECK ) {
				
				// If it is grayed, make it checked
				// All Trees
				if( selectedItem.getGrayed() ) {
					selectedItem.setChecked( true );
					selectedItem.setGrayed( false );
				}
				
				
				// If it is in the Auto Tree, just handle the check marks
				if (selectedItem.getParent() == _treeAuto){

					//Recursively update children
					updateChildrenItems(selectedItem);
				
					TreeItem item = selectedItem.getParentItem();
				
					if (item != null)
						verifyTreeItemParent(item);
				}
							
				// If it is in the Manual Tree, handle the check marks
				// and the instrumentation as well
				else{
					
					// The selection was at the root (File Names)
					if( selectedItem.getParentItem() == null ) {
						
						int itemCount = selectedItem.getItemCount();
						int currentCount = 0;
						
						// Make all it's children the same check status and instrument them where needed
						if( itemCount > 0 ) {
							
							
							for( TreeItem i : selectedItem.getItems() ) {
								
								refreshManualTreeItem(selectedItem);
								
								currentCount++;
								
								// Only (un)instrument if the check mark is changing
								if (i.getChecked() != selectedItem.getChecked()){
									
									boolean refresh = false;
									
									if (currentCount == itemCount)
										refresh = true;
									
									if (selectedItem.getChecked()){
										instrumentManualTreeItem(i, refresh);
									}
									else
										uninstrumentManualTreeItem(i, refresh);
									
								}
								
							}
						}
						verifyTreeItemParent(selectedItem);
						
					}
					
					// If the Selection was at the line level (instrumentation point)
					else {
	
						// Get it's root item (File Name)
						TreeItem parent = selectedItem.getParentItem();
	
						// Check to see if it was checked or unchecked
						if( selectedItem.getChecked() ) {
							instrumentManualTreeItem(selectedItem, true);
						}
	
						// It was unchecked
						else {
							parent.setChecked( false );
							uninstrumentManualTreeItem(selectedItem, true);
						}
						
						verifyTreeItemParent(parent);

					}
				}
			}
			
			// The tree item selected was not the checkmark, the tree item itself
			else {
				// Selected item is a root file, disable fields not needed
				if( selectedItem.getParentItem() == null ) {
					disableManualInfoLabels();
				}
				else {
					// Only enable fields when the selection is an Instrumentation Point
					if( selectedItem.getData() instanceof InstrumentationPoint ) {
						fillManualInfoLabels( (InstrumentationPoint)selectedItem.getData() );
					}
					else
						disableManualInfoLabels();
				}
			}
		}
		
		
		
		// The Widget was a button
		else if( arg0.widget instanceof Button ) {
			Instrumentor i = new Instrumentor();
			ArrayList<SourceFile> sources = _newFP.getFiles( _workspacePath );
			
			// Revert Files instead of instrumenting them
			if( _newFP.checkIfBackupExists( _workspacePath ) ) {
				
				setButtonsInstrument(true);
				for( SourceFile sf : sources ) {
					try {
						i.revertToOriginalState( sf );
					}
					catch( IOException e ) {
						
						e.printStackTrace();
					}
				}
			}
			
			// Instrument the files
			else {
				setButtonsInstrument(false);
				// Manual Instrumentation
				if( arg0.widget == _manualButton ) {
					for( SourceFile sf : sources ) {
						sf.clearInterestingPoints();
						_newFP.findInterestPoints( sf );
						i.instrument( sf, false );
					}
				}
				
				// Automatic Instrumentation
				else if( arg0.widget == _autoButton ) {
					{
						// There are Tree Items (Files)
						if( _treeAuto.getItemCount() > 0 ) {
							TreeItem[] items = _treeAuto.getItems();
							//Go through each top tree item
							for( TreeItem tI : items){
								
								// Recursively Instrument the tree item 
								instrumentAutoTreeItem(tI);
							}
						}
					}
				}
			}		
			//Make sure the buttons are correctly labeled.
			//checkButtons();
		}

		// The Widget selected was a Combo box
		else if( arg0.widget == _cmbType ) {
			
			// Look at the tree item currently being editted.
			TreeItem[] item = _treeManual.getSelection();
			InstrumentationPoint point = (InstrumentationPoint)item[ 0 ].getData();

			// If the selection differs from the model, raise a flag
			if( _cmbType.getText().compareTo( "Sleep" ) == 0 ) {
				if( point.getType() != Constants.NOISE_SLEEP ) {
					point.setType( Constants.NOISE_SLEEP );
					_modified = true;
				}
			}
			else if( _cmbType.getText().compareTo( "Yield" ) == 0 ){
				if( point.getType() != Constants.NOISE_YIELD ) {
					point.setType( Constants.NOISE_YIELD );
					_modified = true;
				}
			}
			
			// If the modified flag is raised, the current treeitem has been changed so write out to xml
			if( _modified ) {
				_modified = false;
				
				SourceFile sf = (SourceFile) item[0].getParentItem().getData();
				refreshManualTreeItem( item[0].getParentItem() );
				try {
					_newFP.manipulateAnnotation( sf, point, Constants.ANNOTATION_UPDATE, true );
				}
				catch( IOException e ) {
					
					e.printStackTrace();
				}
				refreshManualTreeItem( item[0].getParentItem() );	
			}
		}
		else if( arg0.widget == _sleepYield ) {
			// If it was moved set the flag
			if( _sleepYield.getSelection() != _ach.getConfiguration().getYieldProbability() ) {
				_ach.getConfiguration().setYieldProbability( _sleepYield.getSelection() );
				_ach.getConfiguration().setSleepProbability( 100 - _sleepYield.getSelection() );
				_modified = true;
			}
		}

		// If the Widget Selected is a Barrier Scale
		else if( arg0.widget == _scaleBarrier ) {
			// If it was moved set the flag
			if( _scaleBarrier.getSelection() != _ach.getConfiguration().getBarrierProbability() ) {
				_ach.getConfiguration().setBarrierProbability( _scaleBarrier.getSelection() );
				_modified = true;
			}
		}

		// If the Widget Selected is a Latch Scale
		else if( arg0.widget == _scaleLatches ) {
			// If it was moved set the flag
			if( _scaleLatches.getSelection() != _ach.getConfiguration().getLatchProbability() ) {
				_ach.getConfiguration().setLatchProbability( _scaleLatches.getSelection() );
				_modified = true;
			}
		}

		// If the Widget Selected is a Semaphore Scale
		else if( arg0.widget == _scaleSemaphores ) {
			// If it was moved set the flag
			if( _scaleSemaphores.getSelection() != _ach.getConfiguration().getSemaphoreProbability() ) {
				_ach.getConfiguration().setSemaphoreProbability( _scaleSemaphores.getSelection() );
				_modified = true;
			}
		}

		// If the Widget Selected is a Synchronization Scale
		else if( arg0.widget == _scaleSync ) {
			// If it was moved set the flag
			if( _scaleSync.getSelection() != _ach.getConfiguration().getSynchronizeProbability() ) {
				_ach.getConfiguration().setSynchronizeProbability( _scaleSync.getSelection() );
				_modified = true;
			}
		}

		// If the flag has been raised update the Auto Config Scale
		if( _modified ) {
			try {
				_ach.writeXml();
			}
			catch( IOException e ) {
				
				e.printStackTrace();
			}
			_modified = false;
		}
	}

	@Override
	/*
	 * If the text is modified in a textbox set a flag
	 */
	public void modifyText( ModifyEvent e ) {
		_modified = true;
	}

	@Override
	public void focusGained( FocusEvent e ) {
	}

	@Override
	public void focusLost( FocusEvent e ) {
		
		// Flag Variable
		int temp = 0;
		
		// Upon the widget loosing focus it checks to see if anything has been modified
		if( _modified ) {
			TreeItem[] selectedItem = null;
			InstrumentationPoint pointChanging = null;
			if( e.widget != _txtAutoHigher && e.widget != _txtAutoLower ) {
				// If it has been modified
				selectedItem = _treeManual.getSelection();
				pointChanging = (InstrumentationPoint)selectedItem[ 0 ].getData();
			
			
			
				// Find which widget lost focus then update it
				if( e.widget == _txtHigher ) {
					pointChanging.setHigh( Integer.parseInt( _txtHigher.getText() ) );
					temp = 1;
				}
				else if( e.widget == _txtLower ) {
					pointChanging.setLow( Integer.parseInt( _txtLower.getText() ) );
					temp = 1;
				}
				else if( e.widget == _txtProb ) {
					pointChanging.setProbability( Integer.parseInt( _txtProb.getText() ) );
					temp = 1;
				}
			}
			else if( e.widget == _txtAutoHigher ) {
				_ach.getConfiguration().setHighDelayRange( Integer.parseInt( _txtAutoHigher.getText() ) );
			}
			else if( e.widget == _txtAutoLower ) {
				_ach.getConfiguration().setLowDelayRange( Integer.parseInt( _txtAutoLower.getText() ) );
			}
			// Update the respective XML
			
			// Update the respective configuration
			try {
				if (temp == 0)
					_ach.writeXml();
				else{
					refreshManualTreeItem( selectedItem[0].getParentItem() );
					SourceFile sf = (SourceFile) selectedItem[0].getParentItem().getData();
					_newFP.manipulateAnnotation( sf, pointChanging, Constants.ANNOTATION_UPDATE, true);
					refreshManualTreeItem( selectedItem[0].getParentItem() );
				}			
			}
			catch( IOException e1 ) {
				e1.printStackTrace();
			}
			_modified = false;
		}
	}

	/**
	 * Recursively auto instrument the tree items
	 * @param item the top tree item to check.
	 */
	public void instrumentAutoTreeItem(TreeItem item){
		
		// If it is checked then check if it a source file
		// or has children
		if (item.getChecked()){
			
			// The Tree Item is a sourceFile
			if (item.getData() instanceof SourceFile){
				Instrumentor i = new Instrumentor();
				SourceFile sf = (SourceFile) item.getData();
				sf.clearInterestingPoints();
				_newFP.findInterestPoints( sf );
				
				// set the lower and upper bound if it exists
				try{
					int lower = Integer.parseInt(item.getText(1));
					int higher = Integer.parseInt(item.getText(2));
					
					if ( lower >= 0 && lower <= 100 && higher >= 0 && higher <= 100 && lower <= higher){
						sf.setLowerBound(lower);
						sf.setUpperBound(higher);
					}
				}
				catch (Exception e) {
					// TODO: handle exception
				}
				
				// Only instrument it if it has points worth looking at.
				if( sf.getInterestingPoints().size() > 0 )
					i.instrument( sf, true );
			}
			
			// Recursively Instrument the children
			else{
				if (item.getItemCount() > 0){
					for (TreeItem childItem : item.getItems()){
						instrumentAutoTreeItem(childItem);
					}
				}
			}
				
		}
	}
	
	/**
	 * Takes the given tree item and changes it's status based on it's children items. Will change to
	 * unchecked, checked, or grayed
	 * @param item
	 */
	public void verifyTreeItemParent(TreeItem item){
		int count = 0;
		Boolean siblingUnchecked = false;
		Boolean grayed = false;
		
		//Go though all children
		for (TreeItem i : item.getItems()){
			if (i.getGrayed()){
				grayed = true;
				break;
			}
			// When hit a check increase count
			if (i.getChecked() ){
				count++;
				
				// There's been checked siblings and encountered and unchecked, exit because
				// we already know it will be a grayed check
				if (siblingUnchecked && count > 0)
					break;
				
			}
			
			// Found an unchecked item
			else
				siblingUnchecked = true;
		}
		
		if(grayed){
			item.setGrayed(true);
			item.setChecked(true);
		}
		// Has a checked item
		else if (count > 0){
			// Found an unchecked sibling
			if (siblingUnchecked){
				item.setGrayed(true);
			}
			// All must be checked
			else{
				item.setGrayed(false);
				
			}
			item.setChecked(true);
			
		}
		else{
			item.setGrayed(false);
			item.setChecked(false);
		}
		

		//item.setChecked(false);
		// Parent is not a tree
		if (item.getParentItem() != null){
			
			verifyTreeItemParent(item.getParentItem());
			
		}

	}
	/**
	 * Check the buttons in the Auto and Manual to see if they have the correct text to represent reverting
	 * files or not.
	 */
	public void checkButtons() {
		// There's backup files, so set appropriate text for the buttons
		if( _newFP.checkIfBackupExists( _workspacePath ) ) {
			setButtonsInstrument(false);
		}
		else {
			setButtonsInstrument(true);
		}
	}
	
	/**
	 * Set the buttons either to instrument or uninstrument
	 * @param instrument
	 */
	private void setButtonsInstrument(boolean instrument){
		if (instrument){
			_autoButton.setText( "Instrument Files" );
			_manualButton.setText( "Instrument Files" );
		}
		else {
			_autoButton.setText( "Uninstrument" );
			_manualButton.setText( "Uninstrument" );
		}
		_autoButton.pack();
		_manualButton.pack();
		_autoButton.redraw();
		_manualButton.redraw();
	}
}