package ca.uoit.eclipticon.test;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.Constants;

public class InstrumentationPointTest extends TestCase {
	
	private InstrumentationPoint actual;
	
	int line = 20;
	int instance = 0;
	String construct = Constants.SEMAPHORE;
	String constructSyntax = Constants.SEMAPHORE_ACQUIRE;
	int type = 0;
	int prob = 80;
	int low = 1;
	int high = 500;
	
	@Before public void setUp() {
		
		actual = new InstrumentationPoint(line, instance, construct, constructSyntax, type, prob, low, high);
		//InstrumentationPoint( int line, int instance, String construct, int type, int prob, int low, int high )
	}
	
	@Test public void testGetLine() {
		assertEquals(line, actual.getLine());
	}
	
	@Test public void testGetSequence() {
		assertEquals(instance, actual.getSequence());
	}

	@Test public void testGetConstruct() {
		assertEquals(construct, actual.getConstruct());
	}
	
	@Test public void testGetConstructSyntax() {
		assertEquals(constructSyntax, actual.getConstructSyntax());
	}
	
	@Test public void testGetType() {
		assertEquals(type, actual.getType());
	}
	
	@Test public void testGetProbability() {
		assertEquals(prob, actual.getProbability());
	}
	
	@Test public void testGetLow() {
		assertEquals(low, actual.getLow());
	}
	
	@Test public void testGetHigh() {
		assertEquals(high, actual.getHigh());
	}
	
}
