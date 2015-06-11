package scripts.mining.locations;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;

public class AlKharid extends Location{
	private String ore;
	private Coordinate[] rocks;

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Tin":
			rocks = new Coordinate[]{new Coordinate(3295,3311)};
			break;
		case "Copper":
			rocks = new Coordinate[]{new Coordinate(3297,3315),new Coordinate(3301,3318)};
			break;
		case "Iron":
			rocks = new Coordinate[]{};
			break;
		case "Silver":
			rocks = new Coordinate[]{new Coordinate(3295,3303),new Coordinate(3294,3301),new Coordinate(3303,3312),new Coordinate(3303,3313),new Coordinate(3293,3300)};
			break;
		case "Coal":
			rocks = new Coordinate[]{new Coordinate(3301,3300),new Coordinate(3302,3299)};
			break;
		case "Gold":
			rocks = new Coordinate[]{new Coordinate(3296,3287),new Coordinate(3297,3288)};
			break;
		case "Mithril":
			rocks = new Coordinate[]{new Coordinate(3303,3314),new Coordinate(3303,3305),new Coordinate(3303,3304)};
			break;
		case "Adamantite":
			rocks = new Coordinate[]{new Coordinate(3300,3317),new Coordinate(3299,3317)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = ore;
		pathBuilder.setWebWalker(Traversal.getDefaultWeb());
		bank = new Area.Rectangular(new Coordinate(3267,3169), new Coordinate(3273,3165));
		mine = new Area.Rectangular(new Coordinate(3290,3319), new Coordinate(3305,3285));
	}

	@Override
	public String getName() {
		return "Al Kharid";
	}

	@Override
	public String getOre() {
		return ore;
	}

	@Override
	public String[] getOres() {
		return new String[]{"Tin", "Copper", /*"Iron",*/ "Silver", "Coal", "Gold", "Mithril", "Adamantite"};
	}

	@Override
	public Coordinate[] getRocks() {
		return rocks;
	}
}
