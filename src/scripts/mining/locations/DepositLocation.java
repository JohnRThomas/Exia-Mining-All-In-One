package scripts.mining.locations;

import java.awt.event.KeyEvent;

import scripts.mining.ReflexAgent;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.script.Execution;

public abstract class DepositLocation extends Location {

	final int PARENT_NTERFACE = Environment.isRS3() ? 11 : 0;
	final int DEPOSIT_BUTTON = Environment.isRS3() ? 9 : 0;
	final int CLOSE_BUTTON = Environment.isRS3() ? 41 : 0;
	
	@Override
	public String getBankInteract() {
		return "Deposit";
	}
	
	@Override
	protected LocatableEntityQueryResults<? extends LocatableEntity> getBanker(){
		return Banks.getLoadedDepositBoxes();
	}
	
	@Override
	public void closeBank() {
		if(!PlayerSense.getAsBoolean(PlayerSense.Key.USE_MISC_HOTKEYS)){
			InterfaceComponent button = Interfaces.getLoadedAt(PARENT_NTERFACE).first().getComponent(CLOSE_BUTTON);
			if(button != null){
				button.click();
			}
		}else{
			Keyboard.typeKey(KeyEvent.VK_ESCAPE);
		}

		ReflexAgent.delay();
	}
	
	@Override
	public boolean isBankOpen() {
		InterfaceComponent boxwindow = Interfaces.getLoaded(Interfaces.getContainerIndexFilter(PARENT_NTERFACE)).first();
		return boxwindow != null && boxwindow.isVisible();
	}
	
	@Override
	public void deposit(){
		InterfaceComponent button = Interfaces.getLoadedAt(PARENT_NTERFACE, DEPOSIT_BUTTON).first();
		if(button != null){
			button.click();

			Timer timer = new Timer(ReflexAgent.getReactionTime() * 3);
			timer.start();
			while(timer.getRemainingTime() > 0 && Inventory.isFull()){
				Execution.delay(10);
			}
		}
	}
}
