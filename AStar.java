package pathfinding;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import utility.math.Transform2d;
import utility.math.Vec2d;

public class AStar {
	private ArrayList<Node> nodes;
	private ArrayList<Node> open_set;
	private ArrayList<Node> close_set;
	Node start;
	Node end;
	
	public AStar(Node start, Node end)
	{
		nodes = new ArrayList<>();
		open_set = new ArrayList<>();
		close_set = new ArrayList<>();
		this.start = start;
		this.end = end;
		nodes.add(start);
		nodes.add(end);
	}
	public void add(Node node)
	{
		nodes.add(node);
	}
	private static final Comparator<Node> comparator = new Comparator<Node>()
	{
		public int compare(Node a, Node b)
		{
			if(a.global < b.global)
				return -1;
			if(a.global > b.global)
				return 1;
			return 0;
		}
	};
	public ArrayList<Node> calculate()
	{
		Thread.currentThread();
		open_set.add(start);
		close_set.clear();
		start.local = 0;
		start.global = Vec2d.subtract(end.position, start.position).length();
		while(!open_set.isEmpty())
		{
			Node curr = open_set.get(0);
			if(curr == end)
				break;
			close_set.add(curr);
			for(int i = 0; i < curr.connected.size(); i++)
			{
				Node other = curr.connected.get(i);
				float local = curr.local + curr.distances.get(i);
				if(local < other.local)
				{
					other.parent = curr;
					other.local = local;
					other.global = other.local + Vec2d.subtract(end.position, other.position).length();
					open_set.add(other);
				}
				curr.visited = true;
				open_set.remove(curr);
				open_set.sort(comparator);
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(end.parent == null)
		{
			System.out.println("Could not find a path!");
			return null;
		}
		ArrayList<Node> path = new ArrayList<>();
		Node n = end;
		while(n.parent != null)
		{
			path.add(0, n);
			n = n.parent;
		}
		path.add(0, start);
		open_set.clear();
		close_set.clear();
		System.out.println("Found the shortest path!");
		return path;
	}
	public void draw(Graphics2D g2, Transform2d transform)
	{
		float len = transform.tx.x / 4;
		g2.setColor(Color.ORANGE);
		for(int i = 0; i < open_set.size(); i++)
		{
			Vec2d pos = transform.projectToTransform(open_set.get(i).position);
			g2.fillRect((int) (pos.x-len), (int) (pos.y-len), (int) (len*2), (int) (len*2));
		}
		g2.setColor(Color.PINK);
		for(int i = 0; i < close_set.size(); i++)
		{
			Vec2d pos = transform.projectToTransform(close_set.get(i).position);
			g2.fillRect((int) (pos.x-len), (int) (pos.y-len), (int) (len*2), (int) (len*2));
		}
	}

}
