package scripts.mining.locations.rs3;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import scripts.mining.Rock;
import scripts.mining.locations.DepositLocation;

public class LivingRockCavern extends DepositLocation{
	private boolean idleAtBank = false;

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Minerals":
			rocks = new Coordinate[]{};
			break;
		case "Coal":
			rocks = new Coordinate[]{new Coordinate(3301,3300),new Coordinate(3302,3299)};
			break;
		case "Gold":
			rocks = new Coordinate[]{new Coordinate(3668,5076),new Coordinate(3638,5095)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(3653,5117), new Coordinate(3657,5111));
	}

	@Override
	public String getName() {
		return "Living Rock Cavern";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Minerals", "Coal", "Gold"};
	}

	@Override
	public boolean validate(GameObject rock) {
		return rock.getDefinition().getName().equals("Mineral deposit");
	}

	@Override
	public boolean shouldBank() {
		return Inventory.isFull();// || (getNextRock() == null && idleAtBank && !inBank());
	}
	
	/*
	 * TODO All of this needs redoing
	 * public LocatableEntity getBestRock(){
		LocatableEntityQueryResults<? extends LocatableEntity> rocksObjs = GameObjects.getLoaded(new Filter<GameObject>(){
			@Override
			public boolean accepts(GameObject o) {
				if(validate(o)){
					Coordinate pos = o.getPosition();
					for (Coordinate rock : getRocks()) {
						if(pos.equals(rock)) return true;
					}
				}
				return false;
			}
		}).sortByDistance();

		if(rocksObjs.size() > index) return rocksObjs.get(index);
		else{
			if(idleAtBank){
				return null;
			}else{
				LocatableEntityQueryResults<? extends LocatableEntity> remains = Npcs.getLoaded("Living rock remains").sortByDistance();
				//This will pseudo append the next list to the end of the previous list
				if(remains.size() > index - rocksObjs.size()) return remains.get(index - rocksObjs.size());
				else return null;

			}
		}
	}

	@Override
	protected LocatableEntityQueryResults<? extends LocatableEntity> getBanker(){
		return GameObjects.getLoaded("Pulley lift");
	}

	@Override
	public boolean inMine() {
		Player me = Players.getLocal();
		LocatableEntity rock = getBestRock(0);
		if(rock != null){
			if(me.distanceTo(rock) <= 3){
				minePath = null;
				return true;
			}else{
				return false;
			}
		}else{
			for(Coordinate rockLoc : rocks){
				if(me.distanceTo(rockLoc) <= 3){
					minePath = null;
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void walkToMine() {
		LocatableEntity rock = getRock(0);
		if(minePath == null){
			if(rock != null){
				minePath = BresenhamPath.buildTo(rock);
			}else{
				if(idleAtBank){
					Paint.status = "Waiting in safe-zone";
					Execution.delay(200,300);
				}else{
					Paint.status = "Waiting in safe-zone";
					Execution.delay(200,300);
				}
			}
		}else if((Traversal.getDestination() == null || Traversal.getDestination().distanceTo(rock) > 14)){
			minePath.step();
		}
	}*/

	CheckBox box = new CheckBox("Idle at bank");
	@Override
	public void loadSettings() {
		idleAtBank = box.isSelected();
	}

	@Override
	public Node[] getSettingsNodes(){
		box.setStyle("-fx-text-fill: -fx-text-input-text");
		box.setPadding(new Insets(0,0,0,5));
		box.setPrefWidth(165);
		return new Node[]{box};
	}
}
