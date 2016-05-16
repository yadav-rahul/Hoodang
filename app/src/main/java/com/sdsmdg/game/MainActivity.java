package com.sdsmdg.game;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    /***************************************************************************
     * Initialization of variables
     ****************************************************************************/

    private static final String NAME = "Game";
    private static final String TAG = "com.sdsmdg.game";
    private String UUID = "4cf6a202-fe8e-484e-89f7-0a546739b427";
    private UUID MY_UUID = java.util.UUID.fromString(UUID);
    int REQUEST_ENABLE_BT = 1551;

    /***************************************************************************
     * Initialization of Objects
     ***************************************************************************/
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private ListView pairedListView, unpairedListView;


    /********************************************************************************
     * Default method which launches itself as soon as activity starts
     ********************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pairedListView = (ListView) findViewById(R.id.pairedListView);
        unpairedListView = (ListView) findViewById(R.id.unpairedListView);

        //Check if Bluetooth is available on the device
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "No Bluetooth available !", Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /********************************************************************************
          On find devices button Clicked
     *******************************************************************************/

    public void onFindDevicesClicked(View view) {

        //First check if some paired devices are available or not
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            pairedListView.setAdapter(arrayAdapter);
        }
        mBluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

    }

    /********************************************************************************
     Broadcast receiver to discover bluetooth devices available in range
     *******************************************************************************/
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                unpairedListView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
            }
        }
    };

    /********************************************************************************
     Stop listening for Intents as soon as activity gets destroyed
     *******************************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
    


}


