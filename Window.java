package pathfinding;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Mario Velez
 * 
 *
 */
public class Window extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3494605708565768482L;
	public static final int WIDTH = 900;
	public static final int HEIGHT = 600;
	
	public static Window window;
	
	public Window(String name)
	{
		super(name);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Dimension windowSize = new Dimension(WIDTH, HEIGHT);
		this.setSize(windowSize);
	}
	public static void main(String[] args) throws Exception
	{
		window = new Window("Pathfinding");
		Field field = new Field(window.getSize());
		field.setBackground(Color.DARK_GRAY);
		window.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		JButton maze_button = new JButton("Generate Maze");
		maze_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				field.generateMaze();
			}
		});
		panel.add(maze_button, gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		JButton path_button = new JButton("Find Solution");
		path_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				field.generatePath();
			}
		});
		panel.add(path_button, gc);
		
		window.add(panel, BorderLayout.NORTH);
		
		window.add(field, BorderLayout.CENTER);
		
		window.setVisible(true);
		window.setLocation(200, 100);
	}
	public static void close()
	{
		window.dispose();
	}
}
