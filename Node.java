package pathfinding;

import java.util.ArrayList;

import utility.math.Vec2d;

public class Node {
	Vec2d position;
	
	ArrayList<Node> connected;
	ArrayList<Float> distances;
	
	float global;
	float local;
	Node parent;
	boolean visited;
	
	public Node(float x, float y)
	{
		position = new Vec2d(x, y);
		connected = new ArrayList<>();
		distances = new ArrayList<>();
		global = Float.MAX_VALUE;
		local = Float.MAX_VALUE;
		parent = null;
		visited = false;
	}
	public void add(Node node, float dist)
	{
		connected.add(node);
		distances.add(dist);
	}

}
