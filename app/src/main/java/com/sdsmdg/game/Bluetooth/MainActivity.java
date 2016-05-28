package com.sdsmdg.game.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sdsmdg.game.GameWorld.GameWorld;
import com.sdsmdg.game.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public final static UUID MY_UUID = UUID.fromString("4cf6a202-fe8e-484e-89f7-0a546739b427");
    /***************************************************************************
     * Initialization of Objects
     ***************************************************************************/
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothSocket bluetoothSocket;
    public static BluetoothDevice bluetoothDevice;
    /***************************************************************************
     * Initialization of variables
     ****************************************************************************/

    public Context ctx = MainActivity.this;
    int REQUEST_ENABLE_BT = 1551;//Any random number(>0)
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private ListView unpairedListView;
    /********************************************************************************
     * Broadcast receiver to discover bluetooth devices available in range
     *******************************************************************************/

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                unpairedListView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
            }
        }
    };

    public MainActivity() {

    }

    public Context getCtx() {
        return ctx;
    }

    public void startChat(int x) {
        Intent i = new Intent(this, GameWorld.class);
        i.putExtra("orientation", x);
        startActivity(i);

    }

    /********************************************************************************
     * Default method which launches itself as soon as activity starts
     ********************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        unpairedListView = (ListView) findViewById(R.id.unpairedListView);

        //Check if Bluetooth is available on the device
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Sorry, Your device doesn't support Bluetooth !", Toast.LENGTH_SHORT).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_ENABLE_BT == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                Toast.makeText(MainActivity.this, "Bluetooth Successfully Enabled", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.startDiscovery();

                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);

                AcceptThread acceptThread = new AcceptThread();
                acceptThread.start();

            } else {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        unpairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceInfo = (String) unpairedListView.getItemAtPosition(position);
                BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceInfo.substring(deviceInfo.length() - 17));

                SendThread sendThread = new SendThread(bluetoothDevice);
                sendThread.start();
            }
        });


    }

    /********************************************************************************
     * Stop listening for Intents as soon as activity gets destroyed
     *******************************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    private class AcceptThread extends Thread {

        private static final String NAME = "Game";
        private static final String TAG = "com.sdsmdg.game";
        private final BluetoothServerSocket bluetoothServerSocket;


        public AcceptThread() {
            BluetoothServerSocket temp = null;
            try {
                temp = MainActivity.mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MainActivity.MY_UUID);
            } catch (IOException e) {
            }

            bluetoothServerSocket = temp;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;

            while (true) {
                //This loop will continue until someone other tried to make a connection or an exception occurs.
                try {
                    socket = bluetoothServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                if (socket != null) {
                    //Now we got the socket and it's the time to send it to the separate thread.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "LETS PLAY DUDE !", Toast.LENGTH_SHORT).show();
                        }
                    });

                    MainActivity.bluetoothDevice = socket.getRemoteDevice();
                    MainActivity.bluetoothSocket = socket;

                    startChat(-1);

                    try {
                        bluetoothServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        public void cancel() {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class SendThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;


        public SendThread(BluetoothDevice device) {

            BluetoothSocket temp = null;
            bluetoothDevice = device;

            try {
                temp = bluetoothDevice.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothSocket = temp;
        }

        @Override
        public void run() {
            MainActivity.mBluetoothAdapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e.printStackTrace();
                }
            }

            MainActivity.bluetoothSocket = bluetoothSocket;
            MainActivity.bluetoothDevice = bluetoothDevice;
            startChat(1);
        }
    }
}
