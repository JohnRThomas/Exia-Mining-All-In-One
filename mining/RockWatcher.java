package scripts.mining;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.util.Filter;

public class RockWatcher extends Thread{

	private Pair<Coordinate, Long, GameObject>[] locations;
	private Validater validater;
	
	@SuppressWarnings("unchecked")
	public RockWatcher(Validater validater, Coordinate ... locations){
		this.validater = validater;
		this.locations = (Pair<Coordinate, Long, GameObject>[])new Pair[locations.length];
		for (int i = 0; i < locations.length; i++) {
			this.locations[i] = new Pair<Coordinate, Long, GameObject>(locations[i], 0L);
		}
	}

	@Override
	public void run() {
		try{
			while(true){
				GameObjects.getLoaded(new Filter<GameObject>(){
					@Override
					public boolean accepts(GameObject o) {
						Coordinate pos = o.getPosition();
						for (Pair<Coordinate, Long, GameObject> rock : locations) {
							//Match the rocks based on location
							if(rock.pos.equals(pos)){
								//If the rock has ore, then update it's last seen time
								String name = o.getDefinition().getName();
								if(validater.validate(name)){
									rock.time = System.currentTimeMillis();
									rock.object = null;
									return true;
								}else{
									rock.object = o;
								}
							}
						}
						return false;
					}
				});
				Thread.sleep(400);
			}
		}catch(InterruptedException e){}
	}

	public Pair<Coordinate, Long, GameObject> nextRock(){
		long bestTime = System.currentTimeMillis();
		Pair<Coordinate, Long, GameObject> bestRock = null;
		for (Pair<Coordinate, Long, GameObject> rock : locations) {
			if(rock.time < bestTime && rock.object != null){
				bestTime = rock.time;
				bestRock = rock;
			}
		}			
		return bestRock;
	}
	
	class Pair<X, Y, Z> { 
		public X pos; 
		public Y time; 
		public Z object; 

		public Pair(X pos, Y time) { 
			this.pos = pos; 
			this.time = time; 
			object = null;
		} 
	}
	
	public interface Validater{
		public boolean validate(String s);
	}
}