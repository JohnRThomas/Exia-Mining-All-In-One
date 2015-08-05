package scripts;
import java.util.List;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.location.navigation.Path;

public class WebPath extends Path{
	
	//Variables
	PathError lastError = PathError.NONE;
	
	//Functions
	public PathError error(){
		return lastError;
	}
	
	//Functions from Path
	@Override
	public Locatable getNext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends Locatable> getVertices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean step(TraversalOption... arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	//Path builder (static)
	private static Graph graph = null;
	
	public static WebPath build(Locatable start, Locatable dest){
		if(graph == null)graph = new WebPath().new Graph();
		return null;
	}
	
	public static WebPath buildTo(Locatable dest){
		if(graph == null)graph = new WebPath().new Graph();
		return null;
	}
	
	private class Graph{
		
	}
	
	//Path Error
	enum PathError{
		NONE;
	}
}
