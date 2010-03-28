package ca.uoit.eclipticon.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.instrumentation.AnnotationParser;
import ca.uoit.eclipticon.Constants;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AnnotationParserTest extends TestCase {

	private final static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final static ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	AnnotationParser a;
	InstrumentationPoint p;
	InstrumentationPoint pp;
	String comment;
	
	@BeforeClass public static void setUpBeforeClass() throws Exception {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}
	
	@Before public void setUp() throws Exception {
		
		p = new InstrumentationPoint(10, 1, Constants.BARRIER, Constants.BARRIER_AWAIT, 0, 55, 100, 1000);
		a = new AnnotationParser();
		comment = a.createAnnotationComment(p);
		System.out.println(comment);
		
	}

	
	@Test public void testSearchForAndChangeCommentBasedOnSequenceNumberThatExistsInComment() {
		
		// update the instrumentation point
		p = new InstrumentationPoint(10, 1, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIRE, 0, 89, 101, 999);
		String comment = "/* @PreemptionPoint (sequence = 0, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* @PreemptionPoint (sequence = 1, type = \"sleep\", low = 100, high = 1000, probability = 55) */ /* @PreemptionPoint (sequence = 2, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* blah */ if (test == pass) return win;";
		String updated = a.updateAnnotationComment(p, comment);
		//System.out.println("\n\nNew comment updated: " + updated);
		String answer = "/* @PreemptionPoint (sequence = 0, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* @PreemptionPoint (sequence = 1, type = \"sleep\", low = 101, high = 999, probability = 89) */  /* @PreemptionPoint (sequence = 2, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* blah */ if (test == pass) return win;";
		assertEquals(answer, updated);
	}
	
	
	@Test public void testHasCommentLineButMissingTheCommentWithCorrectSequenceNumber() {
		
		// update the instrumentation point
		p = new InstrumentationPoint(10, 1, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIRE, 0, 89, 101, 999);
		String comment = "/* @PreemptionPoint (sequence = 0, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* @PreemptionPoint (sequence = 2, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* blah */";
		String updated = a.updateAnnotationComment(p, comment);
		//System.out.println("\n\nNew comment updated: " + updated);
		String answer = "/* @PreemptionPoint (sequence = 0, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* @PreemptionPoint (sequence = 1, type = \"sleep\", low = 101, high = 999, probability = 89) */  /* @PreemptionPoint (sequence = 2, type = \"sleep\", low = 10, high = 1200, probability = 75)*/ /* blah */";
		assertEquals(answer, updated);
	}
	
	
	@Test public void testNoAnnotationInLine () {
		// test that the comment-line has no annotation comment. which means, a new comment should be created anyways.
		p = new InstrumentationPoint(10, 1, Constants.SEMAPHORE, Constants.SEMAPHORE_ACQUIRE, 0, 89, 101, 999);
		comment = "if (test == pass) return win;";
		String updated = a.updateAnnotationComment(p, comment);
		String answer = comment + " " + a.createAnnotationComment(p);
		//System.out.println("\n\nOrigional comment :" + comment + "\nNew comment updated: " + updated + "\nAnswer comment: " + answer);
		assertEquals(answer, updated);
	}
	
	
	@After	public void cleanUpStreams() {
	    System.setOut(null);
	    System.setErr(null);
	}

}
