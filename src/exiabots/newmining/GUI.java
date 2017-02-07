package exiabots.newmining;

import com.runemate.game.api.hybrid.util.Resources;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class GUI extends SimpleObjectProperty<Node>{
	public static ImageView warnImage;

	private GUIState state;
	private ImageView styleImage;
	private ImageView locationImage;
	private ImageView startImage;
	private ImageView cancelImage;

	public MiningStyle miner;
	public Paint paint;
	
	// An simple enum to keep track of the gui's state.
	public enum GUIState {
		WAITING(),
		OPEN(),
		PAINT(),
		CLOSED();
	}
	
	public GUI(){
		super();
		state = GUIState.WAITING;
		super.setValue(new Label("Waiting for game data to be loaded..."));
	}
	
	public void load(String name, String version, boolean isRS3, boolean fullVersion){
		
		styleImage = new ImageView(new Image(Resources.getAsStream("exiabots/newmining/style.png")));
		locationImage = new ImageView(new Image(Resources.getAsStream("exiabots/newmining/location.png")));
		startImage = new ImageView(new Image(Resources.getAsStream("exiabots/newmining/start.png")));
		cancelImage = new ImageView(new Image(Resources.getAsStream("exiabots/newmining/cancel.png")));
		warnImage = new ImageView(new Image(Resources.getAsStream("exiabots/newmining/warn.png")));
		
		javafx.application.Platform.runLater(() -> {
			// Create the actual GUI.
			BorderPane root = new BorderPane();
			//CenterContent center = new CenterContent(name, version, isRS3, fullVersion); 
			ButtonBar buttons = new ButtonBar();
			
			buttons.setAction(0, new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					buttons.setDisabled(2, true);
					//root.setCenter(styleContent);
				}
			});

			buttons.setAction(1, new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					//root.setCenter(locationContent);
				}
			});
			
			buttons.setAction(2, new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					miner.loadSettings();

					//ReflexAgent.initialize(getReflexSeed());
					
					paint = new Paint(miner);
					//if(getReflexSeed() == -1){
					//	paint.showGraph = false;
					//}
					setValue(paint.root);
					state = GUIState.PAINT;
				}
			});
			
			buttons.setAction(3, new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					state = GUIState.CLOSED;
				}
			});
			
			root.setLeft(buttons);
			//root.setCenter(center);
			super.setValue(root);
		});

		state = GUIState.OPEN;
	}

	////////////////////////////
	//    Settings Getters    //
	///////////////////////////
//	public int getReflexSeed() {
//		if(enableReflex.isSelected()){
//			return Integer.parseInt(reflexSeed.getText());
//		}else{
//			return -1;
//		}
//	}
	
	public GUIState getState(){
		return state;
	}
	
	////////////////////////////
	// GUI Creation Functions //
	////////////////////////////
