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
import ca.uoit.eclipticon.gui.Activator;
import ca.uoit.eclipticon.instrumentation.Instrumentor;
import ca.uoit.eclipticon.parsers.PreParser;

public class InstrumentorTest extends TestCase {

	private SourceFile		sourceFile			= null;
	ArrayList<SourceFile>	sources				= new ArrayList<SourceFile>();
	private Instrumentor	instrumentor		= null;
	private String			packageAndImports	= "package ca.uoit.eclipticon.test.instrumentor_tests;\n\nimport java.util.concurrent.*;\n\nimport ca.uoit.eclipticon.instrumentation.*;";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new Activator();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		instrumentor = new Instrumentor();
	}

	@After
	public void tearDown() throws Exception {
		instrumentor.revertToOriginalState( sourceFile );
	}

	// @Test
	// public void testRevertToOriginalState() {
	// fail( "Not yet implemented" ); // TODO
	// }

	@Test
	public void testInstrumentSimple() throws IOException {

		// Set the source file up
		sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test1.txt" ).getPath() ) );
		sources.add( sourceFile );

		// Add package and imports to source file
		sourceFile.setPackageAndImports( packageAndImports );

		// Add the instrumentation points
		sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.BARRIER, Constants.BARRIER_AWAIT,
				Constants.SLEEP, 1, 11, 111 ) );

		// Manual Instrument the source file
		instrumentor.instrument( sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test1_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testResult.trim(), testSolution.trim() );
	}

	@Test
	public void testInstrumentMultiple() throws IOException {

		// Set the source file up
		sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test2.txt" ).getPath() ) );
		sources.add( sourceFile );

		// Add package and imports to source file
		sourceFile.setPackageAndImports( packageAndImports );

		// Add the instrumentation points
		sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.BARRIER, Constants.BARRIER_AWAIT,
				Constants.SLEEP, 1, 11, 111 ) );
		sourceFile.addInterestingPoint( new InstrumentationPoint( 11, 0, Constants.LATCH, Constants.LATCH_COUNTDOWN,
				Constants.SLEEP, 2, 22, 222 ) );

		// Manual Instrument the source file
		instrumentor.instrument( sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test2_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testResult.trim(), testSolution.trim() );
	}

	@Test
	public void testInstrumentMultipleOnSameLine() throws IOException {

		// Set the source file up
		sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test3.txt" ).getPath() ) );
		sources.add( sourceFile );

		// Add package and imports to source file
		sourceFile.setPackageAndImports( packageAndImports );

		// Add the instrumentation points
		sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.BARRIER, Constants.BARRIER_AWAIT,
				Constants.SLEEP, 1, 11, 111 ) );
		sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.LATCH, Constants.LATCH_COUNTDOWN,
				Constants.SLEEP, 2, 22, 222 ) );

		// Manual Instrument the source file
		instrumentor.instrument( sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test3_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testResult.trim(), testSolution.trim() );
	}

	@Test
	public void testInstrumentValidMethodCall() throws IOException {

		// Set the source file up
		sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test4.txt" ).getPath() ) );
		sources.add( sourceFile );

		// Add package and imports to source file
		sourceFile.setPackageAndImports( packageAndImports );

		// Add the instrumentation points
		sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.SYNCHRONIZE, "methodCall",
				Constants.SLEEP, 1, 11, 111 ) );

		// Manual Instrument the source file
		instrumentor.instrument( sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test4_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testResult.trim(), testSolution.trim() );
	}

	@Test
	public void testInstrumentMethodCallAfterMethodCall() throws IOException {

		// Set the source file up
		sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test5.txt" ).getPath() ) );
		sources.add( sourceFile );

		// Add package and imports to source file
		sourceFile.setPackageAndImports( packageAndImports );

		// Add the instrumentation points
		sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.SYNCHRONIZE, "methodCall",
				Constants.SLEEP, 1, 11, 111 ) );

		// Manual Instrument the source file
		instrumentor.instrument( sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test5_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testResult.trim(), testSolution.trim() );
	}

	@Test
	public void testInstrumentMethodWithinMethod() throws IOException {

		// Set the source file up
		sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test6.txt" ).getPath() ) );
		sources.add( sourceFile );

		// Add package and imports to source file
		sourceFile.setPackageAndImports( packageAndImports );

		// Add the instrumentation points
		sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.SYNCHRONIZE, "methodCall",
				Constants.SLEEP, 1, 11, 111 ) );

		// Manual Instrument the source file
		instrumentor.instrument( sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test6_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testResult.trim(), testSolution.trim() );
	}

	@Test
	public void testInstrumentSynchronizedMethodBlock() throws IOException {

		// Set the source file up
		sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test7.txt" ).getPath() ) );
		sources.add( sourceFile );

		// Add package and imports to source file
		sourceFile.setPackageAndImports( packageAndImports );

		// Add the instrumentation points
		sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.SYNCHRONIZE,
				Constants.SYNCHRONIZE_BLOCK, Constants.SLEEP, 1, 11, 111 ) );

		// Manual Instrument the source file
		instrumentor.instrument( sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test7_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testResult.trim(), testSolution.trim() );
	}

	@Test
	public void testInstrumentMultipleSynchronizedBlockMethodOnSameLine() throws IOException {

		// Set the source file up
		sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test8.txt" ).getPath() ) );
		sources.add( sourceFile );

		// Add package and imports to source file
		sourceFile.setPackageAndImports( packageAndImports );

		// Add the instrumentation points
		sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 0, Constants.SYNCHRONIZE,
				Constants.SYNCHRONIZE_BLOCK, Constants.SLEEP, 1, 11, 111 ) );
		sourceFile.addInterestingPoint( new InstrumentationPoint( 10, 1, Constants.SYNCHRONIZE,
				Constants.SYNCHRONIZE_BLOCK, Constants.SLEEP, 2, 22, 222 ) );

		// Manual Instrument the source file
		instrumentor.instrument( sourceFile, false );

		// Create a string to represent the results and the expected solution
		BufferedReader bufReader = new BufferedReader( new FileReader( sourceFile.getPath().toFile() ) );
		String line = null;
		String testResult = "";
		String testSolution = "";
		while( ( line = bufReader.readLine() ) != null ) {
			testResult = testResult.concat( line + "\n" );
		}

		bufReader = new BufferedReader( new FileReader( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/instrumentor_tests/test8_solution.txt" ) ) );
		while( ( line = bufReader.readLine() ) != null ) {
			testSolution = testSolution.concat( line + "\n" );
		}

		assertEquals( testResult.trim(), testSolution.trim() );
	}
}