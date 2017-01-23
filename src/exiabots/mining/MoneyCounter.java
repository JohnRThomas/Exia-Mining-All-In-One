package exiabots.mining;

import java.util.HashMap;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.net.GrandExchange;
import com.runemate.game.api.hybrid.net.GrandExchange.Item;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;

public class MoneyCounter implements InventoryListener{

	private final HashMap<Integer, Long> cache = new HashMap<Integer, Long>();
	private volatile long totalProfit = 0;
	private volatile long oreCount = 0;
	private volatile boolean locked = false;
	private String[] ores;

	public MoneyCounter(String... ores){
		this.ores = ores;
	}

	@Override
	public void onItemAdded(ItemEvent event){
		SpriteItem invItem = Inventory.getItemIn(event.getItem().getIndex());
		ItemDefinition def = null;
		String name = "";
		int id = -1;
		if(invItem != null) def = invItem.getDefinition();
		if(def != null){
			name = def.getName();
			id = def.getId();

		}

		for(String o : ores){
			if(name.equals(o)){
				oreCount += event.getQuantityChange();
				break;
			}
		}

		if(!locked){
			long price = 0;
			if(cache.containsKey(id)){
				if(cache.get(id) != null){
					price = cache.get(id);
				}
			}else{
				Item item = GrandExchange.lookup(id);
				if(item != null){
					if(name.equals(""))name = item.getName();
					price = item.getPrice();
					System.out.println("Looked up " + name + " for " + price + "gp");
				}				
				cache.put(id, price);
			}
			totalProfit += price * event.getQuantityChange();
		}
	}

	public long getProfit() {
		return totalProfit;
	}
	public long getOreCount() {
		return oreCount;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}


}
