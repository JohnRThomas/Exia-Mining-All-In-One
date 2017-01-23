package exiabots.mining.locations.osrs;

import com.runemate.game.api.hybrid.location.Coordinate;

public class CoalTrucks extends OSRSLocation {

	@Override
	public void intialize(String ore) {
		rocks = new Coordinate[0];
	}

	@Override
	public String getName() {
		return "Coal Trucks";
	}

	@Override
	public String[] getOres() {
		return new String[0];
	}
}
