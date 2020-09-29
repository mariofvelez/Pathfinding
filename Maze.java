package pathfinding;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import utility.math.Transform2d;

public class Maze {
	int width;
	int height;
	Cell[][] cells;
	
	public Maze(int width, int height)
	{
		this.width = width;
		this.height = height;
		cells = new Cell[width][height];
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				cells[x][y] = new Cell(x, y);
	}
	private Color c = new Color(0, 50, 100);
	public void draw(Graphics2D g2, Transform2d transform)
	{
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				if(cells[x][y] == current)
					cells[x][y].draw(g2, transform, Color.MAGENTA);
				else
					cells[x][y].draw(g2, transform, c);
	}
	Cell current;
	private static final int SLEEP_TIME = 0;
	public void generate()
	{
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{
				Thread.currentThread();
				ArrayList<Cell> stack = new ArrayList<>();
				current = cells[0][0];
				current.visited = true;
				Cell next = checkNeighbors(current);
				while(!isFinishedGenerating())
				{
					next = checkNeighbors(current);
					if(next != null)
					{
						next.visited = true;
						stack.add(current);
						removeWalls(current, next);
						current = next;
					}
					else if(stack.size() > 0)
						current = stack.remove(stack.size()-1);
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				for(int i = 0; i < width*height; i++)
				{
					int x = (int) (Math.random()*width);
					int y = (int) (Math.random()*height);
					int a = (int) (Math.random()*3)-1;
					int b = (int) (Math.random()*3)-1;
					Cell A = getIndex(x, y);
					Cell B = getIndex(x+a, y+b);
					if(B != null)
						removeWalls(A, B);
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				current = null;
				System.out.println("Maze creation complete!");
			}
		});
		thread.start();
	}
	private boolean isFinishedGenerating()
	{
//		boolean finished = true;
		for(int x = 0; x < cells.length; x++)
			for(int y = 0; y < cells[x].length; y++)
				if(!cells[x][y].visited)
					return false;
//				finished &= cells[x][y].visited;
		return true;
	}
	private Cell getIndex(int x, int y)
	{
		if(x < 0 || x >= width || y < 0 || y >= height)
			return null;
		return cells[x][y];
	}
	private void removeWalls(Cell a, Cell b)
	{
		int x = a.x-b.x;
		int y = a.y-b.y;
		if(x == 1 && y == 0)
		{
			a.walls[3] = false;
			b.walls[1] = false;
		}
		else if(x == -1 && y == 0)
		{
			a.walls[1] = false;
			b.walls[3] = false;
		}
		if(y == 1 && x == 0)
		{
			a.walls[0] = false;
			b.walls[2] = false;
		}
		else if(y == -1 && x == 0)
		{
			a.walls[2] = false;
			b.walls[0] = false;
		}
	}
	private Cell checkNeighbors(Cell cell)
	{
		ArrayList<Cell> neighbors = new ArrayList<>();
		
		Cell top = getIndex(cell.x, cell.y-1);
		Cell right = getIndex(cell.x+1, cell.y);
		Cell bottom = getIndex(cell.x, cell.y+1);
		Cell left = getIndex(cell.x-1, cell.y);
		
		if(top != null && !top.visited)
			neighbors.add(top);
		if(right != null && !right.visited)
			neighbors.add(right);
		if(bottom != null && !bottom.visited)
			neighbors.add(bottom);
		if(left != null && !left.visited)
			neighbors.add(left);
		
		if(neighbors.size() > 0)
			return neighbors.get((int) (Math.random()*neighbors.size()));
		return null;
			
	}

}
