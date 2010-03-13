package ca.uoit.eclipticon;

/**
 * This is a constants class that holds the constants being used throughout
 * the project. These constants hold the string place-holders of the synchronization
 * constructs as well as the syntax that will indicate an interest point, there
 * is also a representation of the type of instrumentation point.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public final class Constants {

	/** These are the types of synchronization constructs */
	static public String	SYNCHRONIZE							= "synchronize";
	static public String	LATCH								= "latch";
	static public String	BARRIER								= "barrier";
	static public String	SEMAPHORE							= "semaphore";

	/** These are the syntax representation of synchronized constructs */
	static public String	SYNCHRONIZE_BLOCK					= "synchronized";
	static public String	SYNCHRONIZE_LOCK					= ".lock";
	static public String	SYNCHRONIZE_LOCKINTERRUPTIBLY		= ".lockInterruptibly";
	static public String	SYNCHRONIZE_TRYLOCK					= ".tryLock";
	static public String	SYNCHRONIZE_UNLOCK					= ".unlock";
	static public String	SYNCHRONIZE_NEWCONDITION			= ".newCondition";

	/** These are the syntax representation of latch constructs */
	static public String	LATCH_COUNTDOWN						= ".countdown";
	static public String	LATCH_AWAIT							= ".await";

	/** These are the syntax representation of barrier constructs */
	static public String	BARRIER_RESET						= ".reset";
	static public String	BARRIER_AWAIT						= ".await";

	/** These are the syntax representation of semaphore constructs */
	static public String	SEMAPHORE_ACQUIRE					= ".acquire";
	static public String	SEMAPHORE_ACQUIREUNINTERRUPTIBLY	= ".acquireUninterruptibly";
	static public String	SEMAPHORE_TRYACQUIRE				= ".tryAcquire";
	static public String	SEMAPHORE_DRAIN						= ".drainPermits";
	static public String	SEMAPHORE_RELEASE					= ".release";

	/** These are the types of noise */
	static public int		SLEEP								= 0;
	static public int		YEILD								= 1;
}