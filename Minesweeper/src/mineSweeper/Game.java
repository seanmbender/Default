package mineSweeper;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Game {
	
	private static int width = 20;
	private static int height = 20;
	
	private final int AMOUNT_OF_BOMBS = 40;
	
	private Random random;
	private boolean finish;
	private boolean dead;
	private Board board;
	
	private Tile[][] tiles;
	
	private BufferedImage bomb = ImageLoader.scale(ImageLoader.loadImage("fx/bomb.png"), Tile.getWidth(), Tile.getHeight());
	private BufferedImage flag = ImageLoader.scale(ImageLoader.loadImage("fx/flag.png"), Tile.getWidth(), Tile.getHeight());
	private BufferedImage normal = ImageLoader.scale(ImageLoader.loadImage("fx/normal.png"), Tile.getWidth(), Tile.getHeight());
	private BufferedImage pressed = ImageLoader.scale(ImageLoader.loadImage("fx/pressed.png"), Tile.getWidth(), Tile.getHeight());
	
	public Game(Board board)
	{
		random = new Random();
		tiles  = new Tile[width][height]; 
		this.board = board;
		
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				tiles[x][y] = new Tile(x, y, normal, bomb, pressed, flag);
			}
		}
		
		reset();
		
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
	
	public void clickedLeft(int x, int y)
	{
		if(!dead && !finish)
		{
			int tileX = x/width;
			int tileY = y/height;
			if(!tiles[tileX][tileY].isFlag())
			{
				tiles[tileX][tileY].setOpened(true);
				
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
	
	public void clickedRight(int x, int y)
	{
		if(!dead && !finish)
		{
			int tileX = x/width;
			int tileY = y/height;
			tiles[tileX][tileY].placeFlag();
		}
	}
	
	public void clickedMiddle(int x, int y)
	{
		int numFlags = 0;
		
		if(!dead && !finish)
		{
			int tileX = x/width;
			int tileY = y/height;
			
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
			checkFinish();
		}
	}
	
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
			
			//if((mx >= 0) && (tiles[mx][y].canOpen()) ) open(mx, y);
			//if((gx < width) && (tiles[gx][y].canOpen()) ) open(gx, y);
			//if((my >= 0) && (tiles[x][my].canOpen()) ) open(x, my);
			//if((gy < height) && (tiles[x][gy].canOpen()) ) open(x, gy);
		}
	}
	
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
	}
	
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
		if(dead)
		{
			g.setColor(Color.RED);	
			g.drawString("You Lost!", 30, 30);
			board.stopTimer();
		}
		else if(finish)
		{
			g.setColor(Color.RED);
			g.drawString("You Won!", 30, 30);
			board.stopTimer();
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


}

