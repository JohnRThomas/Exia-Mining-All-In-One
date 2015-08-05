package scripts.mining.locations;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.Web;

public class GenericPathBuilder {
	
	private Web web;

	public GenericPathBuilder(){
		web  = Traversal.getDefaultWeb();
	}
	
	public void setWebWalker(Web web){
		this.web = web;
	}
		
	public Path build(Locatable start, Locatable dest){
		Path path = RegionPath.build(start, dest);
		
		if(path == null || path.getNext() == null)
			path = web.getPathBuilder().build(start, dest);
		if(path == null || path.getNext() == null)
			path = BresenhamPath.build(start, dest);
		
		return path;
	}
	
	public Path buildTo(Locatable dest){
		Path path = RegionPath.buildTo(dest);
		
		if(path == null || path.getNext() == null){
			path = web.getPathBuilder().buildTo(dest);
			System.out.println("Using WebPath");
			System.out.println("\t"+ path.getNext() + " -> " +  path.getVertices().get(path.getVertices().size()-1));
			System.out.println("\t"+ path.getVertices().size());
		}else{
			System.out.println("Using RegionPath");
			System.out.println("\t"+ path.getNext() + " -> " +  path.getVertices().get(path.getVertices().size()-1));
			System.out.println("\t"+ path.getVertices().size());
		}
		if(path == null || path.getNext() == null){
			path = BresenhamPath.buildTo(dest);
			System.out.println("Using BresenhamPath");
			System.out.println("\t"+ path.getNext() + " -> " +  path.getVertices().get(path.getVertices().size()-1));
			System.out.println("\t"+ path.getVertices().size());
		}
		
		return path;
	}
}
