package exiabots.mining;

import java.util.ArrayList;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.Web;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.util.calculations.Random;

public class GenericPathBuilder {
	
	private Web web;

	public GenericPathBuilder(){
		web  = Traversal.getDefaultWeb();
	}
		
	public Path build(Locatable start, Locatable dest){
		ArrayList<Locatable> stupidList = new ArrayList<Locatable>();
		stupidList.add(dest);
		Path path = RegionPath.buildBetween(start, stupidList);
	
		if(path == null || path.getNext() == null)
			path = web.getPathBuilder().build(start, dest);
		if(path == null || path.getNext() == null)
			path = BresenhamPath.buildBetween(start, dest);
		if(path != null && Random.nextInt(100) <= PlayerSense.getAsInteger(CustomPlayerSense.Key.VIEW_PORT_WALKING.playerSenseKey)){}
			//path = ViewportPath.convert(path);
		return path;
	}
	
	public Path buildTo(Locatable dest){
		Path path = RegionPath.buildTo(dest);
		
		if(path == null || path.getNext() == null)
			path = web.getPathBuilder().buildTo(dest);
		if(path == null || path.getNext() == null)
			path = BresenhamPath.buildTo(dest);
		if(path != null && Random.nextInt(100) <= PlayerSense.getAsInteger(CustomPlayerSense.Key.VIEW_PORT_WALKING.playerSenseKey)){}
			//path = ViewportPath.convert(path);
		
		return path;
	}
	public Path build(Locatable start, Locatable dest, boolean vp){
		ArrayList<Locatable> stupidList = new ArrayList<Locatable>();
		stupidList.add(dest);
		Path path = RegionPath.buildBetween(start, stupidList);
	
		if(path == null || path.getNext() == null)
			path = web.getPathBuilder().build(start, dest);
		if(path == null || path.getNext() == null)
			path = BresenhamPath.buildBetween(start, dest);
		
		/*if(vp){
			if(path != null && Random.nextInt(100) <= PlayerSense.getAsInteger(CustomPlayerSense.Key.VIEW_PORT_WALKING.playerSenseKey))
				path = ViewportPath.convert(path);
		}*/
		
		return path;
	}
	
	public Path buildTo(Locatable dest, boolean vp){
		Path path = RegionPath.buildTo(dest);
		
		if(path == null || path.getNext() == null)
			path = web.getPathBuilder().buildTo(dest);
		if(path == null || path.getNext() == null)
			path = BresenhamPath.buildTo(dest);
		
		/*if(vp){
			if(path != null && Random.nextInt(100) <= PlayerSense.getAsInteger(CustomPlayerSense.Key.VIEW_PORT_WALKING.playerSenseKey))
				path = ViewportPath.convert(path);
		}*/
		
		return path;
	}
}
