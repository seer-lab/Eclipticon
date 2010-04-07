package ca.uoit.eclipticon.test;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.uoit.eclipticon.data.SourceFile;
import ca.uoit.eclipticon.parsers.PreParser;
import ca.uoit.eclipticon.parsers.PreParser.SynchronizedMethods;

public class PreParserTest extends TestCase {

	private ArrayList<SourceFile>			_sources			= new ArrayList<SourceFile>();
	private PreParser						_preParser			= new PreParser();
	private ArrayList<SynchronizedMethods>	_actualSyncMethods	= null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		_actualSyncMethods = new ArrayList<SynchronizedMethods>();
	}

	@After
	public void tearDown() throws Exception {
	}

	private void compareResults() {
		
		// Compare expected and actual synchronized methods
		for( int i = 0; i < _actualSyncMethods.size(); i++ ) {
			if( !( _preParser.getSynchronizedMethods().get( i ).getFilePath().equals( ( _actualSyncMethods.get( i ).getFilePath() ) ) ) ) {
				fail( "Synchronized Method " + i + " file name mismatch" );
			}
	
			if( !( _preParser.getSynchronizedMethods().get( i ).getName().equals( ( _actualSyncMethods.get( i ).getName() ) ) ) ) {
				fail( "Synchronized Method " + i + " method name mismatch" );
			}
		}
	}
	
	@Test
	public void testFindSynchronizedMethodNone() {
		
		// Set the source file up
		_sources.add( new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test1.txt" ).getPath() ) ) );
		
		// Find the synchronized methods
		_preParser.findSynchronizedMethods( _sources );
		
		assertEquals( 0, _preParser.getSynchronizedMethods().size() );
	}

	@Test
	public void testFindSynchronizedMethodSingle() {
		
		// Set the source file up
		_sources.add( new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test4.txt" ).getPath() ) ) );
		
		// Find the synchronized methods
		_preParser.findSynchronizedMethods( _sources );

		// Create expected results
		_actualSyncMethods.add( _preParser.new SynchronizedMethods( "methodCall", new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test4.txt" ).getPath() )));
		
		// Compare actual vs expected
		compareResults();
	}
	
	@Test
	public void testFindSynchronizedMethodMultiple() {
		
		// Set the source file up
		_sources.add( new SourceFile( new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test6.txt" ).getPath() ) ) );
		
		// Find the synchronized methods
		_preParser.findSynchronizedMethods( _sources );

		// Create expected results
		_actualSyncMethods.add( _preParser.new SynchronizedMethods( "methodCall", new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test6.txt" ).getPath() )));
		_actualSyncMethods.add( _preParser.new SynchronizedMethods( "methodCall2", new Path( new File( System.getProperty( "user.dir" )
				+ "/src/ca/uoit/eclipticon/test/testfiles/test6.txt" ).getPath() )));
		
		// Compare actual vs expected
		compareResults();
	}
}