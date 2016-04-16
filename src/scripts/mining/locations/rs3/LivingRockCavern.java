package scripts.mining.locations.rs3;

import java.util.function.Predicate;

import com.runemate.game.api.client.ClientUI;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
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
import scripts.mining.ReflexAgent;
import scripts.mining.Rock;
import scripts.mining.locations.DepositLocation;

public class LivingRockCavern extends DepositLocation{
	private boolean mineMinerals = false;
	private boolean runFromCombat = true;
	boolean notified = false;
	boolean deathNotification = false;
	boolean safeIdle = false;

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
		GameObjectDefinition def = rock.getDefinition();
		String name = "";
		if(def != null)name = def.getName();

		return name.equals("Mineral deposit");
	}

	@Override
	public boolean shouldBank() {
		return Inventory.isFull() || (Npcs.getLoaded(new Predicate<Npc>(){
			@Override
			public boolean test(Npc npc) {
				return Players.getLocal().equals(npc.getTarget());
			}
		}).size() > 0 && runFromCombat) || Players.getLocal().distanceTo(new Coordinate(3652, 5115)) > 80;
	}

	@Override
	public LocatableEntity getNextRock(LocatableEntity currentRock){
		LocatableEntityQueryResults<? extends LocatableEntity> rocksObjs = GameObjects.getLoaded(new Predicate<GameObject>(){
			@Override
			public boolean test(GameObject o) {
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
			if(mineMinerals){
				LocatableEntityQueryResults<? extends LocatableEntity> remains = Npcs.getLoaded(new Predicate<Npc>(){

					@Override
					public boolean test(Npc npc) {
						return npc.getName() != null && npc.getName().equals("Living rock remains") && !npc.equals(currentRock);
					}

				}).sortByDistance();
				if(remains.size() > 0) return remains.get(0);
				else return null;
			}else{
				return null;
			}
		}
	}

	@Override
	protected LocatableEntityQueryResults<? extends LocatableEntity> getBankers(){
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
	public void walkToMine(Area... destL) {
		LocatableEntity rock = getNextRock(miner.currentRock);
		if(minePath == null){
			if(rock != null){
				minePath = pathBuilder.buildTo(rock);
			}else{
				if(safeIdle){
					minePath = pathBuilder.buildTo(bank);
				}
			}
		}else if((Traversal.getDestination() == null || Traversal.getDestination().distanceTo(rock) > 14)){
			minePath.step();
		}
	}

	Predicate<InterfaceComponent> warningPredicate = new Predicate<InterfaceComponent>(){
		@Override
		public boolean test(InterfaceComponent i) {
			return i.getText() != null & i.getText().contains("Proceed regardless");
		}
	};

	@Override
	public void walkToBank(boolean walk, Area... destL) {
		Player me = Players.getLocal();

		if(me.distanceTo(new Coordinate(3652, 5115)) > 80){
			if(!notified && deathNotification){
				ClientUI.sendTrayNotification("You have died! Walking back to LRC!");
				notified = true;
			}
			//distance from the LRC bank is greater than 80 tiles

			Paint.status = "Re-entering Cavern";

			//distance to ladder of dwarven mine
			if(me.distanceTo(new Coordinate(3018, 3450)) <= 5){
				bankPath = null;
				LocatableEntityQueryResults<GameObject> ladders = GameObjects.getLoaded("Ladder").sortByDistance();
				if(ladders.size() > 0){
					GameObject ladder = ladders.get(0);
					if(ladder.distanceTo(me) > 8){
						miner.walkTo(ladder);
					}else{
						miner.turnAndClick(ladder, "Climb-down");
					}
					Timer timer = new Timer((int)(ladder.distanceTo(me) * ReflexAgent.getReactionTime()) + Random.nextInt(900, 1000));
					timer.start();
					while(timer.getRemainingTime() > 0 && Players.getLocal().distanceTo(new Coordinate(3018, 3450)) <= 5 && Mouse.getCrosshairState() != Mouse.CrosshairState.YELLOW){
						Execution.delay(10);
					}
				}
			}else{
				//distance to the rope
				if(me.distanceTo(new Coordinate(3014, 9831)) <= 25){
					InterfaceComponent warning = Interfaces.getLoaded(warningPredicate).first();
					if(warning != null && warning.isValid() && warning.isVisible()){
						//Click proceed
						warning.click();
						Timer timer = new Timer((int)(ReflexAgent.getReactionTime() * 4) + Random.nextInt(900, 1000));
						timer.start();
						while(timer.getRemainingTime() > 0 && Interfaces.getLoaded(warningPredicate).first() != null && Mouse.getCrosshairState() != Mouse.CrosshairState.YELLOW){
							Execution.delay(10);
						}	
					}else{
						//open the warning
						LocatableEntityQueryResults<GameObject> ropes = GameObjects.getLoaded("Rope").sortByDistance();
						if(ropes.size() > 0){
							GameObject rope = ropes.get(0);
							if(rope.distanceTo(me) > 8){
								miner.walkTo(rope);
							}else{
								miner.turnAndClick(rope, "Climb");
								Timer timer = new Timer((int)(rope.distanceTo(me) * ReflexAgent.getReactionTime()) + Random.nextInt(900, 1000));
								timer.start();
								while(timer.getRemainingTime() > 0 && Players.getLocal().distanceTo(new Coordinate(3014, 9831)) <= 25 &&
										Interfaces.getLoaded(warningPredicate).first() == null && Mouse.getCrosshairState() != Mouse.CrosshairState.YELLOW){
									Execution.delay(10);
								}
							}
						}
					}
				}else{
					//Walk to the ladder
					if(bankPath == null){
						bankPath = pathBuilder.buildTo(new Coordinate(3018, 3450), false);
					}else{
						lastStep = bankPath.getNext();

						bankPath.step();
					}
				}
			}
		}else{
			notified = false;
			super.walkToBank(walk);
		}

	}

	CheckBox idle = new CheckBox("Idle at bank");
	CheckBox box = new CheckBox("Mine remains\nwhile waiting");
	CheckBox combat = new CheckBox("Run from combat");
	CheckBox death = new CheckBox("Notify on death");
	@Override
	public void loadSettings() {
		mineMinerals = box.isSelected();
		runFromCombat = combat.isSelected();
		deathNotification = death.isSelected();
		safeIdle = idle.isSelected();
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

		idle.setSelected(safeIdle);
		idle.setStyle("-fx-text-fill: -fx-text-input-text");
		idle.setPadding(new Insets(0,0,5,5));
		idle.setPrefWidth(165);

		box.setSelected(mineMinerals);
		box.setStyle("-fx-text-fill: -fx-text-input-text");
		box.setPadding(new Insets(0,0,5,5));
		box.setPrefWidth(165);

		death.setSelected(deathNotification);
		death.setStyle("-fx-text-fill: -fx-text-input-text");
		death.setPadding(new Insets(0,0,5,5));
		death.setPrefWidth(165);

		combat.setSelected(runFromCombat);
		combat.setStyle("-fx-text-fill: -fx-text-input-text");
		combat.setPadding(new Insets(5,0,0,5));
		combat.setPrefWidth(165);

		return new Node[]{labela, labelb, labelc, labeld, idle, box, death, combat};
	}
}
