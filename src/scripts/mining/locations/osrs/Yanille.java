package scripts.mining.locations.osrs;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import scripts.mining.Rock;

public class Yanille extends OSRSLocation{
	
	@Override
	public void intialize(String ore){
		switch(ore){
			case "Iron":
				rocks = new Coordinate[] {new Coordinate(2628,3140),new Coordinate(2628,3141),new Coordinate( 2627,3142 )};
				break;
			case "Tin":
				rocks = new Coordinate[] {new Coordinate(2630, 3150, 0),new Coordinate( 2630, 3148, 0 ), new Coordinate( 2631, 3147 ), new Coordinate( 2632, 3141 ), new Coordinate( 2632, 3142 ), new Coordinate( 2630, 3140, 0 )};
				break;
			case "Clay":
				rocks = new Coordinate[] {new Coordinate(2632, 3139),new Coordinate( 2630,3143 ),new Coordinate( 2629, 3143 ), new Coordinate( 2629, 3145 )};
				break;
			default:
				throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		mine = new Area.Rectangular(new Coordinate(2623,3135,0), new Coordinate(2634,3150,0));
		bank = new Area.Rectangular(new Coordinate(2608,3087,0), new Coordinate(2616,3098,0));
	}
		
	@Override
	public String getName() {
		return "Yanille";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Clay", "Iron", "Tin"};
	}

	@Override
	public boolean validate(GameObject rock ) {
		GameObjectDefinition def = rock.getDefinition();
		String name = "";
		if(def != null)name = def.getName();

		return name.equals("Rocks") && !name.contains("rocks") && ( def != null && !def.getColorSubstitutions().isEmpty() ) ;
	}
}
