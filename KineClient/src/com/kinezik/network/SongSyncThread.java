package com.kinezik.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONException;

import android.util.Log;

import com.kinezik.music.LocalSong;
import com.kinezik.music.Song;

public class SongSyncThread extends Thread{
	SongSyncer syncer;
	ArrayList<LocalSong> nonEvaluatedSongs;
	
	public SongSyncThread(SongSyncer syncer, ArrayList<LocalSong> nonEvaluatedSongs){
		this.syncer = syncer;
		this.nonEvaluatedSongs = nonEvaluatedSongs;
	}
	
	public void run(){
		if (nonEvaluatedSongs == null) {
			return;
		}
		Iterator<LocalSong> iteLocalSong = nonEvaluatedSongs.iterator();
		while (iteLocalSong.hasNext()){
			LocalSong currentLocalSong = iteLocalSong.next();
			Message mess = new Message();
			mess.add("action", "getSongEval");
			mess.add("JSON", currentLocalSong.toJSONString());
			String response = mess.send();
			Song evaluatedSong;
			
			try {
				evaluatedSong = new Song(response);
				Iterator<Entry<Integer, Float>> iteEval  = evaluatedSong.evaluationIterator();
				while(iteEval.hasNext()){
					Entry<Integer, Float> currentEval = iteEval.next();
					currentLocalSong.addEvaluations(currentEval.getKey(), currentEval.getValue());
				}
				syncer.onSongSynced(currentLocalSong);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.d("ERROR", "Erreur lors de l'interpretation des données");
			}
		}
	}

}
