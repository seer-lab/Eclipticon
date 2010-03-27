package ca.uoit.eclipticon.data;

import java.util.ArrayList;
import org.eclipse.core.runtime.Path;

/**
 * This data class represents source files within the workspace and contain an arraylist
 * of {@link InterestPoint} in regards to synchronization constructs.
 * 
 * @author Chris Forbes, Kevin Jalbert, Cody LeBlanc
 */
public class SourceFile {

	private Path	_path		= null; // The abstract path name of this source file
	private int		_numLines	= 0;	// The number of lines this source file has
	private String	_name		= null; // The name of this source file

	// A collection of interest points for this source file
	public ArrayList<InterestPoint> _interestingPoints = new ArrayList<InterestPoint>();

	/**
	 * Constructor that will end up creating a source file instance with a source path
	 * 
	 * @param path the source file path
	 */
	public SourceFile( Path path ) {
		setPath( path );
	}

	/**
	 * Sets the path of the source file and the name of the source file.
	 * 
	 * @param path the source file path
	 */
	public void setPath( Path path ) {
		_path = path;
		_name = _path.lastSegment();
	}

	/**
	 * Gets the path of the source file.
	 * 
	 * @return the source file path
	 */
	public Path getPath() {
		return _path;
	}

	/**
	 * Gets the name of the source file
	 * 
	 * @return the source file name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Add a single interesting point to this source file.
	 * 
	 * @param point a single interesting point
	 */
	public void addInterestingPoint( InterestPoint point ) {
		_interestingPoints.add( point );
	}

	/**
	 * Add an arraylist of interesting points to this source file.
	 * 
	 * @param points an arraylist of interesting points
	 */
	public void addInterestingPoints( ArrayList<InterestPoint> points ) {
		_interestingPoints.addAll( points );
	}

	/**
	 * Gets the arraylist of interesting points in this source file.
	 * 
	 * @return an arraylist of interesting points
	 */
	public ArrayList<InterestPoint> getInterestingPoints() {
		return _interestingPoints;
	}

	/**
	 * Prints out all the interesting points for this source file along with
	 * all its related information.
	 */
	public void printIP() {
		if( !_interestingPoints.isEmpty() ) {
			for( InterestPoint ip : _interestingPoints ) {
				System.out.println( "\tLine: " + ip.getLine() + " | Instance: " + ip.getSequence() + " | "
						+ ip.getConstruct() );
			}
		}
	}
}