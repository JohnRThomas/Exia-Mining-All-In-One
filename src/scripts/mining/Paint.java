package scripts.mining;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;

import javax.imageio.ImageIO;

import com.runemate.game.api.client.ClientUI;
import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.util.Time;

import scripts.ExiaMinerAIO;

public class Paint implements PaintListener{
	public static int tempLevel = 0;
	public static int levelsGained = 0;
	public static boolean first = true;
	public static boolean showGraph = true;
	public static MoneyCounter profitCounter = null;
	public static int startEXP = 0;
	public static long startTime = 0;
	public static String status = "";
	public static BufferedImage mouseImage;
	public static GameObject rock = null;
	
	
	@SuppressWarnings("unused")
	private boolean RS3;
	
	public Paint(boolean RS3){
		this.RS3 = RS3;
		try{
			mouseImage = ImageIO.read(new File(Environment.getStorageDirectory() + "/mouse.png"));
		}catch(IOException e){
			try {
				BufferedImage saveAs = ImageIO.read(new URL("http://i.imgur.com/LOPBaa0.png").openStream());
				ImageIO.write(saveAs, "png", new File(Environment.getStorageDirectory() + "/mouse.png"));
				mouseImage = saveAs;
			} catch (IOException ex) {
				System.out.println("Failed to Read Files from web!");
				ex.printStackTrace();
			}
		}

	}
	
	@Override
	public void onPaint(Graphics2D g) {
		if(mouseImage != null){
			Point mspt = Mouse.getPosition();
			g.drawImage(mouseImage, mspt.x, mspt.y-mouseImage.getHeight(), null);
		}
		
		try{
			if(first){
				tempLevel = Skill.MINING.getCurrentLevel();
				first = false;
			}
			long time = System.currentTimeMillis();
			int totalEXP = Skill.MINING.getExperience() - startEXP;
			long expPhr = ((long)totalEXP*3600000)/(time - startTime);
			long profPhr = ((long)profitCounter.getProfit()*3600000)/(time - startTime);
			long orePhr = ((long)profitCounter.getOreCount()*3600000)/(time - startTime);
			int nextLevelEXP = Skill.MINING.getExperienceToNextLevel();
			int currentLevel = Skill.MINING.getCurrentLevel();
			int percentage = Skill.MINING.getExperienceAsPercent();
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
			g.drawString("ExiaMiner AIO v" + ExiaMinerAIO.version, x+6, y+1);
			g.setFont(new Font("Dialog", 0, 12));
			g.drawString("Runtime: " + Time.format(time - startTime), x+6, y+=20);
			g.drawString("Location: " + ExiaMinerAIO.miner.getLocationName() + " (" + ExiaMinerAIO.miner.getOre().name + ")", x+6, y+=17);
			g.drawString("Status: "+ status, x+6, y+=17);
			g.drawString("Ores/Hour: " + formatBigNumber(orePhr), x+6, y+=17);
			g.drawString("Profit: " + formatBigNumber(profitCounter.getProfit()), x+6, y+=17);
			g.drawString("Profit/Hour: " + formatBigNumber(profPhr), x+6, y+=17);
			g.drawString("Experience: " + formatBigNumber(totalEXP), x+6, y+=17);
			g.drawString("Exp/Hour: " + formatBigNumber(expPhr), x+6, y+=17);

			g.setColor(new Color(0, 0, 0, 180));
			g.fill3DRect(x+5, y+=10, 210, 17, true);
			g.setColor(new Color(255,215,0, 175));
			g.fillRect(x+6, y+1, (int) (208 * (double)(percentage/100.0)), 15);
			g.setColor(Color.WHITE);
			if(totalEXP > 0){
				long ttl = (long) ((time - startTime) * ((long)nextLevelEXP)/ totalEXP);
				g.drawString(currentLevel + "(+" + levelsGained + ")|" + (int)(percentage) +"% to " + (currentLevel+1) + "|TTL: " + Time.format(ttl),x+8,y+=13);
			}else{
				g.drawString(currentLevel + "(+" + levelsGained + ")|" + (int)(percentage) +"% to " + (currentLevel+1) + "|TTL: " + Time.format(0L),x+8,y+=13);
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
			if(rock != null){
				try{
					g.setColor(Color.green);
					Point pt = rock.getPosition().minimap().getInteractionPoint();
					g.drawLine(pt.x, pt.y-2, pt.x, pt.y+2);
					g.drawLine(pt.x-2, pt.y, pt.x+2, pt.y);
				}catch(Exception e){}
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
		
	private static String formatBigNumber(long number){
		return NumberFormat.getInstance().format(number);
	}
}
