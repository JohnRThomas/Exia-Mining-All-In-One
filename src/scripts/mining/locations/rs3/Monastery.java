package scripts.mining.locations.rs3;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import scripts.mining.Rock;
import scripts.mining.locations.Location;

public class Monastery extends Location{
	
	@Override
	public void intialize(String ore){
		switch(ore){
		case "Iron":
			rocks = new Coordinate[] {new Coordinate(2605, 3237),new Coordinate(2602, 3236),new Coordinate(2602, 3233),new Coordinate(2602, 3234),new Coordinate(2605, 3235),new Coordinate(2605, 3233)};
			break;
		case "Coal":
			rocks = new Coordinate[] {new Coordinate(2608, 3223),new Coordinate(2604, 3223),new Coordinate(2607, 3224),new Coordinate(2603, 3224)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		mine = new Area.Rectangular(new Coordinate(2610, 3239), new Coordinate(2600, 3221));
		bank = new Area.Rectangular(new Coordinate(2648, 3287), new Coordinate(2655, 3280));
	}
		
	@Override
	public String getName() {
		return "Monastery";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Iron", "Coal"};
	}

	public boolean validate(GameObject rock) {
		GameObjectDefinition def = rock.getDefinition();
		String name = "";
		if(def != null)name = def.getName();

		return !name.equals("Rocks") && name.contains("rocks") && (name.contains("ore") || rock.getAnimationId() > 0);
	}
}
