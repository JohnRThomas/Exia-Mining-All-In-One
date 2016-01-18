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

import scripts.mining.Paint;
import scripts.mining.ReflexAgent;
import scripts.mining.Rock;

public class VarrockEast extends OSRSLocation{

	private Area shop = new Area.Rectangular(new Coordinate(3250, 3404), new Coordinate(3256, 3398));
	
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
		LocatableEntityQueryResults<GameObject> essenceObjects = GameObjects.getLoaded("Rune Essence").sortByDistance();
		LocatableEntityQueryResults<Npc> essenceNPCs = Npcs.getLoaded("Rune Essence").sortByDistance();
		Coordinate[] outRocks = new Coordinate[essenceObjects.size() + essenceNPCs.size()];
		int i = 0;
		for(GameObject o : essenceObjects){
			outRocks[i++] = o.getPosition();
		}
		for(Npc npc : essenceNPCs){
			outRocks[i++] = npc.getPosition();
		}
		System.out.println(outRocks.length);
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
			
			LocatableEntityQueryResults<Npc> auburys = Npcs.getLoaded("Aubury");
			if(auburys.size() > 0){
				LocatableEntity aubury = auburys.nearest();
				if(aubury.getVisibility() <= 10){
					Camera.turnTo(aubury);
				}else{
					aubury.interact("Teleport");
					if(Camera.getPitch() <= 0.3){
						Camera.concurrentlyTurnTo(Random.nextDouble(0.4, 0.7));
					}
					
					Timer timer = new Timer((int)(aubury.distanceTo(me) * ReflexAgent.getReactionTime() * 5));
					timer.start();
					while(timer.getRemainingTime() > 0 && !inMine()){
						Execution.delay(10);
					}
				}
			}

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
		LocatableEntityQueryResults<GameObject> portalObject = GameObjects.getLoaded("Portal").sortByDistance();
		//The portal can also be an NPC on some maps.
		LocatableEntityQueryResults<Npc> portalNPC = Npcs.getLoaded("Portal").sortByDistance();
		
		//Grab the proper portal
		LocatableEntity portal = portalNPC.size() > 0 ? portalNPC.first() : (portalObject.size() > 0 ? portalObject.first() : null);
		
		if(portal != null){ //If we are in the mine, a portal will exist
			Paint.status = "Walking to bank: Entering portal";
			if(portal.getVisibility() <= 70){
				Camera.turnTo(portal);
				Path portalPath = BresenhamPath.buildTo(portal);
				portalPath.step();
			}else{
				portal.interact(Pattern.compile("Exit|Use"));
				if(Camera.getPitch() <= 0.3){
					if(Random.nextBoolean()){
						Camera.concurrentlyTurnTo(Random.nextDouble(0.4, 0.7));
					}else{
						Camera.concurrentlyTurnTo(Random.nextInt(0, 360));
					}
				}

				Player me = Players.getLocal();
				Timer timer = new Timer((int)(portal.distanceTo(me) * ReflexAgent.getReactionTime() * 3));
				timer.start();
				while(timer.getRemainingTime() > 0 && !inMine()){
					Execution.delay(10);
				}
			}
		}else{//Otherwise, walk to the bank normally
			super.walkToBank(walk, destL);
		}
	}
}
