package scripts.mining.locations;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.region.Players;

public class WestVarrockIron extends Location{
	
	private Coordinate[] rocks;
	private String ore;
	
	@Override
	public void intialize(String ore){
		switch(ore){
		case "Iron":
			rocks = new Coordinate[] {new Coordinate(3181,3373),new Coordinate(3175,3368),new Coordinate(3175,3366)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = ore;
		
		pathBuilder.setWebWalker(Traversal.getDefaultWeb());
		bank = new Area.Rectangular(new Coordinate(3181,3436, 0), new Coordinate(3190,3439, 0));
		mine = new Area.Rectangular(new Coordinate(3172,3364), new Coordinate(3183,3375));
	}
	
	@Override
	public String getOre() {
		return ore;
	}
	
	@Override
	public String getName() {
		return "West Varrock";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Iron"};
	}
	
	@Override
	public Coordinate[] getRocks() {
		return rocks;
	}

	@Override
	public boolean inBank() {
		if(bank.contains(Players.getLocal())){
			bankPath = null;
			return true;
		}else return false;
	}

	@Override
	public boolean inMine() {
		if(mine.contains(Players.getLocal())){
			minePath = null;
			return true;
		}else return false;
	}
}
