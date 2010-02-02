package ca.uoit.eclipticon.gui.views;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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


public class EclipticonViewer extends Viewer implements SelectionListener{

	Composite _compositeParent = null;
	Composite _compositePoints = null;
	CTabFolder _folderTab = null;
	

	public EclipticonViewer(Composite parent, int i) {
		// TODO Auto-generated constructor stub
		super();
	}

	protected Control createControl( Composite compositeParent ) {
		_compositeParent = compositeParent;
		_compositePoints = new Composite(compositeParent,SWT.NULL);
		_compositePoints.setLayout( new FillLayout() );
		
		_folderTab = new CTabFolder( _compositePoints, SWT.NULL );
		_folderTab.setTabPosition( SWT.TOP);
		
		createManualTab(_folderTab);
		createAutoTab(_folderTab);
		
		_folderTab.setSelection( 0 );
		_folderTab.addSelectionListener(this);
		return _compositePoints;
	}
	
	
	/**
	 * 
	 * @param tabFolder
	 * @return
	 */
	private void createManualTab(CTabFolder tabFolder){
				
		CTabItem tabManual = new CTabItem( tabFolder, SWT.NULL );
		tabManual.setText( "Manual" );
		
		Composite composite = new Composite(tabFolder,SWT.NULL);
		GridData gridData = new GridData( GridData.FILL_BOTH );
		composite.setLayoutData( gridData );
		GridLayout gridLayout = new GridLayout();
		composite.setLayout( gridLayout );
		
		//----------------------------------
		
		Table tablePoints = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);
		gridData = new GridData( GridData.FILL_BOTH);
		
		tablePoints.setLayoutData( gridData );
		tablePoints.setHeaderVisible(true);
		tablePoints.setLinesVisible(true);
		TableLayout tableLayout = new TableLayout();
		
		TableColumn tableColumn = new TableColumn(tablePoints,SWT.NULL);
		ColumnLayoutData columnLayout = new ColumnWeightData(10,true);
		tableColumn.setText("Line");
		tableLayout.addColumnData( columnLayout );
		
		tableColumn = new TableColumn(tablePoints,SWT.NULL);
		columnLayout = new ColumnWeightData(75,true);
		tableColumn.setText("Source");
		tableLayout.addColumnData( columnLayout );
	
		tableColumn = new TableColumn(tablePoints,SWT.NULL);
		columnLayout = new ColumnWeightData(15,true);
		tableColumn.setText("Type");
		tableLayout.addColumnData( columnLayout );
		
		tablePoints.setLayout(tableLayout);
		
		//----------------------------------
		for (int i = 0; i < 50; i++){
			String[] testData = new String[] { Integer.toString((i+10)%3), "HelloWorld.java", Integer.toString(i+45) };
			TableItem item = new TableItem( tablePoints, SWT.NULL );
			item.setText( testData );
		}
		 
		
		//----------------------------------
		Group groupProperties = new Group(composite, SWT.NULL);
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.minimumHeight = 200;
		groupProperties.setLayoutData( gridData );
		
		groupProperties.setText("Properties");
		gridLayout = new GridLayout(3,false);
		groupProperties.setLayout( gridLayout );
		
		Label typeLbl = new Label(groupProperties, SWT.NULL);
		gridData = new GridData();
		typeLbl.setLayoutData(gridData);
		typeLbl.setText("Type:");
		
		Combo cmbType = new Combo(groupProperties, SWT.NULL);
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		cmbType.setLayoutData(gridData);
		cmbType.add("Sleep");
		cmbType.add("Yield");
		cmbType.select(1);
		
		Composite composite2 = new Composite(groupProperties,SWT.NULL);
		gridData = new GridData( GridData.FILL_BOTH );
		gridData.verticalSpan = 3;
		composite2.setLayoutData( gridData );
		gridLayout = new GridLayout(2,false);
		gridLayout.marginWidth=0;
		composite2.setLayout( gridLayout );
		

		
		Label lowerLbl = new Label(groupProperties, SWT.NULL);
		gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		lowerLbl.setLayoutData(gridData);
		lowerLbl.setText("Lower Bound:");
		
		Text txtLower = new Text(groupProperties,SWT.BORDER);
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		txtLower.setLayoutData(gridData);
		
		Label higherLbl = new Label(groupProperties, SWT.NULL);
		gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		higherLbl.setLayoutData(gridData);
		higherLbl.setText("Higher Bound:");
		
