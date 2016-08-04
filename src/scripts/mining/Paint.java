package scripts.mining;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.runemate.game.api.hybrid.util.Time;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Paint{
	
	public BorderPane root;
	public int tempLevel = 0;
	public long startTime = 0;
	public int levelsGained = 0;
	public int startEXP = -1;
	public boolean showGraph = true;
	public boolean stop = false;
	public int currentEXP = 0;
	public int nextLevelEXP = 0;
	public int currentLevel = 0;
	public int percentage = 0;

	public static MoneyCounter profitCounter = null;
	public static String status = "";

	private MiningStyle miner;
	
	// GUI elements
	private LineChart<Number,Number> lineChart;
	private ProgressBar pb;
	private Label[] labels = new Label[11];

	public Paint(MiningStyle miner){
		super();
		startTime = System.currentTimeMillis();
		profitCounter = new MoneyCounter(miner.getOre().oreNames);
		this.miner = miner;
		createScene();
		update();

		// Schedule this to update every second
		Timer timer = new Timer("Paint Updater");
		timer.schedule(new TimerTask(){ 
			public void run(){
				javafx.application.Platform.runLater(() -> {
					update();
					if(stop){
						cancel();
					}
				});
			}
		}, 0, 700);
	}

	private void createScene(){
		VBox left = new VBox();
		VBox center = new VBox();
		for(int i = 0; i < labels.length - 1; i++){
			labels[i] = new Label();
			left.getChildren().add(labels[i]);
		}
		HBox progress = new HBox();
		pb = new ProgressBar(0f);
		pb.prefWidthProperty().bind(left.widthProperty().subtract(30));
		labels[labels.length - 1] = new Label();
		labels[labels.length - 1].setMaxWidth(30);
		progress.getChildren().addAll(labels[labels.length - 1], pb);
		left.getChildren().add(progress);
		left.setMinWidth(200);

		if(showGraph){
			final NumberAxis xAxis = new NumberAxis();
			final NumberAxis yAxis = new NumberAxis();
			xAxis.setMinorTickVisible(false);
			xAxis.setForceZeroInRange(false);
			xAxis.setAutoRanging(false);
			xAxis.setStyle("-fx-border-color: #333333 transparent transparent transparent;");

			yAxis.setMinorTickVisible(false);
			yAxis.setForceZeroInRange(false);
			yAxis.setStyle("-fx-border-color: transparent #333333 transparent transparent;");

			lineChart = new LineChart<Number,Number>(xAxis,yAxis);
			lineChart.setTitle("Reaction Time");
			lineChart.setCreateSymbols(false);
			lineChart.setLegendVisible(false);
			lineChart.setMaxWidth(300);
			lineChart.prefHeightProperty().bind(left.heightProperty());

			Node styler = lineChart.lookup(".chart-vertical-grid-lines");
			styler.setStyle("-fx-stroke: #333333;");
			styler = lineChart.lookup(".chart-horizontal-grid-lines");
			styler.setStyle("-fx-stroke: #333333;");

			styler = xAxis.lookup(".axis-tick-mark");
			styler.setStyle("-fx-stroke: #333333;");

			styler = yAxis.lookup(".axis-tick-mark");
			styler.setStyle("-fx-stroke: #333333;");

			updateGraph();

			center.getChildren().add(lineChart);
		}

		root.setLeft(left);
		root.setCenter(center);
	}

	public void update() {
		int totalEXP = currentEXP - startEXP;

		long time = System.currentTimeMillis() - startTime;
		long expPhr = time != 0 ? ((long)totalEXP*3600000)/time : 0;
		long profPhr = time != 0 ? ((long)profitCounter.getProfit()*3600000) / time : 0;
		long orePhr = time != 0 ? ((long)profitCounter.getOreCount()*3600000) / time : 0;

		labels[0].setText("Runtime: " + Time.format(time));
		labels[1].setText("Location: " + miner.getLocationName() + " (" + miner.getOre().name + ")");
		labels[2].setText("Status: "+ status);
		labels[3].setText("Ores/Hour: " + formatBigNumber(orePhr));
		labels[4].setText("Profit: " + formatBigNumber(profitCounter.getProfit()));
		labels[5].setText("Profit/Hour: " + formatBigNumber(profPhr));
		labels[6].setText("Experience: " + formatBigNumber(totalEXP));
		labels[7].setText("Exp/Hour: " + formatBigNumber(expPhr));
		labels[8].setText("Current Level: " + currentLevel + "(+" + levelsGained + ")");
		if(totalEXP > 0){
			long ttl = (long) ((time) * ((long)nextLevelEXP)/ totalEXP);
			labels[9].setText("Next Level: " + Time.format(ttl));
		}else{
			labels[9].setText("Next Level: " + Time.format(0L));
		}
		labels[labels.length - 1].setText((int)percentage + "%");
		pb.setProgress((double)percentage / 100.0);
	}

	public void updateGraph(){
		if(showGraph){
			XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();

			for(double i = 0; i <= 7.875; i+=.125){
				series.getData().add(new XYChart.Data<Number, Number>(i + (8*ReflexAgent.resets), ReflexAgent.applyPolynomial(i)));
			}

			lineChart.getData().clear();
			lineChart.getData().add(series);
			((NumberAxis)(lineChart.getXAxis())).setLowerBound(ReflexAgent.resets * 8);
			((NumberAxis)(lineChart.getXAxis())).setUpperBound((ReflexAgent.resets * 8) + 8);
			((NumberAxis)(lineChart.getXAxis())).setTickUnit(1);
			lineChart.getData().get(0).getNode().setStyle("-fx-stroke: #2DCC71");
		}
	}

	private static String formatBigNumber(long number){
		return NumberFormat.getInstance().format(number);
	}
}
