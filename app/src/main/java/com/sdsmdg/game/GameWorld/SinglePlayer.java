package com.sdsmdg.game.GameWorld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.sdsmdg.game.Launcher;

/**
 * Created by Rahul Yadav on 6/4/2016.
 */
public class SinglePlayer extends Activity implements SensorEventListener{

    public static float aB1X;
    public static int height, width;

    public String TAG = "com.sdsmdg.game";
    private SensorManager sensorManager;
    private Sensor sensor;
    private SinglePlayerView singlePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = (displaymetrics.heightPixels);
        width = displaymetrics.widthPixels;
        
        singlePlayerView = new SinglePlayerView(this,this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(singlePlayerView);
    }

    public void popDialog(final int x) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Launcher.winner = x;
                Launcher.isDialog = true;
                SinglePlayer.this.finish();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        aB1X = event.values[0];
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

        this.finish();
    }

    public static class RenderThread extends Thread {

        private SurfaceHolder surfaceHolder;
        private SinglePlayerView singlePlayerView;
        private boolean isRunning = false;

        public RenderThread(SurfaceHolder surfaceHolder, SinglePlayerView singlePlayerView) {
            this.surfaceHolder = surfaceHolder;
            this.singlePlayerView = singlePlayerView;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            Canvas canvas;

            while (isRunning) {
                singlePlayerView.update();
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);

                    synchronized (surfaceHolder) {
                        if (canvas != null)
                            singlePlayerView.onDraw(canvas);
                    }
                } catch (IllegalArgumentException e){                }

                finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
