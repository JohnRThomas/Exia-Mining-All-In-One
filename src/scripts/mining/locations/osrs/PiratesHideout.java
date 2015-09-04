package scripts.mining.locations.osrs;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

import scripts.mining.Rock;

public class PiratesHideout extends OSRSLocation{

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Iron":
			rocks = new Coordinate[]{new Coordinate(3036,9775),new Coordinate(3037,9776),new Coordinate(3039,9777)};
			break;
		case "Coal":
			rocks = new Coordinate[]{new Coordinate(3037,9763),new Coordinate(3039,9762),new Coordinate(3039,9761),new Coordinate(3041,9760),new Coordinate(3046,9762)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(1049,4580, 0), new Coordinate(1040,4571, 0));
		mine = new Area.Rectangular(new Coordinate(3048,9780, 0), new Coordinate(3034,9759, 0));
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Pirate's Hideout";
	}

	@Override
	public String[] getOres() {
		// TODO Auto-generated method stub
		return null;
	}
}
