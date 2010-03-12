package ca.uoit.eclipticon.gui.views;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableLayout;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import ca.uoit.eclipticon.data.AutomaticConfiguration;
import ca.uoit.eclipticon.data.AutomaticConfigurationHandler;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.data.InstrumentationPointHandler;
import ca.uoit.eclipticon.data.InterestPoint;
import ca.uoit.eclipticon.data.SourceFile;
import ca.uoit.eclipticon.instrumentation.FileParser;

public class EclipticonViewer extends Viewer implements SelectionListener, ModifyListener, FocusListener {

	// The GUI components that need to be modified during run time

	Composite						_compositeParent	= null;
	Composite						_compositePoints	= null;
	CTabFolder						_folderTab			= null;
	Table							_tablePoints		= null;
	Text							_txtLower			= null;
	Text							_txtHigher			= null;
	Text							_txtPerc			= null;
	Label							_line2Lbl			= null;
	Label							_char2Lbl			= null;
	Label							_sourceLbl			= null;
	Combo							_cmbType			= null;
	Scale							_sleepYield			= null;
	Text							_txtAutoLower		= null;
	Text							_txtAutoHigher		= null;
	Scale							_scaleBarrier		= null;
	Scale							_scaleSync			= null;
	Scale							_scaleSemaphores	= null;
	Scale							_scaleLatches		= null;
	Tree							_tree				= null;
	boolean							_modified			= false;
	InstrumentationPointHandler		_iph				= null;
	AutomaticConfigurationHandler	_ach				= null;

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

		// Initialize the models
		_ach = new AutomaticConfigurationHandler();
		_iph = new InstrumentationPointHandler();

