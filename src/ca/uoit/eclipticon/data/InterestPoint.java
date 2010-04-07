package ca.uoit.eclipticon.data;

/**
 * This data class represents interest points in a source file, an interest point
 * is a location that is associated with a synchronization construct. These constructs
 * are defined in the Constants class.
 * <p>
 * An interest point is composed of the line number in the source file, the construct of what synchronization it is
 * associated with and finally the sequence number. The sequence number represents the relative position of this
 * interest point to others (same syntax) in the same source line (first interest point is 0, the second one found is 1, etc...)
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class InterestPoint {

	private int		_line				= 0;	// The line number this point is located at
	private int		_sequence			= 0;	// The sequence number of the interest points on this line
	private String	_construct			= null; // The synchronization construct type of this interest point
	private String	_constructSyntax	= null; // The synchronization construct type of this interest point

	/**
	 * Constructor for instantiating an interest point that will set the variables for it.
	 * 
	 * @param line the line number
	 * @param sequence the sequence number location
	 * @param construct the synchronization construct type
	 * @param constructSyntax the synchronization construct syntax
	 */
	public InterestPoint( int line, int sequence, String construct, String constructSyntax ) {
		_line = line;
		_sequence = sequence;
		_construct = construct;
		_constructSyntax = constructSyntax;
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
	 * Gets the sequence location number of the interest point.
	 * 
	 * @return the sequence location number
	 */
	public int getSequence() {
		return _sequence;
	}

	/**
	 * Sets the sequence number location of the instrumentation point.
	 * 
	 * @param sequence the instance location number
	 */
	public void setSequence( int sequence ) {
		_sequence = sequence;
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

	/**
	 * Gets the synchronization construct syntax of the interest point.
	 * 
	 * @return the synchronization construct syntax
	 */
	public String getConstructSyntax() {
		return _constructSyntax;
	}

	/**
	 * Sets the synchronization construct syntax of this interesting point.
	 * 
	 * @param constructSyntax
	 */
	public void setConstructSyntax( String constructSyntax ) {
		_constructSyntax = constructSyntax;
	}
}