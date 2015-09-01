package scripts.mining;

import com.runemate.game.api.hybrid.location.Coordinate;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class SpecialMiner extends MiningStyle{

	@Override
	public void onStart(String... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rock getOre() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocationName() {
		return null;
	}

	@Override
	public Coordinate[] getRockLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	private GridPane content = null;
	@Override
	public GridPane getContentPane(Button startButton) {
		if(content != null)return content;
		content = new GridPane();
		content.setPadding(new Insets(25,3,25,3));
		content.setHgap(1.0);
		content.setVgap(1.0);

		return content;
	}

	@Override
	public void loadSettings() {
		// TODO Auto-generated method stub
		
	}
	
}
