package scripts.mining.locations.rs3;

import scripts.mining.Rock;
import scripts.mining.locations.Location;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

public class VarrockEast extends Location{

	@Override
	public void intialize(String ore){
		switch(ore){
		//TODO tin
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
		mine = new Area.Rectangular(new Coordinate(3282,3366), new Coordinate(3290,3372));
		bank = new Area.Rectangular(new Coordinate(3250,3418, 0), new Coordinate(3257,3423, 0));
	}
	
	@Override
	public String getName() {
		return "Varrock East";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Copper", "Iron"};
	}
	
	@Override
	public Coordinate[] getRocks() {
		return rocks;
	}
}
