package scripts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.NumberFormat;
import java.util.HashMap;

import javafx.application.Platform;
import scripts.mining.AIOMinerGUI;
import scripts.mining.MiningStyle;
import scripts.mining.MoneyCounter;
import scripts.mining.ReflexAgent;

import com.runemate.game.api.client.ClientUI;
import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.util.Time;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingScript;

public class BatMinerAIO extends LoopingScript implements PaintListener {
	int startEXP = 0;
	double exp = 5.0;
	long startTime = 0;
	MiningStyle miner;
	String version = "";
	AIOMinerGUI gui;
	MoneyCounter profitCounter = new MoneyCounter();
	public static String status = "";

	@SuppressWarnings("serial")
	private static final HashMap<String, Double> exps = new HashMap<String, Double>(){
		{
			put("Clay", 5.0);
			put("Tin", 17.5);
			put("Copper", 17.5);
			put("Iron", 35.0);
			put("Silver", 40.0);
			put("Coal", 50.0);
			put("Gold", 65.0);
			put("Mithril", 80.0);
			put("Adamantite", 95.0);
			put("Runite", 125.0);
			put("Gems", 65.0);
		}
	};

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
			showGraph = false;
		}
		
		ReflexAgent.initialize(reflexSeed);

		startTime = System.currentTimeMillis();
		miner = gui.miner;
		gui = null;
		getEventDispatcher().addListener(this);
		getEventDispatcher().addListener(profitCounter);
		startEXP = Skill.MINING.getExperience();
		exp = exps.get(miner.location.getOre());
		miner.onStart(args);
	}

	@Override
	public void onLoop() {
		miner.loop();
		
		//At ~8 hours we need to generate a new line
		if(System.currentTimeMillis() - startTime >=  3600000 * 7.875 * (1 + ReflexAgent.resets)){
			BatMinerAIO.status = "Regenerating reflex delay";
			ReflexAgent.reinitialize(ReflexAgent.getReactionTime());
		}
	}

	private int tempLevel = 0;
	private int levelsGained = 0;
	private boolean first = true;
	private boolean showGraph = true;
	@Override
	public void onPaint(Graphics2D g) {
		try{
			if(first){
				tempLevel = Skill.MINING.getCurrentLevel();
				first = false;
			}
			long time = System.currentTimeMillis();
			int totalEXP = Skill.MINING.getExperience() - startEXP;
			long expPhr = ((long)totalEXP*3600000)/(time - startTime);
			long profPhr = ((long)profitCounter.getProfit()*3600000)/(time - startTime);
			long orePhr = (long)((double)expPhr / exp);
			int nextLevelEXP = Skill.MINING.getExperienceToNextLevel();
			int currentLevel = Skill.MINING.getCurrentLevel();
			int percentage = 100-Skill.MINING.getPercentTowardsNextLevel();
			if(tempLevel < currentLevel){
				ClientUI.sendTrayNotification("Congradulation! You have just advanced a Mining level! You now reached level " + currentLevel + ".");
				levelsGained++;
			}
			tempLevel = currentLevel;
			//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(new Color(0, 0, 0, 180));
			int y = 27;
			int x = 7; 
			int width = 212;
			int height = 193;
			if(showGraph){
				width = 424;
			}
			g.fillRect(x, y-20, width, height);
			g.setColor(new Color(200,200,200,128));
			g.drawRect(x, y-20, width, height);
			g.setColor(new Color(247,246,242));
			g.setFont(new Font("Lucida Bright", 0, 18));
			g.drawString("BatMiner AIO v" + version, x+6, y+1);
			g.setFont(new Font("Dialog", 0, 12));
			g.drawString("Runtime: " + Time.format(time - startTime), x+6, y+=20);
			if(miner.location != null)g.drawString("Location: " + miner.location.getName() + " (" + miner.location.getOre() + ")", x+6, y+=17);
			else g.drawString("Location: Unknown" , x+6, y+=17);
			g.drawString("Status: "+ status, x+6, y+=17);
			g.drawString("Ores/Hour: " + formatBigNumber(orePhr), x+6, y+=17);
			g.drawString("Profit: " + formatBigNumber(profitCounter.getProfit()), x+6, y+=17);
			g.drawString("Profit/Hour: " + formatBigNumber(profPhr), x+6, y+=17);
			g.drawString("Experience: " + formatBigNumber(totalEXP), x+6, y+=17);
			g.drawString("Exp/Hour: " + formatBigNumber(expPhr), x+6, y+=17);

			g.setColor(new Color(0, 0, 0, 180));
			g.fill3DRect(x+5, y+=10, 202, 17, true);
			g.setColor(new Color(255,215,0, 175));
			g.fillRect(x+6, y+1, (int) (200 * (double)(percentage/100.0)), 15);
			g.setColor(Color.WHITE);
			if(totalEXP > 0){
				long ttl = (long) ((time - startTime) * ((long)nextLevelEXP)/ totalEXP);
				g.drawString(currentLevel + "(+" + levelsGained + ") | " + (int)(percentage) +"% to " + (currentLevel+1) + " | TTL: " + Time.format(ttl),x+8,y+=13);
			}else{
				g.drawString(currentLevel + "(+" + levelsGained + ") | " + (int)(percentage) +"% to " + (currentLevel+1) + " | TTL: " + Time.format(0L),x+8,y+=13);
			}
			if(showGraph){
				y -= 10;
				g.setColor(new Color(247,246,242));
				g.drawString("Reaction Time", x+275, y-157);
				g.drawLine(x+225, y, x+225, y-149);
				g.drawLine(x+225, y, x+409, y);
				
				double hours = ((time-startTime) / 3600000.0) - (7.875 * ReflexAgent.resets);
				g.setColor(new Color(200,200,200,128));
				g.drawLine(x+225 + (int)(hours*23), y, x+225 + (int)(hours*23), y-149);
				
				g.setColor(new Color(247,246,242));
				for(int i = 0; i <= 8; i++){
					g.drawLine(x+225 + (int)(i*23), y+2, x+225 + (int)(i*23), y-2);
				}
				
				for(int i = 0; i <= 4; i++){
					g.drawLine(x+223, y-(i*50), x+227, y-(i*50));
				}
				
				g.setStroke(new BasicStroke(2));
				g.setColor(new Color(255,215,0));
				double lastX = 0;
				int lastY = ReflexAgent.applyPolynomial(0);
				for(double i = .125; i <= 7.875; i+=.125){
					double newX = i;
					int newY = ReflexAgent.applyPolynomial(newX);
					g.drawLine((int)(lastX*23) + x+225, y - (lastY / 2) + 50, (int)(newX * 23) + x+225, y - (newY / 2) + 50);
					lastX = newX;
					lastY = newY;
				}
			}
		}catch(Exception e){}
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

	public String formatBigNumber(long number){
		return NumberFormat.getInstance().format(number);
	}
}