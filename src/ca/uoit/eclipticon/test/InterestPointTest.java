package ca.uoit.eclipticon.test;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ca.uoit.eclipticon.data.InterestPoint;
import ca.uoit.eclipticon.Constants;

public class InterestPointTest extends TestCase {

	private InterestPoint actual;
	
	int line = 20;
	int instance = 0;
	String construct = Constants.SEMAPHORE;
	String constructSyntax = Constants.SEMAPHORE_ACQUIRE;
	
	@Before
	public void setUp() throws Exception {
		actual = new InterestPoint(line, instance, construct, constructSyntax);
		/// public InterestPoint( int line, int sequence, String construct, String constructSyntax ) {
	}

	@Test
	public void testGetLine() {
		assertEquals(line, actual.getLine());
	}

	@Test
	public void testSetLine() {
		actual.setLine(505);
		int testLine = actual.getLine();
		assertEquals(505, testLine);
	}

	@Test
	public void testGetSequence() {
		assertEquals(instance, actual.getSequence());
	}

	@Test
	public void testSetSequence() {
		actual.setSequence(4);
		int testSequence = actual.getSequence();
		assertEquals(4, testSequence);
	}

	@Test
	public void testGetConstruct() {
		assertEquals(construct, Constants.SEMAPHORE);
	}

	@Test
	public void testSetConstruct() {
		actual.setConstruct(Constants.BARRIER);
		assertEquals(Constants.BARRIER, actual.getConstruct());
	}

	@Test
	public void testGetConstructSyntax() {
		assertEquals(constructSyntax, Constants.SEMAPHORE_ACQUIRE);
	}

	@Test
	public void testSetConstructSyntax() {
		actual.setConstructSyntax(Constants.BARRIER_AWAIT);
		assertEquals(Constants.BARRIER_AWAIT, actual.getConstructSyntax());
	}

}
