package web;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.rs3.local.hud.interfaces.Lodestone;;

public class WebPath extends Path{
	
	//Variables
	PathError lastError = PathError.NONE;
	LinkedList<Edge> steps = new LinkedList<Edge>();
	
	public WebPath(){}
	
	public WebPath(WebPath path, Edge addition) {
		for(Edge e : path.steps){
			steps.add(e);
		}
		steps.add(addition);
	}

	//Functions
	public PathError error(){
		return lastError;
	}
	
	//Functions from Path
	@Override
	public Locatable getNext() {
		Point3D dest = steps.getFirst().destination.point;
		return new Coordinate(dest.x, dest.y, dest.z);
	}

	@Override
	public List<? extends Locatable> getVertices() {
		//Turn the edges into locatables
		LinkedList<Locatable> out = new LinkedList<Locatable>();
		for(Edge e : steps){
			Point3D dest = e.destination.point;
			out.add(new Coordinate(dest.x, dest.y, dest.z));
		}
		return out;
	}

	@Override
	public boolean step(TraversalOption... options) {
		Edge step = steps.getFirst();
		
		boolean tookStep = step.step();
		
		//if the step worked, pop it off the remaining steps
		if(tookStep)
			steps.pop();
		
		return tookStep;
	}
	
	//Path builder (static)
	private static Graph graph = null;
	
	public static void buildWeb(){
		graph = new Graph();
	}
	
	public static WebPath build(Locatable start, Locatable dest){
		//if there is no current graph, make a new one
		if(graph == null)graph = new Graph();
		
		//Translate the locatables into objects that will hash reliably
		Point3D start3D = new Point3D(start.getPosition().getX(), start.getPosition().getY(), start.getPosition().getPlane());
		Point3D dest3D = new Point3D(dest.getPosition().getX(), dest.getPosition().getY(), dest.getPosition().getPlane());
		return graph.getPath(start3D, dest3D);
	}
	
	public static WebPath buildTo(Locatable dest){
		return build(Players.getLocal(), dest);
	}
	
	//Under the hood stuff
	private static class Graph{
		
		//This is where every vertex is stored
		//Each vertex stores its own out-going edges
		private HashMap<Point3D, Vertex> vertices = new HashMap<Point3D, Vertex>();
		private boolean teleCheck = false;
		
		public Graph(){
			//TODO populate the graph
			this.addEdge(new Point3D(0, 0, 0), new Point3D(1, 0, 0), false);
			this.addEdge(new Point3D(1, 0, 0), new Point3D(2, 0, 0), false);
			this.addEdge(new Point3D(2, 0, 0), new Point3D(2, 1, 0), false);
			
			//sample agility short cut edge
			this.addCustomEdge(new Point3D(0, 0, 0), new Point3D(2, 1, 0), new EdgeActions(){
				@Override
				public int getWeight() {
					//If the agility level is greater than 32, then the cost is just 1, otherwise
					//the cost is infinity (represented by -1)
					return Skill.AGILITY.getCurrentLevel() >= 32 ? 1 : -1;
				}

				@Override
				public boolean step() {
					//TODO this is just a sample, fill it in
					//This should be clicking the obstacle
					return true;
				}
				
			});
			//Add the shortcut going the other way too (assuming that the shortcut actually works both ways)
			this.addCustomEdge(new Point3D(2, 1, 0), new Point3D(0, 0, 0), new EdgeActions(){
				@Override
				public int getWeight() {
					//If the agility level is greater than 32, then the cost is just 1, otherwise
					//the cost is infinity (represented by -1)
					return Skill.AGILITY.getCurrentLevel() >= 32 ? 1 : -1;
				}

				@Override
				public boolean step() {
					//TODO this is just a sample, fill it in
					//This should be clicking the obstacle
					return true;
				}
				
			});
		}
		
		public WebPath getPath(Point3D start, Point3D dest){
			//Grab vertices for the start and end point
			Vertex startVert = null, destVert = null;
			if(vertices.containsKey(start))startVert = vertices.get(start);
			if(vertices.containsKey(dest))destVert = vertices.get(dest);
			
			//if the exact start or end points are not in the graph, find the closest point by 
			//spialing outward from each origin
			int i = start.x + 1, j = start.y;
			while(startVert == null){
				//Just to get rid of the damn warning, this needs top be changed
				if(i == j){
					
				}
				//TODO spiral outward from the point to find the closest point
			}
			i = dest.x + 1;
			j = dest.y;
			while(destVert == null){
				//TODO spiral outward from the point to find the closest point
			}
			
			//Mark teleportation as not checked
			teleCheck = false;
			
			return aStarSearch(startVert, destVert);
		}
		
		
		private WebPath aStarSearch(Vertex start, Vertex dest){
			//Check if we have checked teleports yet
			if(!teleCheck){
				for(final Teleport tele : Teleport.values()){
					if(tele.teleportActions.canTeleport()){
						//Add the teleportation edge to the start node
						addCustomEdge(start.point, tele.dest, new EdgeActions(){
							public int getWeight(){
								return tele.cost;
							}
							
							public boolean step(){
								return tele.teleportActions.teleport();
							}
						});
					}
				}
				//Now that the teleportation check has been made, mark it as so.
				teleCheck = true;
			}

			//Create our frontier and explored vertex lists
			HashMap<Vertex, Integer> openList = new HashMap<Vertex, Integer>();
			Set<Vertex> closedList = new HashSet<Vertex>();
			
			//create the list paths to each vertex
			HashMap<Vertex, WebPath> paths = new HashMap<Vertex, WebPath>();
			
			//initialize our lists
			openList.put(start, 0);
			paths.put(start, new WebPath());
			
			while(!openList.isEmpty()){
				//Consume the current best vertex
				Vertex current = mapMin(openList);
				int cost = openList.remove(current);
				WebPath currentPath = paths.get(current);
				
				if(dest.equals(current)){
					return currentPath;
				}
				
				//if the current node is not in the closed list
				if(!closedList.contains(current)){
					//Loop through all of the outgoing edges
					for(Edge e : current.adjacentcies){
						Vertex successor = e.destination;
						int stepCost = e.getWeight();
						
						//Make sure the other end of this edge is not in the closed list
						//also make sure the cost is not negative, a negative indicates
						//an infinite weight, or unreachable via this edge.
						//This will be useful for things like shortcuts that depend on the
						//current player who is traversing the graph.
						if(!closedList.contains(successor) && stepCost >= 0){
							int newCost = cost + stepCost; 
							
							//if the successor is not in the open list, or
							//the new value is lower than it's current value
							if(!openList.containsKey(successor) || newCost < openList.get(successor)){
								
								//Add this new vertex to the open list to be expanded later
								openList.put(successor, newCost);
								
								//update the path to the successor
								paths.put(successor, new WebPath(currentPath, e));
							}
						}
					}
				}
				
				closedList.add(current);
			}
			
			//We will only get here if the path is not possible
			return null;
		}
		
