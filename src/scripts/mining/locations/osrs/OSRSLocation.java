package scripts.mining.locations.osrs;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.runemate.game.api.hybrid.entities.GameObject;

import scripts.mining.locations.Location;

public abstract class OSRSLocation extends Location{

	public OSRSLocation() {
		depositBlackList.add("pickaxe");
	}

	@Override
	public boolean validate(GameObject o) {
		Map<Color, Color> colors = new HashMap<Color, Color>();
		String name = "";
		try{
			colors = o.getDefinition().getColorSubstitutions();
			name = o.getDefinition().getName();
		}catch(NullPointerException e){}

		for (int i = 0; i < ore.colors.length; i++) {
			if(colors.containsValue(ore.colors[i]) && name.contains("Rock"))return true;
		}
		return false;
	}
}

