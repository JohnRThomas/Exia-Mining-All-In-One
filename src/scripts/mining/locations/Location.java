package scripts.mining.locations;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Path.TraversalOption;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.basic.ViewportPath;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import scripts.mining.CustomPlayerSense;
import scripts.mining.GenericPathBuilder;
import scripts.mining.ReflexAgent;
import scripts.mining.Rock;

public abstract class Location {

	protected Area mine;
	protected Area bank;
	public ArrayList<String> depositBlackList = new ArrayList<String>();

	protected GenericPathBuilder pathBuilder = new GenericPathBuilder();
	protected Path minePath = null;
	protected Path bankPath = null;
	protected Rock ore;
	protected Coordinate[] rocks;
	protected boolean earlyBanking = true;
	
	public abstract void intialize(String ore);

	public abstract String getName();

	public abstract String[] getOres();

	public Coordinate[] getRocks(){
		return rocks;
	}

	public Rock getOre(){
		return ore;
	}

	public boolean shouldBank() {
		return Inventory.isFull();
	}

	public void openBank(){
		ReflexAgent.delay();
		LocatableEntityQueryResults<? extends LocatableEntity> banks = getBankers();

		if(banks.size() > 0){
			LocatableEntity bank = banks.nearest();
			if(bank.getVisibility() <= 10){
				Camera.turnTo(bank);
			}else{
				bank.interact(getBankInteract());
				if(Camera.getPitch() <= 0.3){
					Camera.concurrentlyTurnTo(Random.nextDouble(0.4, 0.7));
				}
				
				Player me = Players.getLocal();
				Timer timer = new Timer((int)(bank.distanceTo(me) * ReflexAgent.getReactionTime() * 3));
				timer.start();
				while(timer.getRemainingTime() > 0 && !isBankOpen()){
					Execution.delay(10);
				}
			}
		}
	}

	public Pattern getBankInteract() {
		return Pattern.compile("Bank|Use");
	}

	public LocatableEntity getNextRock(LocatableEntity currentRock) {
		LocatableEntityQueryResults<GameObject> rocksObjs = null;
		try{
			rocksObjs = GameObjects.getLoaded(new Predicate<GameObject>(){
				@Override
				public boolean test(GameObject o) {
					if(o != null && validate(o)){
						Coordinate pos = o.getPosition();
						for (Coordinate rock : getRocks()) {
							if(pos.equals(rock)) return !o.equals(currentRock);
						}
					}
					return false;
				}
			}).sortByDistance();
		}catch(Exception e){}

		if(rocksObjs != null && rocksObjs.size() > 0) return rocksObjs.get(0);
		return null;
	}

