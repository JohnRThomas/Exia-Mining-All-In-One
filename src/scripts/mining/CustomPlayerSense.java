package scripts.mining;

import java.util.Random;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.region.Players;

public class CustomPlayerSense{
	public enum Key{

		DISTANCE_ANGLE_TIE_BREAKER(PlayerSense.Key.DISTANCE_ANGLE_TIE_BREAKER.getKey()),
		DISTANCE_VISIBILITY_TIE_BREAKER(PlayerSense.Key.DISTANCE_VISIBILITY_TIE_BREAKER.getKey()),
		ENABLE_RUN_AT(PlayerSense.Key.ENABLE_RUN_AT.getKey()),
		MOVE_CAMERA_WITH_MOUSE(PlayerSense.Key.MOVE_CAMERA_WITH_MOUSE.getKey()),
		USE_ACTIONBAR_HOTKEYS(PlayerSense.Key.USE_ACTIONBAR_HOTKEYS.getKey()),
		USE_MISC_HOTKEYS(PlayerSense.Key.USE_MISC_HOTKEYS.getKey()),
		USE_NUMPAD(PlayerSense.Key.USE_NUMPAD.getKey()),
		USE_WASD_KEYS(PlayerSense.Key.USE_WASD_KEYS.getKey()),
		WALL_AVOIDANCE_MODIFIER(PlayerSense.Key.WALL_AVOIDANCE_MODIFIER.getKey()),
		BANKER_PREFERENCE("bat_miner_bp"),
		DOUBLE_CLICK("bat_miner_dc"),
		VIEW_PORT_WALKING("bat_miner_vpw");

		public String playerSenseKey;

		Key(String playerSenseKey){
			this.playerSenseKey = playerSenseKey;
		}
	}

	public static boolean playerSenseIntited = false;

	public static void intialize() {
		if(RuneScape.isLoggedIn()){
			int seed = sumBytes(Environment.getForumName()) + sumBytes(Players.getLocal().getName());
			Random random = new Random(seed);
			PlayerSense.put(Key.BANKER_PREFERENCE.playerSenseKey, random.nextInt(100));
			System.out.println(Key.BANKER_PREFERENCE.playerSenseKey + ": " + PlayerSense.getAsInteger(Key.BANKER_PREFERENCE.playerSenseKey));

			PlayerSense.put(Key.DOUBLE_CLICK.playerSenseKey, random.nextInt(15) + 5);
			System.out.println(Key.DOUBLE_CLICK.playerSenseKey + ": " + PlayerSense.getAsInteger(Key.DOUBLE_CLICK.playerSenseKey));
			
			PlayerSense.put(Key.VIEW_PORT_WALKING.playerSenseKey, random.nextInt(20));
			System.out.println(Key.VIEW_PORT_WALKING.playerSenseKey + ": " + PlayerSense.getAsInteger(Key.VIEW_PORT_WALKING.playerSenseKey));
			
			playerSenseIntited = true;
		}else{
			int seed = sumBytes(Environment.getForumName());
			Random random = new Random(seed);
			PlayerSense.put(Key.BANKER_PREFERENCE.playerSenseKey, random.nextInt(100));
			System.out.println(Key.BANKER_PREFERENCE.playerSenseKey + ": " + PlayerSense.getAsInteger(Key.BANKER_PREFERENCE.playerSenseKey));

			PlayerSense.put(Key.DOUBLE_CLICK.playerSenseKey, random.nextInt(15) + 5);
			System.out.println(Key.DOUBLE_CLICK.playerSenseKey + ": " + PlayerSense.getAsInteger(Key.DOUBLE_CLICK.playerSenseKey));
			
			PlayerSense.put(Key.VIEW_PORT_WALKING.playerSenseKey, random.nextInt(100));
			System.out.println(Key.VIEW_PORT_WALKING.playerSenseKey + ": " + PlayerSense.getAsInteger(Key.VIEW_PORT_WALKING.playerSenseKey));
		}
	}

	private static int sumBytes(String string) {
		int value = 0;
		for(int i = 0; i < string.length(); i++){
			value += string.charAt(i);
		}
		return value;
	}
}
