package ca.uoit.eclipticon.data;

/**
 * This data class is used to hold information for an instrumentation point. A point
 * will contain various pieces of information such as the type, probability and delay values
 * for the instrumentation when it is applied. An instrumentation point extends {@link InterestPoint} 
 * and thus has those general pieces of information regarding location in the source.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class InstrumentationPoint extends InterestPoint {

	private int	_type			= 0;	// The type of instrumentation point (0 = sleep, 1 = yield)
	private int	_probability	= 0;	// The probability of an instrumentation point in activating out of 100
	private int	_low			= 0;	// The low delay range of the instrumentation point
	private int	_high			= 0;	// The high delay range of the instrumentation point

	/**
	 * Constructor for instantiating an instrumentation point that will set the variables for it.
	 * 
	 * @param line the line number 
	 * @param instance the instance number location
	 * @param construct the construct type
	 * @param type the instrumentation type
	 * @param prob the probability of the instrumentation point
	 * @param low the low delay range
	 * @param high the high delay range
	 */
	public InstrumentationPoint( int line, int instance, String construct, int type, int prob, int low, int high ) {
		super( line, instance, construct );
		_type = type;
		_probability = prob;
		_low = low;
		_high = high;
	}

	/**
	 * Gets the type of the instrumentation point.
	 * 
	 * @return the type of the instrumentation point
	 */
	public int getType() {
		return _type;
	}

	/**
	 * Sets the type of the instrumentation point.
	 * 
	 * @param type the type of the instrumentation point
	 */
	public void setType( int type ) {
		_type = type;
	}

	/**
	 * Gets the probability that this instrumentation point will activate during execution.
	 * 
	 * @return the probability of the instrumentation point
	 */
	public int getProbability() {
		return _probability;
	}

	/**
	 * Sets the probability that this instrumentation point will activate during execution.
	 * 
	 * @param probability the probability of the instrumentation point
	 */
	public void setProbability( int probability ) {
		_probability = probability;
	}

	/**
	 * Gets the low delay range of the instrumentation point.
	 * 
	 * @return the low delay range of the instrumentation point
	 */
	public int getLow() {
		return _low;
	}

	/**
	 * Sets the low delay range of the instrumentation point.
	 * 
	 * @param low the low delay range of the instrumentation point
	 */
	public void setLow( int low ) {
		_low = low;
	}

	/**
	 * Gets the high delay range of the instrumentation point.
	 * 
	 * @return the high delay range of the instrumentation point
	 */
	public int getHigh() {
		return _high;
	}

	/**
	 * Sets the high delay range of the instrumentation point.
	 * 
	 * @param high the high delay range of the instrumentation point
	 */
	public void setHigh( int high ) {
		_high = high;
	}
}