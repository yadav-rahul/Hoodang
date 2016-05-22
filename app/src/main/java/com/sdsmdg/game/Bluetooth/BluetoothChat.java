package com.sdsmdg.game.Bluetooth;


import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.game.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothChat extends AppCompatActivity {

    private static final String TAG = "com.sdsmdg.game";
    public static EditText editText;
    static ConnectedThread connectedThread;
    public TextView textView;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chat);

        bluetoothSocket = MainActivity.bluetoothSocket;
        bluetoothDevice = MainActivity.bluetoothDevice;

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);


        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();

        Intent i = new Intent(getApplicationContext(), SendService.class);
        startService(i);

    }

    public static class SendService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i(TAG, "onStartCommand called");
            new CountDownTimer(60000, 100) {

                public void onTick(long millisUntilFinished) {
                    if (true) {
                        String outputText = editText.getText().toString();
                        if (outputText != null) {
                            byte[] bytes = outputText.getBytes();

                            connectedThread.write(bytes);
                        }
                    }
                }

                public void onFinish() {
                    Log.i(TAG, "Count Down finish");
                    Toast.makeText(SendService.this, "Time Over", Toast.LENGTH_SHORT).show();
                }
            }.start();

            return Service.START_STICKY;
        }


        @Override
        public void onDestroy() {
            Log.i(TAG, "onDestroy called");
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
        Handler handler;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {


            socket = bluetoothSocket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
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

                    final String inputText = new String(buffer, 0, bytes);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (inputText != null) {
                                textView.setText(inputText);
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
