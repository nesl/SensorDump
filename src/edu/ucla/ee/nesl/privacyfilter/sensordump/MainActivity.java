package edu.ucla.ee.nesl.privacyfilter.sensordump;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	private static final String TAG = "Activity";
	private static HashSet<Integer> pool;

	private class SensorDisplayer implements SensorEventListener { // {{{
		private MainActivity parent;
		private Sensor sensor;
		private long count;
		private long start;
		
		public SensorDisplayer (MainActivity act, Sensor baseSensor) {
			parent = act;
			this.sensor = baseSensor;
			count = 0;
			start = System.nanoTime();
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
//			if (event.values[0] == -this.sensor.getType()) {
//				Log.d(TAG, "constant works for " + this.sensor.getType());
//			}
			count++;
			//Log.d(TAG, "for sensor " + sensor.getName() + " count=" + count);
			if (count >= 10000) {
				long now = System.nanoTime();
				Log.d(TAG, "time to get 10000 values for Sensor " + sensor.getName() + "=" + ((double)(now - start) / 1000000000.0));
				parent.sensorManager.unregisterListener(this);
				count = 0;
			}
		}

	} // }}}
	
	private SensorManager sensorManager;
	private ArrayList<Sensor> allSensors;

	// update the display, showing a page-worth of sensors, starting at the index given by currentlyShowingFrom
	private void updateDisplay() {
		// start displaying a page of sensors
		for (Sensor s:allSensors) {
			if (!pool.contains(s.getType())) {
				SensorDisplayer sd = new SensorDisplayer(this, s);
				sensorManager.registerListener(sd, s, SensorManager.SENSOR_DELAY_FASTEST);
				//Log.d(TAG, "sensor name=" + s.getName() + ", sensor type=" + s.getType());
				pool.add(s.getType());
			}
		}
	}

	private void initializeData () {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		allSensors = new ArrayList<Sensor>(sensorManager.getSensorList(Sensor.TYPE_ALL));
		Log.d(TAG, "sensor size=" + allSensors.size());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pool = new HashSet<Integer>();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onBtnClicked(View v){
		if (v.getId() == R.id.button1) {
			initializeData();		
			updateDisplay();
		}
	}
	
}

