import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class DinoGame extends JFrame implements KeyListener
{	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	Random rand = new Random();
	
	Robot r;

	static int frameRate = 15;
	
	final int FRAME_WIDTH = 700;
	final int FRAME_HEIGHT = 500;
	
	final int SCREEN_WIDTH = (int)screenSize.getWidth();
	final int SCREEN_HEIGHT = (int)screenSize.getHeight();

	boolean upKey = false;
	boolean leftKey = false;
	boolean rightKey = false;
	boolean downKey = false;
	boolean spaceKey = false;
	boolean died = false;
	boolean once = true;
	boolean foodExists = true;
	boolean dinoWalk = false;
	boolean birdFlap = false;
	boolean ducking = false;
	
	int groundY = FRAME_HEIGHT / 3 * 2;
	
	//Dino
	int dinoWidth = 44;
	int dinoHeight = 44;
	int dinoX = 50;
	int dinoY = groundY - dinoHeight;
	int jumpSpeed = -7;

	//Ducking stuff
	final int duckHeight = 29;
	final int duckWidth = 59;
	//hitbox
	Rectangle dinoHitbox = new Rectangle(dinoX, dinoY, dinoWidth, dinoHeight);
	int canJump = 0;
	int maxCanJump = 8;
	
	//Ground hitbox
	Rectangle groundHitbox = new Rectangle(dinoX, groundY, dinoWidth, dinoHeight);
	
	//speed
	double vSpeed = 0;

	//Restart stuff
	final int spawnX = dinoX;
	final int spawnY = dinoY;
	final int spawnWidth = dinoWidth;
	final int spawnHeight = dinoHeight;
	
	//Cactus stuff
	double cactusSpeed = 7;
	int cactiTimer = 0;
	int distanceModifier = 10;
	int spawnDistanceModifier = distanceModifier;
	ArrayList<Cactus> cactusArray = new ArrayList<Cactus>();
	
	//Game stuff
	int score = 0;
	int highscore = 0;
	final double gravity = 0.6;
	int offset = 5;
	
	//Cheat
	int bsCounter = 0;
	
	BufferedImage dinoDead;
	BufferedImage dinoWalk1;
	BufferedImage dinoWalk2;
	BufferedImage dinoDuck1;
	BufferedImage dinoDuck2;
	BufferedImage bird1;
	BufferedImage bird2;

	ArrayList<Cloud> cloudList = new ArrayList<Cloud>(Arrays.asList(new Cloud((int)(Math.random() * FRAME_WIDTH), 0), new Cloud((int)(Math.random() * FRAME_WIDTH), 0), new Cloud((int)(Math.random() * FRAME_WIDTH), 0), new Cloud((int)(Math.random() * FRAME_WIDTH), 0)));
	double cloudSpeed = cactusSpeed / 3 / 2;
	
    public DinoGame(String s) 
    {
        super(s);
        addKeyListener(this);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocation((SCREEN_WIDTH / 2) - (FRAME_WIDTH / 2), 75);
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
		try
		{
			r = new Robot();
			dinoDead = removeBG(ImageIO.read(new FileInputStream("./Images/DinoDead.PNG")));
			dinoWalk1 = removeBG(ImageIO.read(new FileInputStream("./Images/DinoWalk1.PNG")));
			dinoWalk2 = removeBG(ImageIO.read(new FileInputStream("./Images/DinoWalk2.PNG")));
			dinoDuck1 = removeBG(ImageIO.read(new FileInputStream("./Images/DinoDuck1.PNG")));
			dinoDuck2 = removeBG(ImageIO.read(new FileInputStream("./Images/DinoDuck2.PNG")));
			bird1 = removeBG(ImageIO.read(new FileInputStream("./Images/Bird1.PNG")));
			bird2 = removeBG(ImageIO.read(new FileInputStream("./Images/Bird2.PNG")));
		} catch(Exception e){}   
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

    public void paint(Graphics gay)
    {
    	BufferedImage bImage = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
    	Graphics g = bImage.getGraphics();
    	g.setColor(Color.WHITE);
    	g.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
    	
    	//DO STUFF
    	//Draw clouds
    	for(int i = 0; i < cloudList.size(); i++)
    	{
    		g.setColor(new Color(200, 200, 200));
    		g.drawOval(cloudList.get(i).x, cloudList.get(i).y, cloudList.get(i).rad1, cloudList.get(i).rad2);
    	}
    	
    	//Ground
    	g.setColor(Color.GRAY.darker().darker());
   		g.fillRect(0, groundY - 5, FRAME_WIDTH, 1);
    	
   		//Dino
   		if(!died)
   		{
   			if(score % 8 == 0)
	   		{
	   			dinoWalk = !dinoWalk;
	   		}
	   		
	   		if(!ducking)
	   		{
		   		if(dinoWalk)
		   		{
		   			g.drawImage(dinoWalk1, dinoX, dinoY, null);
		   		} else
		   		{
		   			g.drawImage(dinoWalk2, dinoX, dinoY, null);
		   		}	
	   		} else
	   		{
	   			if(dinoWalk)
		   		{
		   			g.drawImage(dinoDuck1, dinoX, dinoY, null);
		   		} else
		   		{
		   			g.drawImage(dinoDuck2, dinoX, dinoY, null);
		   		}		
	   		}	
   		} else
   		{
   			if(dinoY > groundY - spawnHeight)
   			{
   				dinoY = groundY - spawnHeight;
   			}
	   		g.drawImage(dinoDead, dinoX, dinoY, null);
   		}
   		
   		//Bird
		if(score % 10 == 0)
		{
			birdFlap = !birdFlap;
		}
	   	
   		//Draw CACTI
    	for(int i = 0; i < cactusArray.size(); i++)
    	{
    		if(!(cactusArray.get(i).bird))
    		{
   				g.drawImage(cactusArray.get(i).image, (int)cactusArray.get(i).x, cactusArray.get(i).y, null);
    		} else
    		{
	    		if(birdFlap)
	    		{
	   				g.drawImage(bird1, (int)cactusArray.get(i).x, cactusArray.get(i).y, null);
	    		} else
	    		{
	   				g.drawImage(bird2, (int)cactusArray.get(i).x, cactusArray.get(i).y, null);
	    		}
    		}
    	}
    	
		//GUI
		//Score string
    	g.setColor(Color.GRAY.darker());
    	g.setFont(new Font("CourierNew", Font.PLAIN, 15));
    	g.drawString("HI: " + highscore + "     " + "Score: " + score, FRAME_WIDTH - 200, 50);
    	
    	if(died)
    	{
    		g.setFont(new Font("CourierNew", Font.PLAIN, 25));
    		g.drawString("You Died!", FRAME_WIDTH / 2, FRAME_HEIGHT / 4);
    		g.setFont(new Font("CourierNew", Font.PLAIN, 15));
    		g.drawString("Press space to try again", FRAME_WIDTH / 2, FRAME_HEIGHT / 4 + 50);
    	}
    	//DO STUFF
    	
    	gay.drawImage(bImage, 0, 0, null);
    }
    
	public void run()
	{
		if(!died) //Alive
		{
			score++;
			
			//Move and spawn cacti
			moveCacti();
			
			collideFloor();
			collideCactus();
			movement();
			
			//dINO SPEED
			dinoY += vSpeed;
			
			//Update hitboxes
			updateHitboxes();
		} else
		{
			if(spaceKey)
			{
				restart();
			}
		}
	}
	
	public void updateHitboxes()
	{
		//dino
		dinoHitbox = new Rectangle(dinoX + offset + 5, dinoY + offset, dinoWidth - offset - 13, dinoHeight - offset);
		
		//Ground
		groundHitbox = new Rectangle(dinoX, groundY, dinoWidth, dinoHeight);
		
		//Cacti
		for(int i = 0; i < cactusArray.size(); i++)
		{
			cactusArray.get(i).hitbox = new Rectangle((int)cactusArray.get(i).x + offset, cactusArray.get(i).y + offset, cactusArray.get(i).width - offset * 2, cactusArray.get(i).height - offset);
		}
	}
	
	public void collideFloor()
	{
		if(dinoHitbox.y + dinoHitbox.height + vSpeed >= groundHitbox.y)
		{
			//vSpeed = 0;
			vSpeed -= vSpeed;
			dinoY = groundY - dinoHeight + 1;
			canJump = maxCanJump;
		} else
		{
			gravity();
		}
		
		updateHitboxes();
	}
	
	public void collideCactus()
	{
		for(int i = 0; i < cactusArray.size(); i++)
		{
			if(dinoHitbox.intersects(cactusArray.get(i).hitbox))
			{
				died = true;
			}
		}

		updateHitboxes();
	}
	
	public void gravity()
	{
		vSpeed += gravity;
	}
	
	public void movement()
	{
		if(upKey && canJump > 0 && dinoWidth != duckWidth)
		{
			canJump--;
			vSpeed = jumpSpeed;
		} else if(dinoHitbox.intersects(groundHitbox))
		{
			//Ducking
			if(downKey)
			{
				ducking = true;
				dinoWidth = duckWidth;
				dinoY += dinoHeight - duckHeight;
				dinoHeight = duckHeight;
			}
		} else
		{
			//Fast falling
			if(downKey && !(dinoHitbox.intersects(groundHitbox)))
			{
				vSpeed += 1;
			}
			canJump = 0;
		}
	}
	
	public void robotStuff()
	{
		if(bsCounter == 69)
		{
			r.keyPress(KeyEvent.VK_DOWN);
			r.keyRelease(KeyEvent.VK_DOWN);
		}
	}
	
	public void spawnCactus()
	{
		cactusArray.add(new Cactus(FRAME_WIDTH + 100 - (int)(Math.random() * 80), 0));
		cactiTimer = 0;
	}

    public static void main(String[] args)
    {
        DinoGame frame = new DinoGame("weeee");
        
		frame.restart();
		
		while(true)
		{
			frame.robotStuff();

			frame.run();
			frame.info();
			frame.repaint();
			
			try
			{
				Thread.sleep(frameRate);
			}
			catch(Exception e){}	
		}
    }
    
    public void moveCacti()
    {
    	cactiTimer++;
    	
    	if(cactiTimer >= cactusSpeed * (distanceModifier + (int)(Math.random() * 8 - 4)))
    	{
    		spawnCactus();
    	}
    	
    	for(int i = 0; i < cactusArray.size(); i++)
    	{
    		if(!(cactusArray.get(i).bird))
    		{
	    		cactusArray.get(i).x -= cactusSpeed;
	    		cactusArray.get(i).y = groundY - cactusArray.get(i).height;
	    		
	    		if(cactusArray.get(i).x + cactusArray.get(i).width <= 0)
	    		{
	    			cactusArray.remove(i);
	    			cactusSpeed += 0.05;
	    		}
    		} else
    		{
    			cactusArray.get(i).x -= cactusSpeed * 0.9;

    			if(cactusArray.get(i).rng < 0.33)
    			{
    				cactusArray.get(i).y = groundY - cactusArray.get(i).height - 2;
    			} else if(cactusArray.get(i).rng < 0.66)
    			{
    				cactusArray.get(i).y = groundY - spawnHeight - cactusArray.get(i).height / 2;
    			} else
    			{
    				cactusArray.get(i).y = groundY - spawnHeight - cactusArray.get(i).height * 2;
    			}
	    		
	    		
	    		if(cactusArray.get(i).x + cactusArray.get(i).width <= 0)
	    		{
	    			cactusArray.remove(i);
	    			cactusSpeed += 0.05;
	    		}
    		}
    	}
    	
    	//Clouds
    	for(int i = 0; i < cloudList.size(); i++)
    	{
    		cloudList.get(i).x -= cloudSpeed;
    		
    		if(cloudList.get(i).x + cloudList.get(i).rad1 * 4 <= 0)
    		{
    			cloudList.get(i).x = FRAME_WIDTH + (int)(Math.random() * 50);
    			cloudList.get(i).y = 30 + (int)(Math.random() * FRAME_HEIGHT / 3 * 1.25);
    			cloudList.get(i).rad1 = 10 + (int)(Math.random() * 40 + 1);
    			cloudList.get(i).rad2 = 5 + (int)(Math.random() * 10 + 1);
    		}
    	}
    }
	
	public void restart()
	{
		//Cactus stuff
		cactusSpeed = 6;
		cactiTimer = 0;
		distanceModifier = spawnDistanceModifier;
	
		//Dino
		dinoX = spawnX;
		dinoY = spawnY;
		dinoWidth = spawnWidth;
		dinoHeight = spawnHeight;
		//speed
		vSpeed = 0;
		
		if(score > highscore)
		{
			highscore = score;
		}
		
		score = 0;
		cactusArray = new ArrayList<Cactus>();
		died = false;
		
		spawnCactus();
	}
    
    public void info()
    {
    	
    }
    
    @Override
    public void keyTyped(KeyEvent e) 
    {

    }

    @Override
    public void keyPressed(KeyEvent e) 
    {
		//ARROW KEYS
		if (e.getKeyCode() == KeyEvent.VK_UP) 
        {
        	upKey = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) 
        {
        	downKey = true;
	
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) 
        {
        	leftKey = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) 
        {
        	rightKey = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) 
        {
        	spaceKey = true;
        }
        
        if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
        {
        	bsCounter++;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) 
    {
        //ARROW KEYS
		if (e.getKeyCode() == KeyEvent.VK_UP) 
        {
        	upKey = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) 
        {
        	downKey = false;
        	  	
        	if(ducking)
        	{
        		dinoY = groundY - dinoHeight;
        		ducking = false;
        		dinoWidth = spawnWidth;
				dinoHeight = spawnHeight;
        	}
        	
        	if(!(dinoHitbox.intersects(groundHitbox)))
			{
				vSpeed -= 3;
			}
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) 
        {
        	leftKey = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) 
        {
        	rightKey = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) 
        {
        	spaceKey = false;
        }
    }
}