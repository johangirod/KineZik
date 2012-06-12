package com.kinezik.music;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.kinezik.database.SongDAO;

/**
 * A supprimer une fois qu'on a changé l'architecture.
 * @author victormours
 *
 */
public class MusicManager {

	private SongDAO dao;

	public MusicManager(Context context){
		dao = new SongDAO(context);
	}

	/**
	 * Scans the external memory and returns the list of songs that need to be evaluated.
	 * This method may take a while, so it shouldn't be called from the main thread.
	 * 
	 * @return
	 */
	public ArrayList<LocalSong> getNonEvaluatedSongs(ContentResolver cr){
		ArrayList<LocalSong> unknownSongs = getSongsOnDevice(cr);
		unknownSongs = dao.updateAndFilter(unknownSongs);
		Log.d("DEBUG", "Taille de unknown song : " + unknownSongs.size());
		return unknownSongs;
	}

	/**
	 * Adds the given song to the database
	 * @param song
	 */
	public void addSong(LocalSong song){
		dao.addSong(song);
	}

	/**
	 * Return all the evaluated songs currently on the sdcard.
	 * @return
	 */
	public ArrayList<LocalSong> getSongs(){
		ArrayList<LocalSong> list = dao.getSongs();
		
		//Shuffling the list to return 
		//<TODO> : that's pretty bad, as it shuffle the whole library. It's just a shaky solution.
		if (list.size() > 0) {
			ArrayList<LocalSong> copy = new ArrayList<LocalSong>();
			for (LocalSong song: list)
				copy.add(song);
			Random generator = new Random();
			ArrayList<LocalSong> result = new ArrayList<LocalSong>();
			do{
				int index = (int) (generator.nextDouble() * (double) copy.size());
				result.add(copy.remove(index));
			} while (copy.size() > 0);
			return result;
		} else
			return new ArrayList<LocalSong>();

	}


	private ArrayList<LocalSong> getSongsOnDevice(ContentResolver cr){
		ArrayList<LocalSong> res = new ArrayList<LocalSong>();

		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Log.e("DEBUG", "STARTING MEDIA QUERY");
		// Perform a query on the content resolver. The URI we're passing specifies that we
		// want to query for all audio media on external storage (e.g. SD card)
		Cursor cur = cr.query(uri, null,
				MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
		Log.e("DEBUG", "DONE MEDIA QUERY");
		if (cur == null) {
			// Query failed...
			return res;
		}
		if (!cur.moveToFirst()) {
			// Nothing to query. There is no music on the device. How boring.
			return res;
		}


		// retrieve the indices of the columns where the ID, title, etc. of the song are
		int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
		int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
		int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
		int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);



		LocalSong localSong ;

		// add each song to the library
		do {
			localSong = new LocalSong(
					cur.getString(artistColumn),
					cur.getString(albumColumn),
					cur.getString(titleColumn),
					ContentUris.withAppendedId(
							android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cur.getInt(idColumn)));
			Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
			Uri coverUri = ContentUris.withAppendedId(sArtworkUri, cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
			localSong.setUriAlbum(coverUri);

			res.add(localSong);



		} while (cur.moveToNext());

		return res;
	}
}
