package scripts.mining.locations.rs3;

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
import scripts.mining.locations.Location;

public class Yanille extends Location{

	@Override
	public void intialize(String ore) {
		switch(ore){
			case "Coal":
				rocks = new Coordinate[] {new Coordinate(2626, 3131)};
				break;
			case "Copper":
				rocks = new Coordinate[] {new Coordinate(2630, 3137),new Coordinate(2630, 3145)};
				break;
			case "Iron":
				rocks = new Coordinate[] {};
				break;
			case "Tin":
				rocks = new Coordinate[] {};
				break;
			case "Mithril":
				rocks = new Coordinate[] {new Coordinate(2628, 3147),new Coordinate(2629, 3147)};
				break;
			case "Clay":
				rocks = new Coordinate[] {new Coordinate(2629, 3145),new Coordinate(2630, 3143),new Coordinate(2629, 3143),new Coordinate(2632, 3139)};
				break;
			default:
				throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		mine = new Area.Rectangular(new Coordinate(2623, 3129), new Coordinate(2634, 3153));
		bank = new Area.Rectangular(new Coordinate(2609, 3095), new Coordinate(2613, 3091));
	}

	@Override
	public String getName() {
		return "Yanille";
	}

	@Override
	public String[] getOres() {
		ironArea.getSelectionModel().select(0);
		return new String[]{"Clay", "Coal", "Copper", "Iron", "Mithril", "Tin"};
	}

	ObservableList<String> options =
			FXCollections.observableArrayList(
					"North",
					"South"
			);
	ComboBox<String> ironArea = new ComboBox<String>(options);

	@Override
	public void loadSettings() {
		if(ore == Rock.IRON){
			switch(ironArea.getSelectionModel().getSelectedIndex()){
				case 0:
					//North
					rocks = new Coordinate[] {new Coordinate(2626, 3149),new Coordinate(2626, 3150),new Coordinate(2625, 3150),new Coordinate(2625, 3151)};
					break;
				case 1:
					//South
					rocks = new Coordinate[] {new Coordinate(2627, 3142),new Coordinate(2628, 3141),new Coordinate(2628, 3140)};
					break;
			}
		}else if (ore == Rock.TIN){
			switch(ironArea.getSelectionModel().getSelectedIndex()){
				case 0:
					//North
					rocks = new Coordinate[] {new Coordinate(2630, 3148),new Coordinate(2630, 3150),new Coordinate(2631, 3147)};
					break;
				case 1:
					//South
					rocks = new Coordinate[] {new Coordinate(2632, 3142),new Coordinate(2632, 3141),new Coordinate(2630, 3140)};
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
		}else if (ore == Rock.TIN){
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
