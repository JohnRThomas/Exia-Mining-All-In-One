package scripts.mining.locations.osrs;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.definitions.GameObjectDefinition;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import scripts.mining.AIOMinerGUI;
import scripts.mining.Rock;

public class LumbridgeWest extends OSRSLocation{

	@Override
	public void intialize(String ore) {
		switch(ore){
		case "Coal":
			rocks = new Coordinate[]{new Coordinate(3146,3151),new Coordinate(3147,3150),new Coordinate(3145,3149),new Coordinate(3144,3148),new Coordinate(3145,3147),new Coordinate(3143,3147),new Coordinate(3148,3144)};
			break;
		case "Mithril":
			rocks = new Coordinate[]{new Coordinate(3229,3146),new Coordinate(3230,3147),new Coordinate(3227,3145),new Coordinate(3228,3151),new Coordinate(3223,3150)};
			break;
		case "Adamantite":
			rocks = new Coordinate[]{new Coordinate(3149,3145),new Coordinate(3149,3147)};
			break;
		default:
			throw new RuntimeException(ore + " is not supported in " + getName());
		}
		this.ore = Rock.getByName(ore);
		bank = new Area.Rectangular(new Coordinate(3155,3234), new Coordinate(3150,3229));
		mine = new Area.Rectangular(new Coordinate(3150,3149), new Coordinate(3142,3142));
	}

	@Override
	public String getName() {
		return "Lumbridge West";
	}

	@Override
	public String[] getOres() {
		return new String[]{"Coal", "Mithril", "Adamantite"};
	}

	@Override
	public Node[] getSettingsNodes(){
		ImageView warnImage = AIOMinerGUI.warnImage;
		Label labela = new Label("WARNING!");
		labela.setStyle("-fx-text-fill: -fx-text-input-text");
		labela.setPadding(new Insets(0,0,3,5));
		labela.setPrefWidth(165);
		labela.setGraphic(warnImage);

		Label labelb = new Label("This area may be");
		labelb.setStyle("-fx-text-fill: -fx-text-input-text");
		labelb.setPadding(new Insets(0,0,3,5));
		labelb.setPrefWidth(165);
		
		Label labelc = new Label("buggy due to counter");
		labelc.setStyle("-fx-text-fill: -fx-text-input-text");
		labelc.setPadding(new Insets(0,0,3,5));
		labelc.setPrefWidth(165);	
		
		Label labeld = new Label("measures by Jagex.");
		labeld.setStyle("-fx-text-fill: -fx-text-input-text");
		labeld.setPadding(new Insets(0,0,3,5));
		labeld.setPrefWidth(165);
		
		return new Node[]{labela, labelb, labelc, labeld};
	}
	
	public boolean validate(GameObject rock) {
		GameObjectDefinition def = rock.getDefinition();
		String name = "";
		if(def != null)name = def.getName();

		return !name.equals("Rocks") && name.contains("rocks") && (name.contains("ore") || rock.getAnimationId() > 0);
	}
}
