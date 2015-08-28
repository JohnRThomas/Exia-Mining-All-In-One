package scripts.mining;

import java.util.HashMap;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.osrs.net.Zybez;
import com.runemate.game.api.rs3.net.GrandExchange;
import com.runemate.game.api.rs3.net.GrandExchange.Item;
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
		try{
			String name = Inventory.getItemIn(event.getItem().getIndex()).getDefinition().getName();
			for(String o : ores){
				if(name.equals(o)){
					oreCount += event.getQuantityChange();
					break;
				}
			}

			if(!locked){
				int id = event.getItem().getId();
				long price = 0;
				if(cache.containsKey(id)){
					if(cache.get(id) != null){
						price = cache.get(id);
					}
				}else{
					if(Environment.isRS3()){
						Item item = GrandExchange.lookup(id);
						if(item != null){
							price = item.getPrice();
							System.out.println("Looked up " + name + " for " + price + "gp");
						}
					}else{
						price = Zybez.getAveragePrice(name);
						if(price == -1){
							price = 0;
						}
						System.out.println("Looked up " + name + " for " + price + "gp");
					}
					cache.put(id, price);
				}
				totalProfit += price * event.getQuantityChange();
			}
		}catch(NullPointerException e){}
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
