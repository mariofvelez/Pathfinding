package pathfinding;

import java.awt.Color;
import java.awt.Graphics2D;

import utility.math.Transform2d;
import utility.math.Vec2d;

public class Cell {
	boolean[] walls;
	boolean visited;
	int x;
	int y;
	Vec2d point;
	static final Vec2d wh = new Vec2d(1, 1);
	
	public Cell(int x, int y)
	{
		walls = new boolean[4];
		visited = false;
		for(int i = 0; i < walls.length; i++)
			walls[i] = true;
		this.x = x;
		this.y = y;
		point = new Vec2d(x, y);
	}
	public void draw(Graphics2D g2, Transform2d transform, Color c)
	{
		Vec2d p1 = transform.projectToTransform(point);
		Vec2d p2 = transform.projectToTransform(Vec2d.add(point, wh));
		
		g2.setColor(c);
		if(visited)
			g2.fillRect((int) p1.x, (int) p1.y, (int) (p2.x-p1.x), (int) (p2.y-p1.y));
		g2.setColor(Color.WHITE);
		if(walls[0])
			g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p1.y);
		if(walls[1])
			g2.drawLine((int) p2.x, (int) p1.y, (int) p2.x, (int) p2.y);
		if(walls[2])
			g2.drawLine((int) p1.x, (int) p2.y, (int) p2.x, (int) p2.y);
		if(walls[3])
			g2.drawLine((int) p1.x, (int) p1.y, (int) p1.x, (int) p2.y);
	}

}
