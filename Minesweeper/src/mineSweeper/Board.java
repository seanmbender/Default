package mineSweeper;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Board extends JFrame implements MouseListener, KeyListener {
	
	private static final long serialVersionUID = 1L;
	private static int width = 400;
	private static int height = 400;
	private Screen screen;
	private Game game;
	private Font font;
	
	private int insetLeft;
	private int insetTop;
	
	public Board()
	{
		super("MineSweeper");
		
		game = new Game();
		
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addMouseListener(this);
		addKeyListener(this);
		
		screen = new Screen();
		add(screen);
		
		pack();
		insetLeft = getInsets().left;
		insetTop  = getInsets().top;
		setSize(width + insetLeft + getInsets().right, height + getInsets().bottom + insetTop);
		setLocationRelativeTo(null);
		setVisible(true);
		
		font = new Font("Arial",0,10);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if(e.getButton() == 1) game.clickedLeft(e.getX() - insetLeft, e.getY() - insetTop);
		if(e.getButton() == 3) game.clickedRight(e.getX() - insetLeft, e.getY() - insetTop);
		if(e.getButton() == 2) game.clickedMiddle(e.getX() - insetLeft, e.getY() - insetTop);
		screen.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		
	}
	
	
	public class Screen extends JPanel {

		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g)
		{
			g.setFont(font);
			game.draw(g);
		}
	}
	
	
	public static int getScreenWidth()
	{
		return width;
	}
	
	public static int getScreenHeight()
	{
		return height;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			game.reset();
			screen.repaint();
		}
	}

	
}

