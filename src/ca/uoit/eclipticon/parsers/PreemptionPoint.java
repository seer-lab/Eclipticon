package ca.uoit.eclipticon.parsers;

/**
 * The PreemptionPoint annotation is used to insert noise (such as a thread delay)
 * before a concurrency mechanism. The &#64;PreemptionPoint needs to be placed on
 * the line before a concurrency mechanism appears in the source code. &#64;PreemptionPoint
 * must be used inside a comment.<br/><br/>
 * 
 * An example declaration is:<br/>
 * &#47;&#42; &#64;PreemptionPoint (syntax = methodCall, sequence = 0, type = "sleep", low = 100, high = 1000, probability = 100) &#42;&#47;
 * &#47;&#42; &#64;PreemptionPoint (syntax = .countDown, sequence = 0, type = "yield", probability = 100) &#42;&#47;
 * 
 * @param sequence Represents the ordering if multiple concurrency mechanisms occur on one line, an int
 * @param type "sleep" or "yield", a string
 * @param low Lower bound of delay range, measured in milliseconds, default 100, an int
 * @param high Upper bound of delay range, measured in milliseconds, default 1000, an int
 * @param probability Percentage of time the preemption point will be executed, from 0 to 100, an int
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public @interface PreemptionPoint {
	String syntax();
	int sequence() default 0;
	String type();
	int low() default 100;
	int high() default 1000;
	int probability() default 100;
}