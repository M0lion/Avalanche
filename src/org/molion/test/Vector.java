package org.molion.test;

public class Vector {
	public float x;
	public float y;
	
	public Vector(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector normalize()
	{
		float len = length();
		
		return new Vector(x/len, y/len);
	}
	
	public float length()
	{
		return (float)Math.sqrt((x*x) + (y*y));
	}
}
