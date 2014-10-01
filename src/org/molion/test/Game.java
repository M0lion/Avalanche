package org.molion.test;

import org.molion.spawner.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class Game extends SurfaceView implements Callback, Runnable {
	
	private Thread loopThread;
	
	private int FPS = 60;
	
	private SurfaceHolder holder;
	
	private boolean running = false;
	
	boolean touchEvent = false;
	
	Player player;
	
	public float tilt = 0;
	
	Spawner<Block> spawner;
	
	Block []blockSnapshot;
	
	int spawnOffset = -1000 - getHeight();
	
	int cameraOffset = 0;
	int heightTraveled = 0;
	
	boolean initialize = true;
	
	private int waterStart = 600;
	private float waterLevel;
	private float waterSpeed = -0.5f;
	
	public Game(Context context) {
		super(context);
		
		holder = getHolder();
		holder.addCallback(this);
	}
	
	public Game(Context context, AttributeSet attrs) {
		this(context);
	}

	public void start()
	{
		loopThread.start();
	}
	
	public void pauseAll()
	{
		try {
			loopThread.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void resumeAll()
	{
		loopThread.notify();
	}
	
	public void run()
	{
		running = true;
		
		long frameStart;
		long pauseTime;
		
		long frameTime = 1000/FPS;
		
		while(running)
		{
			frameStart = System.currentTimeMillis();
			Update();
			try
			{
			Draw();
			} catch (NullPointerException e) {
				return;
			}
			
			pauseTime = frameTime - (System.currentTimeMillis() - frameStart);
			
			while(pauseTime < 0)
			{
				Update();
				pauseTime += frameTime;
			}
			
			try {
				Thread.sleep(pauseTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void Update()
	{
		if(!player.alive)
			initialize();
		
		if(cameraOffset > player.yPos + player.height + ((getHeight()/6)*5))
			cameraOffset = player.yPos + player.height + ((getHeight()/6)*5);
		
		if(cameraOffset < player.yPos + player.height + (getHeight()/6))
			cameraOffset = player.yPos + player.height + (getHeight()/6);
		
		if(spawner.yPos > spawnOffset - getHeight() + cameraOffset)
			spawner.yPos = spawnOffset - getHeight() + cameraOffset;
		
		spawner.update();
		blockSnapshot = new Block[spawner.getSpawns().toArray().length];
		spawner.getSnapshot(blockSnapshot);
		updateBlocks();
		player.Update(touchEvent, tilt, blockSnapshot, (int)waterLevel);
		
		if(heightTraveled > player.yPos)
		{
			heightTraveled = player.yPos;
		}
		
		waterLevel += waterSpeed;
	}
	
	void updateBlocks()
	{
		for(int i = 0; i < blockSnapshot.length; i++)
		{
			if(blockSnapshot[i].active)
				blockSnapshot[i].Update(blockSnapshot, player);
		}
	}
	
	public void Draw()
	{
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.BLACK);
		canvas.translate(0, getHeight() - cameraOffset);
		Paint paint = new Paint();
		paint.setColor(Color.rgb(255, 0, 255));
		canvas.drawRect(0, 0, getWidth(), 1000, paint);
		
		player.Draw(canvas);
		drawBlocks(canvas);
		
		paint.setColor(Color.YELLOW);
		canvas.drawRect(0, waterLevel, getWidth(), getHeight(), paint);
		
		holder.unlockCanvasAndPost(canvas);
	}
	
	void drawBlocks(Canvas canvas)
	{
		for(int i = 0; i < blockSnapshot.length; i++)
		{
			blockSnapshot[i].Draw(canvas, getWidth());
		}
	}
	
	public void surfaceCreated(SurfaceHolder holder)
	{		
		//if(initialize)
		{
			initialize = false;
			initialize();			
		}
		
		loopThread = new Thread(this);
		
		start();
	}
	
	void initialize()
	{
		waterLevel = waterStart;
		
		player = new Player(10, -getHeight() / 2, getWidth(), getHeight());
		
		int[] blockSize = {150, 90};
		
		Block []spawnables = new Block[2];
		spawnables[0] = new Block(0, 0, 0, blockSize[0], getWidth(), getColoredSquare(blockSize[0], blockSize[0], Color.rgb(0, 144, 255)));
		spawnables[1] = new Block(0, 0, 0, blockSize[1], getWidth(), getColoredSquare(blockSize[1], blockSize[1], Color.rgb(255, 0, 100)));
		
		spawner = new Spawner<Block>(0, spawnOffset, getWidth(), 1, (long)1000, spawnables);
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		running = false;
		try {
			loopThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			Math.abs(4);
		}
	}
	
	public void setTouchEvent(boolean val)
	{
		touchEvent = val;
	}
	
	Bitmap getColoredSquare(int width, int height, int color)
	{
		int []colors = new int[width * height];
		
		for(int i = 0; i < width * height; i++)
		{
			colors[i] = color;
		}
		
		return Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
	}
}
