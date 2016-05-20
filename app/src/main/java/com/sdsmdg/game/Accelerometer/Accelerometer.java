package com.sdsmdg.game.Accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Rahul Yadav on 5/20/2016.
 */
public class Accelerometer implements SensorEventListener {



    private float aX;

    public float getaX() {
        return aX;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        aX = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
