package scripts.mining.locations;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.Web;

public class GenericPathBuilder {
	
	private Web web = null;

	public void setWebWalker(Web web){
		this.web = web;
	}
	
	public void setRegionWalker(){
		web = null;
	}
	
	public Path build(Locatable start, Locatable dest){
		if(web == null){
			return RegionPath.build(start, dest);
		}else{
			return web.getPathBuilder().build(start, dest);
		}
	}
	
	public Path buildTo(Locatable dest){
		if(web == null){
			return RegionPath.buildTo(dest);
		}else{
			return web.getPathBuilder().buildTo(dest);
		}
	}
}
