package scripts.mining.locations;

import java.awt.event.KeyEvent;

import scripts.mining.ReflexAgent;

import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

public abstract class DepositLocation extends Location {
	
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
			InterfaceComponent button = Interfaces.getLoadedAt(11).first().getComponent(41);
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
		InterfaceComponent boxwindow = Interfaces.getLoaded(Interfaces.getContainerIndexFilter(11)).first();
		return boxwindow != null && boxwindow.isVisible();
	}
	
	@Override
	public void depositAll(){
		InterfaceComponent button = Interfaces.getLoadedAt(11, 9).first();
		if(button != null){
			button.click();

			Timer timer = new Timer(Random.nextInt(750,1000));
			timer.start();
			while(timer.getRemainingTime() > 0 && Inventory.isFull()){
				Execution.delay(10);
			}
		}
	}
}
