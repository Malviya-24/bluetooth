package com.example.nandy_ble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {

    Button listen, send, listDevices;
    ListView listView;
    TextView msg_box, status;
    EditText writeMsg;
    int REQUEST_ENABLE_BT = 1;
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewByIdes();
        // Use this check to determine whether Bluetooth classic is supported on the device.
// Then you can selectively disable BLE-related features.
        boolean bluetoothAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        if (bluetoothAvailable) {
            Toast.makeText(this, "BLUETOOTH IS AVAILABLE", Toast.LENGTH_SHORT).show();
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                // Device doesn't support Bluetooth
                Toast.makeText(this, "BLUETOOTH ADAPTER IS NOT AVAILABLE", Toast.LENGTH_SHORT).show();
            } else {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 1);
                        return;
                    }
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    Toast.makeText(this, "BLUETOOTH IS ENABLED", Toast.LENGTH_SHORT).show();

                }
                status.setText("BLUETOOTH ENABLED");
                if (ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            2);
                }
            }
        } else {
            Toast.makeText(this, "BLUETOOTH IS NOT AVAILABLE", Toast.LENGTH_SHORT).show();
            return;
        }

        // implementListeners();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "bluetooth Error getting permission", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "location Permission approved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "location Error getting permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void implementListeners() {
        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("MissingPermission") Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                BluetoothDevice[] btArray = new BluetoothDevice[bt.size()]; // Initialize btArray with the size of bonded devices
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("CONNECTION FAILED");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    status.setText("MESSAGE RECEIVED");
                    break;
            }
            return false;

        }
    });

    private void findViewByIdes() {
        listen = (Button) findViewById(R.id.listen);
        send = (Button) findViewById(R.id.send);
        status = (TextView) findViewById(R.id.status);
    }
}


