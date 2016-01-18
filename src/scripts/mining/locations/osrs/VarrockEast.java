package scripts.mining.locations.osrs;

import java.util.regex.Pattern;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

import scripts.mining.Rock;

public class VarrockEast extends OSRSLocation{

	private Area shop = new Area.Rectangular(new Coordinate(3250, 3404), new Coordinate(3256, 3398));
	private boolean treatAuburyAsBanker = false;
	
	@Override
	public void intialize(String ore){
		mine = new Area.Rectangular(new Coordinate(3280,3360), new Coordinate(3291,3371));
		bank = new Area.Rectangular(new Coordinate(3250,3418, 0), new Coordinate(3257,3423, 0));

		switch(ore){
		case "Essence":
			rocks = new Coordinate[0];
			break;
		case "Tin":
			rocks = new Coordinate[] {new Coordinate(3281,3363),new Coordinate(3282,3364)};
			break;
		case "Copper":
			rocks = new Coordinate[] {new Coordinate(3282,3368),new Coordinate(3282,3369)};
			break;
		case "Iron":
			rocks = new Coordinate[]{new Coordinate(3286,3369),new Coordinate(3285,3369),new Coordinate(3288,3370),new Coordinate(3285,3368)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
	}

	@Override
	public String getName() {
		return "Varrock East";
	}

	@Override
	public String[] getOres() {
		if(Environment.isSDK()) return new String[]{"Essence","Copper", "Tin", "Iron"};
		else return new String[]{"Copper", "Tin", "Iron"};
	}

	@Override
	public Coordinate[] getRocks(){
		if(ore != Rock.ESSENCE){
			return super.getRocks();
		}

		//Find all the essence and return their locations
		LocatableEntityQueryResults<GameObject> essence = GameObjects.getLoaded("Rune Essence").sortByDistance();
		Coordinate[] outRocks = new Coordinate[essence.size()];
		int i = 0;
		for(GameObject o : essence){
			outRocks[i++] = o.getPosition();
		}
		return outRocks;
	}

	@Override
	public boolean inMine() {
		if(ore != Rock.ESSENCE){
			return super.inMine();
		}

		//Check if there are any rune essence objects loaded
		if(this.getRocks().length > 0){
			minePath = null;
			return true;
		}else return false;
	}

	@Override
	public Pattern getBankInteract() {
		if(treatAuburyAsBanker)	return Pattern.compile("Teleport");
		else return super.getBankInteract();
	}
	
	@Override
	protected LocatableEntityQueryResults<? extends LocatableEntity> getBankers(){
		if(treatAuburyAsBanker) return Npcs.getLoaded("Aubury");
		else return super.getBankers();
	}
	
	@Override
	public boolean validate(GameObject o) {
		if(ore != Rock.ESSENCE){
			return super.validate(o);
		}else{
			String name = "";
			GameObjectDefinition def = o.getDefinition();
			if(def != null){
				name = def.getName();
			}

			return name.contains("Rune Essence");
		}
	}
	
	@Override
	public void walkToMine(Area... destL) { //works to walk to the Aubury and teleport.
		if(ore != Rock.ESSENCE){
			super.walkToMine(destL);
			return;
		}

		//This part will only run for Essence mining
		Player me = Players.getLocal();

		if(shop.contains(me)){
			//We are in the shop, so teleport on Aubury
			minePath = null;

			//This exploits code that was already written to interact with banks since it's 
			//essentially the same logic just with different strings.
			treatAuburyAsBanker = true;
			openBank();
			//Remove Aubury from being the banker
			treatAuburyAsBanker = false;

		}else{
			//Walk to the shop
			super.walkToMine(shop);
			//TODO Handle the door
		}
	}

	@Override
	public void walkToBank(boolean walk, Area... destL) {
		if(ore != Rock.ESSENCE){
			super.walkToBank(walk);
			return;
		}
		
		//This part will only run for Essence mining
		LocatableEntityQueryResults<GameObject> portalObject = GameObjects.getLoaded("Portal");
		//The portal can also be an NPC on some maps.
		LocatableEntityQueryResults<Npc> portalNPC = Npcs.getLoaded("Portal");

		if(portalObject.size() > 0){ //For when the portal is an object.
			GameObject portal = portalObject.nearestTo(Players.getLocal());
			if(portal.getVisibility() <= 70){
				Camera.turnTo(portal);
				Path portalPath = BresenhamPath.buildTo(portal);
				portalPath.step();
			}else{
				portal.interact("Exit");
				portal.interact("Use");
				if(Camera.getPitch() <= 0.3){
					if(Random.nextBoolean()){
						Camera.concurrentlyTurnTo(Random.nextDouble(0.4, 0.7));
					}else{
						Camera.concurrentlyTurnTo(Random.nextInt(0, 360));
					}
				}

				Timer timer = new Timer(Random.nextInt(2000,4000));
				timer.start();
				while(timer.getRemainingTime() > 0 && !inMine()){
					Execution.delay(10);
				}
			}
		}else if(portalNPC.size() > 0){ //For when the portal is an NPC.
			Npc portal = portalNPC.nearestTo(Players.getLocal());
			if(portal.getVisibility() <= 70){
				Camera.turnTo(portal);
				Path portalPath = BresenhamPath.buildTo(portal);
				portalPath.step();
			}else{
				portal.interact("Use");
				portal.interact("Exit");
				if(Camera.getPitch() <= 0.3){
					if(Random.nextBoolean()){
						Camera.concurrentlyTurnTo(Random.nextDouble(0.4, 0.7));
					}else{
						Camera.concurrentlyTurnTo(Random.nextInt(0, 360));
					}
				}

				Timer timer = new Timer(Random.nextInt(2000,4000));
				timer.start();
				while(timer.getRemainingTime() > 0 && !inMine()){
					Execution.delay(10);
				}
			}
		}else{
			super.walkToBank(walk);
		}
	}
}
