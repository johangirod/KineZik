package com.kinezik.drawing;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.LinearLayout;

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
        
        setContentView(R.layout.drawing);
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
         kinezikService.computePlaylist(drawView.getDrawing().getDesc1(),
        		 						drawView.getDrawing().getDesc2(),
        		 						drawView.getDrawing().getDesc3());
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


    
    public void reset(View view){
    	drawView.reset();
    }
    
}