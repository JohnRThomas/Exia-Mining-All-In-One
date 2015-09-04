package scripts.mining.locations.osrs;

import com.runemate.game.api.hybrid.location.Coordinate;

public class Monastary extends OSRSLocation {

	@Override
	public void intialize(String ore) {
		rocks = new Coordinate[0];
	}

	@Override
	public String getName() {
		return "Monastary";
	}

	@Override
	public String[] getOres() {
		return new String[0];
	}
}
