package com.example.musicshare;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.example.musicshare.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class Reciever extends Activity{
	private BluetoothServerSocket serverSocket;
	private BluetoothAdapter bluutooth_adapter;
	private static final UUID dev_UUID = UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB");
	private String NAME = "MusicShare";
	private String con;
	private TextView conState;
	private static TextView readyFile;
	private ThreadToBeConnected rd_wrt;
	private static TextView recv_message;
	static SharedPreferences sharedPreferences;
	private ListView file_list;
	static String msg = "";
	private static ArrayAdapter<String> arr_adapter_file;
	static Reciever recieverAction;
	MediaPlayer player;
	static File temp_music;
	static FileOutputStream file_op;
	static FileInputStream file_in;
	SharedPreferences.Editor editor;
	String fileName = "";
	static boolean Received_file = false;
	
	static TextView file_len;
	
	
	AudioManager aud_manager;

	
	public static Reciever getInstance(){
		recieverAction = new Reciever();   
		return   recieverAction;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reciever);
		conState = (TextView) findViewById(R.id.connection);
		readyFile = (TextView) findViewById(R.id.textViewFileReady);
		readyFile.setVisibility(View.INVISIBLE);
		recv_message = (TextView) findViewById(R.id.incoming);

		file_len = (TextView) findViewById(R.id.textViewFileLength);
		
		arr_adapter_file = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

		file_list = (ListView) findViewById(R.id.listViewFiles);
		file_list.setAdapter(arr_adapter_file);
		
		
		bluutooth_adapter = BluetoothAdapter.getDefaultAdapter();

		aud_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		aud_manager.startBluetoothSco();
		
		aud_manager.setBluetoothScoOn(true);

        BluetoothServerSocket tmp = null;
        try {
            // dev_UUID is the app's UUID string, also used by the client code
            tmp = bluutooth_adapter.listenUsingRfcommWithServiceRecord(NAME, dev_UUID);
        } catch (IOException e) { }
        serverSocket = tmp;
        
        //Start of Thread
        Thread thread = new Thread(){
    		@Override
    	    public void run() {
    	        BluetoothSocket socket = null;
    	        // Keep listening until exception occurs or a socket is returned
    	        while (true) {
    	            try {
    	                socket = serverSocket.accept();
    	                con = "Trying to connect";
    	            } catch (IOException e) {
    	                break;
    	            }
    	            
    	            // If a connection was accepted
    	            if (socket != null) {
    	            	
    	            	con = "Accepted";
    	            	
    	                // Do work to manage the connection (in a separate thread)
    	            	manageConnectionSocket(socket);
    	            	//close socket
    	            	try{
    	            		serverSocket.close();
    	            	}
    	            	catch(IOException e){
    	            		
    	            	}
    	                break;
    	            }
    	        }
    	    }
    	};
        thread.start();
        

        
        while(true){
			if(Received_file)
        		break;
        	
        }

        file_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String text = parent.getItemAtPosition(position).toString().trim();
				rd_wrt.start();
				
			}
		});

	}
	
	private class MyOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			String text = arg0.getItemAtPosition(arg2).toString().trim();
			rd_wrt.writeFile(text.getBytes());
			rd_wrt.start();
		}
	}
	
	
	public void fetchFileSocket(BluetoothSocket socket) {
		// TODO Auto-generated method stub

		rd_wrt = new ThreadToBeConnected(socket, this);
		rd_wrt.receiveFiles();
		
	}
	
	public void manageConnectionSocket(BluetoothSocket socket){
		//Toast toast = Toast.makeText(getApplicationContext(), "connection accepted", Toast.LENGTH_SHORT);
		conState.post(new Runnable() {
            public void run() {
                conState.setText("Connected");
            }
        });
		
		rd_wrt = new ThreadToBeConnected(socket, this);
		file_creation();

		rd_wrt.receiveFiles();
	}
 
    private void file_creation() {
		// TODO Auto-generated method stub
		
    	File dir = Environment.getExternalStorageDirectory();
        try {
			temp_music = File.createTempFile("temp", "mp3", dir);
	        temp_music.deleteOnExit();
	        file_op = new FileOutputStream(temp_music, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            serverSocket.close();
        } catch (IOException e) { }
    }
    
    public void play(View view){
    	player = new MediaPlayer();
        // Tried passing path directly, but kept getting 
        // "Prepare failed.: status=0x1"
        // so using file descriptor instead
        try {
        
        	player.setDataSource(file_in.getFD());

        	player.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        player.start();
    }
    
    public static void messageRefresh(byte[] messageBytes) {
    	try {
/*        	fileReady.post(new Runnable() {
				public void run() {
					fileReady.setVisibility(View.VISIBLE);				
				}
			});
*/ 		
//    		int counter = 1;
    		
//    		byte[] countBytes = new byte[1]; 		
//    		for(int i = 0; )
//    		int length = messageBytes.length;
//    		fos.write(messageBytes,0,length);
    		for(int i = 0; i<messageBytes.length;i++)
    			file_op.write(messageBytes[i]);
//            fos.close();
    		file_in = new FileInputStream(temp_music);
    		
    		final double bytes = temp_music.length();
    		final double kilobytes = (bytes / 1024);
			final double megabytes = (kilobytes / 1024);
    		
    		System.out.println("bytes : " + bytes);
    		System.out.println("kilobytes : " + kilobytes);
			System.out.println("megabytes : " + megabytes);
    		
			file_len.post(new Runnable() {
	            public void run() {
	            	file_len.setText("Size of the file is " + bytes + " bytes or " + kilobytes + "kB or " + megabytes + " MB");
	            }
	        });
    		
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

	public static void getFiles(byte[] files) {
		// TODO Auto-generated method stub
		
		String fileList = new String(files);
		
		final String [] n = fileList.split(",");
		
		int nLength = n.length;
				
		for (int i = 1; i < nLength; i++ ) {
	        arr_adapter_file.add(n[i]);
	    }

		Received_file = true;
	}
}
