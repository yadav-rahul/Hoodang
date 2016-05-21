package com.sdsmdg.game.GameWorld;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;

import com.sdsmdg.game.MyView;

/**
 * Created by Rahul Yadav on 5/20/2016.
 */
public class GameWorld extends Activity implements SensorEventListener {

    public static float aX;
    public static float aY;
    public static int height, width;
    public String TAG = "com.sdsmdg.game";
    private SensorManager sensorManager;
    private Sensor sensor;
    private MyView myView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = (int) (0.97 * displaymetrics.heightPixels);
        width = displaymetrics.widthPixels;

        Log.i(TAG,"onCreate Starts");
        myView = new MyView(this);
        myView.setBoardAtCenter(width/2,height);
        setContentView(myView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        aX = event.values[0];
        aY = event.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    public static class RenderThread extends Thread {

        public String TAG = "com.sdsmdg.game";

        private SurfaceHolder surfaceHolder;
        private MyView myView;
        private boolean isRunning = false;

        public RenderThread(SurfaceHolder surfaceHolder, MyView myView) {
            this.surfaceHolder = surfaceHolder;
            this.myView = myView;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        @Override
        public void run() {
            Log.i(TAG, "Thread running");
            Canvas canvas;

            while (isRunning) {
                myView.updateBoardCenter();
                canvas = null;
                try {


                    canvas = surfaceHolder.lockCanvas(null);

                    synchronized (surfaceHolder) {
                        if (canvas != null)
                            myView.onDraw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
