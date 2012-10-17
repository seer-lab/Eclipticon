package ca.sqrlab.eclipticon.test;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.sqrlab.eclipticon.data.AutomaticConfiguration;

public class AutomaticConfigurationTest extends TestCase {

	private AutomaticConfiguration _autoConfig = new AutomaticConfiguration();
	private int _low = 99;
	private int _high = 888;
	private int _probability = 77;
	private int _newProbability = 66;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		_autoConfig.setLowDelayRange(_low);
		_autoConfig.setHighDelayRange(_high);
		_autoConfig.setBarrierProbability(_probability);
		_autoConfig.setLatchProbability(_probability);
		_autoConfig.setSemaphoreProbability(_probability);
		_autoConfig.setSleepProbability(_probability);
		_autoConfig.setSynchronizeProbability(_probability);
		_autoConfig.setYieldProbability(_probability);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetLowDelayRange() {
		assertEquals(_low, _autoConfig.getLowDelayRange());
	}

	@Test
	public void testSetLowDelayRange() {
		_low = 55;
		_autoConfig.setLowDelayRange(_low);
		assertEquals(_low, _autoConfig.getLowDelayRange());
	}

	@Test
	public void testGetHighDelayRange() {
		assertEquals(_high, _autoConfig.getHighDelayRange());
	}

	@Test
	public void testSetHighDelayRange() {
		_high = 932;
		_autoConfig.setHighDelayRange(_high);
		assertEquals(_high, _autoConfig.getHighDelayRange());
	}

	@Test
	public void testGetSleepProbability() {
		assertEquals(_probability, _autoConfig.getSleepProbability());
	}

	@Test
	public void testSetSleepProbability() {
		_autoConfig.setSleepProbability(_newProbability);
		assertEquals(_newProbability, _autoConfig.getSleepProbability());
	}

	@Test
	public void testGetYieldProbability() {
		assertEquals(_probability, _autoConfig.getYieldProbability());
	}

	@Test
	public void testSetYieldProbability() {
		_autoConfig.setYieldProbability(_newProbability);
		assertEquals(_newProbability, _autoConfig.getYieldProbability());
	}

	@Test
	public void testGetSynchronizeProbability() {
		assertEquals(_probability, _autoConfig.getSynchronizeProbability());
	}

	@Test
	public void testSetSynchronizeProbability() {
		_autoConfig.setSynchronizeProbability(_newProbability);
		assertEquals(_newProbability, _autoConfig.getSynchronizeProbability());
	}

	@Test
	public void testGetBarrierProbability() {
		assertEquals(_probability, _autoConfig.getBarrierProbability());
	}

	@Test
	public void testSetBarrierProbability() {
		_autoConfig.setBarrierProbability(_newProbability);
		assertEquals(_newProbability, _autoConfig.getBarrierProbability());
	}

	@Test
	public void testGetLatchProbability() {
		assertEquals(_probability, _autoConfig.getLatchProbability());
	}

	@Test
	public void testSetLatchProbability() {
		_autoConfig.setLatchProbability(_newProbability);
		assertEquals(_newProbability, _autoConfig.getLatchProbability());
	}

	@Test
	public void testGetSemaphoreProbability() {
		assertEquals(_probability, _autoConfig.getSemaphoreProbability());
	}

	@Test
	public void testSetSemaphoreProbability() {
		_autoConfig.setSemaphoreProbability(_newProbability);
		assertEquals(_newProbability, _autoConfig.getSemaphoreProbability());
	}
}