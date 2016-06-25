package com.sdsmdg.game.GameWorld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.sdsmdg.game.Launcher;
import com.sdsmdg.game.LeaderBoard.LocalDB.DBHandler;
import com.sdsmdg.game.LeaderBoard.LocalDB.Profile;
import com.sdsmdg.game.R;

/**
 * Created by Rahul Yadav on 6/4/2016.
 */

public class SinglePlayer extends Activity implements SensorEventListener {

    public static float aB1X;
    public static int height, width;
    public static boolean isUpdate;
    public Dialog dialog;
    public String TAG = "com.sdsmdg.game";
    protected PowerManager.WakeLock mWakeLock;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void touchDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final DBHandler dbHandler = new DBHandler(getApplicationContext());

                dialog = new Dialog(SinglePlayer.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.touch_dialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                dialog.show();

                final EditText userName = (EditText) dialog.findViewById(R.id.user_name);
                final Button start_button = (Button) dialog.findViewById(R.id.start_button);

                if (dbHandler.checkDatabase()) {

                    start_button.setEnabled(false);
                    userName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.toString().trim().length() == 0) {
                                start_button.setEnabled(false);
                            } else {
                                start_button.setEnabled(true);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                } else {
                    userName.setText(dbHandler.getUserName());
                    userName.setEnabled(false);
                    start_button.setEnabled(true);
                }

                start_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //Initialize a score of zero.
                        if (dbHandler.checkDatabase()) {
                            Profile profile = new Profile(userName.getText().toString(), 0);
                            dbHandler.addProfile(profile);
                        }
                        Launcher.startTime = (System.currentTimeMillis()) / 1000;
                        isUpdate = true;
                        singlePlayerView.initializeBallVelocity(SinglePlayer.width, SinglePlayer.height);

                        dialog.dismiss();
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
        this.finish();

        Log.i(TAG, "onPause called");
    }

    @Override
    public void onBackPressed() {
        if (dialog.isShowing()) {
            dialog.cancel();
        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        this.finish();
        Log.i(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
