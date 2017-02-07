package exiabots.newmining;

import com.runemate.game.api.hybrid.util.Resources;
import com.runemate.game.api.hybrid.util.calculations.Random;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class GUI extends SimpleObjectProperty<Node>{
	public static ImageView warnImage;

	private GUIState state;
	private ImageView styleImage;
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
		startImage = new ImageView(new Image(Resources.getAsStream("exiabots/newmining/start.png")));
		cancelImage = new ImageView(new Image(Resources.getAsStream("exiabots/newmining/cancel.png")));
		warnImage = new ImageView(new Image(Resources.getAsStream("exiabots/newmining/warn.png")));

		javafx.application.Platform.runLater(() -> {
			// Create the actual GUI.
			BorderPane root = new BorderPane();
			StylePane style = new StylePane(name, version, isRS3, fullVersion); 
			ButtonBar buttonBar = new ButtonBar();

			buttonBar.setAction(0, (ActionEvent event) -> {
				buttonBar.setDisabled(1, true);
				root.setCenter(style);
			});

			buttonBar.setAction(1, (ActionEvent event) -> {
				miner = style.currentStyle.miner;
				miner.loadSettings();

				ReflexAgent.initialize(getReflexSeed());

				paint = new Paint(miner);
				if(getReflexSeed() == -1){
					paint.showGraph = false;
				}
				setValue(paint.root);
				state = GUIState.PAINT;
			});

			buttonBar.setDisabled(1, true);

			buttonBar.setAction(2, (ActionEvent event) -> {
				super.setValue(new BorderPane());
				state = GUIState.CLOSED;
			});

			style.setAction((ActionEvent event) -> {
				GridPane pane = style.getMiningStyle().miner.getContentPane(buttonBar, isRS3, fullVersion);
				pane.setMaxHeight(300);
				root.setCenter(pane);
			});
			
			
			root.setLeft(buttonBar);
			root.setCenter(style);
			super.setValue(root);
		});

		state = GUIState.OPEN;
	}

	////////////////////////////
	//    Settings Getters    //
	///////////////////////////
	public int getReflexSeed() {
		//if(enableReflex.isSelected()){
		//	return Integer.parseInt(reflexSeed.getText());
		//}else{
			return -1;
		//}
	}

	public GUIState getState(){
		return state;
	}

	////////////////////////////
	// GUI Creation Functions //
	////////////////////////////


	private class StylePane extends GridPane{
		public class Style {
			Button button;
			MiningStyle miner;
			int x;
			int y;
			private Style(String name, MiningStyle miner, int x, int y){
				button = new Button(name + " Miner");
				button.setMinWidth(166);
				button.setMinHeight(130);
				button.setContentDisplay(ContentDisplay.TOP);

				if(miner == null){
					button.setDisable(true);
				}
				
				this.miner = miner;
				this.x = x;
				this.y = y;
			}
		}
		
		Style[] styles = {
				new Style("Standard", new StandardMiner(), 0, 0),
				new Style("Power", null, 1, 0),
				new Style("Special", null, 0, 1),
				new Style("Multi-World", null, 1, 1)
		};

		Style currentStyle = styles[0];
		
		public StylePane(String name, String version, boolean isRS3, boolean fullVersion){
			setHgap(3.0);
			setVgap(3.0);
			setPadding(new Insets(3,3,3,3));

			CheckBox enableReflex = new CheckBox("Dynamic Reflexes");
			TextField reflexSeed = new TextField("" + Random.nextInt(190, 230));

			for (Style s : styles) {
				add(s.button, s.x , s.y);
			}
			FlowPane settings = new FlowPane();
			settings.setPrefWrapLength(166);

			Label reflexLabel = new Label("Reflex Seed");
			reflexLabel.setPadding(new Insets(3,3,3,10));

			reflexSeed.setPadding(new Insets(0,3,3,3));
			reflexSeed.setPrefWidth(60);

			// TODO fix the spacing of the settings.
			enableReflex.setPadding(new Insets(3,3,3,3));
			enableReflex.setPrefWidth(165);
			enableReflex.setSelected(true);
			enableReflex.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
				if(!new_val){
					reflexLabel.setDisable(true);
					reflexSeed.setDisable(true);
				}else{
					reflexLabel.setDisable(false);
					reflexSeed.setDisable(false);
				}
			});

			//			enableErrorCatching.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			//				catchErrors = new_val;
			//			});

			settings.getChildren().addAll(enableReflex, reflexLabel, reflexSeed);

			add(settings, 2, 1, 1, 2);
		}

		public void setAction(EventHandler<ActionEvent> handler){
			for (Style s : styles) {
				s.button.setOnAction((ActionEvent event) -> {
					currentStyle = s;
					handler.handle(event);
				});
			}
		}
		
		public Style getMiningStyle(){
			return currentStyle;
		}
	}	

	class ButtonBar extends FlowPane{
		private Button[] buttons = new Button[]{
				new Button("Style"),
				new Button("Start"),
				new Button("Close")
		};

		public ButtonBar(){
			setPrefWrapLength(100);
			setMinWidth(75);
			setPadding(new Insets(25, 0, 0, 0));

			buttons[0].setGraphic(styleImage);
			buttons[1].setGraphic(startImage);
			buttons[2].setGraphic(cancelImage);

			for (int i = 0; i < buttons.length; i++) {
				buttons[i].setMinWidth(100);
				buttons[i].setMinHeight(75);
				buttons[i].setContentDisplay(ContentDisplay.TOP);
				getChildren().add(buttons[i]);
			}
		}

		public void setDisabled(int i, boolean disabled){
			buttons[i].setDisable(disabled);
		}

		public void setAction(int i, EventHandler<ActionEvent> handler){
			buttons[i].setOnAction(handler);
		}
	}
}
