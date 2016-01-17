package scripts.mining.locations.special;

import com.runemate.game.api.hybrid.location.Coordinate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import scripts.mining.Rock;

public class MotherLoad extends SpecialLocation{

	@Override
	public void intialize(String ore) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Motherload";
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

}