		Text txtHigher = new Text(groupProperties,SWT.BORDER);
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		txtHigher.setLayoutData(gridData);
		
		
		
		
		Label idLbl = new Label(composite2, SWT.NULL);
		gridData = new GridData();
		idLbl.setLayoutData(gridData);
		idLbl.setText("ID:");
		
		Label id2Lbl = new Label(composite2, SWT.NULL);
		gridData = new GridData();
		id2Lbl.setLayoutData(gridData);
		id2Lbl.setText("1");
		
		Label lineLbl = new Label(composite2, SWT.NULL);
		gridData = new GridData();
		lineLbl.setLayoutData(gridData);
		lineLbl.setText("Line:");
		
		Label line2Lbl = new Label(composite2, SWT.NULL);
		gridData = new GridData();
		line2Lbl.setLayoutData(gridData);
		line2Lbl.setText("100");
		
		Label charLbl = new Label(composite2, SWT.NULL);
		gridData = new GridData();
		charLbl.setLayoutData(gridData);
		charLbl.setText("Characters:");
		
		Label char2Lbl = new Label(composite2, SWT.NULL);
		gridData = new GridData();
		char2Lbl.setLayoutData(gridData);
		char2Lbl.setText("10");
		
		Label SourceLbl = new Label(composite2, SWT.NULL);
		gridData = new GridData();
		SourceLbl.setLayoutData(gridData);
		SourceLbl.setText("Source:");
		
		Label sourceLbl = new Label(composite2, SWT.NULL);
		gridData = new GridData();
		sourceLbl.setLayoutData(gridData);
		sourceLbl.setText("Hello.java");
		
		
		Button but = new Button(groupProperties, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.horizontalSpan = 3;
		but.setLayoutData(gridData);
		but.setText("Default");
		
	
		
		tabManual.setControl(composite);
		
	}
	
