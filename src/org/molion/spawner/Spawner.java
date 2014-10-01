package org.molion.spawner;

import java.util.ArrayList;
import java.util.Random;

public class Spawner<T extends Spawnable>
{	
	ArrayList<T> spawns;
	
	//list of possible objects to spawn
	T []spawnables;
	
	long spawnInterval; //in milliseconds
	long lastSpawn = 0;
	
	public int yPos;
	public int height;
	
	public int xPos;
	public int width;
	
	Random rand;
	
	public Spawner(int xPos, int yPos, int width, int height, long spawnInterval, T []spawnables)
	{
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		this.spawnInterval = spawnInterval;
		this.spawnables = spawnables;
		
		spawns = new ArrayList<T>();
		
		rand = new Random();
	}
	
	public Spawner(int xPos, int yPos, int width, int height, int spawnInterval, T []spawnables, int seed)
	{
		this(xPos, yPos, width, height, spawnInterval, spawnables);
		rand = new Random(seed);
	}
	
	public void spawn()
	{
		int spawn = rand.nextInt(spawnables.length);
		int x = rand.nextInt(width - spawnables[spawn].getWidth()) + xPos;
		int y = rand.nextInt(Math.max(1, height - spawnables[spawn].getHeight())) + yPos;
		
		spawns.add((T)spawnables[spawn].getNew(x, y, rand));
	}
	
	public void update()
	{
		long time = System.currentTimeMillis();
		
		if(time - lastSpawn >= spawnInterval)
		{
			spawn();
			lastSpawn = time;
		}
	}
	
	public ArrayList<T> getSpawns()
	{
		return spawns;
	}
	
	public void getSnapshot(T[] snapshot)
	{
		spawns.toArray(snapshot);
	}
}

