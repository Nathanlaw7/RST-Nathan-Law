import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics;

public class snakeApplet extends Applet{
	
	private SnakeCanvas c;
	//this is the Applet ta da 
	public void init()
	{
		c = new SnakeCanvas();
		c.setPreferredSize(new Dimension(1280, 720));
		c.setVisible(true);
		c.setFocusable(true);
		this.add(c);
		this.setVisible(true);
		this.setSize(new Dimension(1280, 720));
		
	}
	
	public void paint(Graphics g)
	{
		this.setSize(new Dimension(1280, 720));
	}
}
