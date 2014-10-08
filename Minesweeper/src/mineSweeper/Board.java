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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JFrame implements MouseListener, KeyListener, ActionListener {
	
	private static final long serialVersionUID = 1L;
	private static int width = 400;
	private static int height = 450;
	private Screen screen;
	private JPanel topPanel;
	private JPanel bomb;
	private JPanel timer;
	private Game game;
	private Font font;
	private JLabel [] timerDigits = new JLabel[3];
	private JLabel [] bombDigits = new JLabel[2];
	private ImageIcon [] timerIcons = new ImageIcon[10];
	private Timer timerTimer;
	private int timerHundreds, timerTens, timerOnes;
	private int bombCounter;
	
	private int insetLeft;
	private int insetTop;
	
	private boolean newGame;
	
	public Board()
	{
		super("MineSweeper");
		
		game = new Game(this);
		
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addMouseListener(this);
		addKeyListener(this);
		
		topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(150,50));
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
				
		bomb = new JPanel();
		bomb.setSize(new Dimension(30,50));
		bomb.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
		topPanel.add(bomb, -1);
		topPanel.add(Box.createHorizontalGlue());
		timer = new JPanel();
		timer.setSize(new Dimension(30, 50));
		timer.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 5));
		topPanel.add(timer, -1);
		
		add(topPanel, BorderLayout.PAGE_START);
		
		for (int i = 0; i < 10; i++){
			timerIcons[i] = new ImageIcon(ImageLoader.scale(ImageLoader.loadImage("fx/" + i + ".png"), 26, 40));
		}
		
		for (int i = 0; i < 3; i++){
			timerDigits[i] = new JLabel(timerIcons[0]);
			timer.add(timerDigits[i], -1);
		}
		
		for (int i = 0; i < 2; i++){
			bombDigits[i] = new JLabel(timerIcons[0]);
			bomb.add(bombDigits[i]);
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
		game.reset();
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
			if ((!timerTimer.isRunning()) && (!newGame)){
				timerTimer.restart();
			}
			newGame = false;
		}
		if(e.getButton() == 3) game.clickedRight(e.getX() - insetLeft, deltaY);
		if(e.getButton() == 2) game.clickedMiddle(e.getX() - insetLeft, deltaY);
		screen.repaint();
	}
	
	public void actionPerformed(ActionEvent e){
		if (timerHundreds*100+timerTens*10+timerOnes == 999){
			stopTimer();
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
	
	public int getBombCounter() {
		return bombCounter;
	}

	public void setBombCounter(int bombCounter) {
		this.bombCounter = bombCounter;
	}

	public void initBombCounter()
	{	
		bombDigits[0].setIcon(timerIcons[game.getBombCount()/10]);
		bombDigits[1].setIcon(timerIcons[game.getBombCount()%10]);
		setBombCounter(game.getBombCount());
	}
	
	public void bombCounter(boolean add){
		if (add){
			if (this.getBombCounter() > 0){
			setBombCounter(this.getBombCounter()-1);
			bombDigits[0].setIcon(timerIcons[this.getBombCounter()/10]);
			bombDigits[1].setIcon(timerIcons[this.getBombCounter()%10]);
			}
		} else {
			setBombCounter(this.getBombCounter()+1);
			bombDigits[0].setIcon(timerIcons[this.getBombCounter()/10]);
			bombDigits[1].setIcon(timerIcons[this.getBombCounter()%10]);
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
	
	public void lostDialog()
	{
		ImageIcon bombIcon = new ImageIcon(ImageLoader.scale(ImageLoader.loadImage("fx/bomb.png"), 32, 32));
		int n = JOptionPane.showConfirmDialog(
			    null,
			    "YOU LOSE!\nPlay Again ?",
			    "Game Over",
			    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                bombIcon);
		if(n == JOptionPane.YES_OPTION)
		{
			game.reset();
			newGame = true;
		}
		else System.exit(0);
	}
	
	public void winDialog()
	{
		ImageIcon winIcon = new ImageIcon(ImageLoader.scale(ImageLoader.loadImage("fx/winner.png"), 32, 32));
		int n = JOptionPane.showConfirmDialog(
				null,
			    "YOU WIN!\nPlay Again ?",
			    "Game Over",
			    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                winIcon);
		if(n == JOptionPane.YES_OPTION)
		{
			game.reset();
			newGame = true;
		}
		else System.exit(0);
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

