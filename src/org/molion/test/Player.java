package org.molion.test;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Player {
	//player attributes
	//--position
	int xPos;
	int yPos;
	
	//--movement
	float dY = 0;
	int dX = 0;
	float maxFallSpeed = 30;
	float jumpSpeed = 20;
	int maxJumpFrames = 12;
	int jumpedFrames = 0;
	boolean hasDoubleJump = true;
	boolean isOnGround = true;
	
	//--texture
	Bitmap texture;
	
	//--size
	int width = 96;
	int height = 144;
	
	//map vars
	int mapWidth;
	int mapHeight;
	
	//enviroment vars
	float gravity = 2f;
	
	//controls
	int tiltMultiplier = 50;
	int minMoveSpeed = 0;

	//death checks
	int xCollision = 0;
	int yCollision = 0;
	
	public boolean alive = true;
	
	public Player(int xPos, int yPos, int mapWidth, int mapHeight)
	{
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		
		int []colors = new int[width*height];
		for(int i = 0; i < colors.length; i++)
		{
			colors[i] = Color.GREEN;
		}
		
		texture = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
	}
	
	public void Update(boolean touch, float tilt, Block []blocks, int waterLevel)
	{
		boolean upCollision = false;
		boolean downCollision = false;
		boolean leftCollision = false;
		boolean rightCollision = false; 
		
		xCollision = 0;
		yCollision = 0;
		
		dX = (int)(tilt*tiltMultiplier);
		if(Math.abs(dX) < minMoveSpeed)
			dX = 0;
		
		dY += gravity;
		
		if(dY > maxFallSpeed)
			dY = maxFallSpeed;
		
		if(touch)
		{	
			if(jumpedFrames < maxJumpFrames && (isOnGround || hasDoubleJump || jumpedFrames > 0))
			{
				dY = -jumpSpeed;
				jumpedFrames++;
				hasDoubleJump = isOnGround;
			}
		}
		else
		{
			jumpedFrames = 0;
		}
		
		isOnGround = false;
		
		xPos += dX;
		yPos += dY;
		
		if(yPos + height >= waterLevel)
		{
			alive = false;
			return;
		}
		
		if(yPos + height >= 0)
		{
			yPos = 0 - height;
			dY = 0;
			isOnGround = true;
			
			yCollision = 1;
		}
		
		for (Block block : blocks) {
			Rect rect;
			if((rect = collides(block)) != null)
			{
				//collision detected
				Vector velocity = new Vector(Math.abs(dX), Math.abs(dY));
				
				if(block.active)
					velocity.y -= block.fallingSpeed;
				
				velocity = velocity.normalize();
				
				Vector collision = getCollisionVector(rect).normalize();
				
				if(velocity.x > collision.x)
				{
					// x-collision
					
					if(xCollision == 0)
						xCollision = (int)Math.signum(dX);
					else if(xCollision != Math.signum(dX))
						alive = false;
					
					if(dX > 0)
					{
						xPos = block.xPos - width;
					}
					else
					{
						xPos = block.xPos + block.size;
					}
				}
				else
				{
					// y-collision
					
					if(yCollision == 0)
						yCollision = (int)Math.signum(dY);
					else if(yCollision != Math.signum(dY))
						alive = false;
					
					if(dY > 0)
					{
						isOnGround = true;
						yPos = block.yPos - height;
					}
					else
					{
						yPos = block.yPos + block.size;
					}
					
					if(block.active)
						dY = block.fallingSpeed;
					else
						dY = 0;
				}
			}
		}
	}
	
	Rect collides(Block block)
	{		
		Rect rect = getRectangle();

		if(rect.intersect(block.getRect(0)))
			return rect;
		
		else if(rect.intersect(block.getRect(1)))
			return rect;
		
		else if(rect.intersect(block.getRect(-1)))
			return rect;
		
		return null;
	}
	
	Vector getCollisionVector(Rect collision)
	{
		Vector vector = new Vector(collision.width(), collision.height());
		
		if(dX > 0)
			vector.x += (xPos + width) - collision.right;
		else
			vector.x += collision.left - xPos;
		
		if(dY > 0)
			vector.y += (yPos + height) - collision.bottom;
		else
			vector.y += collision.top - yPos;
		
		return vector;
	}
	
	public void Draw(Canvas canvas)
	{
		while(xPos < 0)
			xPos += mapWidth;
		while(xPos > mapWidth)
			xPos -= mapWidth;
		
		Paint paint = new Paint();
		
		if(!alive)
		{
			paint.setColor(Color.RED);
		}
		
		canvas.drawBitmap(texture, xPos, yPos, new Paint());
		
		if(xPos + width > mapWidth)
			canvas.drawBitmap(texture, xPos - mapWidth, yPos, new Paint());
	}
	
	public Rect getRectangle()
	{
		return new Rect(xPos, yPos, xPos + width, yPos + height);
	}
	
	public void collisionRecieved(Block block)
	{
		alive = false;
	}
}
