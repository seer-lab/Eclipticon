package ca.uoit.eclipticon.test;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.instrumentation.NoiseMaker;

public class NoiseMakerTest extends TestCase {
	
	NoiseMaker noiseMakers = new NoiseMaker();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMakeRandImport() {
		assertEquals( "import java.util.Random;", noiseMakers.makeRandImport() );
	}

	@Test
	public void testMakeRandVariable() {
		assertEquals( "Random _____rand0123456789_____ = new Random();", noiseMakers.makeRandVariable() );
	}

	@Test
	public void testMakeNoiseSleep() {
		assertEquals( "if((_____rand0123456789_____.nextInt(100-0)+0)<=5)try{Thread.sleep((_____rand0123456789_____.nextInt(30-20)+20));}catch(Exception e){};", noiseMakers.makeNoise( 5, 0, 20, 30 ) );
	}
	
	@Test
	public void testMakeNoiseYield() {
		assertEquals( "if((_____rand0123456789_____.nextInt(100-0)+0)<=50)try{Thread.yield();}catch(Exception e){};", noiseMakers.makeNoise( 50, Constants.NOISE_YIELD, 0, 0 ) );
	}
}