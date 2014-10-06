package mineSweeper;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JFrame implements MouseListener, KeyListener, ActionListener {
	
	private static final long serialVersionUID = 1L;
	private static int width = 400;
	private static int height = 450;
	private Screen screen;
	private JPanel timer;
	private Game game;
	private Font font;
	private JLabel [] timerDigits = new JLabel[3];
	private ImageIcon [] timerIcons = new ImageIcon[10];
	private Timer timerTimer;
	private int timerHundreds, timerTens, timerOnes;
	
	private int insetLeft;
	private int insetTop;
	
	public Board()
	{
		super("MineSweeper");
		
		game = new Game(this);
		
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addMouseListener(this);
		addKeyListener(this);
		
		timer = new JPanel();
		timer.setPreferredSize(new Dimension(30, 50));
		timer.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 5));
		add(timer, BorderLayout.PAGE_START);
		
		for (int i = 0; i < 10; i++){
			timerIcons[i] = new ImageIcon(ImageLoader.scale(ImageLoader.loadImage("fx/" + i + ".png"), 26, 40));
		}
		
		for (int i = 0; i < 3; i++){
			timerDigits[i] = new JLabel(timerIcons[0]);
			timer.add(timerDigits[i]);
		}
		
		timerTimer = new Timer(1000, this);
		timerHundreds = 0;
		timerTens = 0;
		timerOnes = 0;
			
		screen = new Screen();
		add(screen, BorderLayout.CENTER);
		
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
		int deltaY = e.getY() - screen.getY() - insetTop;
		if (deltaY < 0){return;}
		if(e.getButton() == 1) {
			game.clickedLeft(e.getX() - insetLeft, deltaY);
			if (!timerTimer.isRunning()){
				timerTimer.restart();
			}
		}
		if(e.getButton() == 3) game.clickedRight(e.getX() - insetLeft, deltaY);
		if(e.getButton() == 2) game.clickedMiddle(e.getX() - insetLeft, deltaY);
		screen.repaint();
	}
	
	public void actionPerformed(ActionEvent e){
		if (timerHundreds*100+timerTens*10+timerOnes > 999){
			return;
		} else {
			timerOnes = ++timerOnes % 10;
			if(timerOnes == 0) {
				timerTens = ++timerTens % 10;
				
				if(timerTens == 0) {
					timerHundreds = ++timerHundreds % 10;
				}
			}
			setTimerValue();
		}
	}
	
	public void timerReset(){
		if (timerTimer == null){
			return;
		}
		timerTimer.stop();
		timerHundreds = 0;
		timerTens = 0;
		timerOnes = 0;
		setTimerValue();
	}

	private void setTimerValue() {
		timerDigits[0].setIcon(timerIcons[timerHundreds]);
		timerDigits[1].setIcon(timerIcons[timerTens]);
		timerDigits[2].setIcon(timerIcons[timerOnes]);
	}
	
	public void stopTimer(){
		timerTimer.stop();
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

