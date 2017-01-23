package exiabots.mining.locations.rs3;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

import exiabots.mining.Rock;
import exiabots.mining.locations.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class AlKharid extends Location{

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Tin":
			rocks = new Coordinate[]{new Coordinate(3295,3311)};
			break;
		case "Copper":
			rocks = new Coordinate[]{new Coordinate(3297,3315),new Coordinate(3301,3318)};
			break;
		case "Iron":
			rocks = new Coordinate[]{};
			break;
		case "Silver":
			rocks = new Coordinate[]{new Coordinate(3295,3303),new Coordinate(3294,3301),new Coordinate(3303,3312),new Coordinate(3303,3313),new Coordinate(3293,3300)};
			break;
		case "Coal":
			rocks = new Coordinate[]{new Coordinate(3301,3300),new Coordinate(3302,3299)};
			break;
		case "Gold":
			rocks = new Coordinate[]{new Coordinate(3296,3287),new Coordinate(3297,3288)};
			break;
		case "Mithril":
			rocks = new Coordinate[]{new Coordinate(3303,3314),new Coordinate(3303,3305),new Coordinate(3303,3304)};
			break;
		case "Adamantite":
			rocks = new Coordinate[]{new Coordinate(3300,3317),new Coordinate(3299,3317)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(3267,3169), new Coordinate(3273,3165));
		mine = new Area.Rectangular(new Coordinate(3290,3319), new Coordinate(3305,3282));
	}

	@Override
	public String getName() {
		return "Al Kharid";
	}

	@Override
	public String[] getOres() {
		ironArea.getSelectionModel().select(0);
		return new String[]{"Copper", "Tin", "Iron", "Silver", "Coal", "Gold", "Mithril", "Adamantite"};
	}

	ObservableList<String> options = 
		    FXCollections.observableArrayList(
		        "North-west",
		        "South",
		        "Middle",
		        "North-east"
		    );
	ComboBox<String> ironArea = new ComboBox<String>(options);
	CheckBox duelArenaBox = new CheckBox("Bank at Duel Arena");

	@Override
	public void loadSettings() {
		if(ore == Rock.IRON){
			switch(ironArea.getSelectionModel().getSelectedIndex()){
			case 0:
				//North-west
				rocks = new Coordinate[] {new Coordinate(3296,3312),new Coordinate(3296,3313)};
				break;
			case 1:
				//South
				rocks = new Coordinate[] {new Coordinate(3300,3287),new Coordinate(3300,3286)};
				break;
			case 2:
				//Middle
				rocks = new Coordinate[] {new Coordinate(3302,3302),new Coordinate(3301,3301)};
				break;
			case 3:
				//North-east
				rocks = new Coordinate[] {new Coordinate(3303,3310),new Coordinate(3302,3309)};
				break;
				
			}
		}
		if(duelArenaBox.isSelected()){
			bank = new Area.Rectangular(new Coordinate(3345,3240), new Coordinate(3350,3236));
		}
	}

	@Override
	public Node[] getSettingsNodes(){
		duelArenaBox.setPadding(new Insets(10,0,0,5));
		duelArenaBox.setPrefWidth(165);

		if(ore == Rock.IRON){
			ironArea.setPadding(new Insets(0,0,0,5));
			ironArea.setPrefWidth(165);
			return new Node[]{duelArenaBox, ironArea};
		}else{
			return new Node[]{duelArenaBox};
		}
	}
}
