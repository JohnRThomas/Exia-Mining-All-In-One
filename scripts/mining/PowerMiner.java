package scripts.mining;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.Players;

public class PowerMiner extends MiningStyle{
	
	private boolean dropping;
	private boolean mine1drop1 = false;
	LocatableEntity currentRock = null;
	int notMiningCount = 0;
	private Rock ore;
	
	@Override
	public void onStart(String... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLocationName() {
		return "Power Mining";
	}
	
	@Override
	public Rock getOre() {
		return ore;
	}
	@Override
	public void loop() {
		if(Inventory.isFull() || dropping){
			status = "Dropping";
			dropping = true;
			drop();
		}else{
			status = "Mining";
			mine();
			if(Players.getLocal().getAnimationId() == -1)notMiningCount++;
			else notMiningCount = 0;

			if(notMiningCount >= 15){
				notMiningCount = 0;
				currentRock = null;
			}
		}
	}
	
	private void mine() {
		// TODO Auto-generated method stub
		
	}

	private void drop() {
		if(Inventory.isEmpty() || mine1drop1)dropping = false;
	}

	private GridPane content = null;
	
	CheckBox mineOne = new CheckBox("Mine one drop one");
	@Override
	public void loadSettings() {
		mine1drop1  = mineOne.isSelected();
	}
	
	@Override
	public GridPane getContentPane(Button startButton) {
		if(content != null)return content;
		content = new GridPane();
		content.setPadding(new Insets(25,3,25,3));
		content.setHgap(1.0);
		content.setVgap(1.0);


		ListView<String> oreList = new ListView<String>(); 
		oreList.setPrefWidth(167);

		FlowPane settings = new FlowPane();
		settings.setStyle("-fx-background-color: -fx-background-dark-hundred; -fx-border-color: -fx-flair; -fx-border-style: solid; -fx-border-width: 1;");
		settings.setPrefWrapLength(335);
		
		ObservableList<String> ores = Rock.getOres(Environment.isRS3());
		oreList.setItems(ores);
		oreList.getSelectionModel().clearSelection();

		oreList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null){
					startButton.setDisable(false);
					ore = Rock.getByName(newValue);
				}
			}
		});
		
		mineOne.setStyle("-fx-text-fill: -fx-text-input-text");
		mineOne.setPadding(new Insets(0,0,0,5));
		settings.getChildren().add(mineOne);
		
		final String LABEL_STYLE = "-fx-text-fill: -fx-flair-text; -fx-font-size: 15px; -fx-background-color: -fx-flair;";

		Label oreLabel = new Label("Ores");
		oreLabel.setStyle(LABEL_STYLE);
		oreLabel.setAlignment(Pos.CENTER);
		oreLabel.setPrefWidth(167);

		Label setLabel = new Label("Settings");
		setLabel.setStyle(LABEL_STYLE);
		setLabel.setAlignment(Pos.CENTER);
		setLabel.setPrefWidth(337);

		content.add(oreLabel, 0, 0);
		content.add(oreList, 0, 1);

		content.add(setLabel, 1, 0);
		content.add(settings, 1, 1);

		return content;
	}
}
