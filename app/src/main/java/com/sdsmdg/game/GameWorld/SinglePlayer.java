package com.sdsmdg.game.GameWorld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.sdsmdg.game.Launcher;
import com.sdsmdg.game.R;

/**
 * Created by Rahul Yadav on 6/4/2016.
 */

public class SinglePlayer extends Activity implements SensorEventListener {

    public static float aB1X;
    public static int height, width;
    public static boolean isUpdate;
    public String TAG = "com.sdsmdg.game";
    protected PowerManager.WakeLock mWakeLock;
    RelativeLayout ballonLayout;
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
        singlePlayerView = new SinglePlayerView(this, this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(singlePlayerView);
        isUpdate = false;
        touchDialog();
    }

    public void touchDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = new Dialog(SinglePlayer.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.touch_dialog);
                dialog.show();
                dialog.setCancelable(false);
                Button button_start = (Button) dialog.findViewById(R.id.button_start);
                button_start.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        isUpdate = true;
                    }
                });

            }
        });
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
                singlePlayerView.update(isUpdate);
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);

                    synchronized (surfaceHolder) {
                        if (canvas != null)
                            singlePlayerView.onDraw(canvas);
                    }
                } catch (IllegalArgumentException e) {
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
