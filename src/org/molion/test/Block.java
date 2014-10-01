package org.molion.test;

import java.util.Random;

import org.molion.spawner.Spawnable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Debug;
import android.util.Log;

public class Block implements Spawnable{
	public int size;
	
	int fallingSpeed = 5;
	
	int floor;
	
	int xPos;
	int yPos;
	
	int screenWidth;
	
	Bitmap texture;
	
	public boolean active = true;
	
	public Block(int xPos, int yPos, int floor, int size, int screenWidth, Bitmap texture)
	{
		this.xPos = xPos;
		this.yPos = yPos;
		this.floor = floor;
		this.size = size;
		this.screenWidth = screenWidth;
		this.texture = texture;
	}
	
	public void Update(Block []blocks, Player player)
	{
		yPos += fallingSpeed;
		
		if(yPos + size >= floor)
		{
			yPos = floor - size;
			fallingSpeed = 0;
			active = false;
		}
		
		for(int i = 0; i < blocks.length; i++)
		{
			if(blocks[i] != this && blocks[i] != null)
			{
				for(int v = -1; v <= 1; v++)
				{
					if(Rect.intersects(getRect(v), blocks[i].getRect(0)))
					{
						yPos = blocks[i].yPos - size;
						fallingSpeed = 0;
						active = false;
					}
				}
			}
		}
	}
	
	public void Draw(Canvas canvas, int screenWidth)
	{
		//if(xPos + size > screenWidth)
		//	canvas.drawBitmap(texture, xPos - screenWidth, yPos, null);
		
		canvas.drawBitmap(texture, xPos, yPos, null);
		
	}
	
	public Rect getRect(int version) 
	{
		return new Rect(xPos + (screenWidth * version), yPos, xPos + size + (screenWidth * version), yPos + size); 
	}

	public Spawnable getNew(int xPos, int yPos, Random rand) {
		return new Block(xPos, yPos, floor, size, screenWidth, texture);
	}

	public int getWidth() {
		return size;
	}

	public int getHeight() {
		return size;
	}
}
