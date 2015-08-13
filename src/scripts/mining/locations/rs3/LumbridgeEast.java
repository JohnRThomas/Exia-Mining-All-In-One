package scripts.mining.locations.rs3;

import scripts.mining.Rock;
import scripts.mining.locations.Location;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

public class LumbridgeEast extends Location {

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Tin":
			rocks = new Coordinate[]{new Coordinate(3231,3147),new Coordinate(3225,3146),new Coordinate(3225,3150),new Coordinate(3224,3146),new Coordinate(3223,3148)};
			break;
		case "Copper":
			rocks = new Coordinate[]{new Coordinate(3229,3146),new Coordinate(3230,3147),new Coordinate(3227,3145),new Coordinate(3228,3151),new Coordinate(3223,3150)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(3267,3169), new Coordinate(3273,3165));
		mine = new Area.Polygonal(new Coordinate(3224,3152), new Coordinate(3221,3149),new Coordinate(3221,3145), new Coordinate(3225,3143), new Coordinate(3230,3143), new Coordinate(3237,3153), new Coordinate(3229,3154));
	}

	@Override
	public String getName() {
		return "Lumbridge East";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Copper", "Tin"};
	}
}
