package ca.uoit.eclipticon.data;

/**
 * This data class represents interest points in a source file, an interest point
 * is a location that is associated with a synchronization construct. These constructs
 * are defined in the Constants class.
 * <p>
 * An interest point is composed of the line number in the source file, the construct of what synchronization it is
 * associated with and finally the instance number. The instance number represents the relative position of this
 * interest point to others in the same source line (first interest point is 0, the second one found is 1, etc...)
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class InterestPoint {

	private int		_line		= 0;	// The line number this point is located at
	private int		_instance	= 0;	// The instance number of in respect to the other interest points on this line
	private String	_construct	= null; // The synchronization construct type of this interest point

	/**
	 * Constructor for instantiating an interest point that will set the variables for it.
	 * 
	 * @param line the line number
	 * @param instance the instance number location
	 * @param construct the constructor type
	 */
	public InterestPoint( int line, int instance, String construct ) {
		_line = line;
		_instance = instance;
		_construct = construct;
	}

	/**
	 * Gets the line number location of the of the interest point.
	 * 
	 * @return the line number
	 */
	public int getLine() {
		return _line;
	}

	/**
	 * Sets the line number location of the interest point.
	 * 
	 * @param line the line number
	 */
	public void setLine( int line ) {
		_line = line;
	}

	/**
	 * Gets the instance location number of the interest point.
	 * 
	 * @return the instance location number
	 */
	public int getInstance() {
		return _instance;
	}

	/**
	 * Sets the instance number location of the instrumentation point.
	 * 
	 * @param instance the instance location number
	 */
	public void setInstance( int instance ) {
		_instance = instance;
	}

	/**
	 * Gets the synchronization construct of the interest point.
	 * 
	 * @return the synchronization construct
	 */
	public String getConstruct() {
		return _construct;
	}

	/**
	 * Sets the synchronization construct of the interest point.
	 * 
	 * @param construct the synchronization construct
	 */
	public void setConstruct( String construct ) {
		_construct = construct;
	}
}