package scripts.mining.locations.osrs;

import scripts.mining.Rock;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

public class VarrockEast extends OSRSLocation{
	
	@Override
	public void intialize(String ore){
		switch(ore){
		case "Tin":
			rocks = new Coordinate[] {new Coordinate(3281,3363),new Coordinate(3282,3364)};
			break;
		case "Copper":
			rocks = new Coordinate[] {new Coordinate(3282,3368),new Coordinate(3282,3369)};
			break;
		case "Iron":
			rocks = new Coordinate[]{new Coordinate(3286,3369),new Coordinate(3285,3369),new Coordinate(3288,3370),new Coordinate(3285,3368)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		mine = new Area.Rectangular(new Coordinate(3280,3360), new Coordinate(3291,3371));
		bank = new Area.Rectangular(new Coordinate(3250,3418, 0), new Coordinate(3257,3423, 0));
	}
		
	@Override
	public String getName() {
		return "Varrock East";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Copper", "Tin", "Iron"};
	}
}
