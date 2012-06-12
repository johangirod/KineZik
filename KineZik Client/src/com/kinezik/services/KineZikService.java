package com.kinezik.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.json.JSONException;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.kinezik.bayesian.DrawEvaluator;
import com.kinezik.database.BayesianDAO;
import com.kinezik.database.FeedbackDAO;
import com.kinezik.feedback.Feedback;
import com.kinezik.music.LocalSong;
import com.kinezik.music.MusicManager;
import com.kinezik.shared.BayesianTable;


public class KineZikService extends IntentService implements ServiceListener {

	//////////////////CHAMPS////////////////////////

	private	DrawEvaluator computedEvaluator; 
	private TreeSet<LocalSong> computedSongs= new TreeSet<LocalSong>();

	private static boolean songListComputed = false;
	private HashSet<ServiceListener> computedSongListener = new HashSet<ServiceListener>();


	float desc1, desc2, desc3; // Descripteur de dessins


	public KineZikService(){
		super("KineZikService");
		Log.d("DEBUG", "KineZikService constructed");
	}

	public void onCreate(){
		super.onCreate();
		Log.d("DEBUG", "KineZikService created !");
	}

	public void onDestroy(){
		if (mBound){
			//binder.getService().removeListener(KineZikService.this);
			//unbindService(mConnection);
		}

		Log.d("DEBUG","KineZikService destroyed !");
		super.onDestroy();
	}

	////////////////
	// Binding and intent methods
	/////////////////



	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public KineZikService getService() {
			// Return this instance of KineZikService so clients can call public methods
			return KineZikService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;

	}


	/////////////////
	// Useful methods
	////////////////////

	private boolean mBound = false;
	private com.kinezik.services.DataWebService.LocalBinder binder;
	ServiceConnection mConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d("TRACE", "KineZikService connected");
			binder = (com.kinezik.services.DataWebService.LocalBinder) service;
			binder.getService().addListener(KineZikService.this);
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};


	/************************METHOD USED BY KINEZIK APPLICATION ************************/

	/**
	 * Perform bayesian inference on the drawing and return the playlist. 
	 * When done, call on ServiceReady for all the listener.
	 * @param drawing
	 */
	public void computePlaylist(final float desc1, final float desc2, final float desc3){
	Log.d("CALIBRAGE", "Descripteurs du dessin : nbPeak " + desc1 +" / vitesse "+ desc2 +" / occupation "+ desc3);
		// Calcul les évaluateurs pour le dessin
		this.desc1 = desc1;
		this.desc2 = desc2;
		this.desc3 = desc3;
		computedSongs= new TreeSet<LocalSong>();
		songListComputed = false;
		new Thread(new Runnable() {
			public void run() {
				BayesianDAO bayesDAO = new BayesianDAO(getApplication());
				HashMap<Integer, BayesianTable> bayesianTable;
				try {
					bayesianTable = bayesDAO.getBayesianTable();
					computedEvaluator = new DrawEvaluator(desc1, desc2, desc3, bayesianTable);
					//Ici, on bind le service DataWebService pour savoir si tout est OK
					Intent intent = new Intent(KineZikService.this, DataWebService.class);
					bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("ERROR", "KineZikService, erreur JSON pour les tables de Bayes");
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * DataWebService a fini la mise à jour des données, 
	 * onServiceReady calcul la liste des chansons.
	 */
	@Override
	public void onServiceReady() {
		unbindService(mConnection);
		// On recupère la liste de toutes les chansons évaluées
		MusicManager mManager = new MusicManager(getApplication());
		computedSongs = computedEvaluator.getBestSong(mManager.getSongs());

		// On prévient tous les ServiceListener abonnées que les chansons sont prêtes.
		songListComputed = true;
		Iterator<ServiceListener> iteListener = computedSongListener.iterator();
		while(iteListener.hasNext()) {
			iteListener.next().onServiceReady();
		}
		computedSongListener.clear();
	}


	/***** Methods for the administration of the playlist *************/
	/**
	 * Get the best fitting song for the descriptor given earlier. 
	 * The song is then removed from the list.
	 * @return The best fitting song. Null if the computation isn't finished yet.
	 */
	public LocalSong getNextSong(){
		LocalSong nextSong = null;
		if (songListComputed && !computedSongs.isEmpty()){
			nextSong = computedSongs.pollLast();
		}
		return nextSong;
	}

	/**
	 * Add a Listener to the service who will be notify when all the song evaluator will be computed
	 * @param s
	 */
	public void add(ServiceListener s){
		if (songListComputed){
			s.onServiceReady();
		} else {
			computedSongListener.add(s);
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
	}
	/**
	 * Save the feedback in a file, under a JSON String (in a new thread)
	 * @param localSong
	 * @param action
	 */
	public void saveFeedback(final LocalSong localSong, final int action) {
		// TODO Auto-generated method stub
		new Thread(new Runnable(){
			public void run(){
				final Feedback feedback = new Feedback();
				feedback.setDesc1(desc1);
				feedback.setDesc2(desc2);
				feedback.setDesc3(desc3);
				feedback.setIdSong(localSong.getId());
				feedback.setAction(action);
				feedback.setTime(System.currentTimeMillis());
				Iterator<Entry<Integer, Float>> iteEval= localSong.evaluationIterator();
				while(iteEval.hasNext()){
					feedback.addEvaluator(iteEval.next().getKey());
				}
				String feedbackJson = feedback.toJSONString();
				FeedbackDAO feeDAO = new FeedbackDAO(getApplication());
				feeDAO.storeFeedback(feedbackJson);
			}
		}).start();
	}


}
