package ca.uoit.eclipticon.instrumentation;

import ca.uoit.eclipticon.Constants;

/**
 * This class will generate sleep and yield noise, as well as create
 * the statement complete with a probability of activating. A simple try
 * and catch statement is included to avoid any Exception.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class NoiseMaker {

	// Default constructor
	/**
	 * Instantiates a new noise maker.
	 */
	public NoiseMaker(){
	}
	
	/**
	 * Method will create a noise statement given the noise type and delay ranges.
	 * 
	 * @param type the noise type
	 * @param chance the chance of this noise activating out of 100
	 * @param low the lower bound of the sleep delay
	 * @param high the upper bound of the sleep delay
	 * 
	 * @return the noise statement
	 */
	public String makeNoise( int chance, int type, int low, int high ) {

		String noise = null;

		// Create the noise statement based on the type of noise to make
		if (type == Constants.SLEEP){
			noise = getIfChance( chance ) + createSleep( low, high );
		}
		else if (type == Constants.YEILD){
			noise = getIfChance( chance ) + createYield();
		}

		// Returns the noise statement
		return noise;
	}

	/**
	 * This method will create the sleep instrumentation statement by using the
	 * delay ranges, it also includes the appropriate try/catch syntax.
	 * 
	 * @param low the low bound of the sleep delay
	 * @param high the high bound of the sleep delay
	 * 
	 * @return the sleep statement
	 */
	private String createSleep( int low, int high ) {
		return "try{Thread.sleep((rand.nextInt(high-low)+low));}catch(Exception e){};";
	}

	/**
	 * This method will create the yield instrumentation statement,
	 * it also includes the appropriate try/catch syntax.
	 * 
	 * @return the yield statement
	 */
	private String createYield() {
		return "try{Thread.yield();}catch(Exception e){};";
	}

	/**
	 * This method will create the if statement that controls the chance (out of
	 * 100) of an instrumentation point actually executing.
	 * 
	 * @param chance the value out of 100 that will decide the executing chance
	 * 
	 * @return the if statement that will decide if the instrument will occur
	 */
	private String getIfChance( int chance ) {
		return "if((rand.nextInt(100-0)+0)<=" + chance + ")";
	}
}