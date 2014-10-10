package mineSweeper;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tile {
	
	private BufferedImage normal;
	private BufferedImage openedImage;
	private BufferedImage bombImage;
	private BufferedImage flagImage;
	
	private int x;
	private int y;
	
	private boolean bomb;
	private boolean opened;
	private boolean flag;
	
	private int proximityCount;
	
	//Set size of individual tiles
	private static int width  = Board.getScreenWidth() / Game.getWidth();
	private static int height  = Board.getScreenWidth() / Game.getHeight();
	
	public Tile(int x, int y, BufferedImage n, BufferedImage b, BufferedImage o, BufferedImage f)
	{
		this.x = x;
		this.y = y;
		this.normal = n;
		this.bombImage = b;
		this.openedImage = o;
		this.flagImage = f;
	}
	
	public void setNormal(BufferedImage n)
	{
		this.normal = n;
	}
	
	public void setOpenedImage(BufferedImage openedImage)
	{
		this.openedImage = openedImage;
	}
	
	public void setOpened(boolean o)
	{
		this.opened = o;
	}
	
	public boolean isOpened()
	{
		return opened;
	}
	
	public void setBomb (boolean bomb)
	{
		this.bomb = bomb;
	}
	
	public boolean isBomb()
	{
		return bomb;
	}
	
	public int getProximityCount()
	{
		return proximityCount;
	}
	
	public void setProximityCount(int x)
	{
		this.proximityCount = x;
	}
	
	public boolean canOpen()
	{
		return !opened && !bomb && proximityCount >= 0;
	}
	
	public void placeFlag()
	{
		if(flag) flag = false;
		else
		{
			if(!opened) flag = true;
		}
	}
	
	public boolean isFlag()
	{
		return flag;
	}
	
	public void reset()
	{
		flag   = false;
		opened = false;
		bomb   = false;
	}
	
	//Logic to handle what appears in each tile location during gameplay
	public void draw(Graphics g)
	{
		if(!opened) 
		{
			if(!flag) g.drawImage(normal, x * width, y * height, null);
			else g.drawImage(flagImage, x * width, y * height, null);
		}
		else 
		{
			if(bomb) g.drawImage(bombImage, x * width, y * height, null);
			else
			{
				g.drawImage(openedImage, x * width, y * height, null);
				if(proximityCount > 0)
				{
					g.setColor(Color.WHITE);
					g.drawString("" + proximityCount, x * width+7, y * height+height-4);
				}
			}
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