//	private class CenterContent extends GridPane{
//		private Button[] buttons = {
//				new Button("Standard Mining"),
//				new Button("Power Mining"),
//				new Button("Special Mining"),
//				new Button("Multi-World Mining")
//		};
//		
//		public CenterContent(String name, String version, boolean isRS3, boolean fullVersion){
//			setHgap(3.0);
//			setVgap(3.0);
//			setPadding(new Insets(3,3,3,3));
//
//			CheckBox enableReflex = new CheckBox("Dynamic Reflexes");
//			CheckBox enableErrorCatching = new CheckBox("Enable Error Catching");
//			TextField reflexSeed = new TextField("" + Random.nextInt(190, 230));
//
//
//			localButtons[0].setOnAction(new EventHandler<ActionEvent>(){
//				@Override
//				public void handle(ActionEvent event) {
//					miner = new exiabots.newmining.StandardMiner();
//					buttons[1].setDisable(false);
//					locationContent = miner.getContentPane(buttons[2]);
//					locationContent.setMaxHeight(leftPane.getHeight());
//					root.setCenter(locationContent);
//				}
//			});
//
//			localButtons[1].setDisable(true);
//			localButtons[2].setDisable(true);
//			
//			/*localButtons[1].setOnAction(new EventHandler<ActionEvent>(){
//				@Override
//				public void handle(ActionEvent event) {
//					miner = new PowerMiner();
//					buttons[1].setDisable(false);
//					locationContent = miner.getContentPane(buttons[2]);
//					locationContent.setMaxHeight(leftPane.getHeight());
//					root.setCenter(locationContent);
//				}
//			});*/
//			/*if(Environment.isSDK()){
//				localButtons[2].setOnAction(new EventHandler<ActionEvent>(){
//					@Override
//					public void handle(ActionEvent event) {
//						miner = new SpecialMiner();
//						buttons[1].setDisable(false);
//						locationContent = miner.getContentPane(buttons[2]);
//						locationContent.setMaxHeight(leftPane.getHeight());
//						root.setCenter(locationContent);
//					}
//				});
//			}else{
//				localButtons[2].setDisable(true);
//			}*/
//
//			localButtons[3].setDisable(true);
//
//			for (int i = 0; i < localButtons.length; i++) {
//				localButtons[i].setMinWidth(166);
//				localButtons[i].setMinHeight(130);
//				localButtons[i].setContentDisplay(ContentDisplay.TOP);
//				add(localButtons[i], i % 2 , (i / 2) + 1);
//			}
//			FlowPane settings = new FlowPane();
//			settings.setPrefWrapLength(166);
//
//			Label reflexLabel = new Label("Reflex Seed");
//			reflexLabel.setPadding(new Insets(3,3,3,10));
//
//			reflexSeed.setPadding(new Insets(0,3,3,3));
//			reflexSeed.setPrefWidth(60);
//
//			enableReflex.setPadding(new Insets(3,3,3,3));
//			enableReflex.setPrefWidth(165);
//			enableReflex.setSelected(true);
//			enableReflex.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
//				if(!new_val){
//					reflexLabel.setDisable(true);
//					reflexSeed.setDisable(true);
//				}else{
//					reflexLabel.setDisable(false);
//					reflexSeed.setDisable(false);
//				}
//			});
//
//			enableErrorCatching.setPadding(new Insets(3,3,3,3));
//			enableErrorCatching.setPrefWidth(165);
//			enableErrorCatching.setSelected(catchErrors);
//			enableErrorCatching.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
//				catchErrors = new_val;
//			});
//			
//			settings.getChildren().addAll(enableErrorCatching, enableReflex, reflexLabel, reflexSeed);
//
//			add(settings, 2, 1, 1, 2);
//		}
//		
//		public void setAction(int i, EventHandler<ActionEvent> handler){
//			buttons[i].setOnAction(handler);
//		}
//
//		public 
//	}	
	
	private class ButtonBar extends FlowPane{
		private Button[] buttons = new Button[]{
				new Button("Style"),
				new Button("Location"),
				new Button("Start"),
				new Button("Close")
		};
		
		public ButtonBar(){
			setPrefWrapLength(100);
			setPadding(new Insets(0,0,25,0));
			setMinWidth(75);
			setMinHeight(349);
					
			buttons[0].setGraphic(styleImage);
			buttons[1].setGraphic(locationImage);
			buttons[2].setGraphic(startImage);
			buttons[3].setGraphic(cancelImage);

			for (int i = 0; i < buttons.length; i++) {
				buttons[i].setMinWidth(100);
				buttons[i].setMinHeight(75);
				buttons[i].setContentDisplay(ContentDisplay.TOP);
				getChildren().add(buttons[i]);
			}
		}
		
		public void setDisabled(int i, boolean disabled){
			buttons[i].setDisable(true);
		}
		
		public void setAction(int i, EventHandler<ActionEvent> handler){
			buttons[i].setOnAction(handler);
		}
	}
}
