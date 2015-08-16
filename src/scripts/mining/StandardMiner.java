package scripts.mining;

import java.awt.Point;
import java.util.ArrayList;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import scripts.ExiaMinerAIO;
import scripts.mining.locations.Location;

public class StandardMiner extends MiningStyle{	
	int notMiningCount = 0;
	Location location;

	@Override
	public String getLocationName() {
		return location == null ? "Unknown" : location.getName();
	}

	@Override
	public Coordinate[] getRockLocations(){
		return location.getRocks();
	}

	@Override
	public Rock getOre() {
		return location == null || location.getOre() == null ? Rock.UNKNOWN : location.getOre();
	}

	@Override
	public void onStart(String... args) {
		ExiaMinerAIO.instance.getEventDispatcher().addListener(Paint.profitCounter);
		rockWatcher = new RockWatcher((GameObject rock) -> location.validate(rock), location.getRocks());
		rockWatcher.start();
		content = null;
	}

	@Override
	public void onStop() {
		if(rockWatcher != null){
			try{
				rockWatcher.interrupt();
			}catch(NullPointerException e){}
		}
	}

	@Override
	public void loop() {
		if(location.shouldBank()){
			if(location.inBank()){
				if(location.isBankOpen()){
					Paint.status = "Depositing items";
					location.deposit();
				}else{
					Paint.status = "Opening bank";
					location.openBank();
				}
			}else{
				Paint.status = "Walking to bank";
				location.walkToBank();
			}
		}else{
			if(location.inMine()){
				mine();
				if(Players.getLocal().getAnimationId() == -1)notMiningCount++;
				else notMiningCount = 0;

				if(notMiningCount >= 10){
					notMiningCount = 0;
					currentRock = null;
				}
			}else {
				if(location.isBankOpen()){
					Paint.status = "Closing bank";
					location.closeBank();
				}else{
					Paint.status = "Walking to mine";
					location.walkToMine();
				}
			}
		}
	}

	protected void mine() {
		if(currentRock == null || !currentRock.isValid()){
			currentRock = null;

			//Get a new rock
			LocatableEntity rock = location.getNextRock(currentRock);
			if(rock != null){
				Player me = Players.getLocal();
				if(rock.distanceTo(me) > 8){
					Paint.status = "Walking to rock";
					walkTo(rock);
				}else{
					turnAndClick(rock);
				}
			}else{
				if(outOfRegion()){
					BresenhamPath.buildTo(location.getRocks()[0]).step();
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
			hoverNext();
		}

	}

	public static Interactable next;
	private void hoverNext(){
		if(Inventory.getUsedSlots() == 27 && currentRock != null){
			next = location.firstStepToBank();
		}else{
			next = location.getNextRock(currentRock);
		}

		if(next == null){
			RockWatcher.Pair<Coordinate, Long, GameObject> pair = rockWatcher.nextRock();
			if(pair != null){
				next = pair.object;
			}
		}

		if(next != null){
			if(!next.contains(Mouse.getPosition())){
				ReflexAgent.delay();
				InteractablePoint pt = next.getInteractionPoint(new Point(Random.nextInt(-2,3), Random.nextInt(-2,3)));
				if(pt != null){
					Mouse.move(pt);
				}else{
					next.hover();
				}
			}else{
				if(next instanceof GameObject && ((GameObject) next).getVisibility() < 80){
					Camera.concurrentlyTurnTo((Camera.getYaw() + Random.nextInt(0, 360)) % 360);
				}
				if(Random.nextInt(0,100) < 5){
					InteractablePoint pt = next.getInteractionPoint(new Point(Random.nextInt(-2,3), Random.nextInt(-2,3)));
					if(pt != null){
						Mouse.move(pt);
					}else{
						next.hover();
					}
				}
				ReflexAgent.delay();
			}
		}
	}

	private GridPane content = null;

	@Override 
	public GridPane getContentPane(final Button startButton) {
		if(content != null)return content;
		content = new GridPane();
		content.setPadding(new Insets(25,3,25,3));
		content.setHgap(1.0);
		content.setVgap(1.0);

		ListView<String> locationList = new ListView<String>(); 
		locationList.setPrefWidth(167);

		ListView<String> oreList = new ListView<String>(); 
		oreList.setPrefWidth(167);

		FlowPane settings = new FlowPane();
		settings.setStyle("-fx-background-color: -fx-background-dark-hundred; -fx-border-color: -fx-flair; -fx-border-style: solid; -fx-border-width: 1;");
		settings.setPrefWrapLength(165);

		ObservableList<String> items = FXCollections.observableArrayList();

		ArrayList<Location> locations = getLocations();
		for (int i = 0; i < locations.size(); i++) {
			items.add(locations.get(i).getName());
		}
		locationList.setItems(items);

		locationList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int index = locationList.getSelectionModel().getSelectedIndex();
				Location loc = locations.get(index);
				location = loc;
				ObservableList<String> items = FXCollections.observableArrayList();

				for(String ore: loc.getOres()){
					items.add(ore);
				}
				oreList.setItems(items);
				oreList.getSelectionModel().clearSelection();

				settings.getChildren().clear();
				
				Node[] nodes = location.getSettingsNodes();
				for (int i = 0; i < nodes.length; i++) {
					settings.getChildren().add(nodes[i]);
				}

			}
		});

