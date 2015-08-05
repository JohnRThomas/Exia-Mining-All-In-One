package web;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.region.Players;

public class WebPath extends Path{
	
	//Variables
	PathError lastError = PathError.NONE;
	LinkedList<Edge> steps = new LinkedList<Edge>();
	
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
		
		//if the step worked, pop it off the remaining steps.
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
	
	@SafeVarargs
	public static WebPath buildTo(Collection<? extends Locatable>... a){
		//TODO figure out what the fuck this is supposed to do
		return null;
	}
	
	//Under the hood stuff
	private static class Graph{
		
		//This is where every vertex is stored
		//Each vertex stores its own out-going edges
		HashMap<Point3D, Vertex> vertices = new HashMap<Point3D, Vertex>();
		
		public Graph(){
			//TODO read from file or some shit and populate the web
		}
		
		public WebPath getPath(Point3D start, Point3D dest){
			//TODO dijkstra's like a mother fucker right here
			return null;
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
		
		public void addCustomEdge(Point3D origin, Edge edge){
			//See if the origin vertex exists, and if it does
			Vertex vertOrigin = null;
			if(vertices.containsKey(origin)){
				vertOrigin = vertices.get(origin);
			}else{
				vertOrigin = new Vertex(origin);
				vertices.put(origin, vertOrigin);
			}
			
			//Add the edge from ORIGIN->EDGE.DESTINATION
			vertOrigin.adjacentcies.add(edge);
		}
		

	}
	
	private static class Vertex{
		public Set<Edge> adjacentcies = new HashSet<Edge>();
		public Point3D point;
		
		public Vertex(Point3D point){
			this.point = point;
		}
	}
	
	private static class Edge{
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
	
	private class DoorEdge extends Edge{
		public DoorEdge(Vertex destination) {
			super(destination);
		}
		
		@Override
		public int getWeight(){
			//TODO possibly check for key if locked, possibly combine with shortcut class
			return 1;
		}
		
		@Override
		public boolean step(){
			//TODO this is a step that requires a door to be opened
			return false;
		}
	}
	
	private class ShortcutEdge extends Edge{
		public ShortcutEdge(Vertex destination) {
			super(destination);
		}
		
		@Override
		public int getWeight(){
			//TODO Check agility level
			return 1;
		}
		
		@Override
		public boolean step(){
			//TODO this is a step that requires clicking a shortcut
			return false;
		}
	}
	
	private class TeleportEdge extends Edge{
		public TeleportEdge(Vertex destination) {
			super(destination);
		}
		
		@Override
		public int getWeight(){
			//TODO check for magic level/items/load stones
			return 1;
		}
		
		@Override
		public boolean step(){
			//TODO this is a step that requires a teleportation
			return false;
		}
	}
	
	private static class Point3D{
		public int x, y, z;
		public Point3D(int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	//Path Error
	enum PathError{
		NONE;
	}
}