	/**
	 * 
	 * @param tabFolder
	 * @return
	 */
	private void createAutoTab(CTabFolder tabFolder){
				
		CTabItem tabAuto = new CTabItem( tabFolder, SWT.NULL );
		tabAuto.setText( "Automatic" );
		
		Composite composite = new Composite(tabFolder,SWT.NULL);
		GridData gridData = new GridData( GridData.FILL_BOTH );
		composite.setLayoutData( gridData );
		GridLayout gridLayout = new GridLayout();
		composite.setLayout( gridLayout );
		
		//----------------------------------
		
		
		//----------------------------------
		Group groupSettings = new Group(composite, SWT.NULL);
		gridData = new GridData( GridData.FILL_BOTH );
		groupSettings.setLayoutData( gridData );
		groupSettings.setText("Settings");
		gridLayout = new GridLayout(3,false);
		groupSettings.setLayout( gridLayout );
		
		
		Label typeLbl = new Label(groupSettings, SWT.NULL);
		gridData = new GridData();
		gridData.horizontalSpan=3;
		typeLbl.setLayoutData(gridData);
		typeLbl.setText("Probability of Type");
		
		Label sleepLbl = new Label(groupSettings, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		sleepLbl.setLayoutData(gridData);
		sleepLbl.setText("Sleep");
		
		Scale sleepYield = new Scale(groupSettings,SWT.NULL);
		gridData = new GridData( GridData.FILL_HORIZONTAL);
		gridData.verticalSpan = 2;
		sleepYield.setLayoutData(gridData);
		
		sleepYield.setMinimum(0);
		sleepYield.setMaximum(100);
		sleepYield.setSelection(100);
		
		Label yieldLbl = new Label(groupSettings, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		yieldLbl.setLayoutData(gridData);
		yieldLbl.setText("Yield");
		
		Label sleepLbl2 = new Label(groupSettings, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		sleepLbl2.setLayoutData(gridData);
		sleepLbl2.setText("0");
	
		Label yieldLbl2 = new Label(groupSettings, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		yieldLbl2.setLayoutData(gridData);
		yieldLbl2.setText("100");
		
		Composite range = new Composite(groupSettings, SWT.NULL);
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 3;
		range.setLayoutData( gridData );
		gridLayout = new GridLayout(3,false);
		gridLayout.marginWidth = 0;
		range.setLayout( gridLayout );
		
		Label rangeLbl = new Label(range, SWT.NULL);
		gridData = new GridData();
		rangeLbl.setLayoutData(gridData);
		rangeLbl.setText("Delay Range(ms):");
		
		Text txtLower = new Text(range,SWT.BORDER);
		gridData = new GridData( GridData.FILL_HORIZONTAL);
		txtLower.setLayoutData(gridData);
		
		Text txtHigher = new Text(range,SWT.BORDER);
		gridData = new GridData( GridData.FILL_HORIZONTAL);
		txtHigher.setLayoutData(gridData);
		
		
		
		Group groupMech = new Group(groupSettings, SWT.NULL);
		gridData = new GridData( GridData.FILL_BOTH );
		gridData.minimumHeight=280;
		gridData.horizontalSpan =3;
		groupMech.setLayoutData( gridData );
		groupMech.setText("Percentage of Mechanisms Instrumented");
		gridLayout = new GridLayout(3,false);
		groupMech.setLayout( gridLayout );
		
		
		
		Label typeMech = new Label(groupMech, SWT.NULL);
		gridData = new GridData();
		gridData.horizontalSpan=3;
		typeMech.setLayoutData(gridData);
		typeMech.setText("Barriers");
		
		Label zeroLbl = new Label(groupMech, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		zeroLbl.setLayoutData(gridData);
		zeroLbl.setText("0");
		
		Scale scaleBarrier = new Scale(groupMech,SWT.NULL);
		gridData = new GridData( GridData.FILL_HORIZONTAL);
		scaleBarrier.setLayoutData(gridData);
		scaleBarrier.setMinimum(0);
		scaleBarrier.setMaximum(100);
		
		Label hundredLbl = new Label(groupMech, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		hundredLbl.setLayoutData(gridData);
		hundredLbl.setText("100");
		
		
		
		
		
		

		Label typeMech2 = new Label(groupMech, SWT.NULL);
		gridData = new GridData();
		gridData.horizontalSpan=3;
		typeMech2.setLayoutData(gridData);
		typeMech2.setText("Latches");
		
		Label zeroLbl2 = new Label(groupMech, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		zeroLbl2.setLayoutData(gridData);
		zeroLbl2.setText("0");
		
		Scale scaleLatches = new Scale(groupMech,SWT.NULL);
		gridData = new GridData( GridData.FILL_HORIZONTAL);
		scaleLatches.setLayoutData(gridData);
		scaleLatches.setMinimum(0);
		scaleLatches.setMaximum(100);
		
		Label hundredLbl2 = new Label(groupMech, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		hundredLbl2.setLayoutData(gridData);
		hundredLbl2.setText("100");
		
		
		
		

		Label typeMech3 = new Label(groupMech, SWT.NULL);
		gridData = new GridData();
		gridData.horizontalSpan=3;
		typeMech3.setLayoutData(gridData);
		typeMech3.setText("Semaphores");
		
		Label zeroLbl3 = new Label(groupMech, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		zeroLbl3.setLayoutData(gridData);
		zeroLbl3.setText("0");
		
		Scale scaleSemaphores = new Scale(groupMech,SWT.NULL);
		gridData = new GridData( GridData.FILL_HORIZONTAL);
		scaleSemaphores.setLayoutData(gridData);
		scaleSemaphores.setMinimum(0);
		scaleSemaphores.setMaximum(100);
		
		Label hundredLbl3 = new Label(groupMech, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		hundredLbl3.setLayoutData(gridData);
		hundredLbl3.setText("100");
		
		
		
		

		Label typeMech4 = new Label(groupMech, SWT.NULL);
		gridData = new GridData();
		gridData.horizontalSpan=3;
		typeMech4.setLayoutData(gridData);
		typeMech4.setText("Synchronizations");
		
		Label zeroLbl4 = new Label(groupMech, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		zeroLbl4.setLayoutData(gridData);
		zeroLbl4.setText("0");
		
		Scale scaleSync = new Scale(groupMech,SWT.NULL);
		gridData = new GridData( GridData.FILL_HORIZONTAL);
		scaleSync.setLayoutData(gridData);
		scaleSync.setMinimum(0);
		scaleSync.setMaximum(100);
		
		Label hundredLbl4 = new Label(groupMech, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		hundredLbl4.setLayoutData(gridData);
		hundredLbl4.setText("100");
		
		
		
		
		
		
		
		
		Button but = new Button(groupSettings, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.horizontalSpan = 3;
		but.setLayoutData(gridData);
		but.setText("Default");
		
	
		
		tabAuto.setControl(composite);
		
	}
	

	
	@Override
	public Control getControl() {
		// TODO Auto-generated method stub
		return _compositePoints;
	}

	@Override
	public Object getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInput(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelection(ISelection arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		
		
	}

}