		oreList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null){
					location.intialize(newValue);
					startButton.setDisable(false);
					Node[] nodes = location.getSettingsNodes();

					settings.getChildren().clear();

					for (int i = 0; i < nodes.length; i++) {
						settings.getChildren().add(nodes[i]);
					}
				}
			}
		});

		final String LABEL_STYLE = "-fx-text-fill: -fx-flair-text; -fx-font-size: 15px; -fx-background-color: -fx-flair;";

		Label locLabel = new Label("Locations");
		locLabel.setStyle(LABEL_STYLE);
		locLabel.setAlignment(Pos.CENTER);
		locLabel.setPrefWidth(167);

		Label oreLabel = new Label("Ores");
		oreLabel.setStyle(LABEL_STYLE);
		oreLabel.setAlignment(Pos.CENTER);
		oreLabel.setPrefWidth(167);

		Label setLabel = new Label("Settings");
		setLabel.setStyle(LABEL_STYLE);
		setLabel.setAlignment(Pos.CENTER);
		setLabel.setPrefWidth(167);

		content.add(locLabel, 0, 0);
		content.add(locationList, 0, 1); 

		content.add(oreLabel, 1, 0);
		content.add(oreList, 1, 1);

		content.add(setLabel, 2, 0);
		content.add(settings, 2, 1);

		return content;
	}

	@Override
	public void loadSettings() {
		location.loadSettings();
	}

	public void removeNodeFromGrid(final int row, final int column, GridPane gridPane) {
		ObservableList<Node> children = gridPane.getChildren();
		for(int i = 0; i < children.size(); i++) {
			if(GridPane.getRowIndex(children.get(i)) == row && GridPane.getColumnIndex(children.get(i)) == column) {
				children.remove(i);
				break;
			}
		}
	}

	private ArrayList<Location> getLocations() {
		ArrayList<Location> locations = new ArrayList<Location>();
		if(Environment.isRS3()){
			locations.add(new scripts.mining.locations.rs3.AlKharid());
			if(Environment.isSDK())locations.add(new scripts.mining.locations.rs3.CoalTrucks());
			if(Environment.isSDK())locations.add(new scripts.mining.locations.rs3.DesertQuarry());
			locations.add(new scripts.mining.locations.rs3.DwarvenMine());
			locations.add(new scripts.mining.locations.rs3.DwarvenResourceMine());
			locations.add(new scripts.mining.locations.rs3.LivingRockCavern(this));
			locations.add(new scripts.mining.locations.rs3.LumbridgeEast());
			locations.add(new scripts.mining.locations.rs3.LumbridgeWest());
			if(Environment.isSDK())locations.add(new scripts.mining.locations.rs3.PiratesHideout());
			locations.add(new scripts.mining.locations.rs3.Rimmington());
			locations.add(new scripts.mining.locations.rs3.ShiloVillage());
			locations.add(new scripts.mining.locations.rs3.VarrockEast());
			locations.add(new scripts.mining.locations.rs3.VarrockWest());
		}else{
			locations.add(new scripts.mining.locations.osrs.AlKharid());
			locations.add(new scripts.mining.locations.osrs.VarrockEast());
			locations.add(new scripts.mining.locations.osrs.VarrockWest());
		}
		return locations;
	}
}
