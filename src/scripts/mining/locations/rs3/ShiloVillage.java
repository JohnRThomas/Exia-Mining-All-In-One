package scripts.mining.locations.rs3;

import scripts.mining.Rock;
import scripts.mining.locations.Location;

import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;

public class ShiloVillage extends Location{

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Gems":
			rocks = new Coordinate[]{new Coordinate(2823,3002),new Coordinate(2821,3000),new Coordinate(2825,3003),new Coordinate(2823,2999),new Coordinate(2825,3001),new Coordinate(2820,2998),new Coordinate(2821,2998)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(2850,2956), new Coordinate(2854,2952));
		mine = new Area.Polygonal(new Coordinate(2818,2996), new Coordinate(2827,2996),new Coordinate(2827,3005), new Coordinate(2823,3004), new Coordinate(2818,3000));
	}

	@Override
	public String getName() {
		return "Shilo Village";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Gems"};
	}
	
	@Override
	protected LocatableEntityQueryResults<? extends LocatableEntity> getBanker(){
		return Npcs.getLoaded("Banker");
	}
}
