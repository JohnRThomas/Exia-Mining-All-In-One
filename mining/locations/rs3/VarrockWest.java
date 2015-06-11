package scripts.mining.locations.rs3;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import scripts.mining.Rock;
import scripts.mining.locations.Location;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

public class VarrockWest extends Location{
	
	@Override
	public void intialize(String ore){
		switch(ore){
		case "Clay":
			rocks = new Coordinate[] {new Coordinate(3179,3371), new Coordinate(3180,3372), new Coordinate(3183,3377)};
			break;
		case "Tin":
			rocks = new Coordinate[] {new Coordinate(3181,3377), new Coordinate(3181,3376), new Coordinate(3183,3376), new Coordinate(3181,3375),
					new Coordinate(3176,3369), new Coordinate(3173,3366), new Coordinate(3173,3365), new Coordinate(3172,3366)};
			break;
		case "Iron":
			rocks = new Coordinate[] {new Coordinate(3175,3368), new Coordinate(3175,3366)};
			break;
		case "Silver":
			rocks = new Coordinate[] {new Coordinate(3177,3366), new Coordinate(3176,3365), new Coordinate(3177,3370), new Coordinate(3177,3370)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		
		bank = new Area.Rectangular(new Coordinate(3181,3434, 0), new Coordinate(3190,3439, 0));
		mine = new Area.Rectangular(new Coordinate(3172,3364), new Coordinate(3183,3375));
	}

	@Override
	public String getName() {
		return "Varrock West";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Clay", "Tin", "Iron", "Silver"};
	}

	@Override
	public Coordinate[] getRocks() {
		return rocks;
	}
	
	CheckBox box = new CheckBox("Use distant Rocks");
	@Override
	public void loadSettings() {
		if(box.isSelected() && ore == Rock.IRON){
			rocks = new Coordinate[] {new Coordinate(3181,3373), new Coordinate(3175,3368), new Coordinate(3175,3366)};
		}
	}

	@Override
	public Node[] getSettingsNodes(){
		if(ore == Rock.IRON){
			box.setStyle("-fx-text-fill: -fx-text-input-text");
			box.setPadding(new Insets(0,0,0,5));
			box.setPrefWidth(165);
			return new Node[]{box};
		}else{
			return super.getSettingsNodes();
		}
	}
}