package scripts.mining;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.NumberFormat;

import scripts.BatMinerAIO;

import com.runemate.game.api.client.ClientUI;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;
import com.runemate.game.api.hybrid.util.Time;

public abstract class Paint {
	public static int tempLevel = 0;
	public static int levelsGained = 0;
	public static boolean first = true;
	public static boolean showGraph = true;
	public static MoneyCounter profitCounter = new MoneyCounter();
	public static int startEXP = 0;
	public static double exp = 5.0;
	public static long startTime = 0;
	public static String status = "";

	public abstract void draw(Graphics2D g);

	public static Paint create(boolean RS3){
		if(RS3){
			return new Paint(){
				@Override
				public void draw(Graphics2D g) {
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
						int percentage = 100-Skill.MINING.getExperienceAsPercent();
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
						g.drawString("BatMiner AIO v" + BatMinerAIO.version, x+6, y+1);
						g.setFont(new Font("Dialog", 0, 12));
						g.drawString("Runtime: " + Time.format(time - startTime), x+6, y+=20);
						g.drawString("Location: " + BatMinerAIO.miner.getLocationName() + " (" + BatMinerAIO.miner.getOre().name + ")", x+6, y+=17);
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

							for(int i = 0; i <= 3; i++){
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
				
			};
		}else{
			return new Paint(){
				@Override
				public void draw(Graphics2D g) {
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
						int percentage = 100-Skill.MINING.getExperienceAsPercent();
						if(tempLevel < currentLevel){
							ClientUI.sendTrayNotification("Congradulation! You have just advanced a Mining level! You now reached level " + currentLevel + ".");
							levelsGained++;
						}
						tempLevel = currentLevel;
						//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						int y = 364;
						int x = 6; 
						int width = 507;
						int height = 130;
						g.setColor(new Color(205,205,160));

						g.fillRect(x, y+95, 150, 15);
						
						g.setColor(new Color(0, 0, 0, 180));
						g.fillRect(x, y-20, width, height);
						g.setColor(new Color(200,200,200,128));
						g.drawRect(x, y-20, width, height);
						g.setColor(new Color(247,246,242));
						g.setFont(new Font("Lucida Bright", 0, 18));
						g.drawString("BatMiner AIO v" + BatMinerAIO.version, x+6, y+1);
						g.setFont(new Font("Dialog", 0, 12));
						g.drawString("Runtime: " + Time.format(time - startTime), x+6, y+=20);
						g.drawString("Location: " + BatMinerAIO.miner.getLocationName() + " (" + BatMinerAIO.miner.getOre().name + ")", x+6, y+=17);
						g.drawString("Status: "+ status, x+6, y+=17);
						g.drawString("Ores/Hour: " + formatBigNumber(orePhr), x+6, y+=17);
						g.drawString("Profit: " + formatBigNumber(profitCounter.getProfit()), x+6, y+=17);
						g.drawString("Profit/Hour: " + formatBigNumber(profPhr), x+6, y+=17);
						g.drawString("Experience: " + formatBigNumber(totalEXP), (x+=160)+6, y-=51);
						g.drawString("Exp/Hour: " + formatBigNumber(expPhr), x+6, y+=17);
						if(totalEXP > 0){
							long ttl = (long) ((time - startTime) * ((long)nextLevelEXP)/ totalEXP);
							g.drawString("TTL: " + Time.format(ttl), x+6, y+=17);
						}else{
							g.drawString("TTL: " + Time.format(0L), x+6, y+=17);
						}

						g.setColor(new Color(0, 0, 0, 180));
						g.fill3DRect(x+5, y+=4, 115, 17, true);
						g.setColor(new Color(255,215,0, 175));
						g.fillRect(x+6, y+1, (int) (117 * (double)(percentage/100.0)), 15);
						g.setColor(Color.WHITE);
						g.drawString(currentLevel + "(+" + levelsGained + ") | " + (int)(percentage) +"% to " + (currentLevel+1),x+8,y+=13);
						
						if(showGraph){
							y -= 10;
							x+=140;
							g.setColor(new Color(247,246,242));
							g.drawString("Reaction Time", x+50, y-100);
							g.drawLine(x, y, x, y-90);
							g.drawLine(x, y, x+184, y);

							double hours = ((time-startTime) / 3600000.0) - (7.875 * ReflexAgent.resets);
							g.setColor(new Color(200,200,200,128));
							g.drawLine(x + (int)(hours*23), y, x + (int)(hours*23), y-90);

							g.setColor(new Color(247,246,242));
							for(int i = 0; i <= 8; i++){
								g.drawLine(x + (int)(i*23), y+2, x + (int)(i*23), y-2);
							}

							for(int i = 0; i <= 3; i++){
								g.drawLine(x-2, y-(i*30), x+2, y-(i*30));
							}

							g.setStroke(new BasicStroke(2));
							g.setColor(new Color(255,215,0));
							double lastX = 0;
							int lastY = ReflexAgent.applyPolynomial(0);
							for(double i = .125; i <= 7.875; i+=.125){
								double newX = i;
								int newY = ReflexAgent.applyPolynomial(newX);
								g.drawLine((int)(lastX*23) + x, y - (lastY / 3) + 50, (int)(newX * 23) + x, y - (newY / 3) + 50);
								lastX = newX;
								lastY = newY;
							}
						}
						if(StandardMiner.next != null){
							InteractablePoint pt = StandardMiner.next.getInteractionPoint();
							g.drawRect(pt.x-2, pt.y-2, 4, 4);
						}
					}catch(Exception e){}					
				}
			};
		}
	}
	
	private static String formatBigNumber(long number){
		return NumberFormat.getInstance().format(number);
	}
}
