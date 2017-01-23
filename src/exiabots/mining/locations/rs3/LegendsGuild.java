package exiabots.mining.locations.rs3;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

import exiabots.mining.Rock;
import exiabots.mining.locations.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class LegendsGuild extends Location{

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Iron":
			rocks = new Coordinate[]{};
			break;
		case "Coal":
			rocks = new Coordinate[]{};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		mine = new Area.Rectangular(new Coordinate(2687, 3326), new Coordinate(2717, 3338));
		bank = new Area.Rectangular(new Coordinate(2648, 3287), new Coordinate(2655, 3280));
	}

	@Override
	public String getName() {
		return "Legend's Guild";
	}

	@Override
	public String[] getOres() {
		ironArea.getSelectionModel().select(0);
		return new String[]{"Iron", "Coal"};
	}

	ObservableList<String> options = 
			FXCollections.observableArrayList(
					"East",
					"West"
					);
	ComboBox<String> ironArea = new ComboBox<String>(options);

	@Override
	public void loadSettings() {
		if(ore == Rock.IRON){
			switch(ironArea.getSelectionModel().getSelectedIndex()){
			case 0:
				//East
				rocks = new Coordinate[] {new Coordinate(2710, 3328),new Coordinate(2713, 3332),new Coordinate(2715, 3331),new Coordinate(2711, 3329),new Coordinate(2714, 3330),new Coordinate(2712, 3329)};
				break;
			case 1:
				//West
				rocks = new Coordinate[] {new Coordinate(2697, 3334),new Coordinate(2696, 3333),new Coordinate(2693, 3329),new Coordinate(2692, 3328),new Coordinate(2691, 3329)};
				break;
			}
		}else if (ore == Rock.COAL){
			switch(ironArea.getSelectionModel().getSelectedIndex()){
			case 0:
				//East
				rocks = new Coordinate[] {new Coordinate(2704, 3328),new Coordinate(2705, 3328),new Coordinate(2705, 3329),new Coordinate(2705, 3331),new Coordinate(2708, 3329),new Coordinate(2708, 3333),new Coordinate(2710, 3332),new Coordinate(2709, 3331)};
				break;
			case 1:
				//West
				rocks = new Coordinate[] {new Coordinate(2694, 3331),new Coordinate(2694, 3332),new Coordinate(2694, 3336),new Coordinate(2691, 3336),new Coordinate(2689, 3335)};
				break;
			}
		}
	}

	@Override
	public Node[] getSettingsNodes(){
		if(ore == Rock.IRON){
			ironArea.setPadding(new Insets(0,0,0,5));
			ironArea.setPrefWidth(165);
			return new Node[]{ironArea};
		}else if (ore == Rock.COAL){
			ironArea.setPadding(new Insets(0,0,0,5));
			ironArea.setPrefWidth(165);
			return new Node[]{ironArea};
		}else{
			return super.getSettingsNodes();
		}
	}

	public boolean validate(GameObject rock) {
		GameObjectDefinition def = rock.getDefinition();
		String name = "";
		if(def != null)name = def.getName();

		return !name.equals("Rocks") && name.contains("rocks") && (name.contains("ore") || rock.getAnimationId() > 0);
	}
}
