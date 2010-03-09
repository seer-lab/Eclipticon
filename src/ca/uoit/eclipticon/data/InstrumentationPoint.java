package ca.uoit.eclipticon.data;

/**
 * This data class is used to hold information for an instrumentation point. A point
 * will contain various pieces of information which details the location and type
 * of instrumentation to be performed. This data is intended to be read and 
 * written using the XStream serialization library.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class InstrumentationPoint {

	// The Data
	private int		_id			= 0;	// A numerical ID of the instrumentation point
	private String	_source		= null; // The path to the sourcefFile
	private int		_line		= 0;	// The line number the point is located at
	private int		_character	= 0;	// Number of characters in to the line it is set
	private int		_type		= 0;	// The type of instrumentation point (0 = sleep, 1 = yield)
	private int		_chance		= 0;	// The chance of an instrumentation point in activating out of 100
	private int		_low		= 0;	// The low delay range of the instrumentation point
	private int		_high		= 0;	// The high delay range of the instrumentation point

	/**
	 * Gets the id of the instrumentation point.
	 * 
	 * @return the id
	 */
	public int getId() {
		return _id;
	}

	/**
	 * Sets the id of the instrumentation point.
	 * 
	 * @param id the id
	 */
	public void setId( int id ) {
		_id = id;
	}

	/**
	 * Gets the source file of the instrumentation point.
	 * 
	 * @return the source
	 */
	public String getSource() {
		return _source;
	}

	/**
	 * Sets the source file of the instrumentation point.
	 * 
	 * @param source the source
	 */
	public void setSource( String source ) {
		_source = source;
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
	 * Gets the character number location of the instrumentation point.
	 * 
	 * @return the char
	 */
	public int getCharacter() {
		return _character;
	}

	/**
	 * Sets the character number location of the instrumentation point.
	 * 
	 * @param character the character
	 */
	public void setCharacter( int character ) {
		_character = character;
	}

	/**
	 * Gets the type of the instrumentation point.
	 * 
	 * @return the type
	 */
	public int getType() {
		return _type;
	}

	/**
	 * Sets the type of the instrumentation point.
	 * 
	 * @param type the type
	 */
	public void setType( int type ) {
		_type = type;
	}

	/**
	 * Gets the chance that this instrumentation point will activate during execution.
	 * 
	 * @return the chance
	 */
	public int getChance() {
		return _chance;
	}

	/**
	 * Sets the chance that this instrumentation point will activate during execution.
	 * 
	 * @param chance the chance
	 */
	public void setChance( int chance ) {
		_chance = chance;
	}

	/**
	 * Gets the low delay range of the instrumentation point.
	 * 
	 * @return the low
	 */
	public int getLow() {
		return _low;
	}

	/**
	 * Sets the low delay range of the instrumentation point.
	 * 
	 * @param low the low
	 */
	public void setLow( int low ) {
		_low = low;
	}

	/**
	 * Gets the high delay range of the instrumentation point.
	 * 
	 * @return the high
	 */
	public int getHigh() {
		return _high;
	}

	/**
	 * Sets the high delay range of the instrumentation point.
	 * 
	 * @param high the high
	 */
	public void setHigh( int high ) {
		_high = high;
	}
}