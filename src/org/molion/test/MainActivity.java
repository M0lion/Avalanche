package org.molion.test;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends ActionBarActivity implements SensorEventListener {

	Game game;
	
	SensorManager sensorManager;
	
	float[] acceleration;
	float[] magnetic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
		
		//basic display
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			
		//sensor values
		acceleration = new float[3];
		magnetic = new float[3];
		
		//sensor input
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		
		//running/displaying game
		game = new Game(this);
		setContentView(game);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			game.setTouchEvent(true);
		}
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			game.setTouchEvent(false);
		}
		
		return true;
	}
	
	public void onSensorChanged(SensorEvent event)
	{
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			System.arraycopy(event.values, 0, acceleration, 0, 3);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			System.arraycopy(event.values, 0, magnetic, 0, 3);
			break;
		}
		
		float []R = new float[9];
		SensorManager.getRotationMatrix(R, null, acceleration, magnetic);
		
		float[] orientation = new float[3];
		SensorManager.getOrientation(R, orientation);
		
		float tilt = (float) (orientation[2]);// - ((Math.PI/180) * 90));
		if(Math.abs(tilt - game.tilt) > 0.07)
			game.tilt += (tilt - game.tilt) * 0.18;
		
		if(Math.abs(game.tilt) < Math.toRadians(1))
			game.tilt = 0;
	}
	
	public void onAccuracyChanged (Sensor sensor, int accuracy)
	{
		
	}
}
