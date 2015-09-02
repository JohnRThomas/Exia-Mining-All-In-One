package scripts.mining.locations.osrs;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

import scripts.mining.MiningStyle;
import scripts.mining.ReflexAgent;
import scripts.mining.Rock;
import scripts.mining.RockWatcher;

public class MiningGuild extends OSRSLocation{

	private RockWatcher rockWatcher;
	private MiningStyle miner;
	private Area mineLadders = new Area.Rectangular(new Coordinate(3017,9741), new Coordinate(3022,9736));
	private Area bankLadders = new Area.Rectangular(new Coordinate(3016,3342), new Coordinate(3022,3336));

	public MiningGuild(RockWatcher rockWatcher, MiningStyle miner) {
		this.rockWatcher = rockWatcher;
		this.miner = miner;
	}

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Coal":
			rocks = new Coordinate[]{};
			break;
		case "Mithril":
			rocks = new Coordinate[]{new Coordinate(3050,9738),new Coordinate(3053,9737),new Coordinate(3052,9739),new Coordinate(3046,9733),new Coordinate(3047,9733)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(3009,3358), new Coordinate(3015,3355));
		mine = new Area.Rectangular(new Coordinate(3023,9755), new Coordinate(3055,9731));
	}

	@Override
	public String getName() {
		return "Mining Guild";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Coal", "Mithril"};
	}

	public LocatableEntity getNextRock(LocatableEntity currentRock) {
		if(ore == Rock.COAL){
			LocatableEntityQueryResults<GameObject> rocksObjs = null;
			try{
				rocksObjs = GameObjects.getLoaded(new Filter<GameObject>(){
					@Override
					public boolean accepts(GameObject o) {
						return validate(o) && !o.equals(currentRock);
					}
				}).sortByDistance();
			}catch(Exception e){}

			if(rocksObjs != null && rocksObjs.size() > 0){
				try{
					rockWatcher.addLocation(rocksObjs.get(0).getPosition());
				}catch(Exception e){}
				return rocksObjs.get(0);
			}
			return null;
		}else{
			return super.getNextRock(currentRock);
		}
	}

	@Override
	public void walkToBank(boolean walk, Area... destL) {
		Player me = Players.getLocal();
		if(mineLadders.contains(me)){
			bankPath = null;
			LocatableEntityQueryResults<GameObject> ladders = GameObjects.getLoaded("Ladder").sortByDistance();
			if(ladders.size() > 0){
				GameObject ladder = ladders.get(0);
				if(ladder.distanceTo(me) > 8){
					miner.walkTo(ladder);
				}else{
					miner.turnAndClick(ladder, "Climb-up");
				}
				Timer timer = new Timer((int)(ladder.distanceTo(me) * ReflexAgent.getReactionTime()) + Random.nextInt(900, 1000));
				timer.start();
				while(timer.getRemainingTime() > 0 && mineLadders.contains(Players.getLocal()) && ladder.isValid() && Mouse.getCrosshairState() != Mouse.CrosshairState.YELLOW){
					Execution.delay(100);
				}
			}
		}else if(inMine()){
			super.walkToBank(false, mineLadders);
		}else{
			super.walkToBank(walk);
		}
	}

	@Override
	public void walkToMine(Area... destL) {
		Player me = Players.getLocal();
		if(bankLadders.contains(me)){
			minePath = null;
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
				while(timer.getRemainingTime() > 0 && bankLadders.contains(Players.getLocal()) && ladder.isValid() && Mouse.getCrosshairState() != Mouse.CrosshairState.YELLOW){
					Execution.delay(100);
				}
			}
		}else if(mineLadders.contains(me)){
			super.walkToMine();
		}else{
			super.walkToMine(bankLadders);
		}
	}
}
