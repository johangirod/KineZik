package com.kinezik.player;


import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


/**The class to manage android-specific media player settings. Another class should tell this one what song to play and listen for
 * completion.
 * TODO : make this a little more robust
 * @author victormours
 *
 */
public class KineZikPlayer extends Service{

    MediaPlayer mPlayer = null;

    AudioManager mAudioManager;
    
    PlayerListener listener;
    boolean playing = false;



    
	// Binder given to clients
    private final IBinder mBinder = new PlayerBinder();
    
    
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class PlayerBinder extends Binder {
        public KineZikPlayer getService() {
            // Return this instance of PlayerService so clients can call public methods
            return KineZikPlayer.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    public void setListener(PlayerListener listener){
    	this.listener = listener;
    }
    
    public void deleteListener(){
    	this.listener = null;
    }
    
    public boolean isPlaying(){
    	return playing;
    }
    
    public void loadAndPlay(Uri song){
    	if(listener != null){
    		listener.onPlayerLoading();
    	}
    	if(mPlayer != null){
    		mPlayer.release();
    	}
    	mPlayer = new MediaPlayer();
    	mPlayer.reset();
    	try{
    		mPlayer.setDataSource(getApplicationContext(), song);
        	mPlayer.prepare();
    	} catch (IOException e){
    		Toast.makeText(getApplicationContext(), "Erreur : chanson introuvable", Toast.LENGTH_LONG);
    		e.printStackTrace();
    	}
    	play();
    }
    
    public void play(){
    	if(listener != null){
    		listener.onPlayerPlay();
    	}
    	mPlayer.start();
    	playing = true;
    }
    
    public void pause(){
    	if(listener != null){
    		listener.onPlayerPaused();
    	}
    	mPlayer.pause();
    	playing = false;
    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        Log.d("DEBUG", "KineZikPlayer created !");
    }

    @Override
    public void onDestroy(){
    	if(mPlayer != null){
    		mPlayer.release();
    	}
    	super.onDestroy();
    }
	
}
