import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class Cactus 
{
	public double x;
	public int y;
	public int width;
	public int height;
	public Rectangle hitbox;
	public int offset = 5;
	public boolean bird;
	double rng;
	BufferedImage image;
	
    public Cactus(int x, int y) 
    {
    	this.x = x;
    	this.y = y;
    	
    	rng = Math.random();
    	
    	if(Math.random() >= 0.92) //0.92
    	{
    		bird = true;
    		this.width = 46;
    		this.height = 34;
    	} else
    	{
    		/*
	    	int[] possibleWidths = new int[]{20, 25, 40, 40, 60, 80};
	    	this.width = possibleWidths[(int)(Math.random() * possibleWidths.length)];
	    	
	    	int[] possibleHeights = new int[]{20, 30, 40, 50, 60, 70};
	    	this.height = possibleHeights[(int)(Math.random() * possibleHeights.length)];
	    	*/
	    	int bruh = (int)(Math.random() * 6 + 1);
	    	try
	    	{
		    	switch(bruh)
		    	{	
		    		case 0:	
					image = removeBG(ImageIO.read(new FileInputStream("./Images/Little1.PNG")));
		    		break;
		    		
		    		case 1:	
					image = removeBG(ImageIO.read(new FileInputStream("./Images/Little2.PNG")));
		    		break;
		    		
		    		case 2:	
					image = removeBG(ImageIO.read(new FileInputStream("./Images/Little3.PNG")));
		    		break;
		    		
		    		case 3:	
					image = removeBG(ImageIO.read(new FileInputStream("./Images/Big1.PNG")));
		    		break;
		    		
		    		case 4:	
					image = removeBG(ImageIO.read(new FileInputStream("./Images/Big2.PNG")));
		    		break;
		    		
		    		case 5:	
					image = removeBG(ImageIO.read(new FileInputStream("./Images/Big4.PNG")));
		    		break;
		    	}
		    	
		    	this.width = image.getWidth();
		    	this.height = image.getHeight();
	    	} catch(Exception e){}
	    	
    	}
    	
    	this.hitbox = new Rectangle(x + offset, y + offset, width - offset, height - offset);	
    }
    
    public String toString()
    {
    	return "X: " + x + "\t" + "Y: " + y + "\t" + "Width: " + width + "\t" + "Height: " + height;
    }
    
    public static BufferedImage removeBG(BufferedImage BG)
    {
        BufferedImage noBG = new BufferedImage(BG.getWidth(), BG.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Color BGColor = new Color(BG.getRGB(0, 0));
        
        if(BG.getWidth() == 59)
        {
        	BGColor = new Color(BG.getRGB(6, 0));
        }
        
        Color white = new Color(BG.getRGB(4, 0));
        
        for(int x = 0; x < BG.getWidth(); x++)
        {
            for(int y = 0; y < BG.getHeight(); y++)
            {
                Color pixel = new Color(BG.getRGB(x, y));
                if(!pixel.equals(BGColor) && !pixel.equals(white)) noBG.setRGB(x, y, pixel.getRGB());
            }
        }
        
        return noBG;
    }
}