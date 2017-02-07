package exiabots.newmining;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.script.framework.tree.TreeBot;

import exiabots.mining.Rock;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public abstract class MiningStyle extends TreeBot {
	public abstract GridPane getContentPane(Button startButton);
	public abstract Rock getOre();
	public abstract String getLocationName();
	public abstract Coordinate[] getRockLocations();

}