		try {
			// Read in their XML's
			_iph.readXml();
			_ach.readXml();

			// Populate the table and the Auto Tab
			fillTable( _iph.getInstrPoints() );
			populateAutoTab( _ach.getConfiguration() );

			// Initialize the tables selection to the first item and fill the info at the bottom
			if( _tablePoints.getItemCount() > 0 ) {
				_tablePoints.select( 0 );
				fillInfoLabels( (InstrumentationPoint)_tablePoints.getItem( 0 ).getData() );
			}
		}
		catch( FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Select the Manual Tab
		_folderTab.setSelection( 0 );

		// Add listeners
		_folderTab.addSelectionListener( this );
		_tablePoints.addSelectionListener( this );
		_cmbType.addSelectionListener( this );
		_scaleBarrier.addSelectionListener( this );
		_scaleLatches.addSelectionListener( this );
		_scaleSemaphores.addSelectionListener( this );
		_scaleSync.addSelectionListener( this );
		_sleepYield.addSelectionListener( this );

		_txtLower.addFocusListener( this );
		_txtPerc.addFocusListener( this );
		_txtHigher.addFocusListener( this );
		_txtAutoLower.addFocusListener( this );
		_txtAutoHigher.addFocusListener( this );

		_txtLower.addModifyListener( this );
		_txtHigher.addModifyListener( this );
		_txtPerc.addModifyListener( this );
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

		// Create the Table
		_tablePoints = new Table( composite, SWT.FULL_SELECTION | SWT.BORDER );
		gridData = new GridData( GridData.FILL_BOTH );
		_tablePoints.setVisible( false );
		//_tablePoints.setLayoutData( gridData );
		_tablePoints.setHeaderVisible( true );
		_tablePoints.setLinesVisible( true );
		TableLayout tableLayout = new TableLayout();

		// Create the Table Columns
		TableColumn tableColumn = new TableColumn( _tablePoints, SWT.NULL );
		ColumnLayoutData columnLayout = new ColumnWeightData( 13, true );
		tableColumn.setText( "Line" );
		tableLayout.addColumnData( columnLayout );

		tableColumn = new TableColumn( _tablePoints, SWT.NULL );
		columnLayout = new ColumnWeightData( 72, true );
		tableColumn.setText( "Source" );
		tableLayout.addColumnData( columnLayout );

		tableColumn = new TableColumn( _tablePoints, SWT.NULL );
		columnLayout = new ColumnWeightData( 15, true );
		tableColumn.setText( "Type" );
		tableLayout.addColumnData( columnLayout );

		_tablePoints.setLayout( tableLayout );

		// Create a Section for the properties
		Group groupProperties = new Group( composite, SWT.NULL );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.minimumHeight = 200;
		groupProperties.setLayoutData( gridData );

		groupProperties.setText( "Properties" );
		gridLayout = new GridLayout( 3, false );
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

		// Create an area for the info you can't change 
		Composite composite2 = new Composite( groupProperties, SWT.NULL );
		gridData = new GridData( GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING );
		gridData.verticalSpan = 4;
		composite2.setLayoutData( gridData );
		gridLayout = new GridLayout( 2, false );
		gridLayout.marginWidth = 0;
		composite2.setLayout( gridLayout );

		// Line Label
		Label lineLbl = new Label( composite2, SWT.NULL );
		gridData = new GridData();
		lineLbl.setLayoutData( gridData );
		lineLbl.setText( "Line:" );

		_line2Lbl = new Label( composite2, SWT.NULL );
		gridData = new GridData();
		_line2Lbl.setLayoutData( gridData );
		_line2Lbl.setText( "" );

		// Character Info
		Label charLbl = new Label( composite2, SWT.NULL );
		gridData = new GridData();
		charLbl.setLayoutData( gridData );
		charLbl.setText( "Characters:" );

		_char2Lbl = new Label( composite2, SWT.NULL );
		gridData = new GridData();
		_char2Lbl.setLayoutData( gridData );
		_char2Lbl.setText( "" );

		// Source Info
		Label SourceLbl = new Label( composite2, SWT.NULL );
		gridData = new GridData();
		SourceLbl.setLayoutData( gridData );
		SourceLbl.setText( "Source:" );

		_sourceLbl = new Label( composite2, SWT.NULL );
		gridData = new GridData();
		_sourceLbl.setLayoutData( gridData );
		_sourceLbl.setText( "" );
		// Ending the Area for the info you can't change

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
		Label percLbl = new Label( groupProperties, SWT.NULL );
		gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
		percLbl.setLayoutData( gridData );
		percLbl.setText( "Probability of Delay (%):" );

		_txtPerc = new Text( groupProperties, SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		_txtPerc.setLayoutData( gridData );

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
	 * @param autoConfig
	 */
	public void populateAutoTab( AutomaticConfiguration autoConfig ) {
		_sleepYield.setSelection( autoConfig.getYieldChance() );
		_txtAutoLower.setText( String.valueOf( autoConfig.getLowDelayRange() ) );
		_txtAutoHigher.setText( String.valueOf( autoConfig.getHighDelayRange() ) );
		_scaleBarrier.setSelection( autoConfig.getBarrierChance() );
		_scaleLatches.setSelection( autoConfig.getLatchChance() );
		_scaleSemaphores.setSelection( autoConfig.getSemaphoreChance() );
		_scaleSync.setSelection( autoConfig.getSynchronizeChance() );
	}

	/**
	 * Fills the table with the instrumentation points specified in the XML file
	 * @param instrPoints
	 */
	public void fillTable( ArrayList<InstrumentationPoint> instrPoints ) {

		//Fill it if need be
		if( !instrPoints.isEmpty() ) {
			// Iterate over all the instrumentation points
			for( InstrumentationPoint currentPoint : instrPoints ) {
				// Create a new item to go into the table
				TableItem item = new TableItem( _tablePoints, SWT.NULL );
				item.setData( currentPoint );
				item.setText( 0, String.valueOf( currentPoint.getLine() ) );
				Path path = new Path( currentPoint.getSource() );

				item.setText( 1, path.lastSegment() );

				// TODO: Make it use CONSTANTS instead.
				if( currentPoint.getType() == 0 )
					item.setText( 2, "Sleep" );
				else if( currentPoint.getType() == 1 )
					item.setText( 2, "Yield" );
			}
		}
	}

	/**
	 * Fill the information below the tables on the manual tab.
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
		_txtPerc.setText( String.valueOf( instrPoint.getChance() ) );
		_line2Lbl.setText( String.valueOf( instrPoint.getLine() ) );
		_char2Lbl.setText( String.valueOf( instrPoint.getCharacter() ) );
		Path path = new Path( instrPoint.getSource() );
		_sourceLbl.setText( path.lastSegment() );
	}

	/**
	 * Refreshes the table item to reflect the model
	 * 
	 */
	public void refreshSelectedTableItem() {
		TableItem[] selectedItems = _tablePoints.getSelection();
		if( ( (InstrumentationPoint)selectedItems[ 0 ].getData() ).getType() == 0 )
			selectedItems[ 0 ].setText( 2, "Sleep" );
		else
			selectedItems[ 0 ].setText( 2, "Yield" );
		//tablePoints.removeAll();
		//fillTable( _iph.getInstrPoints() );
	}

	/**
	 * Clears the table and repopulates from the model
	 */
	public void refreshTable() {
		_tablePoints.removeAll();
		fillTable( _iph.getInstrPoints() );
		if( _tablePoints.getItemCount() > 0 ) {
			_tablePoints.select( 0 );
			TableItem[] selectedItem = _tablePoints.getSelection();
			InstrumentationPoint selectedPoint = (InstrumentationPoint)selectedItem[ 0 ].getData();
			fillInfoLabels( selectedPoint );
		}
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
	/**
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
					//Start it not grayed
					parent.setGrayed( false );

					// Check to see if it was checked or unchecked
					if( selectedItem.getChecked() ) {

						// Go through it's siblings
						for( TreeItem i : parent.getItems() ) {

							// If it comes to a sibling that isn't selected the root is grayed
							if( !i.getChecked() ) {
								parent.setGrayed( true );
								break;
							}
						}
					}

					// It was unchecked
					else {

						// Go through all the siblings
						for( TreeItem i : parent.getItems() ) {

							// if comes to one that is checked make the root grayed
							if( i.getChecked() ) {
								parent.setGrayed( true );
								break;
							}
						}
					}
				}

			}
		}
		// The Widget was a button
		else if( arg0.widget instanceof Button ) {

			//			//TEMPORARYYYYYYY
			//			Button pressedButton = (Button)arg0.item;
			//
			//			String temp;
			//			int id;
			//			String source;
			//			int line;
			//			int character;
			//			int type;
			//			int chance;
			//			int low;
			//			int high;
			//			BufferedReader reader;
			//			reader = new BufferedReader( new InputStreamReader( System.in ) );
			//			try {
			//				System.out.println( "ID:" );
			//				temp = reader.readLine();
			//				id = Integer.parseInt( temp );
			//				System.out.println( "Source:" );
			//				source = reader.readLine();
			//				System.out.println( "Line:" );
			//				temp = reader.readLine();
			//				line = Integer.parseInt( temp );
			//				System.out.println( "Character:" );
			//				temp = reader.readLine();
			//				character = Integer.parseInt( temp );
			//				System.out.println( "Type:" );
			//				temp = reader.readLine();
			//				type = Integer.parseInt( temp );
			//				System.out.println( "Probability:" );
			//				temp = reader.readLine();
			//				chance = Integer.parseInt( temp );
			//				System.out.println( "Low:" );
			//				temp = reader.readLine();
			//				low = Integer.parseInt( temp );
			//				System.out.println( "High:" );
			//				temp = reader.readLine();
			//				high = Integer.parseInt( temp );
			//				_iph.addInstrumentationPoint( id, source, line, character, type, chance, low, high );
			//				_iph.writeXml();
			//				refreshTable();
			//
			//			}
			//			catch( IOException e ) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}

			FileParser newFP = new FileParser();
			Path tmpPath = new Path( "C:/Users/Cody/Downloads/src/src/ca/uoit" );
			ArrayList<SourceFile> sources = newFP.getFiles( tmpPath );

			for( SourceFile sf : sources ) {
				newFP.findInterestPoints( sf );

				TreeItem item = new TreeItem( _tree, SWT.NONE );
				item.setText( sf.getName() );
				for( InterestPoint ip : sf.getInterestingPoints() ) {
					TreeItem subItem = new TreeItem( item, SWT.NONE );

					String[] strings = { "Line: " + ip.getLine(), ip.getConstruct(), "" };
					subItem.setText( strings );
				}

			}
		}

		// The Widget selected was a Combo box
		else if( arg0.widget == _cmbType ) {

			// Look at the table item currently being editted.
			TableItem[] item = _tablePoints.getSelection();
			InstrumentationPoint point = (InstrumentationPoint)item[ 0 ].getData();

			// If the selection differs from the model, raise a flag
			if( _cmbType.getText().compareTo( "Sleep" ) == 0 ) {
				if( point.getType() != 0 ) {
					point.setType( 0 );
					_modified = true;
				}
			}
			else if( _cmbType.getText().compareTo( "Yield" ) == 0 ) {
				if( point.getType() != 1 ) {
					point.setType( 1 );
					_modified = true;
				}
			}

			// If the modified flag is raised, the current tableitem has been changed
			// so write out to xml
			if( _modified ) {
				_modified = false;
				try {
					_iph.writeXml();
					refreshSelectedTableItem();

				}
				catch( IOException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// If the Widget Selected is a Yield Scale
		else if( arg0.widget == _sleepYield ) {
			// If it was moved set the flag
			if( _sleepYield.getSelection() != _ach.getConfiguration().getYieldChance() ) {
				_ach.getConfiguration().setYieldChance( _sleepYield.getSelection() );
				_modified = true;
			}
		}

		// If the Widget Selected is a Barrier Scale
		else if( arg0.widget == _scaleBarrier ) {
			// If it was moved set the flag
			if( _scaleBarrier.getSelection() != _ach.getConfiguration().getYieldChance() ) {
				_ach.getConfiguration().setBarrierChance( _scaleBarrier.getSelection() );
				_modified = true;
			}
		}

		// If the Widget Selected is a Latch Scale
		else if( arg0.widget == _scaleLatches ) {
			// If it was moved set the flag
			if( _scaleLatches.getSelection() != _ach.getConfiguration().getYieldChance() ) {
				_ach.getConfiguration().setLatchChance( _scaleLatches.getSelection() );
				_modified = true;
			}
		}

		// If the Widget Selected is a Semaphore Scale
		else if( arg0.widget == _scaleSemaphores ) {
			// If it was moved set the flag
			if( _scaleSemaphores.getSelection() != _ach.getConfiguration().getYieldChance() ) {
				_ach.getConfiguration().setSemaphoreChance( _scaleSemaphores.getSelection() );
				_modified = true;
			}
		}

		// If the Widget Selected is a Synchronization Scale
		else if( arg0.widget == _scaleSync ) {
			// If it was moved set the flag
			if( _scaleSync.getSelection() != _ach.getConfiguration().getYieldChance() ) {
				_ach.getConfiguration().setSynchronizeChance( _scaleSync.getSelection() );
				_modified = true;
			}
		}

		// If the flag has been raised update the Auto Config Scale
		if( _modified ) {
			try {
				_ach.writeXml();
			}
			catch( IOException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_modified = false;
		}

	}

	@Override
	/**
	 * If the text is modified in a textbox set a flag
	 */
	public void modifyText( ModifyEvent e ) {
		// TODO Auto-generated method stub
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
			// If it has been modified
			TableItem[] selectedItem = _tablePoints.getSelection();
			InstrumentationPoint pointChanging = (InstrumentationPoint)selectedItem[ 0 ].getData();

			// Find which widget lost focus then update it
			if( e.widget == _txtHigher ) {
				pointChanging.setHigh( Integer.parseInt( _txtHigher.getText() ) );
				temp = 1;
			}
			else if( e.widget == _txtLower ) {
				pointChanging.setLow( Integer.parseInt( _txtLower.getText() ) );
				temp = 1;
			}
			else if( e.widget == _txtPerc ) {
				pointChanging.setChance( Integer.parseInt( _txtPerc.getText() ) );
				temp = 1;
			}
			else if( e.widget == _txtAutoHigher ) {
				_ach.getConfiguration().setHighDelayRange( Integer.parseInt( _txtAutoHigher.getText() ) );
			}
			else if( e.widget == _txtAutoLower ) {
				_ach.getConfiguration().setLowDelayRange( Integer.parseInt( _txtAutoLower.getText() ) );
			}
			// Update the respective XML
			try {
				if( temp == 1 )
					_iph.writeXml();
				else
					_ach.writeXml();
			}
			catch( IOException e1 ) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			refreshSelectedTableItem();
			_modified = false;

		}

	}

}
