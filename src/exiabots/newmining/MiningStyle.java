package exiabots.newmining;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeBot;
import com.runemate.game.api.script.framework.tree.TreeTask;

import exiabots.mining.Rock;
import exiabots.mining.locations.Location;
import exiabots.newmining.GUI.ButtonBar;
import javafx.scene.layout.GridPane;

public abstract class MiningStyle extends TreeBot {
	public abstract GridPane getContentPane(ButtonBar buttonBar, boolean isRS3, boolean fullVersion);
	public abstract Rock getOre();
	public abstract String getLocationName();
	public abstract Coordinate[] getRockLocations();
	public abstract void loadSettings();

	
	protected class MiningBranch extends BranchTask {
		private TreeTask success;
		private TreeTask failure;
		Location location;
		
		public MiningBranch(Location location){
			this.location = location;
			failure = success = new WaitTask();	
		}

		@Override
		public boolean validate() {
			return true;
		}
		
		@Override
		public TreeTask failureTask() {
			return success;
		}

		@Override
		public TreeTask successTask() {
			return failure;
		}
	}
}
