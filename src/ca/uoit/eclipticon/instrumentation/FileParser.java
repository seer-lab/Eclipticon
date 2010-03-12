package ca.uoit.eclipticon.instrumentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Path;

import ca.uoit.eclipticon.Constants;
import ca.uoit.eclipticon.data.InstrumentationPoint;
import ca.uoit.eclipticon.data.InterestPoint;
import ca.uoit.eclipticon.data.SourceFile;

public class FileParser {

	public ArrayList<SourceFile> getFiles( Path root ) {
		// Make a file out of the path
		File file = root.toFile();

		// Create an arraylist to hold the source files
		ArrayList<SourceFile> allSourceFiles = new ArrayList<SourceFile>();

		// If the path is a directory
		if( file.isDirectory() ) {

			// Get all Files and Folders in it
			File[] allFiles = file.listFiles();

			// Go through each File/Folder
			for( File fileTemp : allFiles ) {
				Path currentPath = new Path( fileTemp.getPath() );
				// If it is a folder
				if( fileTemp.isDirectory() ) {
					// Recursively call getFiles and add their returns to the current arraylist
					allSourceFiles.addAll( getFiles( currentPath ) );
				}
				//If it is a file
				else if( fileTemp.isFile() ) {

					// and the file is a source file
					if( fileTemp.toString().endsWith( ".java" ) ) {

						allSourceFiles.add( new SourceFile( currentPath ) );
					}

				}

			}
		}

		return allSourceFiles;

	}

	/**
	 * This method will find all the potential synchronization constructs within
	 * the current source file, when one is found then based on the probability
	 * the construct will be instrumented or not.
	 * 
	 * @param sourceFile the source file being examined
	 */
	public void findInterestPoints( SourceFile source ) {

		// Read the sourceFile and create the reading and file content objects
		FileReader fileReader = null;
		try {
			fileReader = new FileReader( source.getPath().toString() );
		}
		catch( FileNotFoundException e ) {
			e.printStackTrace();
		}
		BufferedReader bufReader = new BufferedReader( fileReader );

		// If bufferReader is ready start parsing the sourceFile
		String curLine = "";
		int lineNum = 1;
		try {
			if( bufReader.ready() ) {

				// For as long as there are lines left to read; acquire current one
				while( ( curLine = bufReader.readLine() ) != null ) {

					// Handle appropriate synchronize construct if they reside on current line
					source.addInterestingPoints( parseLineForConstructs( curLine, lineNum, Constants.SYNCHRONIZESYNTAX ));
					source.addInterestingPoints(parseLineForConstructs( curLine, lineNum, Constants.BARRIERSYNTAX ));
					source.addInterestingPoints(parseLineForConstructs( curLine, lineNum, Constants.LATCHSYNTAX ));
					source.addInterestingPoints(parseLineForConstructs( curLine, lineNum, Constants.SEMAPHORESYNTAX ));

					lineNum++;
				}
			}
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will parse the current line and look for any appearance of
	 * the appropriate synchronization construct. If one is found it will might
	 * be added to the collection of instrumentation point, depending on the
	 * probability for this construct.
	 * 
	 * @param curLine the current line being parsed
	 * @param lineNumber the line number that is being parsed
	 * @param construct the synchronization construct that is trying to be matched
	 */
	private ArrayList<InterestPoint> parseLineForConstructs( String curLine, int lineNumber, String construct ) {
		ArrayList<InterestPoint> pointsOfInterest = new ArrayList<InterestPoint>();
		int pos = 0;
		int currentPos = 0;
		boolean stillMore = true;
		int count = 0;
		while( stillMore ) {

			// Keep going unless no more constructs are found
			if( ( currentPos = curLine.indexOf( construct, pos ) ) != -1 ) {

				// A construct is found at currentPos
				pointsOfInterest.add( new InterestPoint( lineNumber, count, construct ) );
				count++;

				pos = currentPos + construct.length();
			}
			else {
				stillMore = false;
			}
		}
		return pointsOfInterest;
	}
	
	/**
	 * This method will parse the current line and look for any appearance of
	 * the appropriate instrumentation point comment. If one is found it will
	 * might be added to the collection of instrumentation point, depending
	 * on the probability for this construct.
	 * 
	 * @param curLine the current line being parsed
	 * @param lineNumber the line number that is being parsed
	 */
	private ArrayList<InstrumentationPoint> parseLineForInstrumentationPoints( String curLine, int lineNumber ) {
		ArrayList<InstrumentationPoint> pointsOfInstrumentation = new ArrayList<InstrumentationPoint>();
		String match = "\\/\\*" + ".*?INSTRUMENTATION POINT" + ".*?\\(" + "(.*?)" + "\\).*?" + "\\*\\/";
		Pattern pattern = Pattern.compile(match, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(curLine);
        String params;
        int count = 0;
        if (matcher.find())
        {
        	params = matcher.group(1);
        	//String start = matcher.group();
            //System.out.print( start.toString() + "\n " + params.toString() + "\n");
        	
        	//pointsOfInstrumentation.add( new InstrumentationPoint( lineNumber, count) );
        }
        
        //return pointsOfInstrumentation;
        return null; // Not finished implementing. Need to extract data out of params.
	}
	
	/* INSTRUMENTATION POINT (Sleep:100ms-1000ms, Probability: 100%) */
	/* INSTRUMENTATION POINT (Yield, Probability: 100%) */
}
