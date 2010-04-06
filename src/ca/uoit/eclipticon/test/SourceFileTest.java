package ca.uoit.eclipticon.test;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.swt.internal.gtk.OS;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.InterestPoint;
import ca.uoit.eclipticon.data.SourceFile;

public class SourceFileTest extends TestCase {

	private SourceFile		_sourceFile			= null;
	private InterestPoint	_interestingPoint	= new InterestPoint( 0, 0, Constants.SEMAPHORE,
														Constants.SEMAPHORE_ACQUIRE );
	private String			_path				= null;
	private String			_pathUnix			= "/eclipticon/src/ca/uoit/eclipticon/test/SourceFileTest.java";
	private String			_pathWindows		= "\\eclipticon\\src\\ca\\uoit\\eclipticon\\test\\SourceFileTest.java";
	private String			_name				= "SourceFileTest.java";
	private String			_imports			= "package ca.uoit.eclipticon.test;\n\nimport ca.uoit.eclipticon.instrumentation;\nimport ca.uoit.eclipticon.test;";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		if( System.getProperty( "os.name" ).indexOf( "Windows" ) == -1 ) {
			_sourceFile = new SourceFile( new Path( _pathUnix ) );
			_path = _pathUnix;
		}
		else {
			_sourceFile = new SourceFile( new Path( _pathWindows ) );
			_path = _pathWindows;
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSourceFile() {

		boolean result = false;

		if( _sourceFile.getName().equals( _name ) && ( _sourceFile.getPath().toString().equals( _path ) ) ) {
			result = true;
		}

		assertEquals( true, result );
	}

	@Test
	public void testGetPath() {

		boolean result = false;

		if( _sourceFile.getPath().toString().equals( _path ) ) {
			result = true;
		}

		assertEquals( true, result );
	}

	@Test
	public void testGetName() {

		boolean result = false;

		if( _sourceFile.getName().equals( _name ) ) {
			result = true;
		}

		assertEquals( true, result );
	}

	@Test
	public void testAddInterestingPoint() {

		boolean result = false;

		_sourceFile.addInterestingPoint( _interestingPoint );

		if( _sourceFile.getInterestingPoints().size() == 1 ) {
			result = true;
		}

		assertEquals( true, result );
	}

	@Test
	public void testGetInterestingPoints() {

		boolean result = false;

		_sourceFile.addInterestingPoint( _interestingPoint );

		if( _sourceFile.getInterestingPoints().get( 0 ).equals( _interestingPoint ) ) {
			result = true;
		}

		assertEquals( true, result );
	}

	@Test
	public void testClearInterestingPoints() {

		boolean result = false;

		_sourceFile.addInterestingPoint( _interestingPoint );

		_sourceFile.clearInterestingPoints();

		if( _sourceFile.getInterestingPoints().size() == 0 ) {
			result = true;
		}

		assertEquals( true, result );
	}

	@Test
	public void testSetPackageAndImports() {

		boolean result = false;

		_sourceFile.setPackageAndImports( _imports );

		if( _sourceFile.getPackageAndImports().equals( _imports ) ) {
			result = true;
		}

		assertEquals( true, result );
	}

	@Test
	public void testGetPackageAndImports() {

		boolean result = false;

		_sourceFile.setPackageAndImports( _imports );

		if( _sourceFile.getPackageAndImports().equals( _imports ) ) {
			result = true;
		}

		assertEquals( true, result );
	}
}