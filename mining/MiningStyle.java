package scripts.mining;

import javafx.scene.layout.GridPane;
import scripts.mining.locations.Location;

public abstract class MiningStyle {
	
	public Location location;
	public String status = "";	
	
	public void setLocation(Location loc){
		location = loc;
	}
	
	public abstract void onStart(String... args);
	public abstract void loop();
	public abstract void onStop();
	
	public abstract GridPane getContentPane(AIOMinerGUI root);

	public abstract void loadSettings();

}