		private Vertex mapMin(HashMap<Vertex, Integer> map){
			Entry<Vertex, Integer> min = null;
			for (Entry<Vertex, Integer> entry : map.entrySet()) {
			    if (min == null || min.getValue() > entry.getValue()) {
			        min = entry;
			    }
			}
			return min.getKey();
		}
		
		
		public void addEdge(Point3D a, Point3D b, boolean directional){
			//Make some new vertex objects
			Vertex vertA = null;
			Vertex vertB = null;
			
			//If vertex A already exists, look it up, otherwise create a new one
			if(vertices.containsKey(a)){
				vertA = vertices.get(a);
			}else{
				vertA = new Vertex(a);
				vertices.put(a, vertA);
			}
			
			//If vertex B already exists, look it up, otherwise create a new one
			if(vertices.containsKey(b)){
				vertB = vertices.get(b);
			}else{
				vertB = new Vertex(b);
				vertices.put(b, vertB);
			}
			
			//Add the edge from A->B
			vertA.adjacentcies.add(new Edge(vertB));

			//If the edge is not directional, add the corresponding reverse edge
			if(!directional){
				//Add the edge from B->A
				vertB.adjacentcies.add(new Edge(vertA));	
			}
		}
		
		public void addCustomEdge(Point3D a, Point3D b, final EdgeActions actions){
			//Make some new vertex objects
			Vertex vertA = null;
			Vertex vertB = null;
			
			//If vertex A already exists, look it up, otherwise create a new one
			if(vertices.containsKey(a)){
				vertA = vertices.get(a);
			}else{
				vertA = new Vertex(a);
				vertices.put(a, vertA);
			}
			
			//If vertex B already exists, look it up, otherwise create a new one
			if(vertices.containsKey(b)){
				vertB = vertices.get(b);
			}else{
				vertB = new Vertex(b);
				vertices.put(b, vertB);
			}
			
			//Add the edge from ORIGIN->EDGE.DESTINATION
			vertA.adjacentcies.add(new Edge(vertB){
				
				@Override
				public int getWeight(){
					return actions.getWeight();
				}
				
				@Override
				public boolean step(){
					return actions.step();
				}
			});
		}

	}
	
	private static class Vertex{
		public Set<Edge> adjacentcies = new HashSet<Edge>();
		public Point3D point;
		
		public Vertex(Point3D point){
			this.point = point;
		}
	}
	
	private static class Edge implements EdgeActions{
		public Vertex destination;
		
		public Edge(Vertex destination){
			this.destination = destination;
		}
		
		public int getWeight(){
			//This should always be 1
			return 1;
		}
		
		public boolean step(){
			//TODO this is a normal walking step
			return false;
		}
	}
	
	private interface EdgeActions{
		public int getWeight();
		public boolean step();
	}
	
	private static class Point3D{
		public int x, y, z;
		public Point3D(int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	private enum Teleport{
		
		LUMBRIDGE_LOAD(new Point3D(0,0,0), 45, new TeleportAction(){

			@Override
			public boolean canTeleport() {
				return Lodestone.LUMBRIDGE.isActivated();
			}

			@Override
			public boolean teleport() {
				return Lodestone.LUMBRIDGE.teleport();
			}
		}),
		VARROCK_LOAD(new Point3D(0,0,0), 45, new TeleportAction(){

			@Override
			public boolean canTeleport() {
				return Lodestone.VARROCK.isActivated();
			}

			@Override
			public boolean teleport() {
				return Lodestone.VARROCK.teleport();
			}
		}),
		GLORY_ITEM(new Point3D(0,0,0), 1, new TeleportAction(){

			@Override
			public boolean canTeleport() {
				return Inventory.contains("Amulet of Glory(") || Equipment.containsAnyOf("Amulet of Glory(");
			}

			@Override
			public boolean teleport() {
				//Click the amulet here (may need to open inv or equip)
				return true;
			}
		});
		
		TeleportAction teleportActions;
		Point3D dest;
		int cost;
		
		Teleport(Point3D dest, int cost, TeleportAction teleportActions){
			this.dest = dest;
			this.cost = cost;
			this.teleportActions = teleportActions;
		}
		
		private interface TeleportAction{
			public boolean canTeleport();
			public boolean teleport();
			
		}
	}
	
	//Path Error
	enum PathError{
		NONE;
	}
}
