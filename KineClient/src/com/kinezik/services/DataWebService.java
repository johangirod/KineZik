package com.kinezik.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.kinezik.database.BayesianDAO;
import com.kinezik.music.LocalSong;
import com.kinezik.music.MusicManager;
import com.kinezik.network.BayesReceiveThread;
import com.kinezik.network.BayesReceiver;
import com.kinezik.network.SongSyncThread;
import com.kinezik.network.SongSyncer;

public class DataWebService extends IntentService implements BayesReceiver, SongSyncer {

	////////////////////////// CHAMPS /////////////////////////////////
	private String UUID;
	private MusicManager manager;

	private static boolean songSynced = false; 
	// True si toutes les chans	ons ont été synchronisée

	private HashSet<ServiceListener> songSyncedListener = new HashSet<ServiceListener>() ; 
	// La liste des listener à prevenir lorsque les chansons sont synchronisées.

	private int numSongToSync;
	// Le nombre de chansons restant à synchroniser


	////////////////////////// METHODES /////////////////////////////////
	public DataWebService() {
		super("DataWebService");
		Log.d("DEBUG","New DataWebService created !");
		manager = new MusicManager(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("DEBUG", "Intent received by DataWebService");
		songSynced = false;
		getSongEvaluation();
		//getBayesTable();
		//			// Le service dort pendant 500 secondes
		//			long endTime = System.currentTimeMillis() + 50*10000;
		//			while (System.currentTimeMillis() < endTime) {
		//				synchronized (this) {
		//					try {
		//						wait(endTime - System.currentTimeMillis());
		//					} catch (Exception e) {
		//					}
		//				}
		//			}
	}

	/**
	 * Retrieve the evaluation from the server for the new songs of the user
	 */
	private void getSongEvaluation(){
		MusicManager manager = new MusicManager(this);
		ArrayList<LocalSong> nonEvaluatedSongs = manager.getNonEvaluatedSongs(getContentResolver());
		numSongToSync = nonEvaluatedSongs.size();
		if (numSongToSync == 0){
			// Si toutes les chansons sont déjà dans la base de donnée
			songSynced = true;
			Iterator<ServiceListener> iteListener= songSyncedListener.iterator();
			while (iteListener.hasNext()){
				iteListener.next().onServiceReady();
			} 
		}
		
		SongSyncThread syncerThread = new SongSyncThread(this, nonEvaluatedSongs);
		syncerThread.run();
	}

	/**
	 * Retrieve the Bayesian Table from the server
	 */
	private void getBayesTable(){
		BayesReceiveThread receiveThread = new BayesReceiveThread(this);
		receiveThread.run();
	}


	@Override
	public void onBayesReceive(String JSONBayesTable) {
		BayesianDAO bayes = new BayesianDAO(getApplication());
		bayes.storeBayesianTables(JSONBayesTable);
		Log.d("DEBUG", "Les tables de Bayes ont été mise à jour");
	}

	@Override
	public void onSongSynced(LocalSong syncedSong) {
		// On ajoute la chanson dans la base de donnée
		manager.addSong(syncedSong);
		Log.d("DEBUG", "La chanson " + syncedSong.toString() +" a été synchronisée");
		numSongToSync --;
		if (numSongToSync == 0) {
			songSynced = true;
			Log.d("DEBUG", "SONGSYNCED EST TRUE !!!!!!!1!!!!");
			//Alors on réveille tous les listeners
			Iterator<ServiceListener> iteListener= songSyncedListener.iterator();
			while (iteListener.hasNext()){
				iteListener.next().onServiceReady();
			} 
		}
	}


	public void onDestroy(){
		Log.d("DEBUG", "Le service DataWebService est détruit");
		super.onDestroy();
	}


	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public DataWebService getService() {
			// Return this instance of DataWebService so clients can call public methods
			return DataWebService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}


	/**
	 * Ajoute un ServiceListener pour ce service. Quand la totalité des chansons seront synchronisée, 
	 * la méthode onServiceReady sera appelé pour ce ServiceListener 
	 * @param listener : le listener à prévenir
	 * Si les chansons sont déjà synchronisé, onServiceReady est appelée immediatement. 
	 */
	public void addListener(ServiceListener listener){
		if (songSynced) {
			listener.onServiceReady();
		} else {
			Log.d("TRACE", "ADDING LISTENER");
			songSyncedListener.add(listener);
		}
	}

}
