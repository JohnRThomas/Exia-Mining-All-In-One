package scripts.mining;

import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.Filter;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.script.Execution;

import scripts.mining.locations.Location;

public class ItemHandlers {
	private static Filter<SpriteItem> porterFilter = new Filter<SpriteItem>(){
		@Override
		public boolean accepts(SpriteItem i) {
			if(i.getDefinition() != null && i.getDefinition().getName() != null){
				String name = i.getDefinition().getName();
				return name.toLowerCase().contains("sign of the porter");
			}else return false;

		}
	};

	private static Filter<SpriteItem> urnFilter = new Filter<SpriteItem>(){
		@Override
		public boolean accepts(SpriteItem i) {
			if(i.getDefinition() != null && i.getDefinition().getName() != null){
				String name = i.getDefinition().getName();
				return name.toLowerCase().contains("mining") && name.contains("urn");
			}else return false;

		}
	};

	private static Filter<SpriteItem> fullUrnFilter = new Filter<SpriteItem>(){
		@Override
		public boolean accepts(SpriteItem i) {
			if(i.getDefinition() != null && i.getDefinition().getName() != null){
				String name = i.getDefinition().getName();
				return name.toLowerCase().contains("mining") && name.contains("urn") && name.contains("(full)");
			}else return false;

		}
	};
	
	private final static String[] urns = new String[]{"Cracked", "Fragile", "Normal", "Strong", "Decorated"};

	public static void manageUrns(int urnAmount) {
		if(Bank.isOpen() && !Inventory.isFull()){
			if(!Inventory.contains(urnFilter)){
				for(int i = urns.length-1; i >= 0; i--) {
					String urnType = urns[i];
					SpriteItemQueryResults items = Bank.getItems(new Filter<SpriteItem>(){
						@Override
						public boolean accepts(SpriteItem i) {
							if(urnType.equals("Normal"))
								return urnFilter.accepts(i) && !i.getDefinition().getName().contains(urns[0]) && !i.getDefinition().getName().contains(urns[1]);
							else
								return urnFilter.accepts(i) && i.getDefinition().getName().contains(urnType);
						}
					});
					
					if(items.size() > 0){
						Bank.withdraw(items.get(0), urnAmount);
						break;
					}
				}
			}
		}else{
			SpriteItemQueryResults urns = Inventory.getItems(fullUrnFilter);
			if(urns.size() > 0){
				urns.get(0).interact("Teleport urn");
				Timer timer = new Timer(ReflexAgent.getReactionTime() * 5);
				timer.start();
				while(timer.getRemainingTime() > 0 && Inventory.getItems(fullUrnFilter).size() == urns.size()){
					Execution.delay(10);
				}
			}
		}
	}
	
	private final static String[] porters = new String[]{"I", "II", "III", "IV", " V", "VI", "Active"};

	public static boolean managePorters() {
		SpriteItemQueryResults inv_porters = Inventory.getItems(porterFilter);

		if(Bank.isOpen()){
			//Disable porters when we run out
			if(Bank.getItems(porterFilter).size() == 0 && inv_porters.size() == 0)return false;
			
			if(inv_porters.size() == 0 && !Inventory.isFull()){
				for(int i = porters.length-1; i >= 0; i--) {
					String porterType = porters[i];
					SpriteItemQueryResults items = Bank.getItems(new Filter<SpriteItem>(){
						@Override
						public boolean accepts(SpriteItem i) {
							return porterFilter.accepts(i) && i.getDefinition().getName().contains(porterType);
						}
					});
					
					if(items.size() > 0){
						Bank.withdraw(items.get(0), Equipment.getItems(porterFilter).size() == 0 ? 28 : 28 - Inventory.getUsedSlots() - 1);
						break;
					}
				}
			}else{
				SpriteItemQueryResults porter = Equipment.getItems(porterFilter);

				if(porter.size() == 0 && inv_porters.size() > 0){
					ReflexAgent.delay();
					inv_porters.get(0).interact("Wear");
					Timer timer = new Timer(ReflexAgent.getReactionTime() * 5);
					timer.start();
					while(timer.getRemainingTime() > 0 && Equipment.getItems(porterFilter).size() == 0){
						Execution.delay(10);
					}
				}
			}
		}else{
			SpriteItemQueryResults porter = Equipment.getItems(porterFilter);

			if(porter.size() == 0 && inv_porters.size() > 0){
				ReflexAgent.delay();
				inv_porters.get(0).interact("Wear");
				Timer timer = new Timer(ReflexAgent.getReactionTime() * 5);
				timer.start();
				while(timer.getRemainingTime() > 0 && Equipment.getItems(porterFilter).size() == 0){
					Execution.delay(10);
				}
			}	
		}
		return true;
	}

	public static boolean shouldBank(boolean usePorters, Location location) {
		return usePorters && location.inBank() && (Inventory.getItems(porterFilter).size() == 0 || Equipment.getItems(porterFilter).size() == 0);
	}
}
