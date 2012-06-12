package com.kinezik.player;


import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.kinezik.R;
import com.kinezik.feedback.Feedback;
import com.kinezik.music.LocalSong;
import com.kinezik.player.KineZikPlayer.PlayerBinder;
import com.kinezik.services.FeedbackUploadService;
import com.kinezik.services.KineZikService;
import com.kinezik.services.KineZikService.LocalBinder;
import com.kinezik.services.ServiceListener;

public class PlayerActivity extends Activity implements ServiceListener, PlayerListener{


	//Service pour retrouver les chansons
	private KineZikService kinezikService;
	private KineZikPlayer player;
	/** Flag indicating whether we have called bind on KineZikService. */
	private boolean serviceBound = false;
	private boolean playerBound = false;
	private boolean hasPlaylist = false;//indicates if a playlist has been given by KineZikService


	ListView songList;
	Button playButton;
	Button nextButton;
	SongAdapter adapter;
	
	/******************METHOD FOR MANAGING THE LIFECYCLE OF THE ACTIVITY ************************/

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
		startService(new Intent(this, KineZikPlayer.class));
		bindService(new Intent(this, KineZikPlayer.class), playerConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		Log.d("TRACE", "on pause called for PlayerActivity");
		Intent i = new Intent(getApplication(),FeedbackUploadService.class);
		startService(i);
		super.onPause();
	}


	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences.Editor editor = getSharedPreferences("previousDraw",0).edit();
		editor.putBoolean("drawing", false);
		// Unbind from the service
		if (serviceBound) {
			unbindService(mConnection);
			serviceBound = false;
		}
		if(playerBound){
			unbindService(playerConnection);
		}
	}

	
	
	
	ArrayList<LocalSong> songs;

	@Override
	public void onServiceReady() {
		Log.d("TRACE", "onServiceReady in PlayerKinezik");
		if(!hasPlaylist){
			runOnUiThread(new  Runnable(){
				public void run(){
					setContentView(R.layout.music);
					playButton = (Button) findViewById(R.id.playButton);
					nextButton = (Button) findViewById(R.id.nextButton);
					songList = (ListView) findViewById(R.id.songList);
					songs = new ArrayList<LocalSong>();

					for(int i = 0; i < 8 ; i++){
						LocalSong curSong = kinezikService.getNextSong();
						if (curSong == null){
							break;
						} else {
							songs.add(curSong);
						}
					}
					adapter = new SongAdapter(PlayerActivity.this, songs);
					songList.setAdapter(adapter);
					if(player != null){
						player.loadAndPlay(songs.get(0).getUri());
					} else {
						Log.d("DEBUG","PLAYER IS NULL");
					}
				}
			});
			hasPlaylist = true;
		}
	}

	
	/**************************METHOD FOR THE DIFFERENT UI COMPONENT OF THE ACTIVITY ********************/
	public boolean pause = false;
	public void onPushPlay(View v){
		if(player != null){
			if(player.isPlaying()){
				player.pause();
			} else {
				player.play();
			}
		}
	}

	public void previousDrawing (View v) {
		SharedPreferences settings = getSharedPreferences("previousDraw",0);
		if (settings.getBoolean("drawing", true)) {
			onBackPressed();
		}
		else {
			((Button) findViewById(R.id.button1)).setVisibility(View.GONE);
		}

	}

	
	public void onPushNext(View v){
		if (songs.isEmpty()){
			Toast.makeText(this, "il n'y a plus de musique à jouer", Toast.LENGTH_LONG).show();
			return;
		}
		songs.remove(0);
		LocalSong song = kinezikService.getNextSong();
		if (song != null){
			songs.add(song);
		}
		if(player != null){
			if(songs.isEmpty()){
				player.pause();
			} else {
				Iterator<LocalSong> iter = songs.iterator();
				if(iter.hasNext()){
					player.loadAndPlay(iter.next().getUri());
				}
			}
		}
		if(songs.size()==1){
			nextButton.setVisibility(View.INVISIBLE);//If there is only one song left, hide the "next" button
		}
		adapter.notifyDataSetChanged();
	}

	public void onThumbUp(View v){
		final int position = ((ListView) findViewById(R.id.songList)).getPositionForView((LinearLayout)v.getParent());	
		Toast.makeText(this, "Le vote positif a été pris en compte", Toast.LENGTH_SHORT).show();
		Log.d("TRACE", "La chanson a été plussé");
		if (serviceBound){
			kinezikService.saveFeedback(songs.get(position), Feedback.THUMB_UP);
		}
		if (position != 0){
			LocalSong song = songs.remove(position);
			songs.add(1, song);
			adapter.notifyDataSetChanged();
		}
	}

	public void onThumbDown(View v){
		final int position = ((ListView) findViewById(R.id.songList)).getPositionForView((LinearLayout)v.getParent());
		Toast.makeText(this, "Le vote negatif a été pris en compte", Toast.LENGTH_SHORT).show();
		Log.d("TRACE", "La chanson a été moinssé");

		if (serviceBound){
			kinezikService.saveFeedback(songs.get(position), Feedback.THUMB_DOWN);
		}
		if (position==0) {
			this.onPushNext(v);
		} else {
			songs.remove(position);
			LocalSong song = kinezikService.getNextSong();
			if (song != null){
				songs.add(song);
			}
			adapter.notifyDataSetChanged();
		}
	}




	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			serviceBound = true;

			Log.d("TRACE", "bind de KineZikService reussi dans PlayerActivity");
			kinezikService = ((LocalBinder) service).getService();
			kinezikService.add(PlayerActivity.this);
		}

		public void onServiceDisconnected(ComponentName className) {
			serviceBound = false;
		}
	};


	/****************************MUSIC PLAYER METHODS*************/

	private ServiceConnection playerConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			playerBound = true;
			Log.d("TRACE", "bind de PlayerService reussi dans PlayerActivity");
			player = ((PlayerBinder) service).getService();
			player.setListener(PlayerActivity.this);

		}

		public void onServiceDisconnected(ComponentName className) {
			playerBound = false;
			player = null;
		}
	};

	@Override
	public void onPlayerLoading() {
		playButton.setBackgroundResource(R.drawable.kicon_pause_v2);

	}

	@Override
	public void onPlayerPaused() {
		playButton.setBackgroundResource(R.drawable.kicon_lect_v2);
	}

	@Override
	public void onPlayerPlay() {
		playButton.setBackgroundResource(R.drawable.kicon_pause_v2);

	}

}

