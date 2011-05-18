package ca.uoit.eclipticon;

import java.util.regex.Pattern;

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
	static public String	BARRIER								= "barrier";
	static public String	LATCH								= "latch";
	static public String	SEMAPHORE							= "semaphore";

	/** These are the syntax representation of synchronized constructs */
	static public String	SYNCHRONIZE_BLOCK					= "synchronized";
	static public String	SYNCHRONIZE_LOCK					= ".lock";
	static public String	SYNCHRONIZE_LOCKINTERRUPTIBLY		= ".lockInterruptibly";
	static public String	SYNCHRONIZE_NEWCONDITION			= ".newCondition";
	static public String	SYNCHRONIZE_TRYLOCK					= ".tryLock";
	static public String	SYNCHRONIZE_UNLOCK					= ".unlock";

	/** These are the syntax representation of latch constructs */
	static public String	LATCH_AWAIT							= ".await";
	static public String	LATCH_COUNTDOWN						= ".countDown";

	/** These are the syntax representation of barrier constructs */
	static public String	BARRIER_AWAIT						= ".await";
	static public String	BARRIER_RESET						= ".reset";

	/** These are the syntax representation of semaphore constructs */
	static public String	SEMAPHORE_ACQUIRE					= ".acquire";
	static public String	SEMAPHORE_ACQUIREUNINTERRUPTIBLY	= ".acquireUninterruptibly";
	static public String	SEMAPHORE_DRAIN						= ".drainPermits";
	static public String	SEMAPHORE_RELEASE					= ".release";
	static public String	SEMAPHORE_TRYACQUIRE				= ".tryAcquire";

	/** These are the types of noise */
	static public int		NOISE_SLEEP							= 0;
	static public int		NOISE_YIELD							= 1;

	/** These are the types of synchronized types during ambiguity stage */
	static public int		SYNCHRONIZED_NOT_FOUND				= 0;
	static public int		SYNCHRONIZED_BLOCK_FOUND			= 1;
	static public int		SYNCHRONIZED_METHOD_FOUND			= 2;

	/** These are the types of annotation actions */
	static public int		ANNOTATION_DELETE					= 0;
	static public int		ANNOTATION_UPDATE					= 1;
	static public int		ANNOTATION_ADD						= 2;

	/** These are the Regexs and Patterns used */
	static private	String 	REGEX_PACKAGE 	= "(package[\\s]+[a-z][a-z\\.\\d\\-\\_\\s]*[\\*]*[\\s]*;)";
	static private  String 	REGEX_IMPORT 	= "(import[\\s]+[a-z][a-z\\.\\d\\-\\_\\s]*[\\s]*;)";
	static private 	String 	REGEX_METHOD 	= "synchronized[\\s]+?[public|private|protector]+[\\s]*?[\\w]+?[\\s]+?(\\w+)\\(";
	static private 	String 	REGEX_CLASS 	= "(public|private|protected|\\s)+[(\\s)+](class|interface|abstract class)[(\\s)+]([a-z]+[a-z0-9_])*[(\\s)+](.*?)(\\{)";
	static public 	Pattern PATTERN_PACKAGE = Pattern.compile( REGEX_PACKAGE, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
	static public 	Pattern PATTERN_IMPORT 	= Pattern.compile( REGEX_IMPORT, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
	static public 	Pattern PATTERN_METHOD 	= Pattern.compile( REGEX_METHOD, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE );
	static public 	Pattern PATTERN_CLASS 	= Pattern.compile( REGEX_CLASS, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE );
	
	/** These are the file extension types used */
	static public String	EXTENSION_ECLIPTICON				= ".eclipticon";
	static public String	EXTENSION_JAVA						= ".java";
	static public String	EXTENSION_TEST_IN					= "in";
	static public String	EXTENSION_TEST_EXP					= "exp";
	static public String	EXTENSION_TEST_OUT					= "out";
	
	/** The file writer's buffer size */
	static public int		BUFFER_SIZE 						= 127;
}