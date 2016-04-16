package scripts.mining.locations.osrs;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import scripts.mining.Rock;

public class AlKharid extends OSRSLocation{

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Tin":
			rocks = new Coordinate[]{new Coordinate(3302,3316)};
			break;
		case "Copper":
			rocks = new Coordinate[]{new Coordinate(3297,3315),new Coordinate(3296,3314)};
			break;
		case "Iron":
			break;
		case "Silver":
			rocks = new Coordinate[]{new Coordinate(3295,3304),new Coordinate(3294,3301),new Coordinate(3303,3314),new Coordinate(3303,3313),new Coordinate(3293,3300)};
			break;
		case "Coal":
			rocks = new Coordinate[]{new Coordinate(3304,3300),new Coordinate(3303,3299), new Coordinate(3302,3317)};
			break;
		case "Gold":
			rocks = new Coordinate[]{new Coordinate(3294,3287),new Coordinate(3294,3288)};
			break;
		case "Mithril":
			rocks = new Coordinate[]{new Coordinate(3304,3305),new Coordinate(3305,3304)};
			break;
		case "Adamantite":
			rocks = new Coordinate[]{new Coordinate(3300,3318),new Coordinate(3298,3317)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(3267,3169), new Coordinate(3273,3165));
		mine = new Area.Rectangular(new Coordinate(3293,3319), new Coordinate(3306,3283));
	}

	@Override
	public Pattern getBankInteract() {
		if(duelArena)return Pattern.compile("Bank|Use|Open");
		else return super.getBankInteract();
	}
	
	@Override
	protected LocatableEntityQueryResults<? extends LocatableEntity> getBankers(){
		if(duelArena){
			LocatableEntityQueryResults<? extends LocatableEntity> bankers = super.getBankers();
			if(bankers.isEmpty()){
				bankers = GameObjects.getLoaded(new Predicate<GameObject>(){
					@Override
					public boolean test(GameObject o) {
						GameObjectDefinition def = o.getDefinition();
						String name = "";
						if(def != null)name = def.getName();
						return name.contains("chest");
					}
				});
			}
			return bankers;
		}
		else return super.getBankers();

	}
	
	@Override
	public String getName() {
		return "Al Kharid";
	}
	
	@Override
	public String[] getOres() {
		ironArea.getSelectionModel().select(0);
		return new String[]{"Copper","Tin", "Iron", "Silver", "Coal", "Gold", "Mithril", "Adamantite"};
	}
	
	@Override
	public Coordinate[] getRocks() {
		return rocks;
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
	private boolean duelArena = false;

	@Override
	public void loadSettings() {
		if(ore == Rock.IRON){
			switch(ironArea.getSelectionModel().getSelectedIndex()){
			case 0:
				//North-west
				rocks = new Coordinate[] {new Coordinate(3295,3311),new Coordinate(3294,3310),new Coordinate(3295,3309)};
				break;
			case 1:
				//South
				rocks = new Coordinate[] {new Coordinate(3302,3285),new Coordinate(3303,3284)};
				break;
			case 2:
				//Middle
				rocks = new Coordinate[] {new Coordinate(3305,3302),new Coordinate(3304,3301)};
				break;
			case 3:
				//North-east
				rocks = new Coordinate[] {new Coordinate(3303,3309),new Coordinate(3303,3310)};
				break;
				
			}
		}
		if(duelArenaBox.isSelected()){
			duelArena = true;
			bank = new Area.Rectangular(new Coordinate(3380,3271), new Coordinate(3384,3267));
		}
	}

	@Override
	public Node[] getSettingsNodes(){
		duelArenaBox.setStyle("-fx-text-fill: -fx-text-input-text");
		duelArenaBox.setPadding(new Insets(10,0,0,5));
		duelArenaBox.setPrefWidth(165);

		if(ore == Rock.IRON){
			ironArea.setStyle("-fx-text-fill: -fx-text-input-text");
			ironArea.setPadding(new Insets(0,0,0,5));
			ironArea.setPrefWidth(165);
			return new Node[]{duelArenaBox, ironArea};
		}else{
			return new Node[]{duelArenaBox};
		}
	}
}
