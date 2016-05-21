package com.sdsmdg.game.Bluetooth;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.game.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothChat extends AppCompatActivity {

    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    Button sendButton;
    EditText editText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chat);

        bluetoothSocket = MainActivity.bluetoothSocket;
        bluetoothDevice = MainActivity.bluetoothDevice;
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        sendButton = (Button) findViewById(R.id.sendButton);
        final ConnectedThread connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();
        sendButton.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                //Due to NullPointerException
                if (true) {
                    String outputText = editText.getText().toString();
                    byte[] bytes = outputText.getBytes();
                    connectedThread.write(bytes);

                    editText.setText("");
                    Toast.makeText(BluetoothChat.this, "Message has been Send !", Toast.LENGTH_SHORT).show();
                }

            }
        });


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
