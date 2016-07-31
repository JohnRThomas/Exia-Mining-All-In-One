package scripts.mining;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.util.calculations.Random;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.util.Duration;
import scripts.mining.locations.Location;

public class AIOMinerGUI extends SimpleObjectProperty<Node>{

	String style;
	Location location;
	public MiningStyle miner;
	public boolean catchErrors = true;
	public int dispose = 0;
	public static ImageView warnImage = null;

	public AIOMinerGUI(){
		super();
		super.setValue(createScene());
	}

	BorderPane root = new BorderPane();
	GridPane styleContent;
	GridPane locationContent;

	private Node createScene(){
		root.setLeft(createLeftPane());
		styleContent = createStylePane();

		selectStyle();

		FadeTransition ft = new FadeTransition(Duration.millis(500), root);
		ft.setFromValue(0.0f);
		ft.setToValue(1.0f);
		ft.play();
		return root;
	}

	Button[] buttons;
	private Node createLeftPane() {
		FlowPane vbox = new FlowPane();
		vbox.setPrefWrapLength(75);
		vbox.setPadding(new Insets(25,0,25,0));
		vbox.setMinWidth(75);
		vbox.setMinHeight(349);

		buttons = new Button[]{
				new Button("Style"),
				new Button("Location"),
				new Button("Start"),
				new Button("Close")
		};

		ImageView styleImage = null;
		ImageView locationImage = null;
		ImageView startImage = null;
		ImageView closeImage = null;

		try{
			styleImage = new ImageView(new Image(new FileInputStream(new File(Environment.getStorageDirectory() + "/style.png"))));
			locationImage = new ImageView(new Image(new FileInputStream(new File(Environment.getStorageDirectory() + "/location.png"))));
			startImage = new ImageView(new Image(new FileInputStream(new File(Environment.getStorageDirectory() + "/start.png"))));
			closeImage = new ImageView(new Image(new FileInputStream(new File(Environment.getStorageDirectory() + "/close.png"))));
			warnImage = new ImageView(new Image(new FileInputStream(new File(Environment.getStorageDirectory() + "/warning.png"))));
		}catch(FileNotFoundException e){
			try {
				Image saveAs = new Image(new URL("http://i.imgur.com/WmZ6KYL.png").openStream());
				ImageIO.write(SwingFXUtils.fromFXImage(saveAs, null), "png", new File(Environment.getStorageDirectory() + "/style.png"));
				styleImage = new ImageView(saveAs);

				saveAs = new Image(new URL("http://i.imgur.com/ws9xyTd.png").openStream());
				ImageIO.write(SwingFXUtils.fromFXImage(saveAs, null), "png", new File(Environment.getStorageDirectory() + "/location.png"));
				locationImage = new ImageView(saveAs);

				saveAs = new Image(new URL("http://i.imgur.com/wdFM8s2.png").openStream());
				ImageIO.write(SwingFXUtils.fromFXImage(saveAs, null), "png", new File(Environment.getStorageDirectory() + "/start.png"));
				startImage = new ImageView(saveAs);

				saveAs = new Image(new URL("http://i.imgur.com/60mcBHM.png").openStream());
				ImageIO.write(SwingFXUtils.fromFXImage(saveAs, null), "png", new File(Environment.getStorageDirectory() + "/close.png"));
				closeImage = new ImageView(saveAs);

				saveAs = new Image(new URL("http://i.imgur.com/4bOrdWf.png").openStream());
				ImageIO.write(SwingFXUtils.fromFXImage(saveAs, null), "png", new File(Environment.getStorageDirectory() + "/warning.png"));
				warnImage = new ImageView(saveAs);

			} catch (IOException ex) {
				System.out.println("Failed to Read Files from web!");
				ex.printStackTrace();
			}
		}

		buttons[0].setGraphic(styleImage);
		buttons[1].setGraphic(locationImage);
		buttons[2].setGraphic(startImage);
		buttons[3].setGraphic(closeImage);

		buttons[0].setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				selectStyle();
			}
		});
		buttons[1].setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				selectLocation(event);
			}
		});
		buttons[2].setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				selectStart(event);
			}
		});
		buttons[3].setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				selectClose();
			}
		});

		buttons[1].setDisable(true);
		buttons[2].setDisable(true);

		for (int i = 0; i < buttons.length; i++) {
			//changeBackgroundOnHoverUsingEvents(buttons[i]);
			buttons[i].setMinWidth(75);
			buttons[i].setMinHeight(75);
			buttons[i].setContentDisplay(ContentDisplay.TOP);
			vbox.getChildren().add(buttons[i]);
		}

		return vbox;
	}

	CheckBox enableReflex = new CheckBox("Dynamic Reflexes");
	CheckBox enableErrorCatching = new CheckBox("Enable Error Catching");
	TextField reflexSeed = new TextField("" + Random.nextInt(190, 230));
	private GridPane createStylePane() {
		GridPane content = new GridPane();
		content.setHgap(3.0);
		content.setVgap(3.0);
		content.setPadding(new Insets(3,3,3,3));

		final Button[] localButtons = {
				new Button("Standard Mining"),
				new Button("Power Mining"),
				new Button("Special Mining"),
				new Button("Multi-World Mining")
		};

		localButtons[0].setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				miner = new StandardMiner();
				//setSelected(1);
				buttons[1].setDisable(false);
				locationContent = miner.getContentPane(buttons[2]);
				root.setCenter(locationContent);
			}
		});

		localButtons[1].setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				miner = new PowerMiner();
				//setSelected(1);
				buttons[1].setDisable(false);
				locationContent = miner.getContentPane(buttons[2]);
				root.setCenter(locationContent);
			}
		});
		if(Environment.isSDK()){
			localButtons[2].setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					miner = new SpecialMiner();
					//setSelected(1);
					buttons[1].setDisable(false);
					locationContent = miner.getContentPane(buttons[2]);
					root.setCenter(locationContent);
				}
			});
		}else{
			localButtons[2].setDisable(true);
		}

		localButtons[3].setDisable(true);

		for (int i = 0; i < localButtons.length; i++) {
			localButtons[i].setMinWidth(166);
			localButtons[i].setMinHeight(130);
			localButtons[i].setContentDisplay(ContentDisplay.TOP);
			content.add(localButtons[i], i % 2 , (i / 2) + 1);
		}
		FlowPane settings = new FlowPane();
		settings.setPrefWrapLength(166);

		Label reflexLabel = new Label("Reflex Seed");
		reflexLabel.setPadding(new Insets(3,3,3,10));

		reflexSeed.setPadding(new Insets(0,3,3,3));
		reflexSeed.setPrefWidth(60);

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

		enableErrorCatching.setPadding(new Insets(3,3,3,3));
		enableErrorCatching.setPrefWidth(165);
		enableErrorCatching.setSelected(catchErrors);
		enableErrorCatching.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			catchErrors = new_val;
		});
		
		settings.getChildren().addAll(enableErrorCatching, enableReflex, reflexLabel, reflexSeed);

		content.add(settings, 2, 1, 1, 2);
		return content;
	}

	/*private void setSelected(int button){
		for(Button node: buttons){
			node.setStyle(STANDARD_BUTTON_STYLE);
			node.getStyleClass().remove("selected");
		}
		buttons[button].setStyle(HOVERED_BUTTON_STYLE);
		buttons[button].getStyleClass().add("selected");
	}*/

	private void selectStyle(){
		//setSelected(0);
		buttons[2].setDisable(true);
		root.setCenter(styleContent);
	}

	private void selectLocation(ActionEvent event){
		//setSelected(1);
		root.setCenter(locationContent);
	}

	private void selectStart(ActionEvent event){
		//setSelected(2);

		FadeTransition ft = new FadeTransition(Duration.millis(500), root);
		ft.setFromValue(1.0f);
		ft.setToValue(0.0f);
		ft.play();
		ft.setOnFinished(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				miner.loadSettings();
				dispose = 1;
			}
		});
	}

	private void selectClose(){
		//setSelected(3);

		FadeTransition ft = new FadeTransition(Duration.millis(500), root);
		ft.setFromValue(1.0f);
		ft.setToValue(0.0f);
		ft.play();
		ft.setOnFinished(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				dispose = 2;
			}
		});
	}

	/*public void changeBackgroundOnHoverUsingEvents(final Node node) {
		node.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				node.setStyle(HOVERED_BUTTON_STYLE);
			}
		});
		node.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				if(!node.getStyleClass().contains("selected")){
					node.setStyle(STANDARD_BUTTON_STYLE);
				}
			}
		});
	}*/

	public int getReflexSeed() {
		if(enableReflex.isSelected()){
			return Integer.parseInt(reflexSeed.getText());
		}else{
			return -1;
		}
	}
}
