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

    public static float aB1X;
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
        height = (int) (0.95 * displaymetrics.heightPixels);
        width = displaymetrics.widthPixels;

        Log.i(TAG, "onCreate Starts");
        myView = new MyView(this);
        myView.setBoardOneAtCenter(width / 2, height);
        myView.setBoardTwoAtCenter(width / 2, 0);
        setContentView(myView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        aB1X = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int getB1Direction() {
        if (Math.abs(aB1X) < 1)
            return 0;
        else if (aB1X < 0)
            return 1;
        else
            return -1;
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
                myView.updateB1Center();
                myView.updateB2Center();
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
