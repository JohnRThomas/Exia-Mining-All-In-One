package scripts.mining.locations;

import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.DepositBox;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.DepositBoxes;

import scripts.mining.ReflexAgent;

public abstract class DepositLocation extends Location {
	
	@Override
	public Pattern getBankInteract() {
		return Pattern.compile("Deposit");
	}
	
	@Override
	protected LocatableEntityQueryResults<? extends LocatableEntity> getBankers(){
		return DepositBoxes.getLoaded();
	}
	
	@Override
	public void closeBank() {
		if(!PlayerSense.getAsBoolean(PlayerSense.Key.USE_MISC_HOTKEYS)){
			DepositBox.close();
		}else{
			Keyboard.typeKey(KeyEvent.VK_ESCAPE);
		}

		ReflexAgent.delay();
	}
	
	@Override
	public boolean isBankOpen() {
		return DepositBox.isOpen();
	}
	
	@Override
	public void deposit(){
		DepositBox.depositInventory();
	}
}
