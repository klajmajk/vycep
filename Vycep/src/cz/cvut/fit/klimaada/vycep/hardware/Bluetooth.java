package cz.cvut.fit.klimaada.vycep.hardware;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import cz.cvut.fit.klimaada.vycep.MainActivity;
import cz.cvut.fit.klimaada.vycep.controller.Controller;

/**
 * Created by Adam on 17. 1. 2015.
 */
public class Bluetooth {
    private static final String LOG_TAG = "Bluetooth";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;

    volatile boolean stopWorker;
    private MainActivity mActivity;
    private ConnectThread mConnectThread;
    private boolean connected = false;
    private boolean connectionInProgress = false;
    private Handler handler = new Handler();
    private boolean pairingInProgress = false;
    private IntentFilter filter = new IntentFilter();

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            pairingInProgress = true;
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if (device.getName().equals("HC-06")) {
                    Log.d(LOG_TAG, "Bluetooth Device Found");
                    connectToDevice(device);
                }
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                Log.d(LOG_TAG, "Device disconnected");
                connected = false;
            }
        }
    };


    public Bluetooth() {
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    }

    public void closeBT() {
        mConnectThread.cancel();
    }

    private BluetoothDevice findBT(Activity activity) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {

            Log.d(LOG_TAG, "No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("HC-06")) {
                    Log.d(LOG_TAG, "Bluetooth Device Found");
                    return device;
                }
            }
        }
        Log.d(LOG_TAG, "Bluetooth Device not found");
        mBluetoothAdapter.startDiscovery();
        return null;
    }

    public void beginListenForData(final MainActivity activity, BluetoothSocket socket) throws IOException {
        final Handler handler = new Handler();
        mmSocket = socket;
        mmSocket.connect();
        mmOutputStream = socket.getOutputStream();
        mmInputStream = socket.getInputStream();
        connectionInProgress = false;
        connected = true;


        Log.d(LOG_TAG, "Bluetooth connected");
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            Log.d(LOG_TAG, "receiving bt data");

                            activity.screenOFFHandler.sendEmptyMessage(0);
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == ':') {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes);
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {

                                            Log.d(LOG_TAG, "Received data" + data);
                                            if (data != null && !data.equals(""))
                                                Controller.getInstanceOf().getArduinoController().serialDataReceived(data + ":");
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }

                            }
                        }
                    } catch (Exception ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendData(String data) throws IOException {
        String msg = data;
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        //myLabel.setText("Data Sent");
    }

    public void checkConnection(MainActivity activity) {
        mActivity = activity;
        if ((connected == false) && !connectionInProgress) connect(mActivity);
    }

    public void connect(MainActivity activity) {
        if (!connectionInProgress) {
            mActivity = activity;
            mActivity.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
            connect();
        }

    }

    private void connect() {
        connectionInProgress = true;
        Log.d(LOG_TAG, "Bluetooth trying connect reconnect:" + connectionInProgress);
        BluetoothDevice device = findBT(mActivity);
        if (device != null) {
            connectToDevice(device);
        } //else reconnect();
    }

    private void connectToDevice(BluetoothDevice device) {
        try {
            mConnectThread = new ConnectThread(device);
            mConnectThread.run();
        } catch (Exception e) {
            reconnect();
        }
    }

    private void reconnect() {
        //Log.d(LOG_TAG, "Connection failed connectionInProgress");
        //TODO smazat komentář
        handler.postDelayed(runnable, 2000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mConnectThread != null) mConnectThread.cancel();
            connect();
        }
    };


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                beginListenForData(mActivity, mmSocket);
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                cancel();

            }

            // Do work to manage the connection (in a separate thread)

        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                stopWorker = true;
                connectionInProgress = false;
                mActivity.unregisterReceiver(mReceiver);
                if (mmOutputStream != null) mmOutputStream.close();
                if (mmInputStream != null) mmInputStream.close();
                if (mmSocket != null) mmSocket.close();
                Log.d(LOG_TAG, "Disconnecting");
            } catch (IOException e) {
            }
        }
    }


}
