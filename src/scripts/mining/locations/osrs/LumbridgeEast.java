package scripts.mining.locations.osrs;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

import scripts.mining.Rock;

public class LumbridgeEast extends OSRSLocation {

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Tin":
			rocks = new Coordinate[]{new Coordinate(3225,3148),new Coordinate(3224,3146),new Coordinate(3222,3147),new Coordinate(3223,3148),new Coordinate(3223,3146)};
			break;
		case "Copper":
			rocks = new Coordinate[]{new Coordinate(3228,3144),new Coordinate(3229,3148),new Coordinate(3230,3147),new Coordinate(3230,3145),new Coordinate(3229,3145)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(3091,3245), new Coordinate(3096,3241));
		mine = new Area.Polygonal(new Coordinate(3225,3148), new Coordinate(3224,3146),new Coordinate(3222,3147), new Coordinate(3223,3148), new Coordinate(3223,3146), new Coordinate(3228,3144), new Coordinate(3229,3148), new Coordinate(3230,3147), new Coordinate(3230,3145), new Coordinate(3229,3145));
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
