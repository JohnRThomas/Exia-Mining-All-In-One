package scripts.mining.locations.rs3;

import scripts.mining.Rock;
import scripts.mining.locations.DepositLocation;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

public class DwarvenResourceMine extends DepositLocation{
	
	@Override
	public String getName() {
		return "Dwarf Res. Dungeon";
	}

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Coal":
			rocks = new Coordinate[]{new Coordinate(1063,4568),new Coordinate(1066,4570),new Coordinate(1067,4569),new Coordinate(1067,4570),new Coordinate(1069,4571),
									 new Coordinate(1068,4573),new Coordinate(1070,4575),new Coordinate(1067,4577),new Coordinate(1067,4580),new Coordinate(1068,4579),
									 new Coordinate(1066,4579),new Coordinate(1062,4579),new Coordinate(1063,4580)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		mine = new Area.Rectangular(new Coordinate(1071,4580), new Coordinate(1061,4568));
		bank = new Area.Rectangular(new Coordinate(1049,4580, 0), new Coordinate(1040,4571, 0));
		this.ore = Rock.getByName(ore);
	}

	@Override
	public String[] getOres() {
		return new String[]{"Coal"};
	}
}
