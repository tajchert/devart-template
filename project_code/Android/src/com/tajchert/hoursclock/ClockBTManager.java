package com.tajchert.hoursclock;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ClockBTManager extends Thread {

	private static final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static BluetoothSocket socket = null;
	
	public static synchronized void close(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized boolean open(int i){
		if(i>5)
			return false;
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice arduinoBT = btAdapter.getRemoteDevice("20:13:11:05:12:66");
		//BluetoothDevice arduinoBT = btAdapter.getRemoteDevice("B4:52:7D:F1:AD:14");
		//BluetoothDevice arduinoBT = btAdapter.getRemoteDevice("D0:FF:50:67:83:D7");
		socket = null;
		try {
			btAdapter.cancelDiscovery();
			try {
				socket.close();
			} catch (Exception e) {
			}
			socket = arduinoBT.createInsecureRfcommSocketToServiceRecord(APP_UUID);
			socket.connect();
			return true;
		} catch (Exception e) {
			return false;
			//return open(i++);
			//e.printStackTrace();
			//return false;
		}
	}

	public static synchronized void write(String input) {
		input = "<" + input +">";
		Log.d(Data.AWESOME_TAG, "WRITE: " + input);
		if(socket == null){
			return;
		}
		try {
			OutputStream os = socket.getOutputStream();
			os.write(input.getBytes(Charset.forName("ASCII")));
			os.flush();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
