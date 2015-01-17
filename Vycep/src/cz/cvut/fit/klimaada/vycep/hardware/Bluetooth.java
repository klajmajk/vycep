package cz.cvut.fit.klimaada.vycep.hardware;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
    private static final String DEVICE_NAME = "HC-06";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;

    private int counter;
    volatile boolean stopWorker;
    private MainActivity mActivity;

    private boolean findBT(Activity activity) {
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
                if (device.getName().equals(DEVICE_NAME)) {
                    mmDevice = device;
                    Log.d(LOG_TAG, "Bluetooth Device Found");
                    return true;
                }
            }
        }
        return false;
    }

    public void openBT(MainActivity activity) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        //mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);

        mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData(activity);

        Log.d(LOG_TAG, "Bluetooth Opened");
    }

    public void beginListenForData(final MainActivity activity) {
        final Handler handler = new Handler();

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
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    public void sendData(String data) throws IOException {
        String msg = data;
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        //myLabel.setText("Data Sent");
    }

    public void closeBT() throws IOException {
        stopWorker = true;
        if (mmOutputStream != null) mmOutputStream.close();
        if (mmInputStream != null) mmInputStream.close();
        if (mmSocket != null) mmSocket.close();

        Log.d(LOG_TAG, "Bluetooth Closed");
        //myLabel.setText("Bluetooth Closed");
    }

    public void connect(MainActivity activity) {
        mActivity = activity;
        if (findBT(activity)) {
            try {
                openBT(activity);
            } catch (Exception e) {
                reconnect();
            }
        } else reconnect();
    }

    private void reconnect() {
        Log.d(LOG_TAG, "Connection failed reconnecting");
        handler.postDelayed(runnable, 1000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                closeBT();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connect(mActivity);
        }
    };


    private Handler handler = new Handler();


}
