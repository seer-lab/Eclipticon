package ca.uoit.eclipticon.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.data.SourceFile;
import ca.uoit.eclipticon.instrumentation.Instrumentor;

public class InstrumentorTest extends TestCase {

	private SourceFile				_sourceFile			= null;
	private ArrayList<SourceFile>	_sources			= new ArrayList<SourceFile>();
	private Instrumentor			_instrumentor		= null;
	private String					_packageAndImports	= "package ca.uoit.eclipticon.test.instrumentor_tests;\n\nimport java.util.concurrent.*;\n\nimport ca.uoit.eclipticon.instrumentation.*;";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		_instrumentor = new Instrumentor();
	}

	@After
	public void tearDown() throws Exception {
		_instrumentor.revertToOriginalState( _sourceFile );
	}

	// @Test
	// public void testRevertToOriginalState() {
	// fail( "Not yet implemented" ); // TODO
	// }

	@Test
	public void testInstrumentSimple() throws IOException {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test1.txt" ).getPath() ) );
		_sources.add( _sourceFile );

		// Add package and imports to source file
		_sourceFile.setPackageAndImports( _packageAndImports );

		// Add the instrumentation points
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.BARRIER, Constants.BARRIER_AWAIT,
				Constants.NOISE_SLEEP, 1, 11, 111 ) );

		// Manual Instrument the source file
		_instrumentor.instrument( _sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( _sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test1_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testSolution.trim(), testResult.trim() );
	}

	@Test
	public void testInstrumentMultiple() throws IOException {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test2.txt" ).getPath() ) );
		_sources.add( _sourceFile );

		// Add package and imports to source file
		_sourceFile.setPackageAndImports( _packageAndImports );

		// Add the instrumentation points
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.BARRIER, Constants.BARRIER_AWAIT,
				Constants.NOISE_SLEEP, 1, 11, 111 ) );
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 11, 0, Constants.LATCH, Constants.LATCH_COUNTDOWN,
				Constants.NOISE_SLEEP, 2, 22, 222 ) );

		// Manual Instrument the source file
		_instrumentor.instrument( _sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( _sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test2_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testSolution.trim(), testResult.trim() );
	}

	@Test
	public void testInstrumentMultipleOnSameLine() throws IOException {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test3.txt" ).getPath() ) );
		_sources.add( _sourceFile );

		// Add package and imports to source file
		_sourceFile.setPackageAndImports( _packageAndImports );

		// Add the instrumentation points
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.BARRIER, Constants.BARRIER_AWAIT,
				Constants.NOISE_SLEEP, 1, 11, 111 ) );
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.LATCH, Constants.LATCH_COUNTDOWN,
				Constants.NOISE_SLEEP, 2, 22, 222 ) );

		// Manual Instrument the source file
		_instrumentor.instrument( _sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( _sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test3_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testSolution.trim(), testResult.trim() );
	}

	@Test
	public void testInstrumentMethodCall() throws IOException {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test4.txt" ).getPath() ) );
		_sources.add( _sourceFile );

		// Add package and imports to source file
		_sourceFile.setPackageAndImports( _packageAndImports );

		// Add the instrumentation points
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.SYNCHRONIZE, "methodCall",
				Constants.NOISE_SLEEP, 1, 11, 111 ) );

		// Manual Instrument the source file
		_instrumentor.instrument( _sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( _sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test4_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testSolution.trim(), testResult.trim() );
	}

	@Test
	public void testInstrumentMethodAfterMethod() throws IOException {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test5.txt" ).getPath() ) );
		_sources.add( _sourceFile );

		// Add package and imports to source file
		_sourceFile.setPackageAndImports( _packageAndImports );

		// Add the instrumentation points
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.SYNCHRONIZE, "methodCall",
				Constants.NOISE_SLEEP, 1, 11, 111 ) );

		// Manual Instrument the source file
		_instrumentor.instrument( _sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( _sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test5_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testSolution.trim(), testResult.trim() );
	}

	@Test
	public void testInstrumentMethodWithinMethod() throws IOException {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test6.txt" ).getPath() ) );
		_sources.add( _sourceFile );

		// Add package and imports to source file
		_sourceFile.setPackageAndImports( _packageAndImports );

		// Add the instrumentation points
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.SYNCHRONIZE, "methodCall",
				Constants.NOISE_SLEEP, 1, 11, 111 ) );

		// Manual Instrument the source file
		_instrumentor.instrument( _sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( _sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test6_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testSolution.trim(), testResult.trim() );
	}

	@Test
	public void testInstrumentSynchronizedMethodBlock() throws IOException {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test7.txt" ).getPath() ) );
		_sources.add( _sourceFile );

		// Add package and imports to source file
		_sourceFile.setPackageAndImports( _packageAndImports );

		// Add the instrumentation points
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.SYNCHRONIZE,
				Constants.SYNCHRONIZE_BLOCK, Constants.NOISE_SLEEP, 1, 11, 111 ) );

		// Manual Instrument the source file
		_instrumentor.instrument( _sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( _sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test7_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testSolution.trim(), testResult.trim() );
	}

	@Test
	public void testInstrumentMultipleSynchronizedBlockMethodOnSameLine() throws IOException {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test8.txt" ).getPath() ) );
		_sources.add( _sourceFile );

		// Add package and imports to source file
		_sourceFile.setPackageAndImports( _packageAndImports );

		// Add the instrumentation points
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.SYNCHRONIZE,
				Constants.SYNCHRONIZE_BLOCK, Constants.NOISE_SLEEP, 1, 11, 111 ) );
		_sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 1, Constants.SYNCHRONIZE,
				Constants.SYNCHRONIZE_BLOCK, Constants.NOISE_SLEEP, 2, 22, 222 ) );

		// Manual Instrument the source file
		_instrumentor.instrument( _sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( _sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test8_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testSolution.trim(), testResult.trim() );
	}
}