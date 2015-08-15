package scripts.mining;

import java.util.HashSet;
import java.util.Set;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.util.Filter;

public class RockWatcher extends Thread{

	private Set<Pair<Coordinate, Long, GameObject>> locations;
	public Validater validater;

	public RockWatcher(Validater validater, Coordinate... locations){
		this.validater = validater;
		this.locations = new HashSet<Pair<Coordinate, Long, GameObject>>();
		for (int i = 0; i < locations.length; i++) {
			this.locations.add(new Pair<Coordinate, Long, GameObject>(locations[i], 0L));
		}
	}

	public void addLocation(Coordinate location){
		synchronized(locations){
			this.locations.add(new Pair<Coordinate, Long, GameObject>(location, 0L));
		}
	}

	public Coordinate[] getLocations(){
		Coordinate[] out = new Coordinate[locations.size()];
		int i = 0;
		for(Pair<Coordinate, Long, GameObject> p : locations){
			out[i++] = p.pos;
		}
		return out;
	}

	@Override
	public void run() {
		try{
			while(true){
				GameObjects.getLoaded(new Filter<GameObject>(){
					@Override
					public boolean accepts(GameObject o) {
						Coordinate pos = o.getPosition();
						synchronized(locations){
							for (Pair<Coordinate, Long, GameObject> rock : locations) {
								//Match the rocks based on location
								if(rock.pos.equals(pos)){
									//If the rock has ore, then update it's last seen time
									if(validater.validate(o)){
										rock.time = System.currentTimeMillis();
										rock.object = null;
										return true;
									}else{
										rock.object = o;
									}
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
		public boolean validate(GameObject o);
	}
}