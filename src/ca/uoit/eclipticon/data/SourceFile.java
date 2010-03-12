package ca.uoit.eclipticon.data;

import java.util.ArrayList;

import org.eclipse.core.runtime.Path;


public class SourceFile {
	
	public Path _path = null;
	public int _numLines = 0;
	public String _name = null;
	public ArrayList<InterestPoint> _interestingPoints = new ArrayList<InterestPoint>();
	
	
	public SourceFile(Path path){
		setPath( path );
	}
	
	public void setPath(Path path){
		_path = path;
		_name  = _path.lastSegment();
	}
	
	public Path getPath(){
		return _path;
	}
	
	public String getName(){
		return _name;
	}
	
	public void addInterestingPoint(InterestPoint point){
		_interestingPoints.add( point );
	}
	public void addInterestingPoints(ArrayList<InterestPoint> points){
		_interestingPoints.addAll( points );
	}
	public ArrayList<InterestPoint> getInterestingPoints(){
		return _interestingPoints;
	}
	public void printIP(){
		if (!_interestingPoints.isEmpty()){
			for (InterestPoint ip : _interestingPoints){
				System.out.println("\tLine: " + ip.getLine() + " | Instance: " + ip.getInstance() + " | " + ip.getConstruct() ) ;
			}
		}
	}
}
