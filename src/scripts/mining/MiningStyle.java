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
	
	public static LocatableEntity currentRock = null;

	Path rockPath = null;
	protected void walkTo(LocatableEntity rock) {
		Paint.status = "Walking to rock";
		if(rockPath == null && rock != null){
			try{
				rockPath = BresenhamPath.buildTo(rock);
			}catch(Exception e){}
		}else if((Traversal.getDestination() == null || Traversal.getDestination().distanceTo(rock) > 14)){
			ReflexAgent.delay();
			rockPath.step();
		}
	}
	
	protected void turnAndClick(LocatableEntity rock){
		if(rock.getVisibility() <= 20){
			Paint.status = "Turning to rock";
			//if only part of the rock is visible, turn to it
			Camera.turnTo(rock);
		}else{
			Paint.status = "Clicking rock";
			//The rock is visible enough, so we click it
			ReflexAgent.delay();
			rock.interact("Mine");
			
			if(Camera.getPitch() <= 0.3){
				Camera.concurrentlyTurnTo(Random.nextDouble(0.5, 0.9));
			}
			
			//Decide if we should double click or not based on player sense
			boolean doubleClick = Random.nextInt(100) <= PlayerSense.getAsInteger(CustomPlayerSense.Key.DOUBLE_CLICK.playerSenseKey);
					
			//Make sure that we actually clicked the rock 
			currentRock = rock;
			Player me = Players.getLocal();
			Timer timer = new Timer((int)(rock.distanceTo(me) * ReflexAgent.getReactionTime() * 4));
			timer.start();
			while(timer.getRemainingTime() > 0 && !doubleClick && me.getAnimationId() == -1 && rock.isValid() && Mouse.getCrosshairState() != Mouse.CrosshairState.YELLOW){
				Execution.delay(100);
			}
		}
	}
	
	protected boolean outOfRegion() {
		Region baseRegion = Region.getLoaded();
		for (Coordinate rock : getRockLocations()) {
			if(baseRegion.getArea().contains(rock)) return false;
		}
		return true;
	}
	
	protected void walkToNextEmpty(){
		RockWatcher.Pair<Coordinate, Long, GameObject> rockPair = rockWatcher.nextRock();
		GameObject next = rockPair == null ? null : rockWatcher.nextRock().object;
		Player me = Players.getLocal();
		if(next != null && next.distanceTo(me) > 1.0 && !me.isMoving()){
			if(next.distanceTo(me) > 16){
				Paint.status = "Walking to rock";
				walkTo(next);
			}else if(next.getVisibility() <= 20 && next.isValid()){
				Paint.status = "Turning to rock";
				Camera.turnTo(next);
			}else{
				if(next.isValid()){
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
