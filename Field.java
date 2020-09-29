package pathfinding;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import utility.math.Transform2d;
import utility.math.Vec2d;

import java.lang.System;

/**
 * 
 * @author Mario Velez
 *
 */
public class Field extends Canvas
		implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener,
				   Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -796167392411348854L;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Graphics bufferGraphics; // graphics for backbuffer
	private BufferStrategy bufferStrategy;
	
	public static int mousex = 0; // mouse values
	public static int mousey = 0;

	public static ArrayList<Integer> keysDown; // holds all the keys being held down
	boolean leftClick;

	private Thread thread;

	private boolean running;
	private int runTime;
	private float seconds;
	private int refreshTime;
	
	public static int[] anchor = new  int[2];
	public static boolean dragging;
	
	private Maze maze;
	private int[] dimensions = {50, 50};
	private Transform2d transform;
	
	public Field(Dimension size) throws Exception {
		this.setPreferredSize(size);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);

		this.thread = new Thread(this);
		running = true;
		runTime = 0;
		seconds = 0;
		refreshTime = (int) (1f/50 * 1000);

		keysDown = new ArrayList<Integer>();
		
		maze = new Maze(dimensions[0], dimensions[1]);
		
		Vec2d origin = new Vec2d(0, 0);
		float minw = (size.width-15.0f)/maze.width;
		float minh = (size.height-65.0f)/maze.height;
		float tile_len;
		if(minw < minh)
		{
			tile_len = minw;
			origin.set(0, (size.height-(tile_len*maze.height)) / 2);
		}
		else
		{
			tile_len = minh;
			origin.set((size.width-(tile_len*maze.width)) / 2, 0);
		}
		transform = new Transform2d(origin, new Vec2d(tile_len, 0), new Vec2d(0, tile_len));
		
		this.addComponentListener(new ComponentListener()
		{
			public void componentHidden(ComponentEvent arg0)
			{
				
			}
			public void componentMoved(ComponentEvent arg0)
			{
				
			}
			public void componentResized(ComponentEvent arg0)
			{
				Vec2d origin = new Vec2d(0, 0);
				float minw = (Field.this.getWidth())/maze.width;
				float minh = (Field.this.getHeight())/maze.height;
				float tile_len;
				if(minw < minh)
				{
					tile_len = minw;
					origin.set(0, (Field.this.getHeight()-(tile_len*maze.height)) / 2);
				}
				else
				{
					tile_len = minh;
					origin.set((Field.this.getWidth()-(tile_len*maze.width)) / 2, 0);
				}
				transform = new Transform2d(origin, new Vec2d(tile_len, 0), new Vec2d(0, tile_len));
			}
			public void componentShown(ComponentEvent arg0)
			{
				
			}
		});
	}

	public void paint(Graphics g) {


		if (bufferStrategy == null) {
			this.createBufferStrategy(2);
			bufferStrategy = this.getBufferStrategy();
			bufferGraphics = bufferStrategy.getDrawGraphics();

			this.thread.start();
		}
	}
	@Override
	public void run() {
		// what runs when editor is running
		
		while (running) {
			long t1 = System.currentTimeMillis();
			
			DoLogic();
			Draw();

			DrawBackbufferToScreen();

			Thread.currentThread();
			try {
				Thread.sleep(refreshTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long t2 = System.currentTimeMillis();
			
			if(t2 - t1 > 16)
			{
				if(refreshTime > 0)
					refreshTime --;
			}
			else
				refreshTime ++;
			
			seconds += refreshTime/1000f;
			//System.out.println(t2 - t1);
			

		}
	}

	public void DrawBackbufferToScreen() {
		bufferStrategy.show();

		Toolkit.getDefaultToolkit().sync();
	}

	public void DoLogic() {
		
		runTime++;
	}
	private ArrayList<Node> path = new ArrayList<>();
	public void Draw() // titleScreen
	{
		// clears the backbuffer
		bufferGraphics = bufferStrategy.getDrawGraphics();
		try {
			bufferGraphics.clearRect(0, 0, this.getSize().width, this.getSize().height);
			// where everything will be drawn to the backbuffer
			Graphics2D g2 = (Graphics2D) bufferGraphics;
			g2.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke((int) (transform.tx.x/5)));
			maze.draw(g2, transform);
			if(astar != null)
				astar.draw(g2, transform);
			g2.setColor(Color.GREEN);
			ArrayList<Vec2d> points = new ArrayList<>();
			if(path != null)
			{
				for(int i = 0; i < path.size(); i++)
				{
					Node node = path.get(i);
					points.add(transform.projectToTransform(node.position));
				}
			}
			for(int i = 1; i < points.size(); i++)
			{
				Vec2d a = points.get(i-1);
				Vec2d b = points.get(i);
				g2.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bufferGraphics.dispose();
		}
	}
	
	public void generateMaze()
	{
		if(path != null)
			path.clear();
		astar = null;
		maze = new Maze(dimensions[0], dimensions[1]);
		maze.generate();
		maze.cells[0][0].walls[3] = false;
		maze.cells[maze.width-1][maze.height-1].walls[1] = false;
	}
	private AStar astar = null;
	public void generatePath()
	{
		astar = null;
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{
				int w = maze.width;
				int h = maze.height;
				Node[][] nodes = new Node[w][h];
				for(int x = 0; x < w; x++)
				{
					for(int y = 0; y < h; y++)
					{
						nodes[x][y] = new Node(x + 0.5f, y + 0.5f);
					}
				}
				astar = new AStar(nodes[0][0], nodes[w-1][h-1]);
				float sqr = (float) Math.sqrt(2);
				for(int x = 0; x < w; x++)
				{
					for(int y = 0; y < h; y++)
					{
						Cell cell = maze.cells[x][y];
						if(!cell.walls[0])
						{
							nodes[x][y].add(nodes[x][y-1], 1);
							if(isOpen(x, y, 0))
								nodes[x][y].add(nodes[x+1][y-1], sqr);
						}
						if(!cell.walls[1] && !isEnd(x, y))
						{
							nodes[x][y].add(nodes[x+1][y], 1);
							if(isOpen(x, y, 1))
								nodes[x][y].add(nodes[x+1][y+1], sqr);
						}
						if(!cell.walls[2])
						{
							nodes[x][y].add(nodes[x][y+1], 1);
							if(isOpen(x, y, 2))
								nodes[x][y].add(nodes[x-1][y+1], sqr);
						}
						if(!cell.walls[3] && !isStart(x, y))
						{
							nodes[x][y].add(nodes[x-1][y], 1);
							if(isOpen(x, y, 3))
								nodes[x][y].add(nodes[x-1][y-1], sqr);
						}
						astar.add(nodes[x][y]);
					}
				}
				path = astar.calculate();
			}
		});
		thread.start();
	}
	private boolean isStart(int x, int y)
	{
		return (x == 0 && y == 0);
	}
	private boolean isEnd(int x, int y)
	{
		return (x == maze.width-1 && y == maze.height-1);
	}
	private boolean isOpen(int x, int y, int side)
	{
		Cell cell = maze.cells[x][y];
		if(side == 0)
		{
			if(x < maze.width-1 && y > 0)
			{
				Cell other = maze.cells[x+1][y-1];
				if(!cell.walls[side] && !cell.walls[side+1] && !other.walls[3] && !other.walls[2])
					return true;
			}
			return false;
		}
		else if(side == 1)
		{
			if(x < maze.width-1 && y < maze.height-1)
			{
				Cell other = maze.cells[x+1][y+1];
				if(!cell.walls[side] && !cell.walls[side+1] && !other.walls[0] && !other.walls[3])
					return true;
			}
			return false;
		}
		else if(side == 2)
		{
			if(x > 0 && y < maze.height-1)
			{
				Cell other = maze.cells[x-1][y+1];
				if(!cell.walls[side] && !cell.walls[side+1] && !other.walls[0] && !other.walls[1])
					return true;
			}
			return false;
		}
		else if(side == 3)
		{
			if(x > 0 && y > 0)
			{
				Cell other = maze.cells[x-1][y-1];
				if(!cell.walls[side] && !cell.walls[0] && !other.walls[1] && !other.walls[2])
					return true;
			}
			return false;
		}
		return false;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (!keysDown.contains(e.getKeyCode()) && e.getKeyCode() != 86)
			keysDown.add(new Integer(e.getKeyCode()));
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysDown.remove(new Integer(e.getKeyCode()));
		
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 1)
		{
			leftClick = true;
		}
		else if(e.getButton() == 2)
		{
			
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1)
			leftClick = false;
		if(e.getButton() == 2)
			dragging = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(leftClick)
			leftClick = true;
		mousex = e.getX();
		mousey = e.getY();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
	}

}