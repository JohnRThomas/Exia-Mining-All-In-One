package scripts.mining.locations.rs3;

import com.runemate.game.api.hybrid.location.Coordinate;

import scripts.mining.locations.Location;

public class CoalTrucks extends Location {

	@Override
	public void intialize(String ore) {
		rocks = new Coordinate[0];
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Coal Trucks";
	}

	@Override
	public String[] getOres() {
		// TODO Auto-generated method stub
		return new String[0];
	}
}
