package scripts.mining.locations.osrs;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import scripts.mining.Rock;

public class Rimmington extends OSRSLocation{

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Tin":
			rocks = new Coordinate[] {new Coordinate(2984,3237),new Coordinate(2986,3235)};
			break;
		case "Copper":
			rocks = new Coordinate[] {new Coordinate(2977,3247),new Coordinate(2978,3248)};
			break;
		case "Clay":
			rocks = new Coordinate[] {new Coordinate(2987,3240),new Coordinate(2986,3239)};
			break;
		case "Iron":
			rocks = new Coordinate[]{};
			break;
		case "Gold":
			rocks = new Coordinate[] {new Coordinate(2977,3233),new Coordinate(2975,3234)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		mine = new Area.Rectangular(new Coordinate(2988,3250), new Coordinate(2966,3231));
		bank = new Area.Rectangular(new Coordinate(3015,3359), new Coordinate(3010,3354));
	}

	@Override
	public String getName() {
		return "Rimmington";
	}

	@Override
	public String[] getOres() {
		ironArea.getSelectionModel().select(0);
		return new String[]{"Tin", "Copper", "Clay", "Iron", "Gold"};
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
				rocks = new Coordinate[] {new Coordinate(2982,3234),new Coordinate(2981,3233)};
				break;
			case 1:
				//West
				rocks = new Coordinate[] {new Coordinate(2968,3239),new Coordinate(2969,3240),new Coordinate(2969,3242),new Coordinate(2971,3237)};
				break;
			}
		}
	}

	@Override
	public Node[] getSettingsNodes(){
		if(ore == Rock.IRON){
			ironArea.setStyle("-fx-text-fill: -fx-text-input-text");
			ironArea.setPadding(new Insets(0,0,0,5));
			ironArea.setPrefWidth(165);
			return new Node[]{ironArea};
		}else{
			return super.getSettingsNodes();
		}
	}
/* Commented this section out since it stopped the miner from seeing OSRS rocks.
	public boolean validate(GameObject rock) {
		GameObjectDefinition def = rock.getDefinition();
		String name = "";
		if(def != null)name = def.getName();

		return !name.equals("Rocks") && name.contains("rocks") && (name.contains("ore") || rock.getAnimationId() > 0);
	}
*/
}
