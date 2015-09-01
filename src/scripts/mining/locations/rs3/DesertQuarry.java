package scripts.mining.locations.rs3;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

import scripts.mining.Rock;
import scripts.mining.locations.Location;

public class DesertQuarry extends Location{

	@Override
	public void intialize(String ore) {
		switch(ore){
		//TODO
		case "Clay":
			rocks = new Coordinate[]{new Coordinate(3231,3147),new Coordinate(3225,3146),new Coordinate(3225,3150),new Coordinate(3224,3146),new Coordinate(3223,3148)};
			break;
		case "Sandstone":
			rocks = new Coordinate[]{new Coordinate(3229,3146),new Coordinate(3230,3147),new Coordinate(3227,3145),new Coordinate(3228,3151),new Coordinate(3223,3150)};
			break;
		case "Granite":
			rocks = new Coordinate[]{new Coordinate(3229,3146),new Coordinate(3230,3147),new Coordinate(3227,3145),new Coordinate(3228,3151),new Coordinate(3223,3150)};
			break;
		case "Coal":
			rocks = new Coordinate[]{new Coordinate(3229,3146),new Coordinate(3230,3147),new Coordinate(3227,3145),new Coordinate(3228,3151),new Coordinate(3223,3150)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(3267,3169), new Coordinate(3273,3165));
		mine = new Area.Polygonal(new Coordinate(3224,3152), new Coordinate(3221,3149),new Coordinate(3221,3145), new Coordinate(3225,3143), new Coordinate(3230,3143), new Coordinate(3237,3153), new Coordinate(3229,3154));
	}

	@Override
	public String getName() {
		return "Desert Quarry";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Clay", "Sandstone", "Granite", "Coal"};
	}

	@Override
	public boolean validate(GameObject rock) {
		String name = "";
		int id = 0;
		try{
			id = rock.getId();
			name = rock.getDefinition().getName();
		}catch(NullPointerException e){}
		
		if(ore.name.equals("Granite")){
			return id != 2560 && name.contains("rocks");
		}else if(ore.name.equals("SandStone")){
			return id != 2551 && name.contains("rocks");
		}else{
			return super.validate(rock);
		}
	}
}
