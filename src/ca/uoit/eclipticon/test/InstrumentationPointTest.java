package test;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import data.InstrumentationPoint;

public class InstrumentationPointTest extends TestCase {
	
	private InstrumentationPoint actual;
	
	@Before public void setUp() {
		actual = new InstrumentationPoint();
		actual.setId(0);
		actual.setChar(5);
		actual.setHigh(500);
		actual.setLow(1);
		actual.setLine(20);
		actual.setSource("test.java");
		actual.setType(2);
	}
	
	@Test public void testGetChar() {
		assertEquals(5, actual.getChar());
	}

	@Test public void testGetHigh() {
		assertEquals(500, actual.getHigh());
	}

	@Test public void testGetId() {
		assertEquals(0, actual.getId());
	}

	@Test public void testGetLine() {
		assertEquals(20, actual.getLine());
	}

	@Test public void testGetLow() {
		assertEquals(1, actual.getLow());
	}

	@Test public void testGetSource() {
		assertEquals("test.java", actual.getSource());
	}

	@Test public void testGetType() {
		assertEquals(2, actual.getType());
	}
}
