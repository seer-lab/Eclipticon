package ca.uoit.eclipticon.data;
/***********************************************************************************************************
 ********************  ____  ____  _     _____  ____  _____  _____  ____  ____  ____  **********************
 ******************** |  __||  __|| |   |_   _||  . ||_   _||_   _||  __||    ||  | | **********************
 ******************** |  __|| |__ | |__  _| |_ |  __|  | |   _| |_ | |__ | || || || | **********************
 ******************** |____||____||____||_____||_|     |_|  |_____||____||____||_|__| **********************
 ******************** 													 **********************
 ***********************************************************************************************************
 * 																					  *
 * This class is a data class holding information pertaining to the Instrumentation Point.	  		  *
 * 																					  *
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc										       *
 *																					  *
 **********************************************************************************************************/

public class InstrumentationPoint {

	// The Data
	//---------
	private int		_iId		= 0;	//A Numerical ID of the instrumentation point
	private String		_sSource	= null;//The path to the Source File
	private int		_iLine		= 0;	//The Line Number the Point is at
	private int		_iChar		= 0;	//Number of Characters in to the line it is set.
	private int		_iType		= 0;	//The Type of Instrumentation Point
	private int		_iLow		= 0;	//The Low Value of the Instrumentation Point
	private int		_iHigh		= 0;	//The High Value of the Instrumentation Point

	// The SET and GET methods
	//------------------------

	/**
	 * @return the Character Position
	 */
	public int getChar() {
		return _iChar;
	}

	/**
	 * 
	 * @return the High value
	 */
	public int getHigh() {
		return _iHigh;
	}

	/**
	 * 
	 * @return the ID of the instrumentation point
	 */
	public int getId() {
		return _iId;
	}

	/**
	 * 
	 * @return the Line number the instrumentation point is set at
	 */
	public int getLine() {
		return _iLine;
	}
	
	/**
	 * 
	 * @return the Low value of the Instrumentation point
	 */
	public int getLow() {
		return _iLow;
	}
	
	/**
	 * 
	 * @return the path to the source file
	 */
	public String getSource() {
		return _sSource;
	}

	/**
	 * 
	 * @return the type of instrumentation point
	 */
	public int getType() {
		return _iType;
	}

	/**
	 * Sets the Character position the instrumentation point is located
	 * @param iChar the position into the line the Instrumentation point will be set
	 */
	public void setChar( int iChar ) {
		_iChar = iChar;
	}

	/**
	 * Sets the High value of the instrumentation point
	 * @param iHigh the high value of the instrumentation point
	 */
	public void setHigh( int iHigh ) {
		_iHigh = iHigh;
	}

	/**
	 * Sets the ID of the instrumentation point
	 * @param iId the ID of the instrumentation point
	 */
	public void setId( int iId ) {
		_iId = iId;
	}

	/**
	 * Sets the Low value of the instrumentation point
	 * @param iLow the low value of the instrumentation point
	 */	
	public void setLow( int iLow ) {
		_iLow = iLow;
	}

	/**
	 * Sets the line the instrumentation point is on
	 * @param iLine the line value the instrumentation point is on
	 */	
	public void setLine( int iLine ) {
		_iLine = iLine;
	}

	/**
	 * Sets the Source File path the instrumentation point is located in
	 * @param sSource the path to the source file
	 */
	public void setSource( String sSource ) {
		_sSource = sSource;
	}
	
	/**
	 * Sets the Type of instrumentation point
	 * @param iType the type of instrumentation point
	 */
	public void setType( int iType ) {
		_iType = iType;
	}

}
