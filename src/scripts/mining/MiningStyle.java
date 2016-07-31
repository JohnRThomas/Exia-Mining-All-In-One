package scripts.mining;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.region.Region;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public abstract class MiningStyle {
	RockWatcher rockWatcher;

	public abstract void onStart(String... args);
	public abstract void loop();
	public abstract void onStop();

	public abstract GridPane getContentPane(Button startButton);
	public abstract void loadSettings();
	public abstract Rock getOre();
	public abstract String getLocationName();
	public abstract Coordinate[] getRockLocations();

	public LocatableEntity currentRock = null;

	Path rockPath = null;
	public void walkTo(LocatableEntity rock) {
		if(rockPath == null){
			if(rock != null){
				Coordinate pt = rock.getPosition();
				if(pt != null){
					rockPath = BresenhamPath.buildTo(pt);
				}
			}	
		}else if((Traversal.getDestination() == null || Traversal.getDestination().distanceTo(rock) > 7)){
			ReflexAgent.delay();
			rockPath.step();
		}
	}

	public boolean turnAndClick(LocatableEntity rock){
		Paint.status = "Clicking rock";
		rockPath = null;
		if(turnAndClick(rock, "Mine")){
			currentRock = rock;
			return true;
		}
		return false;
	}


	public boolean turnAndClick(LocatableEntity object, String interact){
		if(object.getVisibility() <= 20){
			//if only part of the rock is visible, turn to it
			Camera.turnTo(object);
			return false;
		}else{
			//The rock is visible enough, so we click it
			ReflexAgent.delay();
			object.interact(interact);

			if(Camera.getPitch() <= 0.3){
				Camera.concurrentlyTurnTo(Random.nextDouble(0.5, 0.9));
			}

			//Decide if we should double click or not based on player sense
			boolean doubleClick = Random.nextInt(ReflexAgent.getReactionTime()) <= PlayerSense.getAsInteger(CustomPlayerSense.Key.DOUBLE_CLICK.playerSenseKey);

			//Make sure that we actually clicked the rock 
			Player me = Players.getLocal();
			Timer timer = new Timer((int)(object.distanceTo(me) * ReflexAgent.getReactionTime() * 8));
			timer.start();
			boolean broke = false;
			Execution.delay(100);
			while(me.getAnimationId() == -1 && object.isValid() && me.isMoving() && Mouse.getCrosshairState() != Mouse.CrosshairState.YELLOW){
				if(timer.getRemainingTime() <= 0 || doubleClick){
					broke = true;
					break;
				}
				Execution.delay(100);
			}
			return !broke;
		}
	}

	protected boolean outOfRegion() {
		for (Coordinate rock : getRockLocations()) {
			if(Region.getArea().contains(rock)) return false;
		}
		return getRockLocations().length != 0;
	}

	protected void walkToNextEmpty(){
		RockWatcher.Pair<Coordinate, Long, GameObject> rockPair = rockWatcher.nextRock();
		GameObject next = rockPair == null ? null : rockWatcher.nextRock().object;
		Player me = Players.getLocal();
		if(next != null && me != null && next.distanceTo(me) > 1.0 && !me.isMoving()){
			if(next.distanceTo(me) > 2){
				Paint.status = "Walking to rock";
				walkTo(next);
			}else if(next.getVisibility() <= 20 && next.isValid()){
				Paint.status = "Turning to rock";
				Camera.turnTo(next);
			}else{
				if(next.isValid()){
					rockPath = null;
					Paint.status = "Clicking rock";
					ReflexAgent.delay();
					boolean clicked = next.interact("Mine");
					if(Camera.getPitch() <= 0.3){
						Camera.concurrentlyTurnTo(Random.nextDouble(0.5, 0.9));
					}

					Timer timer = new Timer(ReflexAgent.getReactionTime() * 5);
					timer.start();
					while(timer.getRemainingTime() > 0 && next.distanceTo(me) > 1.0 && next.isValid() && clicked){
						next.hover();
						Execution.delay(10,25);
					}
				}
			}
		}
	}
}
