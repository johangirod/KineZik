package com.kinezik;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kinezik.drawing.DrawingActivity;
import com.kinezik.music.MusicManager;
import com.kinezik.music.MusicRetriever;
import com.kinezik.network.Message;
import com.kinezik.player.PlayerActivity;
import com.kinezik.services.KineZikService;
import com.kinezik.services.KineZikService.LocalBinder;

/**
 * This is the main activity for the application.
 * @author victormours
 *
 */
public class KineZikActivity extends Activity {
	KineZikService kinezikService;
	public static final int DRAW = 1;

	private boolean mBound = false;

	TextView view ;
	MusicRetriever mRetriever;
	MusicManager mManager;

	float desc1, desc2, desc3;
	//Previous values of the descriptor if applicable

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//This static call could be made by the Application class
		Message.setUUID(AndroidUUID.id(this));

		setContentView(R.layout.menu);
		view = (TextView) findViewById(R.id.textView1);
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		if (settings.getBoolean("ApplicationResumed", false)){
			// Si l'application est résumé, et qu'il y a une "playlist" en memoire"
			((Button) findViewById(R.id.button1)).setVisibility(View.GONE);
			desc1 = settings.getFloat("desc1", -1);
			desc1 = settings.getFloat("desc2", -1);
			desc1 = settings.getFloat("desc3", -1);
		} else {
			((Button) findViewById(R.id.button1)).setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onStop(){
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
			kinezikService = ((LocalBinder) service).getService();
			((Button) findViewById(R.id.button1)).setVisibility(View.VISIBLE);

		}

		public void onServiceDisconnected(ComponentName className) {
			mBound = false;
		}
	};



	/**
	 * Called by the UI when the user clicks the "New Drawing" button
	 * @param view
	 */
	public void draw(View view){
		Intent i = new Intent(this, DrawingActivity.class);
		startActivityForResult(i, DRAW);
	}



	//TODO : méthode à implémenter , elle est appelée quand l'utilisateur clique sur le bouton
	//play dans la home page  
	public void play () {
		if (!mBound) return;
		kinezikService.computePlaylist(desc1, desc2, desc3);
		Intent intent = new Intent(this, PlayerActivity.class);
		startActivity(intent);
	}

}
