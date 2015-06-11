package scripts.mining.locations.osrs;

import scripts.mining.locations.Location;

import com.runemate.game.api.hybrid.entities.GameObject;

public abstract class OSRSLocation extends Location{
	
	@Override
	public boolean validate(GameObject o) {
		int id = o.getId();
		for (int i = 0; i < ore.ids.length; i++) {
			if(ore.ids[i] == id)return true;
		}
		return false;
	}
}
