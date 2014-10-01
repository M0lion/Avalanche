package org.molion.spawner;

import java.util.Random;

public interface Spawnable 
{
	//works as constructor
	public Spawnable getNew(int xPos, int yPos, Random rand);

	public int getWidth();
	public int getHeight();
}