	protected LocatableEntityQueryResults<? extends LocatableEntity> getBankers(){
		int banktype = PlayerSense.getAsInteger(CustomPlayerSense.Key.BANKER_PREFERENCE.playerSenseKey);
		if(banktype <= 33){
			//Return the chests or the bankers
			return Banks.getLoaded(new Predicate<LocatableEntity>(){
				@Override
				public boolean test(LocatableEntity a) {
					if(a instanceof Npc){
						return Banks.getBankerPredicate().test((Npc) a); 
					}else if(a instanceof GameObject){
						return Banks.getBankChestPredicate().test((GameObject) a);
					}else{
						return false;
					}
				}
			});
		}else if(banktype > 33 && banktype <= 66){
			//return the chests or the bank booths
			return Banks.getLoaded(new Predicate<LocatableEntity>(){
				@Override
				public boolean test(LocatableEntity a) {
					if(a instanceof GameObject){
						return Banks.getBankBoothPredicate().test((GameObject) a) || Banks.getBankChestPredicate().test((GameObject) a);
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

	public void deposit(){
		ReflexAgent.delay();
		try{
			Bank.depositAllExcept(new Predicate<SpriteItem>(){
				@Override
				public boolean test(SpriteItem i) {
					ItemDefinition def = i.getDefinition();
					String name = "";
					if(def != null)name = def.getName();

					for(String s : depositBlackList){
						if(name.toLowerCase().contains(s))return true;
					}
					return false;					
				}
			});
		}catch(Exception e){}
	}

	public void closeBank() {
		Bank.close();
	}

	public void loadSettings() {}

	public Node[] getSettingsNodes(){
		Label label = new Label("No location settings");
		label.setStyle("-fx-text-fill: -fx-text-input-text");
		label.setAlignment(Pos.CENTER);
		label.setPadding(new Insets(3,3,3,3));
		label.setPrefWidth(165);
		return new Node[]{label};
	}

	public boolean inBank() {
		LocatableEntityQueryResults<? extends LocatableEntity> bankers = getBankers().sortByDistance();
		if(bank.contains(Players.getLocal())){
			bankPath = null;
			return true;
		}else if(earlyBanking && (bankers.size() > 0 && bankers.first().isVisible() && bankers.first().distanceTo(Players.getLocal()) < 5)){
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

	int tryCount = 0;
	protected Locatable lastStep = null;

	public void walkToBank(boolean walk, Area... destL) {
		Area dest;
		if(destL.length == 0)dest = bank;
		else dest = destL[0];

		if(bankPath == null)
			bankPath = pathBuilder.buildTo(dest);		
		else if(!dest.contains(Traversal.getDestination())){
			if(bankPath instanceof ViewportPath){
				Locatable next = bankPath.getNext();
				if(next != null){
					Area area = next.getArea();
					if(area != null && !area.isVisible()){
						Camera.concurrentlyTurnTo(bankPath.getNext());
					}
				}
			}

			lastStep = bankPath.getNext();
			bankPath.step(TraversalOption.MANAGE_DISTANCE_BETWEEN_STEPS, walk ? null : TraversalOption.MANAGE_RUN);

			if(bankPath instanceof BresenhamPath || bankPath instanceof WebPath){
				bankPath = null;
				lastStep = null;
			}else if(bankPath instanceof ViewportPath){
				//Sometimes viewport paths get stuck trying to click through walls
				if(lastStep == bankPath.getNext())tryCount++;
				else tryCount = 0;
				if(tryCount >= 3)bankPath = null;
			}
			Execution.delay(400,600);
		}else{
			Execution.delay(400,600);
		}
	}

	public void walkToMine(Area... destL) {
		Area dest;
		if(destL.length == 0)dest = mine;
		else dest = destL[0];

		if(minePath == null)
			minePath = pathBuilder.buildTo(dest);
		else if(!dest.contains(Traversal.getDestination())){
			if(minePath instanceof ViewportPath){
				Locatable next = minePath.getNext();
				if(next != null){
					Area area = next.getArea();
					if(area != null && !area.isVisible()){
						Camera.concurrentlyTurnTo(minePath.getNext());
					}
				}
			}

			lastStep = minePath.getNext();
			minePath.step(TraversalOption.MANAGE_DISTANCE_BETWEEN_STEPS, TraversalOption.MANAGE_RUN);
			
			if(minePath instanceof BresenhamPath || minePath instanceof WebPath){
				minePath = null;
				lastStep = null;
				return;
			}else if(minePath instanceof ViewportPath){
				//Sometimes viewport paths get stuck trying to click through walls
				if(lastStep == minePath.getNext())tryCount++;
				else tryCount = 0;
				if(tryCount >= 3)minePath = null;
			}
			
			Execution.delay(400,600);
		}else{
			Execution.delay(400,600);
		}
	}

	Path fsPath = null;
	public Interactable firstStepToBank() {
		try{
			if(fsPath == null)fsPath = pathBuilder.build(Players.getLocal(),bank.getArea());
			if(fsPath instanceof RegionPath) return ((RegionPath) fsPath).getNext().getPosition().minimap();
			else if (fsPath instanceof WebPath)return ((WebPath) fsPath).getNext().getPosition().minimap();
			else if (fsPath instanceof BresenhamPath)return ((BresenhamPath) fsPath).getNext().getPosition().minimap();
			else return null;
		}catch(NullPointerException e){
			return null;
		}
	}

	public boolean validate(GameObject rock) {
		GameObjectDefinition def = rock.getDefinition();
		String name = "";
		if(def != null)name = def.getName();

		return !name.equals("Rocks") && name.contains("rocks");
	}
}
