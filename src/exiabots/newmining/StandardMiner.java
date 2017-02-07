package exiabots.newmining;

import java.util.ArrayList;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.LeafTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

import exiabots.mining.Rock;
import exiabots.mining.locations.Location;
import exiabots.newmining.GUI.ButtonBar;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class StandardMiner extends MiningStyle {
	int notMiningCount = 0;
	private int urnAmount = 1;
	Location location;

	boolean onlyWalkToBank = false;
	
	@Override
	public TreeTask createRootTask() {
		location.loadSettings();
		
		TreeTask tree = new InventoryBranch(location);
		
		if(Environment.isRS3()){
			if(porterBox.isSelected()){
				//location.depositBlackList.add("porter");
				//tree = new PorterManagerBranch(tree);
			}
			
			if(urnBox.isSelected()){
				try{
					//location.depositBlackList.add("mining urn");
					//int urnAmount = Integer.parseInt(urnText.getText());
					//tree = new UrnManagerBranch(tree, urnAmount);
				}catch(NumberFormatException e){
					urnAmount = 0;
				}
			}
		}

		onlyWalkToBank = walkBox.isSelected();
		
		return tree;
	}
	
	private GridPane content = null;
	CheckBox urnBox = new CheckBox("Use urns: ");
	TextField urnText = new TextField("" + urnAmount);
	CheckBox porterBox = new CheckBox("Use porters");
	CheckBox walkBox = new CheckBox("Walk when heavy");
	
	@Override
	public void loadSettings() {
		// TODO Auto-generated method stub
		
	}
	
	@Override 
	public GridPane getContentPane(ButtonBar buttonBar, boolean isRS3, boolean fullVersion) {
		if(content != null)return content;
		content = new GridPane();
		content.setPadding(new Insets(0,3,25,3));
		content.setHgap(1.0);
		content.setVgap(1.0);

		ListView<String> locationList = new ListView<String>(); 
		locationList.setPrefWidth(167);

		ListView<String> oreList = new ListView<String>(); 
		oreList.setPrefWidth(167);

		FlowPane settings = new FlowPane();
		settings.setPrefWrapLength(165);

		ObservableList<String> items = FXCollections.observableArrayList();

		ArrayList<Location> locations = getLocations(isRS3, fullVersion);
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

				populateOptions(settings, isRS3);
			}
		});

		oreList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null){
					location.intialize(newValue);
					buttonBar.setDisabled(1, false);
					populateOptions(settings, isRS3);
				}
			}
		});

		Label locLabel = new Label("Locations");
		locLabel.setAlignment(Pos.CENTER);
		locLabel.setPrefWidth(167);

		Label oreLabel = new Label("Ores");
		oreLabel.setAlignment(Pos.CENTER);
		oreLabel.setPrefWidth(167);

		Label setLabel = new Label("Settings");
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

	private void  populateOptions(FlowPane settings, boolean isRS3){
		Node[] nodes = location.getSettingsNodes();
		settings.getChildren().clear();

		for (int i = 0; i < nodes.length; i++) {
			settings.getChildren().add(nodes[i]);
		}

		if(isRS3){
			porterBox.setPadding(new Insets(10,50,0,5));
			settings.getChildren().add(porterBox);

			urnBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					urnText.setDisable(!newValue);
				}
			});

			urnBox.setPadding(new Insets(10,5,0,5));
			settings.getChildren().add(urnBox);

			urnText.setDisable(!urnBox.isSelected());
			urnText.setMaxWidth(35.0f);
			urnText.setPadding(new Insets(3,5,2,5));
			settings.getChildren().add(urnText);

		}

		walkBox.setPadding(new Insets(10,10,0,5));
		settings.getChildren().add(walkBox);
	}

	@Override
	public Rock getOre() {
		return location.getOre();
	}

	@Override
	public String getLocationName() {
		return location.getName();
	}

	@Override
	public Coordinate[] getRockLocations() {
		return location.getRocks();
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

	private ArrayList<Location> getLocations(boolean isRS3, boolean fullVersion) {
		ArrayList<Location> locations = new ArrayList<Location>();
		if(isRS3){
			locations.add(new exiabots.mining.locations.rs3.AlKharid());
			locations.add(new exiabots.mining.locations.rs3.BarbarianVillage());
			if(fullVersion)locations.add(new exiabots.mining.locations.rs3.CoalTrucks());
			if(fullVersion)locations.add(new exiabots.mining.locations.rs3.DesertQuarry());
			locations.add(new exiabots.mining.locations.rs3.DwarvenMine());
			locations.add(new exiabots.mining.locations.rs3.DwarvenResourceMine());
			locations.add(new exiabots.mining.locations.rs3.LegendsGuild());
			//locations.add(new exiabots.mining.locations.rs3.LivingRockCavern(this));
			locations.add(new exiabots.mining.locations.rs3.LumbridgeEast());
			locations.add(new exiabots.mining.locations.rs3.LumbridgeWest());
			//locations.add(new exiabots.mining.locations.rs3.MiningGuild(rockWatcher, this));
			locations.add(new exiabots.mining.locations.rs3.Monastery());
			if(fullVersion)locations.add(new exiabots.mining.locations.rs3.PiratesHideout());
			locations.add(new exiabots.mining.locations.rs3.Rimmington());
			locations.add(new exiabots.mining.locations.rs3.ShiloVillage());
			locations.add(new exiabots.mining.locations.rs3.VarrockEast());
			locations.add(new exiabots.mining.locations.rs3.VarrockWest());
			locations.add(new exiabots.mining.locations.rs3.Yanille());
		}else{
			locations.add(new exiabots.mining.locations.osrs.AlKharid());
			locations.add(new exiabots.mining.locations.osrs.BarbarianVillage());
			if(fullVersion)locations.add(new exiabots.mining.locations.osrs.CoalTrucks());
			locations.add(new exiabots.mining.locations.osrs.LegendsGuild());
			locations.add(new exiabots.mining.locations.osrs.LumbridgeEast());
			locations.add(new exiabots.mining.locations.osrs.LumbridgeWest());
			//locations.add(new exiabots.mining.locations.osrs.MiningGuild(rockWatcher, this));
			locations.add(new exiabots.mining.locations.osrs.Monastery());
			if(fullVersion)locations.add(new exiabots.mining.locations.osrs.PiratesHideout());
			locations.add(new exiabots.mining.locations.osrs.Rimmington());
			locations.add(new exiabots.mining.locations.osrs.VarrockEast());
			locations.add(new exiabots.mining.locations.osrs.VarrockWest());
			locations.add(new exiabots.mining.locations.osrs.Yanille());
		}
		return locations;
	}
	
	
	//////////////////////////////////////////////
	//      Actual Tree Logic below here        //
	//////////////////////////////////////////////
	
	private class InventoryBranch extends BranchTask {
		private TreeTask success;
		private TreeTask failure;
		
		public InventoryBranch(Location location){
			success = new InBankBranch(location);
			failure = new InMineBranch(location);
		}
		
		@Override
		public boolean validate() {
			return Inventory.isFull();
		}
		
		@Override
		public TreeTask failureTask() {
			return failure;
		}

		@Override
		public TreeTask successTask() {
			return success;
		}
	}
	
	private class InMineBranch extends BranchTask {
		private TreeTask success;
		private TreeTask failure;
		Location location;
		
		public InMineBranch(Location location){
			this.location = location;
			success = new MiningBranch(location);
			failure = new WalkTask(location, false);
		}
		
		@Override
		public boolean validate() {
			return location.inMine();
		}
		
		@Override
		public TreeTask successTask() {
			return success;
		}
		
		@Override
		public TreeTask failureTask() {
			return failure;
		}
	}
	
	private class InBankBranch extends BranchTask {
		private TreeTask success;
		private TreeTask failure;
		Location location;
		
		public InBankBranch(Location location){
			this.location = location;
			success = new BankOpenBranch(location);
			failure = new WalkTask(location, true);
		}
		
		@Override
		public boolean validate() {
			return location.inBank();
		}
		
		@Override
		public TreeTask successTask() {
			return success;
		}
		
		@Override
		public TreeTask failureTask() {
			return failure;
		}
	}
	
	private class BankOpenBranch extends BranchTask {
		private TreeTask success;
		private TreeTask failure;
		Location location;
		
		public BankOpenBranch(Location location){
			this.location = location;
			success = new DepositTask(location);
			failure = new OpenBankTask(location);
		}
		
		@Override
		public boolean validate() {
			return location.isBankOpen();
		}
		
		@Override
		public TreeTask successTask() {
			return success;
		}
		
		@Override
		public TreeTask failureTask() {
			return failure;
		}
	}
	
	private class DepositTask extends LeafTask {
		Location location;
		
		public DepositTask(Location location){
			this.location = location;
		}
		
		@Override
		public void execute() {
			location.deposit();
		}
	}
	
	private class OpenBankTask extends LeafTask {
		Location location;
		
		public OpenBankTask(Location location){
			this.location = location;
		}
		
		@Override
		public void execute() {
			location.openBank();
		}
	}
	
	private class WalkTask extends LeafTask {
		Location location;
		boolean bank;
		
		public WalkTask(Location location, boolean bank){
			this.location = location;
			this.bank = bank;
		}
		
		@Override
		public void execute() {
			if(bank){
				location.walkToBank(onlyWalkToBank);
			} else {
				location.walkToMine();
			}
		}
	}
}
