package ca.uoit.eclipticon.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.parsers.AnnotationParser;
import ca.uoit.eclipticon.Constants;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AnnotationParserTest extends TestCase {

	private final static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final static ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	private AnnotationParser _annotationParser;
	private InstrumentationPoint _instrumentationPoint;
	private String _comment;
	
	@BeforeClass public static void setUpBeforeClass() throws Exception {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}
	
	@Before public void setUp() throws Exception {
		
		_instrumentationPoint = new InstrumentationPoint(10, 1, Constants.BARRIER, Constants.BARRIER_AWAIT, Constants.NOISE_SLEEP, 55, 100, 1000);
		_annotationParser = new AnnotationParser();
		_comment = _annotationParser.createAnnotationComment(_instrumentationPoint);
		System.out.println(_comment);
		
	}

	@Test public void testSearchForAndChangeCommentBasedOnSequenceNumberThatExistsInComment() {
		
		// update the instrumentation point
		_instrumentationPoint = new InstrumentationPoint(10, 1, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIRE, Constants.NOISE_SLEEP, 89, 101, 999);
		String comment = "/* @PreemptionPoint (sequence = 0, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* @PreemptionPoint (sequence = 1, type = \"sleep\", low = 100, high = 1000, probability = 55) */ /* @PreemptionPoint (sequence = 2, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* blah */ if (test == pass) return win;";
		String updated = _annotationParser.updateAnnotationComment(_instrumentationPoint, comment);
		String answer = "/* @PreemptionPoint (sequence = 0, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* @PreemptionPoint (sequence = 1, type = \"sleep\", low = 101, high = 999, probability = 89) */  /* @PreemptionPoint (sequence = 2, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* blah */ if (test == pass) return win;";
		assertEquals(answer, updated);
	}
	
	@Test public void testHasCommentLineButMissingTheCommentWithCorrectSequenceNumber() {
		
		// update the instrumentation point
		_instrumentationPoint = new InstrumentationPoint(10, 1, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIRE, Constants.NOISE_SLEEP, 89, 101, 999);
		String comment = "/* @PreemptionPoint (sequence = 0, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* @PreemptionPoint (sequence = 2, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* blah */";
		String updated = _annotationParser.updateAnnotationComment(_instrumentationPoint, comment);
		String answer = "/* @PreemptionPoint (sequence = 0, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* @PreemptionPoint (sequence = 1, type = \"sleep\", low = 101, high = 999, probability = 89) */  /* @PreemptionPoint (sequence = 2, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* blah */";
		assertEquals(answer, updated);
	}
	
	@Test public void testNoAnnotationInLine () {
		// test that the comment-line has no annotation comment. which means, a new comment should be created anyways.
		_instrumentationPoint = new InstrumentationPoint(10, 1, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIRE, Constants.NOISE_SLEEP, 89, 101, 999);
		_comment = "if (test == pass) return win;";
		String updated = _annotationParser.updateAnnotationComment(_instrumentationPoint, _comment);
		String answer = _comment + " " + _annotationParser.createAnnotationComment(_instrumentationPoint);
		assertEquals(answer, updated);
	}
	
	@After	public void cleanUpStreams() {
	    System.setOut(null);
	    System.setErr(null);
	}
}