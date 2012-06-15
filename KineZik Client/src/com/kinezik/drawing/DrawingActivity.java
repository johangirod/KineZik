package com.kinezik.drawing;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kinezik.R;
import com.kinezik.player.PlayerActivity;
import com.kinezik.services.KineZikService;
import com.kinezik.services.KineZikService.LocalBinder;

public class DrawingActivity extends Activity {
	DrawView drawView;
	KineZikService kinezikService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.drawing_v2);
		
		drawView = (DrawView) findViewById(R.id.drawView2);
		drawView.setButtonLayout((LinearLayout) findViewById(R.id.drawingButtonsLayout));

	}


	/** Flag indicating whether we have called bind on KineZikService. */
	boolean mBound;

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBound = true;
			kinezikService = ((LocalBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBound = false;
		}
	};


	public void play(View view){

		if (!mBound) return;

		float desc1 = drawView.getDrawing().getDesc1();
		float desc2 = drawView.getDrawing().getDesc2();
		float desc3 = drawView.getDrawing().getDesc3();

		// ON sauve les descripteur au cas ou l'on veuille reprendre sa playlist précédente
		SharedPreferences.Editor editor = getSharedPreferences("previousDraw",0).edit();
		editor.putBoolean("ApplicationResumed", true);
		editor.putFloat("desc1", desc1);
		editor.putFloat("desc2", desc2);
		editor.putFloat("desc3", desc3);
		//editor.commit();
		editor.apply();
		Log.d("DEBUG", "Dans Drawing, ApplicationResumed vaut : " +
				getSharedPreferences("previousDraw",0).getBoolean("ApplicationResumed", false));


		kinezikService.computePlaylist(desc1, desc2, desc3);
		Intent intent = new Intent(this, PlayerActivity.class);
		startActivity(intent);

	}

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to the service
		bindService(new Intent(this, KineZikService.class), mConnection,
				Context.BIND_AUTO_CREATE);
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
	
	public void showDesc(View view){
		Toast.makeText(getApplicationContext(), "Peaks :" + drawView.getDrawing().getDesc1() +"\n" +
				"Avg speed : " + drawView.getDrawing().getDesc2() +"\n" +
				"Length : " + drawView.getDrawing().getDesc3(), Toast.LENGTH_LONG).show();
	}



	public void reset(View view){
		drawView.reset();
	}

}