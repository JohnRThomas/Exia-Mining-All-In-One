package scripts.mining.locations.osrs;

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

public class VarrockEssence extends OSRSLocation{

	private MiningStyle miner;
	private Area shop = new Area.Rectangular(new Coordinate(3250, 3404), new Coordinate(3256, 3398));
	//private Area exits = new Area.Rectangular(new Coordinate(3250, 3404), new Coordinate(3256, 3398));

	public VarrockEssence(MiningStyle miner) {
		this.miner = miner;
	}

	@Override
	public void intialize(String ore){
		rocks = new Coordinate[] {new Coordinate(7496,7396),new Coordinate(7462,7395),new Coordinate(7494,7431),new Coordinate(7460,7429)};
		this.ore = Rock.getByName(ore);
		mine = new Area.Rectangular(new Coordinate(7501,7436), new Coordinate(7454,7389));
		bank = new Area.Rectangular(new Coordinate(3250,3418), new Coordinate(3257,3423));
	}
		
	@Override
	public String getName() {
		return "Varrock Essence";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Essence"};
	}

/*	@Override
	public void walkToBank(boolean walk, Area... destL) {
		LocatableEntityQueryResults<GameObject> doors = GameObjects.getLoaded(entrance);
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
*/

@Override
	public void walkToMine(Area... destL) { //works to walk to the Aubury and teleport.
		Player me = Players.getLocal();
		if(shop.contains(me)){
			minePath = null;
			LocatableEntityQueryResults<Npc> aubury = Npcs.getLoaded("Aubury").sortByDistance();
			if(aubury.size() > 0){
				Npc aubury1 = aubury.get(0);
				if(aubury1.distanceTo(me) > 8){
					miner.walkTo(aubury1);
				}else{
					MenuItem mItem = null;
					while(mItem == null){
						Mouse.getPathGenerator().hop(aubury1.getInteractionPoint());
						Mouse.click(Mouse.Button.RIGHT);
						Execution.delay(50,100);
						mItem = Menu.getItem("Teleport");
					}
					Mouse.getPathGenerator().hop(mItem.getInteractionPoint());
					Mouse.click(Mouse.Button.LEFT);
				}
				Timer timer = new Timer((int)(aubury1.distanceTo(me) * ReflexAgent.getReactionTime()) + Random.nextInt(900, 1000));
				timer.start();
				while(timer.getRemainingTime() > 0 && shop.contains(Players.getLocal()) && aubury1.isValid() && Mouse.getCrosshairState() != Mouse.CrosshairState.YELLOW){
					Execution.delay(100);
				}
			}
		//}else if(exits.contains(me)){
		//	super.walkToMine();
		}else{
			super.walkToMine(shop);
		}
	}
}
