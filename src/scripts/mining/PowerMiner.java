package scripts.mining;

import java.awt.Point;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.rs3.local.InterfaceMode;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionBar.Slot;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.ActionWindow;
import com.runemate.game.api.rs3.local.hud.interfaces.eoc.SlotAction;
import com.runemate.game.api.rs3.local.hud.interfaces.legacy.LegacyTab;
import com.runemate.game.api.script.Execution;

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
import scripts.mining.RockWatcher.Pair;
import scripts.mining.RockWatcher.Validater;

public class PowerMiner extends MiningStyle{

	private boolean dropping;
	private boolean mine1drop1 = false;
	private boolean forceKeys = false;
	private boolean actionBar = false;
	int radius = 10;
	Coordinate center = null;
	int notMiningCount = 0;
	private Rock ore;

	@Override
	public void onStart(String... args) {
		if(Environment.isRS3()){
			if(ore.name.equals("Granite")){
				rockWatcher = new RockWatcher(new Validater(){

					@Override
					public boolean validate(GameObject o) {
						return o != null && o.getDefinition() != null && o.getDefinition().getName() != null &&
								o.getId() != 2560 && o.getDefinition().getName().contains("rocks");
					}
					
					}, new Coordinate[]{});
			}else if(ore.name.equals("SandStone")){
				rockWatcher = new RockWatcher(new Validater(){

					@Override
					public boolean validate(GameObject o) {
						return o != null && o.getDefinition() != null && o.getDefinition().getName() != null &&
								o.getId() != 2551 && o.getDefinition().getName().contains("rocks");
					}
					
					}, new Coordinate[]{});			}else{
				rockWatcher = new RockWatcher((GameObject rock) -> validateRS3(rock), new Coordinate[]{});
			}
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
		if(shouldDrop()){
			dropping = true;
			drop();
		}else{
			mine();
			if(Players.getLocal().getAnimationId() == -1)notMiningCount++;
			else notMiningCount = 0;

			if(notMiningCount >= 30){
				notMiningCount = 0;
				currentRock = null;
			}
		}
	}

	private void mine() {
		//If the inventory was not initially open, close it
		if(actionBar && closeInv && InterfaceWindows.getInventory().isOpen()){
			if(Environment.isRS3()){
				if(InterfaceMode.getCurrent() == InterfaceMode.LEGACY){
					LegacyTab.BACKPACK.close();
				}else{
					ActionWindow.BACKPACK.close();
				}
				ReflexAgent.delay();
				return;
			}
		}

		if(currentRock == null || !currentRock.isValid()){
			currentRock = null;

			//Get a new rock
			GameObject rock = getNextRock();
			if(rock != null){
				rockWatcher.addLocation(rock.getPosition());
				Player me = Players.getLocal();
				if(rock.distanceTo(me) > 16){
					walkTo(rock);
				}else{
					turnAndClick(rock);
				}
			}else{
				Paint.status = "Preparing for respawn";
				//if there are no new rocks to get, walk to the next spawning rock
				walkToNextEmpty();
				Paint.status = "Waiting for respawn";
			}
		}else{
			Paint.status = "Mining";
			if(!Players.getLocal().isMoving() && currentRock != null && currentRock.getVisibility() < 80){
				Camera.concurrentlyTurnTo((Camera.getYaw() + Random.nextInt(0, 360)) % 360);
			}
			hoverNext();
		}
	}

	private void hoverNext(){
		if(!actionBar && mine1drop1){
			SpriteItemQueryResults items = Inventory.getItems();
			boolean[] open = new boolean[28];
			for(SpriteItem i : items){
				open[i.getIndex()] = true;
			}
			int i = 0;
			for(; i < 28; i++){
				if(!open[i]){
					break;
				}
			}
			InteractablePoint pt = Inventory.getBoundsOf(i).getInteractionPoint(new Point(Random.nextInt(-1,1), Random.nextInt(-1,1)));
			if(pt != null){
				ReflexAgent.delay();
				ReflexAgent.delay();
				ReflexAgent.delay();
				Mouse.move(pt);
			}else{
				Inventory.getBoundsOf(i).hover();
			}
			ReflexAgent.delay();
			return;
		}
		
		LocatableEntity rock = getNextRock();
		if(rock == null){
			Pair<Coordinate, Long, GameObject> pair = rockWatcher.nextRock();
			rock = pair == null ? null : pair.object;
		}

		if(rock != null){
			if(!rock.contains(Mouse.getPosition())){
				ReflexAgent.delay();
				InteractablePoint pt = rock.getInteractionPoint(new Point(Random.nextInt(-2,3), Random.nextInt(-2,3)));
				if(pt != null){
					Mouse.move(pt);
				}else{
					rock.hover();
				}
			}else{
				if(rock instanceof GameObject && ((GameObject) rock).getVisibility() < 80){
					Camera.concurrentlyTurnTo((Camera.getYaw() + Random.nextInt(0, 360)) % 360);
				}
				if(Random.nextInt(0,100) < 5){
					InteractablePoint pt = rock.getInteractionPoint(new Point(Random.nextInt(-2,3), Random.nextInt(-2,3)));
					if(pt != null){
						Mouse.move(pt);
					}else{
						rock.hover();
					}
				}
				ReflexAgent.delay();
			}
		}
	}

	private GameObject getNextRock() {
		LocatableEntityQueryResults<GameObject> rocksObjs = null;
		try{
			rocksObjs = GameObjects.getLoaded(new Filter<GameObject>(){
				@Override
				public boolean accepts(GameObject o) {
					if(o.equals(currentRock))
						return false;
					else
						return o.getDefinition().getName().contains(ore.name) && o.distanceTo(center) <= radius && rockWatcher.validater.validate(o);
				}
			}).sortByDistance();
		}catch(Exception e){}

		if(rocksObjs != null && rocksObjs.size() > 0) return rocksObjs.get(0);
		return null;
	}

	private boolean shouldDrop() {
		SpriteItemQueryResults items = Inventory.getItems(new Filter<SpriteItem>(){
			@Override
			public boolean accepts(SpriteItem i) {
				return ore.exps.containsKey(i.getDefinition().getName());
			}
		});

		return !items.isEmpty() && (mine1drop1 || (Inventory.isFull() || dropping));
	}

	private boolean closeInv = false;
	private void drop() {
		SpriteItemQueryResults items = Inventory.getItems(new Filter<SpriteItem>(){
			@Override
			public boolean accepts(SpriteItem i) {
				return ore.exps.containsKey(i.getDefinition().getName());
			}
		});

		if(items.isEmpty()){
			//no need to keep dropping
			dropping = false;
			return;
		}
		
		Paint.status = "Dropping";

		if(actionBar){
			//check if the action bar contains the ore we need to drop
			for(Slot slot : ActionBar.Slot.values()){
				if(slot.getAction() != null){
					SlotAction action = slot.getAction();
					if(action.getItem() != null && ore.exps.containsKey(action.getItem().getName())){
						//drop each of that item
						if(action.isActivatable()){
							if(forceKeys) 
								action.activate(false);
							else{
								action.activate();
							}

							//If this player spams, then make them click twice
							if(Random.nextInt(100) <= PlayerSense.getAsInteger(CustomPlayerSense.Key.ACTION_BAR_SPAM.playerSenseKey))
								if(forceKeys) 
									action.activate(false);
								
							ReflexAgent.delay();
							return;
						}
					}
				}
			}
			Paint.status = "Setting up action bar";
			//at this point, we didn't find it, so drag it over
			if(InterfaceWindows.getInventory().isOpen()){
				//find the first open action slot
				for(Slot slot : ActionBar.Slot.values()){
					//This indicates that this slot if not an ore dropping slot, so it's ok to overwrite it
					if(slot.getAction() == null || slot.getAction().getItem() == null || !ore.exps.containsKey(slot.getAction().getItem().getName())){
						Mouse.drag(items.first(), slot.getComponent());
						//Wait 2-4 seconds for the item to appear on the action bar
						Timer timer = new Timer(Random.nextInt(2000,4000));
						timer.start();
						while(timer.getRemainingTime() > 0 && slot.getAction() == null){
							Execution.delay(10);
						}
						ReflexAgent.delay();
						break;
					}
				}
			}else{
				InterfaceWindows.getInventory().open();
				closeInv = true;
			}

		}else{
			//Find ore in inventory
			if(InterfaceWindows.getInventory().isOpen()){
				items.get(0).interact("Drop");
				Execution.delay(ReflexAgent.getReactionTime()*4);
			}else{
				InterfaceWindows.getInventory().open();
				closeInv = true;
			}
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
	CheckBox forceNoClick = new CheckBox("Force keyboard for action bar");
	CheckBox radLabel = new CheckBox("Radius:");
	TextField radText = new TextField("10");

	@Override
	public void loadSettings() {
		mine1drop1 = mineOne.isSelected();
		actionBar  = hotkeys.isSelected();
		if(!ore.name.contains("Sandstone") && !ore.name.contains("Granite"))
			forceKeys  = forceNoClick.isSelected();
		try{
			radius = radLabel.isSelected() ? Integer.parseInt(radText.getText()) : 1000;
		}catch(NumberFormatException e){}
		center = Players.getLocal().getPosition();
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
		mineOne.setSelected(true);
		mineOne.setStyle("-fx-text-fill: -fx-text-input-text");
		mineOne.setPadding(new Insets(10,160,0,5));
		settings.getChildren().add(mineOne);

		hotkeys.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				forceNoClick.setDisable(!newValue);
			}
		});

		hotkeys.setSelected(true);
		hotkeys.setStyle("-fx-text-fill: -fx-text-input-text");
		hotkeys.setPadding(new Insets(10,160,0,5));
		settings.getChildren().add(hotkeys);

		forceNoClick.setDisable(!hotkeys.isSelected());
		forceNoClick.setSelected(true);
		forceNoClick.setStyle("-fx-text-fill: -fx-text-input-text");
		forceNoClick.setPadding(new Insets(10,100,0,5));
		settings.getChildren().add(forceNoClick);

		radLabel.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				radText.setDisable(!newValue);
			}
		});
		radLabel.setStyle("-fx-text-fill: -fx-text-input-text");
		radLabel.setPadding(new Insets(0,5,0,5));
		settings.getChildren().add(radLabel);
		
		radText.setDisable(!radLabel.isSelected());
		radText.setStyle("-fx-text-fill: -fx-text-input-text");
		radText.setMaxWidth(35.0f);
		radText.setPadding(new Insets(3,5,2,5));
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
