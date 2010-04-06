package ca.uoit.eclipticon.test;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ca.uoit.eclipticon.data.SourceFile;
import ca.uoit.eclipticon.Constants;

public class SourceFileTest extends TestCase {

	// TODO Finish this class.

	private SourceFile global;
	String path = "C:\\Users\\Chris\\workspace\\eclipticon\\src\\ca\\uoit\\eclipticon\\test\\SourceFileTest.java";
	String name = "SourceFileTest.java";
	String imports = "package ca.uoit.eclipticon.test;\n\nimport junit.framework.TestCase;\nimport org.eclipse.core.runtime.Path;\nimport org.junit.After;\nimport org.junit.AfterClass;\nimport org.junit.Before;\nimport org.junit.BeforeClass;\nimport org.junit.Test;\nimport ca.uoit.eclipticon.data.SourceFile;\nimport ca.uoit.eclipticon.Constants;";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Path p = new Path();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSourceFile() {
		SourceFile file = new SourceFile(path);
	}

	@Test
	public void testSetPath() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPath() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetName() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddInterestingPoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddInterestingPoints() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInterestingPoints() {
		fail("Not yet implemented");
	}

	@Test
	public void testClearInterestingPoints() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrintIP() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPackageAndImports() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPackageAndImports() {
		fail("Not yet implemented");
	}

	@Test
	public void testClearPackageAndImports() {
		fail("Not yet implemented");
	}

}
