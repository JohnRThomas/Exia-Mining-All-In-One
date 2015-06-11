package scripts.mining.locations;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.Web;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;

public class GenericPathBuilder {
	
	private Web web = Traversal.getDefaultWeb();

	public void setWebWalker(Web web){
		this.web = web;
	}
		
	public Path build(Locatable start, Locatable dest){
		Path path = null;
		RegionPath rpath = RegionPath.build(start, dest);
		if(rpath == null || rpath.getNext() == null){
			WebPath wpath = web.getPathBuilder().build(start, dest);
			if(wpath == null || wpath.getNext() == null){
				BresenhamPath bpath = BresenhamPath.build(start, dest);
				path = bpath;
			}else{
				path = wpath;
			}
		}else{
			path = rpath;
		}
		
		return path;
	}
	
	public Path buildTo(Locatable dest){
		Path path = null;
		RegionPath rpath = RegionPath.buildTo(dest);
		if(rpath == null || rpath.getNext() == null){
			WebPath wpath = web.getPathBuilder().buildTo(dest);
			if(wpath == null || wpath.getNext() == null){
				BresenhamPath bpath = BresenhamPath.buildTo(dest);
				System.out.println("Using BresenhamPath");
				System.out.println("\t"+ bpath.getNext() + " -> " +  bpath.getVertices().get(bpath.getVertices().size()-1));
				System.out.println("\t"+ bpath.getVertices().size());
				path = bpath;
			}else{
				path = wpath;
				System.out.println("Using WebPath");
				System.out.println("\t"+ wpath.getNext() + " -> " +  wpath.getVertices().get(wpath.getVertices().size()-1));
				System.out.println("\t"+ wpath.getVertices().size());
			}
		}else{
			path = rpath;
			System.out.println("Using RegionPath");
			System.out.println("\t"+ rpath.getNext() + " -> " +  rpath.getVertices().get(rpath.getVertices().size()-1));
			System.out.println("\t"+ rpath.getVertices().size());
		}
		
		return path;
	}
}
