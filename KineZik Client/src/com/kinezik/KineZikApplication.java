package com.kinezik;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

import com.kinezik.network.Message;
import com.kinezik.services.DataWebService;
import com.kinezik.services.KineZikService;

public class KineZikApplication extends Application {

	
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d("DEBUG", "Creating KineZik Application");
		Message.setUUID(AndroidUUID.id(this));

	}
	
}
