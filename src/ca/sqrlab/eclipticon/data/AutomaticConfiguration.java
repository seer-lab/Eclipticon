package ca.sqrlab.eclipticon.data;

/**
 * A data class that is used to keep track of the automatic instrumentation
 * configurations. This data is intended to be read and written using the
 * XStream serialization library. All the probability fields are out of 100.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class AutomaticConfiguration {

	// Data fields
	private int	_lowDelayRange			= 0;	// The low delay range for the sleep noise
	private int	_highDelayRange			= 0;	// The high delay range for the sleep noise
	private int	_sleepProbability		= 0;	// The probability of a noise being sleep (out of 100)
	private int	_yieldProbability		= 0;	// The probability of a noise being yield (out of 100)
	private int	_synchronizeProbability	= 0;	// The probability of a synchronize being instrumented (out of 100)
	private int	_barrierProbability		= 0;	// The probability of a barrier being instrumented (out of 100)
	private int	_latchProbability		= 0;	// The probability of a latch being instrumented (out of 100)
	private int	_semaphoreProbability	= 0;	// The probability of a semaphore being instrumented (out of 100)

	/**
	 * Gets the low delay range.
	 * 
	 * @return the low delay range
	 */
	public int getLowDelayRange() {
		return _lowDelayRange;
	}

	/**
	 * Sets the low delay range.
	 * 
	 * @param lowDelayRange the low delay range
	 */
	public void setLowDelayRange( int lowDelayRange ) {
		this._lowDelayRange = lowDelayRange;
	}

	/**
	 * Gets the high delay range.
	 * 
	 * @return the high delay range
	 */
	public int getHighDelayRange() {
		return _highDelayRange;
	}

	/**
	 * Sets the high delay range.
	 * 
	 * @param highDelayRange the high delay range
	 */
	public void setHighDelayRange( int highDelayRange ) {
		this._highDelayRange = highDelayRange;
	}

	/**
	 * Gets the sleep probability.
	 * 
	 * @return the sleep probability
	 */
	public int getSleepProbability() {
		return _sleepProbability;
	}

	/**
	 * Sets the sleep probability.
	 * 
	 * @param sleepProbability the sleep probability
	 */
	public void setSleepProbability( int sleepProbability ) {
		this._sleepProbability = sleepProbability;
	}

	/**
	 * Gets the yield probability.
	 * 
	 * @return the yield probability
	 */
	public int getYieldProbability() {
		return _yieldProbability;
	}

	/**
	 * Sets the yield probability.
	 * 
	 * @param yieldProbability the yield probability
	 */
	public void setYieldProbability( int yieldProbability ) {
		this._yieldProbability = yieldProbability;
	}

	/**
	 * Gets the synchronize probability.
	 * 
	 * @return the synchronize probability
	 */
	public int getSynchronizeProbability() {
		return _synchronizeProbability;
	}

	/**
	 * Sets the synchronize probability.
	 * 
	 * @param synchronizeProbability the synchronize probability
	 */
	public void setSynchronizeProbability( int synchronizeProbability ) {
		this._synchronizeProbability = synchronizeProbability;
	}

	/**
	 * Gets the barrier probability.
	 * 
	 * @return the barrier probability
	 */
	public int getBarrierProbability() {
		return _barrierProbability;
	}

	/**
	 * Sets the barrier probability.
	 * 
	 * @param barrierProbability the barrier probability
	 */
	public void setBarrierProbability( int barrierProbability ) {
		this._barrierProbability = barrierProbability;
	}

	/**
	 * Gets the latch probability.
	 * 
	 * @return the latch probability
	 */
	public int getLatchProbability() {
		return _latchProbability;
	}

	/**
	 * Sets the latch probability.
	 * 
	 * @param latchProbability the latch probability
	 */
	public void setLatchProbability( int latchProbability ) {
		this._latchProbability = latchProbability;
	}

	/**
	 * Gets the semaphore probability.
	 * 
	 * @return the semaphore probability
	 */
	public int getSemaphoreProbability() {
		return _semaphoreProbability;
	}

	/**
	 * Sets the semaphore probability.
	 * 
	 * @param semaphoreProbability the semaphore probability
	 */
	public void setSemaphoreProbability( int semaphoreProbability ) {
		this._semaphoreProbability = semaphoreProbability;
	}
}