package com.kinezik.player;


import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.util.Log;
import android.widget.ListView;

import com.kinezik.R;
import com.kinezik.music.LocalSong;
import com.kinezik.services.KineZikService;
import com.kinezik.services.KineZikService.LocalBinder;
import com.kinezik.services.ServiceListener;

public class PlayerActivity extends Activity implements ServiceListener{


	//Service pour retrouver les chansons
	private KineZikService kinezikService;
	 /** Flag indicating whether we have called bind on KineZikService. */
    private boolean mBound = false;
    
	ListView songList;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wait_playlist);
		
	}
	
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        bindService(new Intent(this, KineZikService.class), mConnection,
            Context.BIND_AUTO_CREATE);
    }
    
    ArrayList<LocalSong> songs;
    
	@Override
	public void onServiceReady() {
		Log.d("TRACE", "onServiceReady in PlayerKinezik");
		runOnUiThread(new  Runnable(){
			public void run(){
				setContentView(R.layout.music);
				songList = (ListView) findViewById(R.id.songList);
				songs = new ArrayList<LocalSong>();
				
				for(int i = 0; i < 5 ; i++){
					LocalSong curSong = (kinezikService.getNextSong());
					if (curSong == null){
						break;
					} else {
						songs.add(curSong);
					}
				}
				
				SongAdapter adapter = new SongAdapter(PlayerActivity.this, songs);
				songList.setAdapter(adapter);
			}
		});
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item, getNextSong(3)));
		//ListView lv = getListView();
		//lv.setTextFilterEnabled(true);
	}
    
	public boolean pause = false;
    public void onPushPlay(){
    	pause = !pause;
    	if (pause){
    		Toast.makeText(getApplication(), "La lecture est en pause", 3);
    	} else {
        	Toast.makeText(getApplication(), "La lecture reprend", 3);
    	}
    }
    
    
    public void onPushNext(){
    	songs.remove(0);
    	LocalSong song = kinezikService.getNextSong();
    	if (song != null){
    		songs.add(song);
    	}
    	SongAdapter adapter = new SongAdapter(PlayerActivity.this, songs);
		songList.setAdapter(adapter);
    }
    
    
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
    
    
   
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBound = true;

    		Log.d("TRACE", "bind de KineZikService reussi dans PlayerActivity");
            kinezikService = ((LocalBinder) service).getService();
            kinezikService.add(PlayerActivity.this);
        }

        public void onServiceDisconnected(ComponentName className) {
            mBound = false;
        }
    };

	
	
	
	// TODO cette méthode doit etre réecrite dans un service 
	private String []  getNextSong(int size) {
		return  new String[] {"Back In Black ACDC Back In Black","Title Artist Album","song3"};
	}


}

