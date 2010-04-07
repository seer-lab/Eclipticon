package ca.uoit.eclipticon.test;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ca.uoit.eclipticon.data.InterestPoint;
import ca.uoit.eclipticon.Constants;

public class InterestPointTest extends TestCase {

	private InterestPoint _actual;
	private int _line = 20;
	private int _sequence = 0;
	private String _construct = Constants.SEMAPHORE;
	private String _constructSyntax = Constants.SEMAPHORE_ACQUIRE;
	
	@Before
	public void setUp() throws Exception {
		_actual = new InterestPoint(_line, _sequence, _construct, _constructSyntax);
	}

	@Test
	public void testGetLine() {
		assertEquals(_line, _actual.getLine());
	}

	@Test
	public void testSetLine() {
		_actual.setLine(505);
		int testLine = _actual.getLine();
		assertEquals(505, testLine);
	}

	@Test
	public void testGetSequence() {
		assertEquals(_sequence, _actual.getSequence());
	}

	@Test
	public void testSetSequence() {
		_actual.setSequence(4);
		int testSequence = _actual.getSequence();
		assertEquals(4, testSequence);
	}

	@Test
	public void testGetConstruct() {
		assertEquals(_construct, Constants.SEMAPHORE);
	}

	@Test
	public void testSetConstruct() {
		_actual.setConstruct(Constants.BARRIER);
		assertEquals(Constants.BARRIER, _actual.getConstruct());
	}

	@Test
	public void testGetConstructSyntax() {
		assertEquals(_constructSyntax, Constants.SEMAPHORE_ACQUIRE);
	}

	@Test
	public void testSetConstructSyntax() {
		_actual.setConstructSyntax(Constants.BARRIER_AWAIT);
		assertEquals(Constants.BARRIER_AWAIT, _actual.getConstructSyntax());
	}
}