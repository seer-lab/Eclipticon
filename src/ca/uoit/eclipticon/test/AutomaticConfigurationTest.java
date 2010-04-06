package ca.uoit.eclipticon.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

import ca.uoit.eclipticon.data.AutomaticConfiguration;

public class AutomaticConfigurationTest extends TestCase {

	
	private AutomaticConfiguration ac = new AutomaticConfiguration();
	int low = 99;
	int high = 888;
	int probability = 77;
	int newprobability = 66;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ac.setLowDelayRange(low);
		ac.setHighDelayRange(high);
		ac.setBarrierProbability(probability);
		ac.setLatchProbability(probability);
		ac.setSemaphoreProbability(probability);
		ac.setSleepProbability(probability);
		ac.setSynchronizeProbability(probability);
		ac.setYieldProbability(probability);
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testGetLowDelayRange() {
		assertEquals(low, ac.getLowDelayRange());
	}

	@Test
	public void testSetLowDelayRange() {
		low = 55;
		ac.setLowDelayRange(low);
		assertEquals(low, ac.getLowDelayRange());
	}

	@Test
	public void testGetHighDelayRange() {
		assertEquals(high, ac.getHighDelayRange());
	}

	@Test
	public void testSetHighDelayRange() {
		high = 932;
		ac.setHighDelayRange(high);
		assertEquals(high, ac.getHighDelayRange());
	}

	@Test
	public void testGetSleepProbability() {
		assertEquals(probability, ac.getSleepProbability());
	}

	@Test
	public void testSetSleepProbability() {
		ac.setSleepProbability(newprobability);
		assertEquals(newprobability, ac.getSleepProbability());
	}

	@Test
	public void testGetYieldProbability() {
		assertEquals(probability, ac.getYieldProbability());
	}

	@Test
	public void testSetYieldProbability() {
		ac.setYieldProbability(newprobability);
		assertEquals(newprobability, ac.getYieldProbability());
	}

	@Test
	public void testGetSynchronizeProbability() {
		assertEquals(probability, ac.getSynchronizeProbability());
	}

	@Test
	public void testSetSynchronizeProbability() {
		ac.setSynchronizeProbability(newprobability);
		assertEquals(newprobability, ac.getSynchronizeProbability());
	}

	@Test
	public void testGetBarrierProbability() {
		assertEquals(probability, ac.getBarrierProbability());
	}

	@Test
	public void testSetBarrierProbability() {
		ac.setBarrierProbability(newprobability);
		assertEquals(newprobability, ac.getBarrierProbability());
	}

	@Test
	public void testGetLatchProbability() {
		assertEquals(probability, ac.getLatchProbability());
	}

	@Test
	public void testSetLatchProbability() {
		ac.setLatchProbability(newprobability);
		assertEquals(newprobability, ac.getLatchProbability());
	}

	@Test
	public void testGetSemaphoreProbability() {
		assertEquals(probability, ac.getSemaphoreProbability());
	}

	@Test
	public void testSetSemaphoreProbability() {
		ac.setSemaphoreProbability(newprobability);
		assertEquals(newprobability, ac.getSemaphoreProbability());
	}

}
