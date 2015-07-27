package com.example.musicshare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;










import java.util.Arrays;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ThreadToBeConnected extends Thread {
    private final BluetoothSocket socket;
    private final InputStream instream;
    private final OutputStream opstream;
    private String buffer = "";
    public Context application_context = null;
    String usr_role = "";
    boolean sent_done = false;
    boolean fetched_file = false;
    
    
	public ThreadToBeConnected(BluetoothSocket tsocket, Context con) {
		
		application_context = con;
		socket = tsocket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
            
            
        } catch (IOException e) { }
 
        instream = tmpIn;
        opstream = tmpOut;
    }
 
    public void run() {
    	int bsize = 1024;
        byte[] bufferer = new byte[bsize];  // bufferer store for the stream
        int bytes;// bytes returned from read()
        
        
        byte[] files = new byte[1024];
        
        SharedPreferences sharedPreferences = PreferenceManager
    			.getDefaultSharedPreferences(application_context);
		usr_role = sharedPreferences.getString("role", "");
		
        while (true) {
            try {
                // Read from the InputStream
           			if(usr_role.equalsIgnoreCase("receiver")){
           				while((bytes = instream.read(bufferer, 0, bufferer.length)) != -1){
           					
               					Reciever.messageRefresh(bufferer);
           				}
           				instream.close();
           			}else if(usr_role.equalsIgnoreCase("relay")){
           				while((bytes = instream.read(bufferer)) != -1){
           					Relay.relayMessage(bufferer);
           				}
           				instream.close();
           			}else if(usr_role.equalsIgnoreCase("sender")){
           				while((bytes = instream.read(bufferer)) != -1){
           				}
           				instream.close();
           			}
            	
         } catch (IOException e) {
                break;
            }
        }
    }
    
    
    public void receiveFiles() {
        try {
            byte[] files = new byte[1024];
			instream.read(files);
			Reciever.getFiles(files);
        } catch (IOException e) { }
        
    }
    
    public void fileNamesRelay(){
    	int bytes = 0;
    	byte[] bufferer = new byte[1024];
    	try{
			while((bytes = instream.read(bufferer)) != -1){
					Relay.refreshMessage(bufferer);
				}
				instream.close();
    	}
    	catch(IOException e){
    		
    	}
    }
    
    public void receiveFileName() {
        try {
            byte[] fileName = new byte[1024];
			int size = instream.read(fileName);
			byte[] sendingFileName = new byte[size];
			for(int i = 0; i < size; i++){
				sendingFileName[i] = fileName[i];
			}
			Sender.fileNameToBeSent(sendingFileName);
        } catch (IOException e) { }
        
    }
    
    
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {		
                opstream.write(bytes);
                
        } catch (IOException e) { }
        
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
        	socket.close();
        } catch (IOException e) { }
    }

	public void writeFileNames(byte[] bytes) {
		try {
			opstream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeFile(byte[] bytes) {
		// TODO Auto-generated method stub
		try {
			opstream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
}
