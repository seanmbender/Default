package mineSweeper;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Game {
	
	//Set the size of the tiles used on the game board
	private static int width = 20;
	private static int height = 20;
	
	private static int AMOUNT_OF_BOMBS = 20;
	
	private Random random;
	private boolean finish;
	private boolean dead;
	private Board board;
	
	private Tile[][] tiles;
	
	//Load images used to render the gameboard pieces
	private BufferedImage bomb = ImageLoader.scale(ImageLoader.loadImage("fx/bomb.png"), Tile.getWidth(), Tile.getHeight());
	private BufferedImage flag = ImageLoader.scale(ImageLoader.loadImage("fx/flag.png"), Tile.getWidth(), Tile.getHeight());
	private BufferedImage normal = ImageLoader.scale(ImageLoader.loadImage("fx/normal.png"), Tile.getWidth(), Tile.getHeight());
	private BufferedImage pressed = ImageLoader.scale(ImageLoader.loadImage("fx/pressed.png"), Tile.getWidth(), Tile.getHeight());
	
	//Used to scale the coordinate system properly for different difficulties
	private double coorOffset;
	
	public Game(Board board)
	{
		random = new Random();
		tiles  = new Tile[width][height]; 
		this.board = board;
		
		//Load the board
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				tiles[x][y] = new Tile(x, y, normal, bomb, pressed, flag);
			}
		}
		//Prompt user for difficulty level of this play session
		this.startMenu();
	}
	
	private void placeBombs()
	{
		for(int i = 0; i < AMOUNT_OF_BOMBS; i++)
		{
			placeBomb();
		}
	}
	
	private void placeBomb()
	{
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		
		if(!tiles[x][y].isBomb()) tiles[x][y].setBomb(true);
		else placeBomb();
		
	}
	
	//This method loads the tiles with the number of bombs adjacent to that tile
	private void setProximity()
	{
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				int mx = x - 1;
				int gx = x + 1;
				int my = y - 1;
				int gy = y + 1;
				
				int xbomb = 0;
				
				if((mx >= 0) && (my >= 0) && (tiles[mx][my].isBomb())) xbomb++;
				if((mx >= 0) && (tiles[mx][y].isBomb())) xbomb++;
				if((mx >= 0) && (gy < height) && (tiles[mx][gy].isBomb())) xbomb++;
				
				if((my >= 0) && (tiles[x][my].isBomb())) xbomb++;
				if((gy < height) && (tiles[x][gy].isBomb())) xbomb++;
				
				if((gx < width) && (my >= 0) && (tiles[gx][my].isBomb())) xbomb++;
				if((gx < width) && (tiles[gx][y].isBomb())) xbomb++;
				if((gx < width) && (gy < height) && (tiles[gx][gy].isBomb())) xbomb++;
				
				tiles[x][y].setProximityCount(xbomb);
			}
		}
	}
	
	//The event handler for left-mouse-click
	public void clickedLeft(int x, int y)
	{
		if(!dead && !finish)
		{
			int tileX = (int) (x/(width * coorOffset));
			int tileY = (int) (y/(height * coorOffset));
			if(!tiles[tileX][tileY].isFlag())
			{
				open(tileX, tileY);
				if(tiles[tileX][tileY].isBomb())
				{
					dead = true;
				}
				else
				{
					if(tiles[tileX][tileY].getProximityCount() == 0)
					{
						open(tileX, tileY);
					}
				}
				checkFinish();
			}
		}
	}
	
	//The event handler for right-mouse-click
	public void clickedRight(int x, int y)
	{
		if(!dead && !finish && board.getBombCounter() > 0)
		{
			int tileX = (int) (x/(width * coorOffset));
			int tileY = (int) (y/(height * coorOffset));
			tiles[tileX][tileY].placeFlag();
			if (tiles[tileX][tileY].isFlag() && !tiles[tileX][tileY].isOpened()){
				board.bombCounter(true);
			} else if (!tiles[tileX][tileY].isFlag() && !tiles[tileX][tileY].isOpened()){
				board.bombCounter(false);
			}
		} else if (!dead && !finish && board.getBombCounter() == 0)
		{
			int tileX = (int) (x/(width * coorOffset));
			int tileY = (int) (y/(height * coorOffset));
			if (tiles[tileX][tileY].isFlag() && !tiles[tileX][tileY].isOpened())
			{
			tiles[tileX][tileY].placeFlag();
			board.bombCounter(false);
			}
		}
		checkFinish();
	}
	
	//The event handler for middle-mouse-click. This method calls the open method on tiles adjacent to the tile clicked after checking to ensure they are inside the screen boundary
	public void clickedMiddle(int x, int y)
	{
		int numFlags = 0;
		
		if(!dead && !finish)
		{
			int tileX = (int) (x/(width * coorOffset));
			int tileY = (int) (y/(height * coorOffset));
			
			int mx = tileX - 1;
			int gx = tileX + 1;
			int my = tileY - 1;
			int gy = tileY + 1;
			
			if((mx >= 0) && (gy < height) && tiles[mx][gy].isFlag()) numFlags++;
			if((gy < height) && tiles[tileX][gy].isFlag()) numFlags++;
			if((gx < width) && (gy < height) && tiles[gx][gy].isFlag()) numFlags++;
			
			if((mx >= 0) && tiles[mx][tileY].isFlag()) numFlags++;
			if((gx < width) && tiles[gx][tileY].isFlag()) numFlags++;
			
			if((mx >= 0) && (my >= 0) && tiles[mx][my].isFlag()) numFlags++;
			if((my >= 0) && tiles[tileX][my].isFlag()) numFlags++;
			if((gx < width) && (my >= 0) && tiles[gx][my].isFlag()) numFlags++;
			
			if(tiles[tileX][tileY].isOpened() && (tiles[tileX][tileY].getProximityCount() == numFlags))
			{
				if((mx >= 0) && (my >= 0) && !tiles[mx][my].isFlag())
					{
						open(mx, my);
						if (tiles[mx][my].isBomb()) dead = true;
					}
				if((mx >= 0) && !tiles[mx][tileY].isFlag())
				{
					open(mx, tileY);
					if (tiles[mx][tileY].isBomb()) dead = true;
				}
				if((mx >= 0) && (gy < height) && !tiles[mx][gy].isFlag())
				{
					open(mx, gy);
					if (tiles[mx][gy].isBomb()) dead = true;
				}
				
				if((my >= 0) && !tiles[tileX][my].isFlag())
				{
					open(tileX, my);
					if (tiles[tileX][my].isBomb()) dead = true;
				}
				if((gy < height) && !tiles[tileX][gy].isFlag())
				{
					open(tileX, gy);
					if (tiles[tileX][gy].isBomb()) dead = true;
				}
				
				if((gx < width) && (my >= 0) && !tiles[gx][my].isFlag())
				{
					open(gx, my);
					if (tiles[gx][my].isBomb()) dead = true;
				}
				if((gx < width) && !tiles[gx][tileY].isFlag())
				{
					open(gx, tileY);
					if (tiles[gx][tileY].isBomb()) dead = true;
				}
				if((gx < width) && (gy < height) && !tiles[gx][gy].isFlag())
				{
					open(gx, gy);
					if (tiles[gx][gy].isBomb()) dead = true;
				}
			}
			
		}
		checkFinish();
	}
	
	//The method called by other methods in order to show a closed tile
	private void open(int x, int y)
	{
		tiles[x][y].setOpened(true);
		if(tiles[x][y].getProximityCount() == 0)
		{
			int mx = x - 1;
			int gx = x + 1;
			int my = y - 1;
			int gy = y + 1;
			
			if((mx >= 0) && (my >= 0) && (tiles[mx][my].canOpen())) open(mx, my);
			if((mx >= 0) && (tiles[mx][y].canOpen())) open(mx, y);
			if((mx >= 0) && (gy < height) && (tiles[mx][gy].canOpen())) open(mx, gy);
			
			if((my >= 0) && (tiles[x][my].canOpen())) open(x, my);
			if((gy < height) && (tiles[x][gy].canOpen())) open(x, gy);
			
			if((gx < width) && (my >= 0) && (tiles[gx][my].canOpen())) open(gx, my);
			if((gx < width) && (tiles[gx][y].canOpen())) open(gx, y);
			if((gx < width) && (gy < height) && (tiles[gx][gy].canOpen())) open(gx, gy);
		}
	}
	
	//Resets the board after the game finishes
	public void reset()
	{
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				tiles[x][y].reset();
			}
		}
		dead   = false;
		finish = false;
		placeBombs();
		setProximity();
		board.timerReset();
		board.initBombCounter();
	}
	
	//Used to verify the board condition during the game
	private void checkFinish()
	{
		finish = true;
		outer: for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				if(!tiles[x][y].isOpened() || (tiles[x][y].isBomb() && tiles[x][y].isFlag()))
				{
					finish = false;
					break outer;
				}
			}
		}
		endDialog();
	}
	
	
	public void draw(Graphics g)
	{
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				tiles[x][y].draw(g);
			}
		}
	}
	
	//Logic for deciding which menu to display when the game ends
	public void endDialog()
	{
		int count = 0;
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				if(tiles[x][y].isOpened())
					count++;
			}
		}
		
		if(dead)
		{
			board.stopTimer();
			ImageIcon bombIcon = new ImageIcon(ImageLoader.scale(ImageLoader.loadImage("fx/bomb.png"), 32, 32));
			int n = JOptionPane.showConfirmDialog(
				    null,
				    "YOU LOSE!\nPlay Again ?",
				    "MineSweeper",
				    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
	                bombIcon);
			if(n == JOptionPane.YES_OPTION)
			{
				this.reset();
				board.setNewGame(true);
			}
			else System.exit(0);
		}
		else if(AMOUNT_OF_BOMBS == ((width * height) - count))
		{
			board.stopTimer();
			board.stopTimer();
			ImageIcon bombIcon = new ImageIcon(ImageLoader.scale(ImageLoader.loadImage("fx/bomb.png"), 32, 32));
			int n = JOptionPane.showConfirmDialog(
				    null,
				    "YOU Win!\nScore: " + board.getScore() + "\nPlay Again ?",
				    "MineSweeper",
				    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
	                bombIcon);
			if(n == JOptionPane.YES_OPTION)
			{
				board.setNewGame(true);
				this.reset();
				
			}
			else System.exit(0);
		}
	}
	
	//Allows for selection of difficulty level when the game starts
	public void startMenu()
	{
		String[] options = new String[] {"Expert", "Intermediate", "Beginner"};
	    int level = JOptionPane.showOptionDialog(null, "Select Level", "MineSweeper",
	        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
	        null, options, options[0]);
	    //This logic loads the appropriate variable values according to the difficulty, starting with most difficult
	    if(level == 0)
	    {
	    	width = 20;
	    	height = 20;
	    	AMOUNT_OF_BOMBS = 65;
	    	board.setWidth(400);
	    	board.setHeight(400);
	    	this.setCoorOffset(1);
	    	this.reset();
	    }
	    else if(level == 1)
	    {
	    	width = 16;
	    	height = 16;
	    	AMOUNT_OF_BOMBS = 40;
	    	board.setWidth(320);
	    	board.setHeight(320);
	    	this.setCoorOffset(1.25003);
	    }
	    else
	    {
	    	width = 9;
	    	height = 9;
	    	AMOUNT_OF_BOMBS = 10;
	    	board.setWidth(180);
	    	board.setHeight(180);
	    	this.setCoorOffset(2.1553);
	    }
	}

	public static int getWidth() 
	{
		return width;
	}
	
	public static int getHeight()
	{
		return height;
	}
	
	public int getBombCount()
	{
		return AMOUNT_OF_BOMBS;
	}

	public double getCoorOffset() 
	{
		return coorOffset;
	}

	public void setCoorOffset(double coorOffset) 
	{
		this.coorOffset = coorOffset;
	}


}
