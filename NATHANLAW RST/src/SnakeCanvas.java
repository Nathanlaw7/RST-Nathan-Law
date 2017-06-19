import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.io.BufferedWriter;
import javax.swing.JOptionPane;



public class SnakeCanvas extends Canvas implements Runnable, KeyListener
{
	//Nathan Law RST Speed Snake. started June 8, 2017 and finished June 16, 2017.
	//creating the GRID
	private final int Box_Height = 15;
	private final int Box_Width = 15;
	private final int Grid_Height = 45;
	private final int Grid_Width = 85;
	//creating the snake and the fruit
	private LinkedList<Point> snake;
	private Point fruit;
	private int direction = Direction.NO_DIRECTION;
	
	private Thread runThread;
	private int score = 0;
	private String highscore = "";
	
	
	public void paint(Graphics g)
	{
		//placing everything in with this paint tool
		this.setPreferredSize(new Dimension (1280, 720));
		this.addKeyListener(this);
		if (snake == null)
		{
			snake = new LinkedList<Point>();
			generateDefaultSnake();
			PlaceFruit();
			
		}
		
		if(runThread == null)
		{
			runThread = new Thread(this);
			runThread.start();
		}
		if (highscore.equals(""))
		{
			//place highscore
			highscore = this.GetHighScore();
		}
		
		DrawFruit(g);
		DrawGrid(g);
		DrawSnake(g);
		DrawScore(g);
	}
	
	public void update(Graphics g)
	{
		//this will contain the double buffering which makes it less laggy
		Graphics offscreenGraphics; //these are the graphics use to draw offscreen
		BufferedImage offscreen = null;
		Dimension d = this.getSize();
		
		offscreen = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		offscreenGraphics = offscreen.getGraphics(); 
		offscreenGraphics.setColor(this.getBackground());
		offscreenGraphics.fillRect(0, 0, d.width, d.height);
		offscreenGraphics.setColor(this.getForeground());
		paint(offscreenGraphics);
		
		//flip
		g.drawImage(offscreen, 0, 0, this);
	}
	
	public void generateDefaultSnake()
	{
		
		//creating the snake after death
		snake.clear();
		score = 0;
		snake.add(new Point(0,2));
		snake.add(new Point(0,1));
		snake.add(new Point(0,0));
		direction = Direction.NO_DIRECTION;
	}
	

	public void Move()
	{
		//all moves are here and stops you if you going the wrong direction
		Point Head = snake.peekFirst();
		Point newPoint = Head;
		switch (direction) {
		case Direction.NORTH:
			newPoint = new Point(Head.x, Head.y -1);
			break;
		case Direction.SOUTH:
			newPoint = new Point(Head.x, Head.y + 1);
			break;
		case Direction.WEST:
			newPoint = new Point(Head.x - 1, Head.y);
			break;
		case Direction.EAST:
			newPoint = new Point(Head.x + 1, Head.y);
			break;
		}
		
		snake.remove(snake.peekLast());
		
		if (newPoint.equals(fruit))
		{
			//the snake eats
			score+=10;
			
			Point addPoint = (Point) newPoint.clone();
			
			switch (direction) {
			case Direction.NORTH:
				newPoint = new Point(Head.x, Head.y -1);
				break;
			case Direction.SOUTH:
				newPoint = new Point(Head.x, Head.y + 1);
				break;
			case Direction.WEST:
				newPoint = new Point(Head.x - 1, Head.y);
				break;
			case Direction.EAST:
				newPoint = new Point(Head.x + 1, Head.y);
				break;
			}
			snake.push(addPoint);
			PlaceFruit();
		}
		else if (newPoint.x < 0 || newPoint.x > Grid_Width - 1)
		{
			//The snake is off the grid, and you restart
			CheckScore();
			generateDefaultSnake();
			return;
		}
		else if (newPoint.y < 0 || newPoint.y > Grid_Height - 1)
		{
			//The snake is off the grid, and you restart
			CheckScore();
			generateDefaultSnake();
			return;
		}
		else if (snake.contains(newPoint))
		{
			//why ya hitting yourself? darn you restart
			CheckScore();
			generateDefaultSnake();
			return;
		}
		
		//if your still alive then we're all good
		snake.push(newPoint);
	}
	
