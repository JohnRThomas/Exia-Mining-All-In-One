package scripts.mining.locations;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;

public class EastVarrockIron extends Location{
	private String ore;
	private Coordinate[] rocks;
	
	@Override
	public void intialize(String ore){
		switch(ore){
		case "Iron":
			rocks = new Coordinate[]{new Coordinate(3286,3369),new Coordinate(3285,3369),new Coordinate(3288,3370),new Coordinate(3285,3368)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = ore;
		pathBuilder.setWebWalker(Traversal.getDefaultWeb());
		mine = new Area.Rectangular(new Coordinate(3282,3366), new Coordinate(3290,3372));
		bank = new Area.Rectangular(new Coordinate(3250,3418, 0), new Coordinate(3257,3423, 0));
	}
	
	@Override
	public String getOre() {
		return ore;
	}
	
	@Override
	public String getName() {
		return "East Varrock";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Iron"};
	}
	
	@Override
	public Coordinate[] getRocks() {
		return rocks;
	}
}
