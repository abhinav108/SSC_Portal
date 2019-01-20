package com.example.smartsuitcase.ssc_portal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

public class Controls extends AppCompatActivity {
    Button btnOn, btnOff, btnFollowStart, btnFollowStop, btnHighSpeed, btnLowSpeed, btnPrimaryOpen, btnPrimaryClose, btnSecondaryOpen, btnSecondaryClose;
    ImageButton btnLeftButton,btnRightButton, btnStopButton, btnBackButton, btnForwardButton;
    TextView txtArduino, txtString, txtStringLength, sensorView0;
    Handler bluetoothIn;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_controls);

        //Link the buttons and textViews to respective views
        btnOn = (Button) findViewById(R.id.buttonOn);
        btnOff = (Button) findViewById(R.id.buttonOff);

        btnPrimaryOpen = (Button) findViewById(R.id.btnPLOpen);
        btnPrimaryClose = (Button) findViewById(R.id.btnPLClose);


        btnHighSpeed = (Button) findViewById(R.id.btnHSpeed);
        btnLowSpeed = (Button) findViewById(R.id.btnLSpeed);
        btnFollowStart = (Button) findViewById(R.id.btnFStart);
        btnFollowStop = (Button) findViewById(R.id.btnFStop);
        btnBackButton = (ImageButton) findViewById(R.id.btnBack);
        btnForwardButton = (ImageButton) findViewById(R.id.btnForward);
        btnLeftButton = (ImageButton) findViewById(R.id.btnLeft);
        btnRightButton = (ImageButton) findViewById(R.id.btnRight);
        btnStopButton = (ImageButton) findViewById(R.id.btnStop);

        final TextView txtLights = (TextView) findViewById(R.id.txtLight);
        final TextView txtFollows = (TextView) findViewById(R.id.txtFollow);
        final TextView txtSpeeds = (TextView) findViewById(R.id.txtSpeed);
        final TextView txtControls = (TextView) findViewById(R.id.txtControls);
        final TextView txtPrimary = (TextView) findViewById(R.id.txtPrimaryLock);
         final TextView txtWeight = (TextView) findViewById(R.id.txtWeight);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                             //if message is what we want
                    String readMessage = (String) msg.obj;                                  // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    txtWeight.setText(recDataString);     //get sensor value from string between indices 1-5 update the textviews with sensor values
                    recDataString.delete(0,1);                    //clear all string data
                    }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        btnOff.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                mConnectedThread.write("w");    // Send "0" via Bluetooth
                txtLights.setText("Off");
                Toast.makeText(getBaseContext(), "Turn Off Lights", Toast.LENGTH_SHORT).show();
            }
        });

        btnOn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtLights.setText("On");
                mConnectedThread.write("x");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn on Lights", Toast.LENGTH_SHORT).show();
            }
        });
        btnBackButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtControls.setText("Back");
                mConnectedThread.write("e");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Back", Toast.LENGTH_SHORT).show();
            }
        });
        btnForwardButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtControls.setText("Forward");
                mConnectedThread.write("a");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Forward", Toast.LENGTH_SHORT).show();
            }
        });
        btnLeftButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtControls.setText("Left");
                mConnectedThread.write("d");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Left", Toast.LENGTH_SHORT).show();
            }
        });
        btnRightButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtControls.setText("Right");
                mConnectedThread.write("b");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Right", Toast.LENGTH_SHORT).show();
            }
        });
        btnStopButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtControls.setText("Stop");
                mConnectedThread.write("c");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Stop", Toast.LENGTH_SHORT).show();
            }
        });
        btnFollowStart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtFollows.setText("Start");
                mConnectedThread.write("u");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Following a Line.", Toast.LENGTH_SHORT).show();
            }
        });
        btnFollowStop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtFollows.setText("Stop");
                mConnectedThread.write("v");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Stop following a line.", Toast.LENGTH_SHORT).show();
            }
        });
        btnHighSpeed.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtSpeeds.setText("High");
                mConnectedThread.write("y");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "High Speed", Toast.LENGTH_SHORT).show();
            }
        });
        btnLowSpeed.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtSpeeds.setText("Low");
                mConnectedThread.write("z");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Low Speed", Toast.LENGTH_SHORT).show();
            }
        });
        btnPrimaryOpen.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtPrimary.setText("Open");
                mConnectedThread.write("k");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Primary Lock Open", Toast.LENGTH_SHORT).show();
            }
        });
        btnPrimaryClose.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                txtPrimary.setText("Close");
                mConnectedThread.write("l");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Primary Lock Close", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}
