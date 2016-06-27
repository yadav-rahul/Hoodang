package com.sdsmdg.game.GameWorld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

import com.sdsmdg.game.Bluetooth.Bluetooth;
import com.sdsmdg.game.Launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Rahul Yadav on 5/20/2016.
 */
public class MultiPlayer extends Activity implements SensorEventListener {

    public static float aB1X;

    public static int directionB2;
    public static int temp;
    static ConnectedThread connectedThread;
    public String TAG = "com.sdsmdg.game";
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Launcher launcher;
    private MultiPlayerView multiPlayerView;

    public MultiPlayer() {
    }

    public MultiPlayer(Launcher launcher) {
        this.launcher = launcher;
    }

    public static String getB1Direction() {
        if (Math.abs(aB1X) < 1) {
            return "0";
        } else if (aB1X > 0) {
            return ("1");
        } else {
            return ("2");
        }
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAutoOrientationEnabled(this, true);
        Bundle bundle = getIntent().getExtras();
        temp = bundle.getInt("orientation");
        if (temp == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        bluetoothSocket = Bluetooth.bluetoothSocket;
        bluetoothDevice = Bluetooth.bluetoothDevice;
        multiPlayerView = new MultiPlayerView(this, this);
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();
        Intent i = new Intent(this, SendService.class);
        startService(i);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(multiPlayerView);

        Launcher.startTime = (System.currentTimeMillis()) / 1000;
    }

    public void popDialog(final int x) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Launcher.winner = x;
                Launcher.check = 1;
                MultiPlayer.this.finish();

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

        setAutoOrientationEnabled(this, false);
        this.finish();
    }

    public static class RenderThread extends Thread {

        private SurfaceHolder surfaceHolder;
        private MultiPlayerView multiPlayerView;
        private boolean isRunning = false;

        public RenderThread(SurfaceHolder surfaceHolder, MultiPlayerView multiPlayerView) {
            this.surfaceHolder = surfaceHolder;
            this.multiPlayerView = multiPlayerView;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            Canvas canvas;

            while (isRunning) {
                multiPlayerView.update();
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);

                    synchronized (surfaceHolder) {
                        if (canvas != null)
                            multiPlayerView.onDraw(canvas);
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

    public static class SendService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            new CountDownTimer(600000, 50) {

                public void onTick(long millisUntilFinished) {
                    String outputText = (getB1Direction());

                    if (outputText != null) {
                        byte[] bytes = outputText.getBytes();
                        connectedThread.write(bytes);
                    }
                }

                public void onFinish() {
                    Toast.makeText(SendService.this, "Time Over", Toast.LENGTH_SHORT).show();
                }
            }.start();
            return Service.START_NOT_STICKY;
        }

        @Override
        public void onDestroy() {
            stopSelf();
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            socket = bluetoothSocket;
            InputStream tempIn = null;
            OutputStream tempOut = null;
            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    final String inputText = new String(buffer, 0, 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                directionB2 = Integer.valueOf(inputText);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}