package ca.uoit.eclipticon.data;

/**
 * A data class that is used to keep track of the automatic instrumentation
 * configurations. This data is intended to be read and written using the
 * XStream serialization library. All the chance fields are out of 100.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class AutomaticConfiguration {

	// Data fields
	private int	lowDelayRange		= 0;	// The low delay range for the sleep noise
	private int	highDelayRange		= 0;	// The high delay range for the sleep noise
	private int	sleepChance			= 0;	// The chance of a noise being sleep (out of 100)
	private int	yieldChance			= 0;	// The chance of a noise being yield (out of 100)
	private int	instrumentChance	= 0;	// The chance of noise being activated during execution (out of 100)
	private int	synchronizeChance	= 0;	// The chance of a synchronize being instrumented (out of 100)
	private int	barrierChance		= 0;	// The chance of a barrier being instrumented (out of 100)
	private int	latchChance			= 0;	// The chance of a latch being instrumented (out of 100)
	private int	semaphoreChance		= 0;	// The chance of a semaphore being instrumented (out of 100)

	/**
	 * Gets the low delay range.
	 * 
	 * @return the low delay range
	 */
	public int getLowDelayRange() {
		return lowDelayRange;
	}

	/**
	 * Sets the low delay range.
	 * 
	 * @param lowDelayRange the low delay range
	 */
	public void setLowDelayRange( int lowDelayRange ) {
		this.lowDelayRange = lowDelayRange;
	}

	/**
	 * Gets the high delay range.
	 * 
	 * @return the high delay range
	 */
	public int getHighDelayRange() {
		return highDelayRange;
	}

	/**
	 * Sets the high delay range.
	 * 
	 * @param highDelayRange the high delay range
	 */
	public void setHighDelayRange( int highDelayRange ) {
		this.highDelayRange = highDelayRange;
	}

	/**
	 * Gets the sleep chance.
	 * 
	 * @return the sleep chance
	 */
	public int getSleepChance() {
		return sleepChance;
	}

	/**
	 * Sets the sleep chance.
	 * 
	 * @param sleepChance the sleep chance
	 */
	public void setSleepChance( int sleepChance ) {
		this.sleepChance = sleepChance;
	}

	/**
	 * Gets the yield chance.
	 * 
	 * @return the yield chance
	 */
	public int getYieldChance() {
		return yieldChance;
	}

	/**
	 * Sets the yield chance.
	 * 
	 * @param yieldChance the yield chance
	 */
	public void setYieldChance( int yieldChance ) {
		this.yieldChance = yieldChance;
	}

	/**
	 * Gets the instrument chance.
	 * 
	 * @return the instrument chance
	 */
	public int getInstrumentChance() {
		return instrumentChance;
	}

	/**
	 * Sets the instrument chance.
	 * 
	 * @param instrumentChance the instrument chance
	 */
	public void setInstrumentChance( int instrumentChance ) {
		this.instrumentChance = instrumentChance;
	}

	/**
	 * Gets the synchronize chance.
	 * 
	 * @return the synchronize chance
	 */
	public int getSynchronizeChance() {
		return synchronizeChance;
	}

	/**
	 * Sets the synchronize chance.
	 * 
	 * @param synchronizeChance the synchronize chance
	 */
	public void setSynchronizeChance( int synchronizeChance ) {
		this.synchronizeChance = synchronizeChance;
	}

	/**
	 * Gets the barrier chance.
	 * 
	 * @return the barrier chance
	 */
	public int getBarrierChance() {
		return barrierChance;
	}

	/**
	 * Sets the barrier chance.
	 * 
	 * @param barrierChance the barrier chance
	 */
	public void setBarrierChance( int barrierChance ) {
		this.barrierChance = barrierChance;
	}

	/**
	 * Gets the latch chance.
	 * 
	 * @return the latch chance
	 */
	public int getLatchChance() {
		return latchChance;
	}

	/**
	 * Sets the latch chance.
	 * 
	 * @param latchChance the latch chance
	 */
	public void setLatchChance( int latchChance ) {
		this.latchChance = latchChance;
	}

	/**
	 * Gets the semaphore chance.
	 * 
	 * @return the semaphore chance
	 */
	public int getSemaphoreChance() {
		return semaphoreChance;
	}

	/**
	 * Sets the semaphore chance.
	 * 
	 * @param semaphoreChance the semaphore chance
	 */
	public void setSemaphoreChance( int semaphoreChance ) {
		this.semaphoreChance = semaphoreChance;
	}
}