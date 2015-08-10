package scripts.mining;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.basic.ViewportPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.Web;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.util.calculations.Random;

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
		if(path != null && Random.nextInt(100) <= PlayerSense.getAsInteger(CustomPlayerSense.Key.VIEW_PORT_WALKING.playerSenseKey))
			path = ViewportPath.convert(path);
		
		return path;
	}
	
	public Path buildTo(Locatable dest){
		Path path = RegionPath.buildTo(dest);
		
		if(path == null || path.getNext() == null)
			path = web.getPathBuilder().buildTo(dest);
		if(path == null || path.getNext() == null)
			path = BresenhamPath.buildTo(dest);
		if(path != null && Random.nextInt(100) <= PlayerSense.getAsInteger(CustomPlayerSense.Key.VIEW_PORT_WALKING.playerSenseKey))
			path = ViewportPath.convert(path);
		
		return path;
	}
}
