package ca.uoit.eclipticon;

/**
 * This is a constants class that holds the constants being used throughout
 * the project. All the constants are of type Integer to simplify the use through
 * the project. The constants are used for the various pieces of information for
 * the instrumentation data.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public final class Constants {

	/** These are the types of synchronization constructs */
	static public int		SYNCHRONIZE			= 0;
	static public int		BARRIER				= 1;
	static public int		LATCH				= 2;
	static public int		SEMAPHORE			= 3;

	/** These are the syntax representation of synchronization constructs */
	static public String	SYNCHRONIZESYNTAX	= "synchronized";
	static public String	BARRIERSYNTAX		= "barrier";
	static public String	LATCHSYNTAX			= "latch";
	static public String	SEMAPHORESYNTAX		= "semaphore";

	/** These are the types of instrumentation modes */
	static public int		MANUAL				= 0;
	static public int		AUTOMATIC			= 1;
	static public int		BOTH				= 2;

	/** These are the types of noise */
	static public int		SLEEP				= 0;
	static public int		YEILD				= 1;
}
