package scripts.mining.locations.osrs;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.Menu;
import com.runemate.game.api.hybrid.local.hud.MenuItem;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

import scripts.mining.MiningStyle;
import scripts.mining.ReflexAgent;
import scripts.mining.Rock;

public class VarrockEast extends OSRSLocation{

	private MiningStyle miner;
	private Area shop = new Area.Rectangular(new Coordinate(3250, 3404), new Coordinate(3256, 3398));
	//private Area exits = new Area.Rectangular(new Coordinate(3250, 3404), new Coordinate(3256, 3398));

	public VarrockEast(MiningStyle miner) {
		this.miner = miner;
	}

	@Override
	public void intialize(String ore){
		mine = new Area.Rectangular(new Coordinate(3280,3360), new Coordinate(3291,3371));
		bank = new Area.Rectangular(new Coordinate(3250,3418, 0), new Coordinate(3257,3423, 0));

		switch(ore){
		case "Essence":
			rocks = new Coordinate[] {};
			mine = new Area.Rectangular(new Coordinate(7501,7436), new Coordinate(7454,7389));
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
	public void walkToMine(Area... destL) { //works to walk to the Aubury and teleport.
		if(ore != Rock.ESSENCE){
			super.walkToMine(destL);
			return;
		}
		
		//This part will only run for Essence mining
		Player me = Players.getLocal();
		if(shop.contains(me)){
			minePath = null;
			LocatableEntityQueryResults<Npc> auburys = Npcs.getLoaded("Aubury").sortByDistance();
			if(auburys.size() > 0){
				Npc aubury = auburys.get(0);
				if(aubury.distanceTo(me) > 8){
					miner.walkTo(aubury);
				}else{
					MenuItem mItem = null;
					while(mItem == null){//Sometimes gets stuck on the Walk/Cancel menu. Might add a mouse move so it clears.
						Mouse.getPathGenerator().hop(aubury.getInteractionPoint());
						Mouse.click(Mouse.Button.RIGHT);
						Execution.delay(50,100);
						mItem = Menu.getItem("Teleport");
					}
					Mouse.getPathGenerator().hop(mItem.getInteractionPoint());
					Execution.delay(50,100);
					Mouse.click(Mouse.Button.LEFT);
				}
				Timer timer = new Timer((int)(aubury.distanceTo(me) * ReflexAgent.getReactionTime()) + Random.nextInt(900, 1000));
				timer.start();
				while(timer.getRemainingTime() > 0 && shop.contains(Players.getLocal()) && aubury.isValid() && Mouse.getCrosshairState() != Mouse.CrosshairState.YELLOW){
					Execution.delay(100);
				}
			}
		}else if (runeEssence()){//If the Rune Essence is located, return true
			//Locate the coordinates of the essence; set the ore coors; set the mine coors
			LocatableEntityQueryResults<GameObject> runeEss = GameObjects.getLoaded("Rune Essence").sortByDistance();
			rocks = new Coordinate[] {runeEss.nearest().getPosition()};
			mine = new Area.Rectangular(new Coordinate(runeEss.nearest().getPosition().getX()+5,runeEss.nearest().getPosition().getY()+5), new Coordinate(runeEss.nearest().getPosition().getX()-5,runeEss.nearest().getPosition().getY()-5));
			super.walkToMine();
		}else{
			super.walkToMine(shop);
		}
	}

	public Boolean runeEssence(){
		LocatableEntityQueryResults<GameObject> runeEssence = GameObjects.getLoaded("Rune Essence").sortByDistance();
		if(runeEssence.size() > 0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void walkToBank(boolean walk, Area... destL) {
		if(ore != Rock.ESSENCE){
			super.walkToMine(destL);
			return;
		}
		
		//This part will only run for Essence mining
		LocatableEntityQueryResults<GameObject> doors = GameObjects.getLoaded("Door");
		if(doors.size() > 0){
			GameObject door = doors.nearestTo(new Coordinate(3033, 9772, 0));
			if(door.getVisibility() <= 10){
				Camera.turnTo(door);
			}else{
				door.click();
				if(Camera.getPitch() <= 0.3){
					Camera.concurrentlyTurnTo(Random.nextDouble(0.4, 0.7));
				}

				Timer timer = new Timer(Random.nextInt(3000,5000));
				timer.start();
				while(timer.getRemainingTime() > 0 && !inBank()){
					Execution.delay(10);
				}
			}
		}
	}
}
