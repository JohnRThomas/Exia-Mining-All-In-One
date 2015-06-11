package scripts.mining;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public abstract class MiningStyle {
	
	public String status = "";	
		
	public abstract void onStart(String... args);
	public abstract void loop();
	public abstract void onStop();
	
	public abstract GridPane getContentPane(Button startButton);

	public abstract void loadSettings();

	public abstract Rock getOre();

	public abstract String getLocationName();

}
