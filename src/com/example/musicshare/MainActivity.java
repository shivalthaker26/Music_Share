package com.example.musicshare;

import com.example.musicshare.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	
	@SuppressLint("CommitPrefEdits")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        sharedPreferences = PreferenceManager
    			.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
		
	}
	
	public void senderClick(View view){
		Toast.makeText(getBaseContext(), "Role changed to " + "Sender", Toast.LENGTH_SHORT).show();
		editor.putString("role", "sender");
		editor.commit();
		Intent intent = new Intent(this, Sender.class);
		startActivity(intent);
	}
	
	public void recieverClick(View view){
		Toast.makeText(getBaseContext(), "Role changed to " + "reciever", Toast.LENGTH_SHORT).show();
		editor.putString("role", "receiver");
		editor.commit();
		Intent intent = new Intent(this, Reciever.class);
		startActivity(intent);
	}
	
	public void relayClick(View view){
		Toast.makeText(getBaseContext(), "Role changed to " + "Relay", Toast.LENGTH_SHORT).show();
		editor.putString("role", "relay");
		editor.commit();		
		Intent intent = new Intent(this, Relay.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
