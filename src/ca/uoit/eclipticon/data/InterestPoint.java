package ca.uoit.eclipticon.data;

/**
 * This data class is used to hold information for an instrumentation point. A point
 * will contain various pieces of information which details the location and construct
 * of instrumentation to be performed. This data is intended to be read and 
 * written using the XStream serialization library.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class InterestPoint {

	// The Data
	private int	_line		= 0;	// The line number the point is located at
	private int	_instance	= 0;	// Number of instances in to the line it is set
	private String	_construct		= null;	// The construct of instrumentation point (0 = sleep, 1 = yield)
	

	public InterestPoint( int line, int instance, String construct ) {
		_line = line; // The line number the point is located at
		_instance = instance; // Number of instances in to the line it is set
		_construct = construct; // The construct of instrumentation point (0 = sleep, 1 = yield)
	}

	/**
	 * Gets the line number location of the of the instrumentation point.
	 * 
	 * @return the line
	 */
	public int getLine() {
		return _line;
	}

	/**
	 * Sets the line number location of the instrumentation point.
	 * 
	 * @param line the line
	 */
	public void setLine( int line ) {
		_line = line;
	}

	/**
	 * Gets the instance number location of the instrumentation point.
	 * 
	 * @return the char
	 */
	public int getInstance() {
		return _instance;
	}

	/**
	 * Sets the instance number location of the instrumentation point.
	 * 
	 * @param instance the instance
	 */
	public void setInstance( int instance ) {
		_instance = instance;
	}

	/**
	 * Gets the construct of the instrumentation point.
	 * 
	 * @return the construct
	 */
	public String getConstruct() {
		return _construct;
	}

	/**
	 * Sets the construct of the instrumentation point.
	 * 
	 * @param construct the construct
	 */
	public void setConstruct( String construct ) {
		_construct = construct;
	}

	
}