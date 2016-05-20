package com.sdsmdg.game.GameWorld;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.sdsmdg.game.Accelerometer.Accelerometer;

/**
 * Created by Rahul Yadav on 5/20/2016.
 */
public class GameWorld extends AppCompatActivity {

    Accelerometer accelerometer;

    SensorManager sensorManager;
    Sensor sensor;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }

    public void initializeSensor(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
}
