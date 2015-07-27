package com.example.musicshare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import com.example.musicshare.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Sender extends Activity{
	private static final UUID MY_UUID = UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB");
	private final static int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter bluetooth_adapter;
	private ArrayAdapter<String> array_adapter;
	private static final int REQUEST_PICK_FILE = 1;
	private ListView list;
	public String[] pairlist;
	private static ThreadToBeConnected rd_wrt;
	TextView selected_path;
	byte[] bytes;
	String path;
	boolean start_play = false;
	static InputStream ipstream;
	private ArrayList<File> fileList = new ArrayList<File>();
	String flname = "";
	static String allFiles = "";
	File rootFile;
	boolean send_file = false;
	
	static TextView send_byte;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sender);
		bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();
		array_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		pairlist = new String[10];
		
		list = (ListView) findViewById(R.id.listView1);
//		selected_path = (TextView) findViewById(R.id.textViewpath);
		list.setAdapter(array_adapter);
		
		
		send_byte = (TextView) findViewById(R.id.textViewBytesSent);
		
		rootFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		
		allFiles = getfile(rootFile);
		
//		rd_wrt.start();
		
		//this.setListAdapter(array_adapter);
		 
		if (bluetooth_adapter == null) {
		    // Device does not support Bluetooth
		}
		if (!bluetooth_adapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		showpairlist();
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				//Toast toast = Toast.makeText(getBaseContext(), pairlist[position], 3);
				//toast.show();
				createSocket(pairlist[position]);
			}
		});
	}
	
	@SuppressLint("NewApi")
	protected void createSocket(String devAddr){
		BluetoothSocket mSocket = null;
		BluetoothDevice device = bluetooth_adapter.getRemoteDevice(devAddr);
		Toast toast;
		//toast = Toast.makeText(getBaseContext(), device.getName(), 1);
		//toast.show();
		try{
			mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
		}
		catch(IOException e){
			toast = Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT);
			toast.show();
		}
		try{
			mSocket.connect();
		}
		catch(IOException closeException){ 
			toast = Toast.makeText(getBaseContext(), closeException.toString(), Toast.LENGTH_SHORT);
			toast.show();
		}
		manageConnectedSocket(mSocket);
		//BluetoothSocket mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
	}
	
	private void manageConnectedSocket(BluetoothSocket mSocket){
		Toast.makeText(getBaseContext(), "Ready to send messages", Toast.LENGTH_SHORT).show();
		rd_wrt = new ThreadToBeConnected(mSocket, this);

		rd_wrt.writeFileNames(allFiles.getBytes());
		
		int sec = (int) System.currentTimeMillis();
		int newsec = 0;
		while(newsec != sec+30000)
		{
			newsec = (int) System.currentTimeMillis();
		}
		
//		rd_wrt.receiveFileName();
		fileNameSendAgain(); 	
		
//		while(true){
//			if(send_file){
//				break;
//			}
//		}
	}
	
	/*public void fetchFile(View view){
		Intent intent = new Intent(this, BrowseFolder.class);            
        startActivityForResult(intent, REQUEST_PICK_FILE);
    }*/
	
	public void sendFile(View view){
		
		send_file = true;
		start_play = true;
//		rd_wrt.write(bytes);
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
        if(resultCode == RESULT_OK) {
        	
            switch(requestCode) {
            
            case REQUEST_PICK_FILE:
            	
                              break;
            }
         }
             
        }
	
	
	private void startSendingFile() {
		// TODO Auto-generated method stub
	}

	
	
	public String getfile(File dir) {
		File listFile[] = dir.listFiles();
		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; i++) {
/*				if (listFile[i].isDirectory()) {	
					if (listFile[i].getName().endsWith(".mp3")){
						fileNames = fileNames + "," + listFile[i];
					}
					getfile(listFile[i]);
				} else {
*/					if (listFile[i].getName().endsWith(".mp3")){
						flname = flname + "," + listFile[i];
					}
//				}
			}
		}
		return flname;
	}
	
	
	
	
	private void showpairlist(){
		Set<BluetoothDevice> pairlistDevices = bluetooth_adapter.getBondedDevices();
		// If there are pairlist devices
		if (pairlistDevices.size() > 0) {
		    // Loop through pairlist devices
			int cnt = 0;
		    for (BluetoothDevice device : pairlistDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		        array_adapter.add(device.getName() + "\n" + device.getAddress());
		        pairlist[cnt] = device.getAddress();
		        cnt++;
		    }
		}
	}

	public static void fileNameToBeSent(byte[] fileName){
		
	}
	
	public static void fileNameSendAgain() {
		// TODO Auto-generated method stub
		
//		int size = fileName.length;
		
//		String fileNamePlayPath = "";
//		String fileNam = "";
		
		String fileReceived = allFiles;
		
		String filename = fileReceived.substring(fileReceived.lastIndexOf("/")+1);
		
		Log.e("Mess", filename);
		
		
    	File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
    	
		File aFile = new File(dir, filename.trim());
		
		double lengthOfFile = aFile.length();
		double kilobytes = (lengthOfFile / 1024);
		double megabytes = (kilobytes / 1024);
		
		/*File listFile[] = dir.listFiles();
		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].isDirectory()) {	
					if (listFile[i].getName().endsWith(".mp3")){
						fileNames = fileNames + "," + listFile[i];
					}
					getfile(listFile[i]);
				} else {
					if (listFile[i].getName().endsWith(".mp3")){
						fileNam = "" + listFile[i];
						Log.e("Mess", listFile[i].toString());
						
						System.out.println(listFile[i].toString());
					}
//				}
			}
		}
		
		if (fileNam.equals(fileReceived)){
			System.out.println(fileName);
			System.out.println(fileReceived);
			System.out.println("Hello");
		}*/
		
		
//        File aFile = new File(fileReceived);		
		
//    	File dir = Environment.getExternalStorageDirectory();
    	
//    	String path = "/sdcard/";
    	
//    	File dir = Environment.getExternalStorageDirectory();
        //File yourFile = new File(dir, "path/to/the/file/inside/the/sdcard.ext");
 
        //Get the text file

    	
/*    	File listFile[] = dir.listFiles();
		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; i++) {
					if (listFile[i].getName().toString().equals(filename)){
						fileNamePlayPath = listFile[i].toString();
					}
//				}
			}
		}
*/    	
		
    	
//		File aFile = new File (fileReceived);
		
    	try {
    		ipstream = new FileInputStream(aFile);
			Thread thread = new Thread(){
	    		@Override
	    	    public void run() {
	    			ByteArrayOutputStream bos = null;
	    			try {
	    				bos = new ByteArrayOutputStream();
	    				int bytesRead = 0;

    					double finalBytesRead = 0;
    					
//    					int charCounter = 0;
    					

    					int bsize = 1024;
	    				byte[] b = new byte[bsize];
	    				while ((bytesRead = ipstream.read(b)) != -1) {
	    					rd_wrt.write(b);
	    						    					
	    					//Counting bytes sent
	    					finalBytesRead += bytesRead;
	    					final double finalRead = finalBytesRead;
	    		    		final double kilobytes = (finalRead / 1024);
	    					final double megabytes = (kilobytes / 1024);
	    					
	    					send_byte.post(new Runnable() {
	    						public void run() {
	    							send_byte.setText("Number of Bytes Sent = " + megabytes);
	    						}
	    					});
	    					
	    				}
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    	    }
	    	};
	        thread.start();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}