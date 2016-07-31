package scripts;

import java.math.BigDecimal;

import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingScript;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import scripts.mining.AIOMinerGUI;
import scripts.mining.CustomPlayerSense;
import scripts.mining.ErrorHandler;
import scripts.mining.MiningStyle;
import scripts.mining.MoneyCounter;
import scripts.mining.Paint;
import scripts.mining.ReflexAgent;

public class ExiaMinerAIO extends LoopingScript implements EmbeddableUI{
	public static MiningStyle miner;
	public static String version = "";
	public static String name = "";
	public static ExiaMinerAIO instance;
	public static boolean isPaid = false;
	private AIOMinerGUI gui;
	private Paint paint = new Paint(Environment.isRS3());
	private boolean catchErrors = true;
	public static boolean isRS3;
	
	@Override
	public ObjectProperty<Node> botInterfaceProperty() {
		if (gui == null) gui = new AIOMinerGUI();
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
		int reflexSeed = gui.getReflexSeed();

		if(reflexSeed == -1){
			Paint.showGraph = false;
		}

		ReflexAgent.initialize(reflexSeed);
		CustomPlayerSense.intialize();

		Paint.startTime = System.currentTimeMillis();
		miner = gui.miner;
		catchErrors = !Environment.isSDK() && gui.catchErrors;
		gui = null;
		Paint.startEXP = Skill.MINING.getExperience();
		Paint.profitCounter = new MoneyCounter(miner.getOre().oreNames);
		getEventDispatcher().addListener(paint);
		miner.onStart(args);
	}

	@Override
	public void onLoop() {
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
		if(System.currentTimeMillis() - Paint.startTime >=  3600000 * 7.875 * (1 + ReflexAgent.resets)){
			Paint.status = "Regenerating reflex delay";
			ReflexAgent.reinitialize(ReflexAgent.getReactionTime());
		}
	}

	@Override
	public void onStop() {
		if(miner != null)miner.onStop();
		System.gc();
		if(ErrorHandler.hasErrors()){
			ErrorHandler.throwAll(miner);
		}
	}
}