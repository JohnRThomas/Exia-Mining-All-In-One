package scripts.mining.locations.rs3;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
import scripts.mining.Rock;
import scripts.mining.locations.Location;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

public class BarbarianVillage extends Location{
	
	@Override
	public void intialize(String ore){
		switch(ore){
		case "Tin":
			rocks = new Coordinate[] {new Coordinate(3080,3420),new Coordinate(3081,3419),new Coordinate(3079,3419),new Coordinate(3079,3421),new Coordinate(3082,3420)};
			break;
		case "Coal":
			rocks = new Coordinate[] {new Coordinate(3083,3421),new Coordinate(3081,3422),new Coordinate(3083,3420),new Coordinate(3082,3421)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		mine = new Area.Rectangular(new Coordinate(3078,3418), new Coordinate(3084,3423));
		bank = new Area.Rectangular(new Coordinate(3092,3489), new Coordinate(3098,3496));
	}
		
	@Override
	public String getName() {
		return "Barbarian Village";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Tin", "Coal"};
	}

	public boolean validate(GameObject rock) {
		GameObjectDefinition def = rock.getDefinition();
		String name = "";
		if(def != null)name = def.getName();

		return !name.equals("Rocks") && name.contains("rocks") && (name.contains("ore") || rock.getAnimationId() > 0);
	}
}
