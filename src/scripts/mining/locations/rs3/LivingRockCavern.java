package scripts.mining.locations.rs3;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.script.Execution;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import scripts.mining.AIOMinerGUI;
import scripts.mining.MiningStyle;
import scripts.mining.Paint;
import scripts.mining.Rock;
import scripts.mining.locations.DepositLocation;

public class LivingRockCavern extends DepositLocation{
	private boolean idleAtBank = false;
	private boolean runFromCombat = true;
	private MiningStyle miner;

	public LivingRockCavern(MiningStyle miner){
		super();
		this.miner = miner;
	}

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Living rock remains":
			rocks = new Coordinate[]{};
			break;
		case "Concentrated Coal":
			rocks = new Coordinate[]{new Coordinate(3675,5099),new Coordinate(3665,5091)};
			break;
		case "Concentrated Gold":
			rocks = new Coordinate[]{new Coordinate(3668,5076),new Coordinate(3638,5095)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(3653,5117), new Coordinate(3657,5111));
	}

	@Override
	public String getName() {
		return "Living Rock Cavern";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Living rock remains", "Concentrated Coal", "Concentrated Gold"};
	}

	@Override
	public boolean validate(GameObject rock) {
		return rock.getDefinition().getName().equals("Mineral deposit");
	}

	@Override
	public boolean shouldBank() {
		return Inventory.isFull() || (Npcs.getLoaded(new Filter<Npc>(){
			@Override
			public boolean accepts(Npc npc) {
				return Players.getLocal().equals(npc.getTarget());
			}
		}).size() > 0 && runFromCombat);
	}

	@Override
	public LocatableEntity getNextRock(LocatableEntity currentRock){
		LocatableEntityQueryResults<? extends LocatableEntity> rocksObjs = GameObjects.getLoaded(new Filter<GameObject>(){
			@Override
			public boolean accepts(GameObject o) {
				if(validate(o)){
					Coordinate pos = o.getPosition();
					for (Coordinate rock : getRocks()) {
						if(pos.equals(rock)) return !rock.equals(currentRock);
					}
				}
				return false;
			}
		}).sortByDistance();

		if(rocksObjs.size() > 0) return rocksObjs.get(0);
		else{
			if(idleAtBank){
				return null;
			}else{
				LocatableEntityQueryResults<? extends LocatableEntity> remains = Npcs.getLoaded(new Filter<Npc>(){

					@Override
					public boolean accepts(Npc npc) {
						return npc.getName() != null && npc.getName().equals("Living rock remains") && !npc.equals(currentRock);
					}

				}).sortByDistance();
				//This will pseudo append the next list to the end of the previous list
				if(remains.size() > 0) return remains.get(0);
				else return null;

			}
		}
	}

	@Override
	protected LocatableEntityQueryResults<? extends LocatableEntity> getBanker(){
		return GameObjects.getLoaded("Pulley lift");
	}

	@Override
	public boolean inMine() {
		Player me = Players.getLocal();
		LocatableEntity rock = getNextRock(miner.currentRock);
		if(rock != null){
			if(me.distanceTo(rock) <= 3){
				minePath = null;
				return true;
			}else{
				return false;
			}
		}else{
			for(Coordinate rockLoc : rocks){
				if(me.distanceTo(rockLoc) <= 3){
					minePath = null;
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void walkToMine() {
		LocatableEntity rock = getNextRock(miner.currentRock);
		if(minePath == null){
			if(rock != null){
				minePath = BresenhamPath.buildTo(rock);
			}else{
				if(idleAtBank){
					Paint.status = "Waiting in safe-zone";
					Execution.delay(200,300);
				}else{
					Paint.status = "Waiting in safe-zone";
					Execution.delay(200,300);
				}
			}
		}else if((Traversal.getDestination() == null || Traversal.getDestination().distanceTo(rock) > 14)){
			minePath.step();
		}
	}

	CheckBox box = new CheckBox("Idle at bank");
	CheckBox combat = new CheckBox("Run from combat");
	@Override
	public void loadSettings() {
		idleAtBank = box.isSelected();
		runFromCombat = combat.isSelected();
	}

	@Override
	public Node[] getSettingsNodes(){
		ImageView warnImage = AIOMinerGUI.warnImage;

		Label labela = new Label("WARNING!");
		labela.setStyle("-fx-text-fill: -fx-text-input-text");
		labela.setAlignment(Pos.CENTER);
		labela.setPadding(new Insets(0,0,3,5));
		labela.setGraphic(warnImage);
				
		Label labelb = new Label("This area has not");
		labelb.setStyle("-fx-text-fill: -fx-text-input-text");
		labelb.setPadding(new Insets(0,0,3,5));
		
		Label labelc = new Label("been tested and ");
		labelc.setStyle("-fx-text-fill: -fx-text-input-text");
		labelc.setPadding(new Insets(0,0,3,5));
		
		Label labeld = new Label("it may be buggy!");
		labeld.setStyle("-fx-text-fill: -fx-text-input-text");
		labeld.setPadding(new Insets(0,0,3,5));
				
		box.setSelected(idleAtBank);
		box.setStyle("-fx-text-fill: -fx-text-input-text");
		box.setPadding(new Insets(0,0,5,5));
		box.setPrefWidth(165);
		
		combat.setSelected(runFromCombat);
		combat.setStyle("-fx-text-fill: -fx-text-input-text");
		combat.setPadding(new Insets(0,0,0,5));
		combat.setPrefWidth(165);
		
		return new Node[]{labela, labelb, labelc, labeld, box, combat};
	}
}
