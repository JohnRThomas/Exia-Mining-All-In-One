package exiabots.mining.locations.osrs;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.Web;

import exiabots.mining.Rock;

public class VarrockWest extends OSRSLocation{
	Web defaultWeb = Traversal.getDefaultWeb();

	@Override
	public void intialize(String ore){
		switch(ore){
		case "Clay":
			rocks = new Coordinate[] {new Coordinate(3179,3371), new Coordinate(3180,3372)};
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
	
	CheckBox box = new CheckBox("Use distant Rocks");
	@Override
	public void loadSettings() {
		if(box.isSelected() && ore == Rock.IRON){
			rocks = new Coordinate[] {new Coordinate(3181,3373), new Coordinate(3175,3368), new Coordinate(3175,3366)};
		}
		if(box.isSelected() && ore == Rock.CLAY){
			rocks = new Coordinate[] {new Coordinate(3179,3371), new Coordinate(3180,3372), new Coordinate(3183,3377)};
		}
	}

	@Override
	public Node[] getSettingsNodes(){
		if(ore == Rock.IRON || ore == Rock.CLAY){
			box.setPadding(new Insets(10,0,0,5));
			box.setPrefWidth(165);
			return new Node[]{box};
		}else{
			return super.getSettingsNodes();
		}
	}
}
