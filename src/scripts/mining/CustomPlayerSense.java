package scripts.mining;

import java.util.Random;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.region.Players;

public class CustomPlayerSense{
	public enum Key{
		ACTION_BAR_SPAM("exia_miner_abs"),
		BANKER_PREFERENCE("exia_miner_bp"),
		DOUBLE_CLICK("exia_miner_dc"),
		VIEW_PORT_WALKING("exia_miner_vpw");

		public String playerSenseKey;

		Key(String playerSenseKey){
			this.playerSenseKey = playerSenseKey;
		}
	}

	public static boolean playerSenseIntited = false;

	public static void intialize() {
		int seed = 0;
		if(RuneScape.isLoggedIn()){
			seed = (sumBytes(Environment.getForumName()) | sumBytes(Players.getLocal().getName())) * sumBytes(Players.getLocal().getName());
			Random random = new Random(seed);
			PlayerSense.clear(Key.ACTION_BAR_SPAM.playerSenseKey);
			PlayerSense.put(Key.ACTION_BAR_SPAM.playerSenseKey, random.nextInt(100));
			PlayerSense.clear(Key.BANKER_PREFERENCE.playerSenseKey);
			PlayerSense.put(Key.BANKER_PREFERENCE.playerSenseKey, random.nextInt(100));
			PlayerSense.clear(Key.DOUBLE_CLICK.playerSenseKey);
			PlayerSense.put(Key.DOUBLE_CLICK.playerSenseKey, random.nextInt(25) + 5);
			PlayerSense.clear(Key.VIEW_PORT_WALKING.playerSenseKey);
			PlayerSense.put(Key.VIEW_PORT_WALKING.playerSenseKey, random.nextInt(15));
			
			playerSenseIntited = true;
		}else{
			seed = sumBytes(Environment.getForumName());
			Random random = new Random(seed);
			PlayerSense.clear(Key.ACTION_BAR_SPAM.playerSenseKey);
			PlayerSense.put(Key.ACTION_BAR_SPAM.playerSenseKey, random.nextInt(100));
			PlayerSense.clear(Key.BANKER_PREFERENCE.playerSenseKey);
			PlayerSense.put(Key.BANKER_PREFERENCE.playerSenseKey, random.nextInt(100));
			PlayerSense.clear(Key.DOUBLE_CLICK.playerSenseKey);
			PlayerSense.put(Key.DOUBLE_CLICK.playerSenseKey, random.nextInt(25) + 5);
			PlayerSense.clear(Key.VIEW_PORT_WALKING.playerSenseKey);
			PlayerSense.put(Key.VIEW_PORT_WALKING.playerSenseKey, random.nextInt(15));
		}
	}

	private static int sumBytes(String string) {
		int value = 0;
		for(int i = 0; i < string.length(); i++){
			value += string.charAt(i)*i;
		}
		return value;
	}
}
