package scripts.mining;

import java.util.List;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.SlotAction;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class PowerMiner extends MiningStyle{

	private boolean dropping;
	private boolean mine1drop1 = false;
	private boolean actionBar = false;
	int radius = 10;
	int notMiningCount = 0;
	private Rock ore;

	@Override
	public void onStart(String... args) {
		if(Environment.isRS3()){
			rockWatcher = new RockWatcher((GameObject rock) -> validateRS3(rock), new Coordinate[]{});
		}else{
			rockWatcher = new RockWatcher((GameObject rock) -> validateOSRS(rock), new Coordinate[]{});
		}
		rockWatcher.start();
		content = null;
	}

	@Override
	public void onStop() {
		if(rockWatcher != null){
			rockWatcher.interrupt();
		}
	}

	@Override
	public String getLocationName() {
		return "Power Mining";
	}

	@Override
	public Coordinate[] getRockLocations(){
		return rockWatcher.getLocations();
	}
	
	@Override
	public Rock getOre() {
		return ore;
	}
	
	@Override
	public void loop() {
		if(Inventory.isFull() || dropping || mine1drop1){
			status = "Dropping";
			dropping = true;
			drop();
		}else{
			status = "Mining";
			mine();
			if(Players.getLocal().getAnimationId() == -1)notMiningCount++;
			else notMiningCount = 0;

			if(notMiningCount >= 15){
				notMiningCount = 0;
				currentRock = null;
			}
		}
	}

	private void mine() {
		if(currentRock == null || !currentRock.isValid()){
			currentRock = null;

			//Get a new rock
			LocatableEntity rock = getNextRock();
			if(rock != null){
				Player me = Players.getLocal();
				if(rock.distanceTo(me) > 16){
					Paint.status = "Walking to rock";
					walkTo(rock);
				}else{
					if(!turnAndClick(rock))return;
				}
			}else{
				if(outOfRegion()){
					BresenhamPath.buildTo(rockWatcher.getLocations()[0]).step();
				}else{
					Paint.status = "Preparing for respawn";
					//if there are no new rocks to get, walk to the next spawning rock
					walkToNextEmpty();
					Paint.status = "Waiting for respawn";
				}
			}
		}else{
			Paint.status = "Mining";
			if(currentRock != null && currentRock.getVisibility() < 80){
				Camera.concurrentlyTurnTo((Camera.getYaw() + Random.nextInt(0, 360)) % 360);
			}
			
		}
	}

	private LocatableEntity getNextRock() {
		//TODO figure out which rock to go to next
		return null;
	}

	private void drop() {
		if(Inventory.isEmpty() || mine1drop1)dropping = false;
		if(actionBar){
			//check if the action bar contains the ore we need to drop
			List<SlotAction> actions = ActionBar.getActions();
			for(SlotAction action : actions){
				if(action.getName().toLowerCase().contains(ore.name.toLowerCase())){
					//if we found it, then drop it.
					action.activate();
					return;
				}
			}
			
			//at this point, we didn't find it, so drag it over
			//TODO add or to action bar
		}else{
			//Find ore in inventory
			//TODO click the ore in the inventory to drop it.
		}
	}

	public boolean validateRS3(GameObject rock) {
		return rock != null && rock.getDefinition() != null && rock.getDefinition().getName() != null &&
				!rock.getDefinition().getName().equals("Rocks") && rock.getDefinition().getName().contains("rocks");
	}

	public boolean validateOSRS(GameObject o) {
		int id = o.getId();
		for (int i = 0; i < ore.ids.length; i++) {
			if(ore.ids[i] == id)return true;
		}
		return false;
	}
	
	private GridPane content = null;

	CheckBox mineOne = new CheckBox("Mine one drop one");
	CheckBox hotkeys = new CheckBox("Use Action Bar");
	TextField radText = new TextField("10");

	@Override
	public void loadSettings() {
		mine1drop1  = mineOne.isSelected();
		actionBar  = hotkeys.isSelected();
		try{
			radius = Integer.parseInt(radText.getText());
		}catch(NumberFormatException e){}
	}

	@Override
	public GridPane getContentPane(Button startButton) {
		if(content != null)return content;
		content = new GridPane();
		content.setPadding(new Insets(25,3,25,3));
		content.setHgap(1.0);
		content.setVgap(1.0);


		ListView<String> oreList = new ListView<String>(); 
		oreList.setPrefWidth(167);

		FlowPane settings = new FlowPane();
		settings.setStyle("-fx-background-color: -fx-background-dark-hundred; -fx-border-color: -fx-flair; -fx-border-style: solid; -fx-border-width: 1;");
		settings.setPrefWrapLength(335);

		ObservableList<String> ores = Rock.getOres(Environment.isRS3());
		oreList.setItems(ores);
		oreList.getSelectionModel().clearSelection();

		oreList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null){
					startButton.setDisable(false);
					ore = Rock.getByName(newValue);
				}
			}
		});

		mineOne.setStyle("-fx-text-fill: -fx-text-input-text");
		mineOne.setPadding(new Insets(10,160,0,5));
		settings.getChildren().add(mineOne);
		
		hotkeys.setStyle("-fx-text-fill: -fx-text-input-text");
		hotkeys.setPadding(new Insets(10,160,0,5));
		settings.getChildren().add(hotkeys);

		Label radLabel = new Label("Radius:");
		radLabel.setStyle("-fx-text-fill: -fx-text-input-text");
		radLabel.setPadding(new Insets(0,5,0,5));
		settings.getChildren().add(radLabel);

		radText.setStyle("-fx-text-fill: -fx-text-input-text");
		radText.setMaxWidth(35.0f);
		radText.setPadding(new Insets(1,1,1,5));
		settings.getChildren().add(radText);

		final String LABEL_STYLE = "-fx-text-fill: -fx-flair-text; -fx-font-size: 15px; -fx-background-color: -fx-flair;";

		Label oreLabel = new Label("Ores");
		oreLabel.setStyle(LABEL_STYLE);
		oreLabel.setAlignment(Pos.CENTER);
		oreLabel.setPrefWidth(167);

		Label setLabel = new Label("Settings");
		setLabel.setStyle(LABEL_STYLE);
		setLabel.setAlignment(Pos.CENTER);
		setLabel.setPrefWidth(337);

		content.add(oreLabel, 0, 0);
		content.add(oreList, 0, 1);

		content.add(setLabel, 1, 0);
		content.add(settings, 1, 1);

		return content;
	}
}
