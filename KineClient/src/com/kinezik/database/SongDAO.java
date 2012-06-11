package com.kinezik.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.kinezik.music.LocalSong;
import com.kinezik.music.Song;

public class SongDAO {

	private DatabaseOpenHelper helper;
	
	public SongDAO(Context context){
		helper  = new DatabaseOpenHelper(context);
	}
		
	/**
	 * If a song with the same title, album and artist already exists but has no evaluations, it is replaced.
	 * @param song
	 */
	public void addSong(LocalSong song){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues val = new ContentValues();
		val.put("Title", song.getTitle());
		val.put("Album", song.getAlbum());
		val.put("Artist", song.getArtist());
		val.put("Uri", song.getUri().toString());
		Long id = db.insert("Songs", null, val);
		Iterator iter = song.evaluationIterator();
		while(iter.hasNext()){
			Map.Entry<Integer, Float> current = (Entry<Integer, Float>) iter.next();
			val = new ContentValues();
			val.put("idSong", id);
			val.put("idEval", current.getKey());
			val.put("Evaluation", current.getValue());
			long idLong = db.insert("Evaluations", null, val);
			if(idLong == -1){
				Log.d("ERROR", "Error while inserting Evaluation in SQLite database");
			}
		}
		db.close();
	}
	
	public ArrayList<LocalSong> getSongs(){
		// On récupère les chansons
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM Songs;", null);
		if(cur == null || !cur.moveToFirst()){
			db.close();
			return null;
		}
		ArrayList<LocalSong> res = new ArrayList<LocalSong>();
		do{
			LocalSong curSong = new LocalSong(cur.getString(1), cur.getString(2), cur.getString(3), Uri.parse(cur.getString(4)));
			Log.d("TRACE", "==========================");
			Log.d("TRACE", "Nouveau curEval avec comme id de Song :" + cur.getInt(0));
			Log.d("TRACE", "==========================");
			Cursor curEval = db.rawQuery("SELECT * FROM Evaluations WHERE  IdSong = "+String.valueOf(cur.getInt(0)) +" ;", null);

			if(curEval == null || !curEval.moveToFirst()){

				Log.d("TRACE", "CurEval ne marche pas dans getSong " + curEval);
				res.add(curSong);
				continue;
			}
			Log.d("TRACE", "Eval : " + curEval.getInt(1) + " " + curEval.getFloat(2));
			do{
				curSong.addEvaluations(curEval.getInt(1), curEval.getFloat(2));
			}while(curEval.moveToNext());
			res.add(curSong);
			curEval.close();
		}while(cur.moveToNext());

		cur.close();
		db.close();
		return res;
	}
	
	/**
	 * Remove the songs that are not in the list and return the songs that need to be evaluated (modifies the parameter songs)
	 * @param songs
	 * @return
	 */
	public ArrayList<LocalSong> updateAndFilter(ArrayList<LocalSong> songs){
		ArrayList<Song> knownSongs = new ArrayList<Song>();//The list of songs that will not be removed from the database
		ArrayList<LocalSong> unknownSongs = new ArrayList<LocalSong>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Iterator<LocalSong> iter = songs.iterator();
		while(iter.hasNext()){
			LocalSong current = iter.next();
			Cursor cur = db.rawQuery("SELECT * FROM Songs WHERE" + "(Artist = ? AND Album = ? AND Title = ?);",
						 new String[]{current.getArtist(), current.getAlbum(), current.getTitle()});
			if (cur == null) {
	            // Query failed...
	            continue;
	        }
	        if (cur.moveToFirst()) {
	            //This song is known
	        	knownSongs.add(new Song(cur.getString(1), cur.getString(2), cur.getString(3)));
	        } else {
	        	//This song is unknown
	        	unknownSongs.add(current);
	        }
			cur.close();
			
		}
		db.close();
		removeOthersThan(knownSongs);
		return unknownSongs;
	}
	
	private void removeOthersThan(ArrayList<Song> knownSongs){
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM Songs ;", null);
		if (cur == null) {
			db.close();
            return;
        }
        if (!cur.moveToFirst()) {
            //Database is empty
        	db.close();
        	return;
        }
        do{
        	//If the result is not in the list, remove it
        	boolean isInList = false;
        	Iterator<Song> iter = knownSongs.iterator();
        	while(iter.hasNext() && !isInList){
        		Song current = iter.next();
        		boolean corresponds = true;
        		corresponds &= current.getTitle().equals(cur.getString(3));
        		corresponds &= current.getAlbum().equals(cur.getString(2));
        		corresponds &= current.getAlbum().equals(cur.getString(1));
        		isInList = !corresponds;
        	}
        	if(!isInList){
        		db.rawQuery("DELETE FROM Evaluations WHERE idSong = ? ;", new String[]{String.valueOf(cur.getInt(0))});
        		db.rawQuery("DELETE FROM Songs WHERE id = ? ;", new String[]{String.valueOf(cur.getInt(0))});
        	}
        } while  (cur.moveToNext());
        db.close();
	}
	
}
