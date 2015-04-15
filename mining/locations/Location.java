package scripts.mining.locations;

import scripts.mining.ReflexAgent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

public abstract class Location {

	protected Area mine;
	protected Area bank;

	protected GenericPathBuilder pathBuilder = new GenericPathBuilder();
	protected Path minePath = null;
	protected Path bankPath = null;
	
	public abstract void intialize(String ore);

	public abstract String getName();
	public abstract String getOre();
	public abstract String[] getOres();

	public abstract Coordinate[] getRocks();
	
	public LocatableEntity getBestRock(int index){
		LocatableEntityQueryResults<GameObject> rocksObjs = GameObjects.getLoaded(new Filter<GameObject>(){
			@Override
			public boolean accepts(GameObject o) {
				String name = o.getDefinition().getName();
				if(validateName(name)){
					Coordinate pos = o.getPosition();
					for (Coordinate rock : getRocks()) {
						if(pos.equals(rock)) return true;
					}
				}

				return false;
			}
		}).sortByDistance();

		if(rocksObjs.size() > index) return rocksObjs.get(index);
		else return null;
	}
	
	public boolean shouldBank() {
		return Inventory.isFull();
	}
	
	public void openBank(){
		ReflexAgent.delay();
		LocatableEntityQueryResults<? extends LocatableEntity> banks = getBanker();

		if(banks.size() > 0){
			LocatableEntity bank = banks.nearest();
			if(bank.getVisibility() <= 10){
				Camera.turnTo(bank);
			}else{
				bank.interact(getBankInteract());
				if(Camera.getPitch() <= 0.3){
					Camera.passivelyTurnTo(Random.nextDouble(0.4, 0.7));
				}

				Timer timer = new Timer(Random.nextInt(2000,4000));
				timer.start();
				while(timer.getRemainingTime() > 0 && !isBankOpen()){
					Execution.delay(10);
				}
			}
		}
	}

	public String getBankInteract() {
		return "Bank";
	}

	protected LocatableEntityQueryResults<? extends LocatableEntity> getBanker(){
		Double banktype = PlayerSense.getAsDouble(PlayerSense.Key.WALL_AVOIDANCE_MODIFIER);
		if(banktype < 0){
			//Return the chests or the bankers
			return Banks.getLoaded(new Filter<LocatableEntity>(){
				@Override
				public boolean accepts(LocatableEntity a) {
					if(a instanceof Npc){
						return Banks.getBankerFilter().accepts((Npc) a); 
					}else if(a instanceof GameObject){
						return Banks.getBankChestFilter().accepts((GameObject) a);
					}else{
						return false;
					}
				}
			});
		}else if(banktype > 0){
			//return the chests or the bank booths
			return Banks.getLoaded(new Filter<LocatableEntity>(){
				@Override
				public boolean accepts(LocatableEntity a) {
					if(a instanceof GameObject){
						return Banks.getBankBoothFilter().accepts((GameObject) a) || Banks.getBankChestFilter().accepts((GameObject) a);
					}else{
						return false;
					}
				}
			});
		}else{
			//return everything
			return Banks.getLoaded();
		}
	}

	public boolean isBankOpen(){
		return Bank.isOpen();
	}

	public void depositAll(){
		ReflexAgent.delay();
		Bank.depositInventory();
	}

	public void closeBank() {
		Bank.close();
	}

	public void loadSettings() {}

	public Node[] getSettingsNodes(){
		Label label = new Label("No custom settings");
		label.setStyle("-fx-text-fill: -fx-text-input-text");
		label.setAlignment(Pos.CENTER);
		label.setPadding(new Insets(3,3,3,3));
		label.setPrefWidth(165);
		return new Node[]{label};
	}

	public boolean inBank() {
		LocatableEntityQueryResults<? extends LocatableEntity> bankers = getBanker().sortByDistance();
		if(bank.contains(Players.getLocal()) || (bankers.size() > 0 && bankers.first().isVisible() && bankers.first().distanceTo(Players.getLocal()) < 5)){
			bankPath = null;
			return true;
		}else return false;
	}

	public boolean inMine() {
		if(mine.contains(Players.getLocal())){
			minePath = null;
			return true;
		}else return false;
	}

	public void walkToBank() {
		if(bankPath == null) bankPath = pathBuilder.build(Players.getLocal(), bank.getArea());
		else if(!bank.contains(Traversal.getDestination())){
			bankPath.step();
			Execution.delay(400,600);
		}
	}

	public void walkToMine() {
		if(minePath == null)minePath = pathBuilder.build(Players.getLocal(), mine.getArea());
		else if(!mine.contains(Traversal.getDestination())){
			minePath.step();
			Execution.delay(400,600);
		}
	}

	public Interactable firstStepToBank() {
		try{
			Path path = pathBuilder.build(Players.getLocal(), bank.getArea());
			if(path instanceof RegionPath) return ((RegionPath) path).getNext().getPosition().minimap();
			else if (path instanceof WebPath)return ((WebPath) path).getNext().getPosition().minimap();
			else return null;
		}catch(NullPointerException e){
			return null;
		}
	}

	public boolean validateName(String name) {
		return !name.equals("Rocks") && name.contains("rocks");
	}
}
