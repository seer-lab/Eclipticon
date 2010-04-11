package ca.uoit.eclipticon.parsers;

import java.util.regex.*;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.instrumentation.PreemptionPoint;

/**
 * The AnnotationParser class extracts the parameters from a given
 * {@link PreemptionPoint} annotation and returns this information as an
 * {@link InstrumentationPoint}.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class AnnotationParser {

	// default values
	private int		_sequence		= 0;
	private int		_type			= Constants.NOISE_SLEEP;
	private int		_low			= 100;
	private int		_high			= 1000;
	private int		_probability	= 100;
	private String	_syntax			= "";

	/**
	 * The parseLineForAnnotations method accepts a line as input and extracts
	 * the parameters. It stores those parameter values in an
	 * {@link InstrumentationPoint} and returns that object. If no annotation
	 * exists, then a null is returned.
	 * 
	 * @param curLine The string of the line the annotation is found on.
	 * @param lineNumber The line number the annotation is found on. NOT the line the concurrency mechanism is found on.
	 * @param sequence Represents the ordering if multiple concurrency mechanisms occur on one line
	 * @param construct I.e. a synchronize, barrier, latch, or semaphore
	 * @param constructSyntax The syntax that was used to find the construct
	 * 
	 * @return {@link InstrumentationPoint}
	 */
	public InstrumentationPoint parseLineForAnnotations( String curLine, int lineNumber, int sequence, String construct, String constructSyntax ) {

		//check that annotation exists
		if( checkAnnotationExists( curLine ) ) { // if true

			while( _syntax != constructSyntax || _sequence != sequence ) {

				_syntax = parseSyntax( curLine );

				if( _syntax.compareTo( constructSyntax ) == 0 ) {

					_sequence = parseSequence( curLine );

					if( _sequence != sequence ) {
						curLine = curLine.substring( curLine.indexOf( "*/" ) + 2 );
						if( checkAnnotationExists( curLine ) == false )
							return null;

					}
					else
						break;

				}
				else {
					curLine = curLine.substring( curLine.indexOf( "*/" ) + 2 );
					if( checkAnnotationExists( curLine ) == false )
						return null;
				}

			}

			_type = parseType( curLine );
			// branch here, because sleep and yield require different syntax
			if( _type == Constants.NOISE_YIELD ) { // if type is yield

				_probability = parseProbability( curLine );
				_low = 0;
				_high = 0;

				return new InstrumentationPoint( lineNumber, sequence, construct, constructSyntax, _type, _probability, _low, _high );

			}
			else { // if type is 0, default to sleep

				_low = parseLow( curLine );
				_high = parseHigh( curLine );
				_probability = parseProbability( curLine );

				return new InstrumentationPoint( lineNumber, sequence, construct, constructSyntax, _type, _probability, _low, _high );

			}

		}

		else { // annotation does not exist
			return null;
		}
	}

	public boolean checkAnnotationExists( String input ) {

		String regularExpression = "\\/\\*" + "[\\s+]?" + "@PreemptionPoint" + ".*?" + "\\*\\/";
		Pattern pattern = Pattern.compile( regularExpression, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher matcher = pattern.matcher( input );
		String annotation = null;
		if( matcher.find() ) {
			annotation = matcher.group();
			if( annotation != null )
				return true;
		}
		return false;
	}

	private String parseParameterString( String input ) {
		// get the string of parameters
		String regularExpression = "\\/\\*" + "[\\s+]?" + "@PreemptionPoint" + "[\\s+]?" + "\\(" + "(.*?)" + "\\)" + "[\\s+]?" + "\\*\\/";
		Pattern pattern = Pattern.compile( regularExpression, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher matcher = pattern.matcher( input );
		String params = null;
		if( matcher.find() ) {
			params = matcher.group( 1 );
		}
		return params;
	}

	private int parseSequence( String params ) {

		int sequence = _sequence;
		String regularExpression = "[\\s+]?" + "sequence" + "[\\s+]?" + "=" + "[\\s+]?" + "(\\d+)";
		Pattern p = Pattern.compile( regularExpression, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher m = p.matcher( params );
		if( m.find() ) {
			sequence = Integer.parseInt( m.group( 1 ) );
		}
		return sequence;
	}

	private int parseType( String params ) {

		int type = _type;
		String regularExpression = "," + "[\\s+]?" + "type" + "[\\s+]?" + "=" + "[\\s+]?" + "." + "((?:[a-z][a-z]+))";
		Pattern p = Pattern.compile( regularExpression, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher m = p.matcher( params );
		if( m.find() ) {
			String string = m.group( 1 );

			if( string.equals( "yield" ) )
				type = 1;
			else
				//(string.equals("sleep"))
				type = 0;
		}
		return type;
	}

	private int parseLow( String params ) {

		int low = _low;
		String regularExpression = "," + "[\\s+]?" + "low" + "[\\s+]?" + "=" + "[\\s+]?" + "(\\d+)";
		Pattern p = Pattern.compile( regularExpression, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher m = p.matcher( params );
		if( m.find() ) {
			low = Integer.parseInt( m.group( 1 ) );
		}
		return low;
	}

	private int parseHigh( String params ) {

		int high = _high;
		String regularExpression = "," + "[\\s+]?" + "high" + "[\\s+]?" + "=" + "[\\s+]?" + "(\\d+)";
		Pattern p = Pattern.compile( regularExpression, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher m = p.matcher( params );
		if( m.find() ) {
			high = Integer.parseInt( m.group( 1 ) );
		}
		return high;
	}

	private int parseProbability( String params ) {

		int probability = _probability;
		String regularExpression = "," + "[\\s+]?" + "probability" + "[\\s+]?" + "=" + "[\\s+]?" + "(\\d+)";
		Pattern p = Pattern.compile( regularExpression, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher m = p.matcher( params );
		if( m.find() ) {
			probability = Integer.parseInt( m.group( 1 ) );
		}
		return probability;
	}

	private String parseSyntax( String params ) {
		String syntax = _syntax;
		String regularExpression = "\\(" + "[\\s+]?" + "syntax" + "[\\s+]?" + "=" + "[\\s+]?" + "([\\.|\\w]\\w+)";
		Pattern p = Pattern.compile( regularExpression, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
		Matcher m = p.matcher( params );
		if( m.find() ) {
			syntax = m.group( 1 );
		}
		return syntax;
	}

	/**
	 * Deletes the given Instrumentation Points Annotation if it exists in the String passed.
	 * 
	 * @param point Instrumentation Point to Remove from the String.
	 * @param previousLine String that holds the annotation to be removed.
	 * @return String with the annotation removed.
	 */
	public String deleteAnnotationComment( InstrumentationPoint point, String previousLine ) {

		String beginningOfLine = "";
		String restOfLine = "";

		boolean foundCorrectAnnotation = false;
		while( foundCorrectAnnotation == false ) {

			//check that annotation exists
			if( checkAnnotationExists( previousLine ) ) { // if true

				_syntax = parseSyntax( previousLine );

				_sequence = parseSequence( previousLine );
				if( _syntax.compareTo( point.getConstructSyntax() ) == 0 )

					if( _sequence == point.getSequence() ) // if sequence number is correct, then exit loop, and continue on
						foundCorrectAnnotation = true;
					else {
						beginningOfLine = beginningOfLine + previousLine.substring( 0, previousLine.indexOf( "*/" ) + 2 );
						previousLine = previousLine.substring( previousLine.indexOf( "*/" ) + 2 ); // then delete the first annotation

						if( previousLine == null ) { // there are no comment matches left, we need to create a new comment, the method was erroneously called
							return beginningOfLine + " " + createAnnotationComment( point );
						}
					}

				else { // sequence is NOT correct, go onto next comment in line and loop again.

					// store the start of the line, because we haven't found the right comment, and are going to get rid of the first part of the line
					beginningOfLine = beginningOfLine + previousLine.substring( 0, previousLine.indexOf( "*/" ) + 2 );
					previousLine = previousLine.substring( previousLine.indexOf( "*/" ) + 2 ); // then delete the first annotation

					if( previousLine == null ) { // there are no comment matches left, we need to create a new comment, the method was erroneously called
						return beginningOfLine;
					}
				}
			}
			else {// we need to create a new comment, the method was erroneously called
				beginningOfLine += previousLine;
				return beginningOfLine;

				//return beginningOfLine + " " + createAnnotationComment(point) + previousLine;
			}
		}
		// found the correct annotation comment, now update it.

		restOfLine = previousLine.substring( previousLine.indexOf( "*/" ) + 2 ); // get the rest of the line

		String newLine = beginningOfLine + restOfLine;
		return newLine;

	}

	/**
	 * The createAnnotationComment method creates a syntactically correct comment from an Instrumentation Point.
	 * 
	 * @param point
	 * @return
	 */
	public String createAnnotationComment( InstrumentationPoint point ) {

		String annotationComment;

		annotationComment = "/* @PreemptionPoint (";

		annotationComment = annotationComment + "syntax = " + point.getConstructSyntax() + ", ";

		annotationComment = annotationComment + "sequence = " + point.getSequence() + ", ";

		if( point.getType() == Constants.NOISE_SLEEP ) { // then write sleep

			annotationComment = annotationComment + "type = \"sleep\", " + "low = " + point.getLow() + ", high = " + point.getHigh() + ", " + "probability = " + point.getProbability() + ") */";
		}
		else {// then write yield

			annotationComment = annotationComment + "type = \"yield\", " + "probability = " + point.getProbability() + ") */";
		}
		return annotationComment;
	}

	/**
	 * This method is same as above. However, it is used to update a comment. Hence the passing of the previous line, which contains the annotation comments.
	 * @param point
	 * @param previousLine
	 * @return
	 */
	public String updateAnnotationComment( InstrumentationPoint point, String previousLine ) { // previous line contains the comment, that I have to update.

		String beginningOfLine = "";
		String restOfLine = "";

		boolean foundCorrectAnnotation = false;
		while( foundCorrectAnnotation == false ) {

			//check that annotation exists
			if( checkAnnotationExists( previousLine ) ) { // if true

				_syntax = parseSyntax( previousLine );

				_sequence = parseSequence( previousLine );
				if( _syntax.compareTo( point.getConstructSyntax() ) == 0 )

					if( _sequence == point.getSequence() ) // if sequence number is correct, then exit loop, and continue on
						foundCorrectAnnotation = true;
					else {
						beginningOfLine = beginningOfLine + previousLine.substring( 0, previousLine.indexOf( "*/" ) + 2 );
						previousLine = previousLine.substring( previousLine.indexOf( "*/" ) + 2 ); // then delete the first annotation

						if( previousLine == null ) { // there are no comment matches left, we need to create a new comment, the method was erroneously called
							return beginningOfLine + " " + createAnnotationComment( point );
						}
					}

				else { // sequence is NOT correct, go onto next comment in line and loop again.

					// store the start of the line, because we haven't found the right comment, and are going to get rid of the first part of the line
					beginningOfLine = beginningOfLine + previousLine.substring( 0, previousLine.indexOf( "*/" ) + 2 );
					previousLine = previousLine.substring( previousLine.indexOf( "*/" ) + 2 ); // then delete the first annotation

					if( previousLine == null ) { // there are no comment matches left, we need to create a new comment, the method was erroneously called
						return beginningOfLine + " " + createAnnotationComment( point );
					}
				}
			}
			else {// we need to create a new comment, the method was erroneously called
				beginningOfLine += previousLine;
				return beginningOfLine + " " + createAnnotationComment( point );

				//return beginningOfLine + " " + createAnnotationComment(point) + previousLine;
			}
		}
		// found the correct annotation comment, now update it.

		restOfLine = previousLine.substring( previousLine.indexOf( "*/" ) + 2 ); // get the rest of the line

		String updatedAnnotationComment;
		updatedAnnotationComment = "/* @PreemptionPoint (";
		updatedAnnotationComment = updatedAnnotationComment + "syntax = " + point.getConstructSyntax() + ", ";
		updatedAnnotationComment = updatedAnnotationComment + "sequence = " + point.getSequence() + ", ";

		if( point.getType() == Constants.NOISE_SLEEP ) { // then write sleep

			updatedAnnotationComment = updatedAnnotationComment + "type = \"sleep\", " + "low = " + point.getLow() + ", high = " + point.getHigh() + ", " + "probability = " + point.getProbability() + ") */";
		}
		else {// then write yield

			updatedAnnotationComment = updatedAnnotationComment + "type = \"yield\", " + "probability = " + point.getProbability() + ") */";
		}

		String newLine = beginningOfLine + " " + updatedAnnotationComment + " " + restOfLine;
		return newLine;
	}
}