	public void DrawScore(Graphics g)
	{
		g.drawString("Score:" + score, 0, Box_Height * Grid_Height + 10);
		g.drawString("Highscore:" + highscore, 0, Box_Height * Grid_Height + 20);
	}
	

	public void CheckScore()
	{
		if (highscore.equals(""))
			return;
		//format Nathan/:/###
		if (score > Integer.parseInt((highscore.split(":")[1])))
		{
			//user as set a new record
			String name = JOptionPane.showInputDialog("you set a new Highscore! What is your name?");
			highscore = name + ":" + score;
			
			File scoreFile = new File("highscore.dat");
			if (!scoreFile.exists())
			{
				try {
					scoreFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileWriter writeFile = null;
			BufferedWriter writer = null;
			try
			{
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);
				writer.write(this.highscore);
			} 
			catch (Exception e)
			{
				//errors
			}
			finally
			{
				try
				{
					if (writer != null)
						writer.close();
				}
				catch (Exception e) {}
			}
		}
	}
	
	//in the beginning there was nothing until there was the Grid
	public void DrawGrid(Graphics g)
	{
		//drawing an outside rectangle
		g.drawRect(0,  0,Grid_Width * Box_Width, Grid_Height * Box_Height);
		//drawing the vertical lines
		for (int x = Box_Width; x < Grid_Width * Box_Width; x+=Box_Width)
		{
			g.drawLine(x, 0, x, Box_Height * Grid_Height);
		}
		//drawing the horizontal lines
		for (int y = Box_Height; y < Grid_Height * Box_Height; y+=Box_Height)
		{
			g.drawLine(0, y, Grid_Width * Box_Width, y);
		}
	}
	
	//Drawing snake into existence
	public void DrawSnake(Graphics g)
	{
		g.setColor(Color.MAGENTA);
		for (Point p : snake)
		{
			g.fillRect(p.x * Box_Width, p.y * Box_Height, Box_Width, Box_Height);
		}
		g.setColor(Color.BLACK);
	}
	
	//This snake must eat so I made a water melon
	public void DrawFruit(Graphics g)
	{
		g.setColor(Color.CYAN);
		g.fillOval(fruit.x * Box_Width, fruit.y * Box_Height, Box_Width, Box_Height);
		g.setColor(Color.BLACK);
	}
	public void PlaceFruit()
	//randomly placing fruit thats not in the snake
	{
		Random rand = new Random();
		int randomX = rand.nextInt(Grid_Width);
		int randomY = rand.nextInt(Grid_Height);
		Point randomPoint = new Point(randomX, randomY);
		while (snake.contains(randomPoint))
		{
			randomX = rand.nextInt(Grid_Width);
			randomY = rand.nextInt(Grid_Height);
			randomPoint = new Point(randomX,randomY);
		}
		fruit = randomPoint;
	}
	
	@Override
	public void run() {
		//ta da a run method
		while (true)
		{
			//this will run forever
			Move();
			repaint();
			
			try
			{
				Thread.currentThread();
				Thread.sleep(50);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public String GetHighScore()
	{
		//format: Nathan:###
		FileReader readFile = null;
		BufferedReader reader = null;
		try
		{
			readFile = new FileReader("highscore.dat");
			reader = new BufferedReader(readFile);
			return reader.readLine();
		}
		
		catch (Exception e)
		{
			return "Nobody:0";
		}
		finally
		{
			try {
				if (reader != null)
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//this is what sets the keys
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_UP:
			if (direction != Direction.SOUTH)
				direction = Direction.NORTH;
			break;
		case KeyEvent.VK_DOWN:
			if (direction != Direction.NORTH)
				direction = Direction.SOUTH;
			break;
		case KeyEvent.VK_RIGHT:
			if (direction != Direction.WEST)
				direction = Direction.EAST;
			break;
		case KeyEvent.VK_LEFT:
			if (direction != Direction.EAST)
				direction = Direction.WEST;
			break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
}
