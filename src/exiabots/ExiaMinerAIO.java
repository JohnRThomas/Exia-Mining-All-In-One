package exiabots;

import java.math.BigDecimal;

import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingScript;

import exiabots.mining.AIOMinerGUI;
import exiabots.mining.CustomPlayerSense;
import exiabots.mining.ErrorHandler;
import exiabots.mining.MiningStyle;
import exiabots.mining.Paint;
import exiabots.mining.ReflexAgent;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public class ExiaMinerAIO extends LoopingScript implements EmbeddableUI{
	public static MiningStyle miner;
	public static String version = "";
	public static String name = "";
	public static ExiaMinerAIO instance;
	public static boolean isPaid = false;
	private AIOMinerGUI gui = new AIOMinerGUI();
	private Paint paint;
	private boolean catchErrors = true;
	public static boolean isRS3;
	
	@Override
	public ObjectProperty<Node> botInterfaceProperty() {
		return gui;
	}
	
	@Override
	public void onStart(String... args){
		setEmbeddableUI(this);
		
		setLoopDelay(0);
		isRS3 = Environment.isRS3();
		version = getMetaData().getVersion();
		name = getMetaData().getName();
		isPaid = getMetaData().getHourlyPrice().compareTo(BigDecimal.ZERO) > 0;
		instance = this;

		while(gui == null || gui.dispose == 0)Execution.delay(500);
		if(gui.dispose == 2){
			stop();
			return;
		}

		paint = gui.paint;
		CustomPlayerSense.intialize();

		miner = gui.miner;
		catchErrors = !Environment.isSDK() && gui.catchErrors;
		
		miner.onStart(args);
	}

	@Override
	public void onLoop() {
		// Update the paint variables
		if(paint.startEXP == -1)paint.startEXP = Skill.MINING.getExperience();
		paint.currentEXP = Skill.MINING.getExperience();
		paint.nextLevelEXP = Skill.MINING.getExperienceToNextLevel();
		paint.currentLevel = Skill.MINING.getCurrentLevel();
		paint.percentage = Skill.MINING.getExperienceAsPercent();

		try{
			miner.loop();
		}catch(Exception e){
			if(catchErrors){
				ErrorHandler.add(e);
			}else{
				throw e;
			}
		}

		//At ~8 hours we need to generate a new line
		if(System.currentTimeMillis() - paint.startTime >=  3600000 * 7.875 * (1 + ReflexAgent.resets)){
			Paint.status = "Regenerating reflex delay";
			ReflexAgent.reinitialize(ReflexAgent.getReactionTime());
			javafx.application.Platform.runLater(() -> {
				paint.updateGraph();
			});
		}
	}

	@Override
	public void onStop() {
		if(miner != null)miner.onStop();
		paint.stop = true;
		
		System.gc();
		if(ErrorHandler.hasErrors()){
			ErrorHandler.throwAll(miner);
		}
	}
}