package ca.uoit.eclipticon.test;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.Constants;

public class InstrumentationPointTest extends TestCase {
	
	private InstrumentationPoint _actual;
	
	private int _line = 20;
	private int _instance = 0;
	private String _construct = Constants.SEMAPHORE;
	private String _constructSyntax = Constants.SEMAPHORE_ACQUIRE;
	private int _type = Constants.NOISE_SLEEP;
	private int _prob = 80;
	private int _low = 1;
	private int _high = 500;
	
	@Before public void setUp() {
		_actual = new InstrumentationPoint(_line, _instance, _construct, _constructSyntax, _type, _prob, _low, _high);
	}
	
	@Test public void testGetLine() {
		assertEquals(_line, _actual.getLine());
	}
	
	@Test public void testGetSequence() {
		assertEquals(_instance, _actual.getSequence());
	}

	@Test public void testGetConstruct() {
		assertEquals(_construct, _actual.getConstruct());
	}
	
	@Test public void testGetConstructSyntax() {
		assertEquals(_constructSyntax, _actual.getConstructSyntax());
	}
	
	@Test public void testGetType() {
		assertEquals(_type, _actual.getType());
	}
	
	@Test public void testGetProbability() {
		assertEquals(_prob, _actual.getProbability());
	}
	
	@Test public void testGetLow() {
		assertEquals(_low, _actual.getLow());
	}
	
	@Test public void testGetHigh() {
		assertEquals(_high, _actual.getHigh());
	}
}