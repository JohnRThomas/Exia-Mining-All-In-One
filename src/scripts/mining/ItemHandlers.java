package scripts.mining;

import java.util.function.Predicate;

import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.script.Execution;

import scripts.mining.locations.Location;

public class ItemHandlers {
	private static Predicate<SpriteItem> porterPredicate = new Predicate<SpriteItem>(){
		@Override
		public boolean test(SpriteItem i) {
			ItemDefinition def = i.getDefinition();
			String name = "";
			if(def != null)name = def.getName();

			return name.toLowerCase().contains("sign of the porter");
		}
	};

	private static Predicate<SpriteItem> urnPredicate = new Predicate<SpriteItem>(){
		@Override
		public boolean test(SpriteItem i) {
			ItemDefinition def = i.getDefinition();
			String name = "";
			if(def != null)name = def.getName();
			
			return name.toLowerCase().contains("mining") && name.contains("urn");
		}
	};

	private static Predicate<SpriteItem> fullUrnPredicate = new Predicate<SpriteItem>(){
		@Override
		public boolean test(SpriteItem i) {
			ItemDefinition def = i.getDefinition();
			String name = "";
			if(def != null)name = def.getName();

			return name.toLowerCase().contains("mining") && name.contains("urn") && name.contains("(full)");
		}
	};
	
	private final static String[] urns = new String[]{"Cracked", "Fragile", "Normal", "Strong", "Decorated"};

	public static void manageUrns(int urnAmount) {
		if(Bank.isOpen() && !Inventory.isFull()){
			if(!Inventory.contains(urnPredicate)){
				for(int i = urns.length-1; i >= 0; i--) {
					String urnType = urns[i];
					SpriteItemQueryResults items = Bank.getItems(new Predicate<SpriteItem>(){
						@Override
						public boolean test(SpriteItem i) {
							ItemDefinition def = i.getDefinition();
							String name = "";
							if(def != null)name = def.getName();

							if(urnType.equals("Normal"))
								return urnPredicate.test(i) && !name.contains(urns[0]) && !name.contains(urns[1]);
							else
								return urnPredicate.test(i) && name.contains(urnType);
						}
					});
					
					if(items.size() > 0){
						Bank.withdraw(items.get(0), urnAmount);
						break;
					}
				}
			}
		}else{
			SpriteItemQueryResults urns = Inventory.getItems(fullUrnPredicate);
			if(urns.size() > 0){
				urns.get(0).interact("Teleport urn");
				Timer timer = new Timer(ReflexAgent.getReactionTime() * 5);
				timer.start();
				while(timer.getRemainingTime() > 0 && Inventory.getItems(fullUrnPredicate).size() == urns.size()){
					Execution.delay(10);
				}
			}
		}
	}
	
	private final static String[] porters = new String[]{"I", "II", "III", "IV", " V", "VI", "Active"};

	public static boolean managePorters() {
		SpriteItemQueryResults inv_porters = Inventory.getItems(porterPredicate);

		if(Bank.isOpen()){
			//Disable porters when we run out
			if(Bank.getItems(porterPredicate).size() == 0 && inv_porters.size() == 0)return false;
			
			if(inv_porters.size() == 0 && !Inventory.isFull()){
				for(int i = porters.length-1; i >= 0; i--) {
					String porterType = porters[i];
					SpriteItemQueryResults items = Bank.getItems(new Predicate<SpriteItem>(){
						@Override
						public boolean test(SpriteItem i) {
							ItemDefinition def = i.getDefinition();
							String name = "";
							if(def != null)name = def.getName();

							return porterPredicate.test(i) && name.contains(porterType);
						}
					});
					
					if(items.size() > 0){
						Bank.withdraw(items.get(0), Equipment.getItems(porterPredicate).size() == 0 ? 28 : 28 - Inventory.getUsedSlots() - 1);
						break;
					}
				}
			}else{
				SpriteItemQueryResults porter = Equipment.getItems(porterPredicate);

				if(porter.size() == 0 && inv_porters.size() > 0){
					ReflexAgent.delay();
					inv_porters.get(0).interact("Wear");
					Timer timer = new Timer(ReflexAgent.getReactionTime() * 5);
					timer.start();
					while(timer.getRemainingTime() > 0 && Equipment.getItems(porterPredicate).size() == 0){
						Execution.delay(10);
					}
				}
			}
		}else{
			SpriteItemQueryResults porter = Equipment.getItems(porterPredicate);

			if(porter.size() == 0 && inv_porters.size() > 0){
				ReflexAgent.delay();
				inv_porters.get(0).interact("Wear");
				Timer timer = new Timer(ReflexAgent.getReactionTime() * 5);
				timer.start();
				while(timer.getRemainingTime() > 0 && Equipment.getItems(porterPredicate).size() == 0){
					Execution.delay(10);
				}
			}	
		}
		return true;
	}

	public static boolean shouldBank(boolean usePorters, Location location) {
		return usePorters && location.inBank() && (Inventory.getItems(porterPredicate).size() == 0 || Equipment.getItems(porterPredicate).size() == 0);
	}

	public static void manageJujus() {
		// TODO Auto-generated method stub
		
	}
}
