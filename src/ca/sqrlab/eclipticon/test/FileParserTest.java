package ca.sqrlab.eclipticon.test;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.sqrlab.eclipticon.Constants;
import ca.sqrlab.eclipticon.data.InterestPoint;
import ca.sqrlab.eclipticon.data.SourceFile;
import ca.sqrlab.eclipticon.parsers.FileParser;
import ca.sqrlab.eclipticon.parsers.PreParser;

public class FileParserTest extends TestCase {

	private SourceFile					_sourceFile			= null;
	private FileParser					_fileParser			= null;
	private ArrayList<InterestPoint>	_interestingPoints	= null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		_fileParser = new FileParser();
	}

	@After
	public void tearDown() throws Exception {
	}

	// @Test
	// public void testGetFiles() {
	// fail( "Not yet implemented" ); // TODO
	// }

	private void compareResults() {
		
		// Compare expected and actual points
		for( int i = 0; i < _interestingPoints.size(); i++ ) {
	
			if( !( _sourceFile.getInterestingPoints().get( i ).getLine() == ( _interestingPoints.get( i ).getLine() ) ) ) {
				fail( "Interest Point " + i + " line mismatch" );
			}
	
			if( !( _sourceFile.getInterestingPoints().get( i ).getSequence() == ( _interestingPoints.get( i )
					.getSequence() ) ) ) {
				fail( "Interest Point " + i + " sequence mismatch" );
			}
	
			if( !_sourceFile.getInterestingPoints().get( i ).getConstruct().equals(
					_interestingPoints.get( i ).getConstruct() ) ) {
				fail( "Interest Point " + i + " construct mismatch" );
			}
	
			if( !_sourceFile.getInterestingPoints().get( i ).getConstructSyntax().equals(
					_interestingPoints.get( i ).getConstructSyntax() ) ) {
				fail( "Interest Point " + i + " construct syntax mismatch" );
			}
		}
	}
	
	@Test
	public void testFindInterestPointSingle() {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test1.txt" ).getPath() ) );

		// Find the interesting points
		_fileParser.findInterestPoints( _sourceFile );

		// Build up expected results
		_interestingPoints = new ArrayList<InterestPoint>();
		// TODO The .await syntax of the Barrier_Await and the Latch_Await capture the same points unfortunately
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.BARRIER, Constants.BARRIER_AWAIT ) );
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.LATCH, Constants.LATCH_AWAIT ) );

		compareResults();

		assertEquals( true, true );
	}
	
	@Test
	public void testFindInterestPointsMulitple() {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test2.txt" ).getPath() ) );

		// Find the interesting points
		_fileParser.findInterestPoints( _sourceFile );

		// Build up expected results
		_interestingPoints = new ArrayList<InterestPoint>();
		// TODO The .await syntax of the Barrier_Await and the Latch_Await capture the same points unfortunately
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.BARRIER, Constants.BARRIER_AWAIT ) );
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.LATCH, Constants.LATCH_AWAIT ) );
		_interestingPoints.add( new InterestPoint( 11, 0, Constants.LATCH, Constants.LATCH_COUNTDOWN ) );

		compareResults();

		assertEquals( true, true );
	}
	
	@Test
	public void testFindInterestPointsMultipleOnSameLine() {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test3.txt" ).getPath() ) );

		// Find the interesting points
		_fileParser.findInterestPoints( _sourceFile );

		// Build up expected results
		_interestingPoints = new ArrayList<InterestPoint>();
		// TODO The .await syntax of the Barrier_Await and the Latch_Await capture the same points unfortunately
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.BARRIER, Constants.BARRIER_AWAIT ) );
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.LATCH, Constants.LATCH_AWAIT ) );
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.LATCH, Constants.LATCH_COUNTDOWN ) );

		compareResults();

		assertEquals( true, true );
	}
	
	@Test
	public void testFindInterestPointsValidMethodCall() {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test4.txt" ).getPath() ) );
		
		// Run preParser
		ArrayList<SourceFile> sources = new ArrayList<SourceFile>();
		sources.add( _sourceFile );
		PreParser preParser = new PreParser();
		preParser.findSynchronizedMethods( sources );

		// Find the interesting points
		_fileParser.findInterestPoints( _sourceFile );

		// Build up expected results
		_interestingPoints = new ArrayList<InterestPoint>();
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.SYNCHRONIZE, "methodCall" ) );

		compareResults();

		assertEquals( true, true );
	}
	
	@Test
	public void testFindInterestPointsMethodCallAfterMethodCall() {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test5.txt" ).getPath() ) );

		// Run preParser
		ArrayList<SourceFile> sources = new ArrayList<SourceFile>();
		sources.add( _sourceFile );
		PreParser preParser = new PreParser();
		preParser.findSynchronizedMethods( sources );

		// Find the interesting points
		_fileParser.findInterestPoints( _sourceFile );

		// Build up expected results
		_interestingPoints = new ArrayList<InterestPoint>();
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.SYNCHRONIZE, "methodCall" ) );

		compareResults();

		assertEquals( true, true );
	}
	
	@Test
	public void testFindInterestPointsMethodWithinMethod() {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test6.txt" ).getPath() ) );

		// Run preParser
		ArrayList<SourceFile> sources = new ArrayList<SourceFile>();
		sources.add( _sourceFile );
		PreParser preParser = new PreParser();
		preParser.findSynchronizedMethods( sources );

		// Find the interesting points
		_fileParser.findInterestPoints( _sourceFile );

		// Build up expected results
		_interestingPoints = new ArrayList<InterestPoint>();
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.SYNCHRONIZE, "methodCall" ) );

		compareResults();

		assertEquals( true, true );
	}
	
	@Test
	public void testFindInterestPointsSynchronizedMethodBlock() {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test7.txt" ).getPath() ) );

		// Find the interesting points
		_fileParser.findInterestPoints( _sourceFile );

		// Build up expected results
		_interestingPoints = new ArrayList<InterestPoint>();
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_BLOCK ) );

		compareResults();

		assertEquals( true, true );
	}
	
	@Test
	public void testFindInterestPointsSynchronizedBlockMethodOnSameLine() {

		// Set the source file up
		_sourceFile = new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test8.txt" ).getPath() ) );

		// Find the interesting points
		_fileParser.findInterestPoints( _sourceFile );

		// Build up expected results
		_interestingPoints = new ArrayList<InterestPoint>();
		_interestingPoints.add( new InterestPoint( 10, 0, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_BLOCK ) );
		_interestingPoints.add( new InterestPoint( 10, 1, Constants.SYNCHRONIZE, Constants.SYNCHRONIZE_BLOCK ) );
		_interestingPoints.add( new InterestPoint( 14, 0, Constants.LATCH, Constants.LATCH_COUNTDOWN ) );

		compareResults();

		assertEquals( true, true );
	}
	
//	@Test
//	public void testCheckIfBackupExists() {
//		fail( "Not yet implemented" ); // TODO
//	}
//
//	@Test
//	public void testManipulateAnnotationDelete() {
//		fail( "Not yet implemented" ); // TODO
//	}
//
//	@Test
//	public void testManipulateAnnotationUpdate() {
//		fail( "Not yet implemented" ); // TODO
//	}
//
//	@Test
//	public void testManipulateAnnotationAdd() {
//		fail( "Not yet implemented" ); // TODO
//	}
}
