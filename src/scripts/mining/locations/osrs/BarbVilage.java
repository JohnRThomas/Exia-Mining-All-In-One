package scripts.mining.locations.osrs;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import scripts.mining.Rock;

public class BarbVilage extends OSRSLocation{
	
	@Override
	public void intialize(String ore){
		switch(ore){
		case "Tin":
			rocks = new Coordinate[] {new Coordinate(3080,3420),new Coordinate(3082,3420),new Coordinate(3079,3421),new Coordinate(3081,3419),new Coordinate(3080,3418)};
			break;
		case "Coal":
			rocks = new Coordinate[] {new Coordinate(3082,3421),new Coordinate(3081,3422),new Coordinate(3083,3421),new Coordinate(3083,3420)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		mine = new Area.Rectangular(new Coordinate(3078,3423), new Coordinate(3084,3417));
		bank = new Area.Rectangular(new Coordinate(3092,3489), new Coordinate(3098,3495));
	}
		
	@Override
	public String getName() {
		return "Barbarian Village";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Tin", "Coal"};
	}
}
