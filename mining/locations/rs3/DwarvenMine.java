package scripts.mining.locations.rs3;

import scripts.mining.Rock;
import scripts.mining.locations.DepositLocation;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Region;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

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
	public Coordinate[] getRocks() {
		return rocks;
	}

	@Override
	public boolean inBank() {
		return Region.getLoaded().getBase().equals(new Coordinate(992,4520,0));
	}

	@Override
	public boolean inMine() {
		//TODO Change this ugly shit
		return Region.getLoaded().getBase().equals(new Coordinate(2984,9720,0)) || Region.getLoaded().getBase().equals(new Coordinate(2984,9728,0)) || 
				Region.getLoaded().getBase().equals(new Coordinate(3008,9728,0)) || Region.getLoaded().getBase().equals(new Coordinate(2992,9728,0));
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
	public void walkToBank() {
		LocatableEntityQueryResults<GameObject> doors = GameObjects.getLoaded(entrance);
		if(doors.size() > 0){
			GameObject door = doors.nearestTo(new Coordinate(3033, 9772, 0));
			if(door.getVisibility() <= 10){
				Camera.turnTo(door);
			}else{
				door.click();
				if(Camera.getPitch() <= 0.3){
					Camera.passivelyTurnTo(Random.nextDouble(0.4, 0.7));
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
						Camera.passivelyTurnTo(Random.nextDouble(0.4, 0.7));
					}else{
						Camera.passivelyTurnTo(Random.nextInt(0, 360));
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
