package scripts.mining.locations.rs3;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import scripts.mining.Rock;
import scripts.mining.locations.Location;

public class Rimmington extends Location{

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Tin":
			rocks = new Coordinate[] {new Coordinate(2972,3229),new Coordinate(2974,3230)};
			break;
		case "Copper":
			rocks = new Coordinate[] {new Coordinate(2966,3234),new Coordinate(2967,3235)};
			break;
		case "Clay":
			rocks = new Coordinate[] {new Coordinate(2979,3241),new Coordinate(2980,3240)};
			break;
		case "Iron":
			rocks = new Coordinate[]{};
			break;
		case "Gold":
			rocks = new Coordinate[] {new Coordinate(2979,3232),new Coordinate(2977,3229)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		mine = new Area.Rectangular(new Coordinate(2984,3243), new Coordinate(2963,3228));
		bank = new Area.Rectangular(new Coordinate(2959,3299), new Coordinate(2954,3295));
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
				rocks = new Coordinate[] {new Coordinate(2980,3233),new Coordinate(2981,3234)};
				break;
			case 1:
				//West
				rocks = new Coordinate[] {new Coordinate(2966,3238),new Coordinate(2967,3239),new Coordinate(2967,3240)};
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

	public boolean validate(GameObject rock) {
		return rock != null && rock.getDefinition() != null && rock.getDefinition().getName() != null &&
				!rock.getDefinition().getName().equals("Rocks") && rock.getDefinition().getName().contains("rocks") && 
				(rock.getDefinition().getName().contains("ore") || rock.getAnimationId() > 0);
	}
}
