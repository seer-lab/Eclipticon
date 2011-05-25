package ca.uoit.eclipticon.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public class Tester {

	private final static int	BUFFSIZE	= 1024;
	private static byte			buff1[]		= new byte[BUFFSIZE];
	private static byte			buff2[]		= new byte[BUFFSIZE];

	/**
	 * Test the given Executable File a specified number of times, with the given inputs
	 * 
	 * @param binary Executable File being tested
	 * @param inputs Array of Files to serve as inputs
	 * @param number Number of times to run the test
	 * @param monitor Progress Monitor
	 */
	public void TestNumberOfTimes( File binary, File[] inputs, int number, IProgressMonitor monitor ) {

		// Run at least once
		if( number < 2 )
			number = 1;

		for( int i = 0; i < number; i++ ) {

			// Stop if the Cancel button is pressed
			if( monitor.isCanceled() )
				break;

			// Run through all of the Inputs
			for( File input : inputs ) {

				if( monitor.isCanceled() )
					break;

				try {

					// Different commands for different OS
					String[] commands = new String[3];
					if( isUnix() || isMac() ) {
						commands[ 0 ] = "/bin/sh";
						commands[ 1 ] = "-c";
					}
					else if( isWindows() ) {
						commands[ 0 ] = "cmd.exe";
						commands[ 1 ] = "/C";
					}

					// Create the path to the output file.
					// Same path different extension out#
					Path des = new Path( input.getCanonicalPath() );
					IPath destination = des.removeFileExtension();
					destination = destination.addFileExtension( "out" + i );

					// The command to run
					commands[ 2 ] = binary.getCanonicalPath() + " < " + input.getCanonicalPath() + " > " + destination.toOSString();

					// If it is a binary java file run it with java
					if( binary.toString().endsWith( ( "class" ) ) ) {
						commands[ 2 ] = "java " + commands[ 2 ];
					}

					// Run the Command
					ProcessBuilder builder = new ProcessBuilder( commands );
					builder.redirectErrorStream( true );
					Process process = builder.start();
					process.waitFor();

					// Increment the monitor
					monitor.worked( 1 );

				}
				catch( IOException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch( InterruptedException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Test the given Executable File a specified number of minutes, with the given inputs
	 * 
	 * @param binary Executable File being tested
	 * @param inputs Array of Files to serve as inputs
	 * @param minutes Number of minutes to run the tests for
	 * @param monitor Progress Monitor
	 * @return number of tests performed
	 */
	public int TestForMinutes( File binary, File[] inputs, int minutes, IProgressMonitor monitor ) {
		return TestForSeconds( binary, inputs, minutes * 60, monitor );
	}

	/**
	 * Test the given Executable File a specified number of Seconds, with the given inputs
	 * 
	 * @param binary Executable File being tested
	 * @param inputs Array of Files to serve as inputs
	 * @param minutes Number of seconds to run the tests for
	 * @param monitor Progress Monitor
	 * @return number of tests performed
	 */
	public int TestForSeconds( File binary, File[] inputs, int seconds, IProgressMonitor monitor ) {
		return TestForMilliseconds( binary, inputs, seconds * 1000, monitor );
	}

	/**
	 * Test the given Executable File a specified number of milliseconds, with the given inputs
	 * 
	 * @param binary Executable File being tested
	 * @param inputs Array of Files to serve as inputs
	 * @param minutes Number of milliseconds to run the tests for
	 * @param monitor Progress Monitor
	 * @return number of tests performed
	 */
	public int TestForMilliseconds( File binary, File[] inputs, int milliseconds, IProgressMonitor monitor ) {

		// Keep track of time running and number of times run
		Date timeBeginning = new Date();
		Date timeNow = new Date();
		long diff = timeNow.getTime() - timeBeginning.getTime();
		int count = 0;

		// Test until time is up or cancelled
		while( !monitor.isCanceled() && ( diff < milliseconds ) ) {

			// Test all of the inputs
			for( File input : inputs ) {
				if( monitor.isCanceled() )
					break;
				try {

					// Different commands for different OS
					String[] commands = new String[3];
					if( isUnix() || isMac() ) {

						commands[ 0 ] = "/bin/sh";
						commands[ 1 ] = "-c";
					}
					else if( isWindows() ) {
						commands[ 0 ] = "cmd.exe";
						commands[ 1 ] = "/C";
					}

					// Create the path to the output file.
					// Same path different extension out#
					Path des = new Path( input.getCanonicalPath() );
					IPath destination = des.removeFileExtension();
					destination = destination.addFileExtension( "out" + count );

					// The command to run
					commands[ 2 ] = binary.getCanonicalPath() + " < " + input.getCanonicalPath() + " > " + destination.toOSString();

					// If it is a binary java file run it with java
					if( binary.toString().endsWith( ( "class" ) ) ) {
						commands[ 2 ] = "java " + commands[ 2 ];
					}

					// Run the Command
					ProcessBuilder builder = new ProcessBuilder( commands );
					builder.redirectErrorStream( true );
					Process process = builder.start();
					process.waitFor();

					// Increment the monitor with number of seconds worked
					Date timeWorked = new Date();
					long worked = timeWorked.getTime() - timeNow.getTime();
					monitor.worked( (int)worked );
					timeNow = timeWorked;
					diff = timeNow.getTime() - timeBeginning.getTime();

				}
				catch( IOException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch( InterruptedException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			count++;
		}
		return count;
	}

	/**
	 * Get the results for the specified input file
	 * 
	 * @param file Input File to retrieve the results
	 * @param numberTested Number of results there should be
	 * @return number of tests passed
	 */
	public int getResults( final File file, final int numberTested ) {

		int count = 0;

		// Get just the name of the input file
		Path path = new Path( file.getAbsolutePath() );
		final String nameFile = path.removeFileExtension().lastSegment();

		//Create Filter to get the correct results files
		FileFilter filterOut = new FileFilter() {

			@Override
			public boolean accept( File pathname ) {

				Path path = new Path( pathname.getAbsolutePath() );
				String extension = path.getFileExtension();

				// Name of file
				boolean nameSame = pathname.getName().startsWith( nameFile );

				// Extension is an output
				boolean extensionRight = extension.startsWith( "out" );
				boolean numberCorrect = false;

				// Check if the output file was produced by this test
				if( extensionRight ) {
					// Split the String "out#" > {"", #}
					String[] subString = extension.split( "out" );
					if( subString.length > 1 ) {
						int a = Integer.parseInt( subString[ 1 ] );
						if( a < numberTested )
							numberCorrect = true;
					}
				}

				return ( nameSame && extensionRight && numberCorrect );
			}
		};

		// Create a filter for Expected files
		FileFilter filterExp = new FileFilter() {

			@Override
			public boolean accept( File pathname ) {

				// Return True if same name and ends with .exp
				Path path = new Path( pathname.getAbsolutePath() );
				String extension = path.getFileExtension();
				boolean nameSame = pathname.getName().startsWith( nameFile );
				boolean extensionRight = extension.startsWith( "exp" );
				return ( nameSame && extensionRight );
			}
		};

		// Get Testing Directory
		File parent = file.getParentFile();

		if( parent != null && parent.isDirectory() ) {

			// Get all necessary files
			final File[] fileOut = parent.listFiles( filterOut );
			final File[] fileExp = parent.listFiles( filterExp );

			// If there is an expected result file
			if( fileExp.length > 0 ) {

				// Check the expected result against all of the outputs
				for( File currentFile : fileOut ) {

					// Check to see they are equal
					if( fileContentsEquals( fileExp[ 0 ], currentFile ) ) {
						count++;
					}
				}
			}
		}

		// Return the number of tests passed
		return count;
	}

	private static boolean inputStreamEquals( InputStream is1, InputStream is2 ) {

		// Preliminary Checks
		if( is1 == is2 )
			return true;
		if( is1 == null && is2 == null )
			return true;
		if( is1 == null || is2 == null )
			return false;

		try {
			int read1 = -1;
			int read2 = -1;

			do {
				int offset1 = 0;
				while( offset1 < BUFFSIZE && ( read1 = is1.read( buff1, offset1, BUFFSIZE - offset1 ) ) >= 0 ) {
					offset1 += read1;
				}

				int offset2 = 0;
				while( offset2 < BUFFSIZE && ( read2 = is2.read( buff2, offset2, BUFFSIZE - offset2 ) ) >= 0 ) {
					offset2 += read2;
				}

				// If they are different sizes, obviously different files
				if( offset1 != offset2 )
					return false;
				if( offset1 != BUFFSIZE ) {
					Arrays.fill( buff1, offset1, BUFFSIZE, (byte)0 );
					Arrays.fill( buff2, offset2, BUFFSIZE, (byte)0 );
				}

				// Fill the array buffers and check them
				if( !Arrays.equals( buff1, buff2 ) )
					return false;
			} while( read1 >= 0 && read2 >= 0 );

			// Reached the end of the files and they are the same
			if( read1 < 0 && read2 < 0 )
				return true; // both at EOF
			return false;

		}
		catch( Exception ei ) {
			return false;
		}
	}

	/**
	 * Check to see if two files are equal
	 * 
	 * @param file1 First File to Test
	 * @param file2 Second File to Test
	 * @return True if equal, False if unequal
	 */
	public static boolean fileContentsEquals( File file1, File file2 ) {
		InputStream is1 = null;
		InputStream is2 = null;

		// Preliminary Check
		if( file1.length() != file2.length() )
			return false;

		try {
			is1 = new FileInputStream( file1 );
			is2 = new FileInputStream( file2 );

			return inputStreamEquals( is1, is2 );

		}
		catch( Exception ei ) {
			return false;
		}
		finally {
			try {

				// Close the streams
				if( is1 != null )
					is1.close();
				if( is2 != null )
					is2.close();
			}
			catch( Exception ei2 ) {
			}
		}
	}

	/**
	 * Check to see if it is running on Windows
	 * 
	 * @return True if OS = Windows, False if not
	 */
	public boolean isWindows() {

		String os = System.getProperty( "os.name" ).toLowerCase();
		//windows
		return ( os.indexOf( "win" ) >= 0 );

	}

	/**
	 * Check to see if it is running on Mac
	 * 
	 * @return True if OS = Mac, False if not
	 */
	public boolean isMac() {

		String os = System.getProperty( "os.name" ).toLowerCase();
		//Mac
		return ( os.indexOf( "mac" ) >= 0 );

	}

	/**
	 * Check to see if it is running on Unix
	 * 
	 * @return True if OS = Unix, False if not
	 */
	public boolean isUnix() {

		String os = System.getProperty( "os.name" ).toLowerCase();
		//linux or unix
		return ( os.indexOf( "nix" ) >= 0 || os.indexOf( "nux" ) >= 0 );

	}

}
