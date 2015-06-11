package scripts;

import java.awt.Graphics2D;

import javafx.application.Platform;
import scripts.mining.AIOMinerGUI;
import scripts.mining.CustomPlayerSense;
import scripts.mining.MiningStyle;
import scripts.mining.Paint;
import scripts.mining.ReflexAgent;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingScript;

public class BatMinerAIO extends LoopingScript implements PaintListener {
	public static MiningStyle miner;
	public static String version = "";
	private AIOMinerGUI gui;
	private Paint paint = Paint.create(Environment.isRS3());

	@Override
	public void onStart(String... args){
		setLoopDelay(0);
		version = getMetaData().getVersion();
		BatMinerAIO THIS = this;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui = new AIOMinerGUI(version, THIS);
				gui.show();
			}
		});

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

		Paint.startTime = System.currentTimeMillis();
		miner = gui.miner;
		gui = null;
		getEventDispatcher().addListener(this);
		getEventDispatcher().addListener(Paint.profitCounter);
		Paint.startEXP = Skill.MINING.getExperience();
		Paint.exp = miner.getOre().exp;
		miner.onStart(args);
	}
		
	@Override
	public void onLoop() {
		if(!CustomPlayerSense.playerSenseIntited)CustomPlayerSense.intialize();
		miner.loop();
		
		//At ~8 hours we need to generate a new line
		if(System.currentTimeMillis() - Paint.startTime >=  3600000 * 7.875 * (1 + ReflexAgent.resets)){
			Paint.status = "Regenerating reflex delay";
			ReflexAgent.reinitialize(ReflexAgent.getReactionTime());
		}
	}
	@Override
	public void onPaint(Graphics2D g) {
		paint .draw(g);
	}

	@Override
	public void onStop() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if(gui != null)gui.close();
			}
		});
		if(miner != null)miner.onStop();
		System.gc();
	}
}