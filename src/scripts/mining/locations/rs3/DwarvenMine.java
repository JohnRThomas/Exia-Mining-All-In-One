package scripts.mining.locations.rs3;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

import scripts.mining.Rock;
import scripts.mining.locations.DepositLocation;

public class DwarvenMine extends DepositLocation{
	
	private String entrance = "Mysterious entrance";
	private String exit = "Mysterious door";
	
	@Override
	public void intialize(String ore){
		switch(ore){
		case "Iron":
			rocks = new Coordinate[]{new Coordinate(3036,9775),new Coordinate(3037,9776),new Coordinate(3039,9777)};
			break;
		case "Coal":
			rocks = new Coordinate[]{new Coordinate(3037,9763),new Coordinate(3039,9762),new Coordinate(3039,9761),new Coordinate(3041,9760),new Coordinate(3046,9762)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(1049,4580, 0), new Coordinate(1040,4571, 0));
		mine = new Area.Rectangular(new Coordinate(3048,9780, 0), new Coordinate(3034,9759, 0));
	}
	
	@Override
	public String getName() {
		return "Dwarven Mine";
	}
	
	@Override
	public String[] getOres() {
		return new String[]{"Iron", "Coal"};
	}

	@Override
	public Interactable firstStepToBank() {
		LocatableEntityQueryResults<GameObject> doors = GameObjects.getLoaded(entrance);
		if(doors.size() > 0){
			return doors.nearestTo(new Coordinate(3033, 9772, 0));
		}
		return null;
	}
	
	@Override
	public void walkToBank(boolean walk) {
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

	@Override
	public void walkToMine() {
		LocatableEntityQueryResults<GameObject> doors = GameObjects.getLoaded(exit);
		if(doors.size() > 0){
			GameObject door = doors.nearestTo(new Coordinate(1040, 4576, 0));
			if(door.getVisibility() <= 10){
				Camera.turnTo(door);
			}else{
				door.click();
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
		}
	}
}
