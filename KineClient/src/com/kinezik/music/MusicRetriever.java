package com.kinezik.music;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.kinezik.music.PrepareMusicRetrieverTask.MusicRetrieverListener;

/**Retrieves all the music from the device's external storage and stores it into a custom data structure.
 * 
 * @author victormours
 *
 */
public class MusicRetriever {

	
	ContentResolver mContentResolver;
	MusicRetrieverListener listener;
    
	/**
	 * Query the device for all songs on the external memory and 
	 * @param cr
	 * @param manager
	 */
    public MusicRetriever(ContentResolver cr, MusicRetrieverListener listener) {
        mContentResolver = cr;
        this.listener = listener;
    }

    /**
     * Loads music data. This method may take long, so be sure to call it asynchronously without
     * blocking the main thread.
     */
    public void prepare() {
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Log.e("DEBUG", "STARTING MEDIA QUERY");
        // Perform a query on the content resolver. The URI we're passing specifies that we
        // want to query for all audio media on external storage (e.g. SD card)
        Cursor cur = mContentResolver.query(uri, null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        Log.e("DEBUG", "DONE MEDIA QUERY");
        if (cur == null) {
            // Query failed...
            return;
        }
        if (!cur.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            return;
        }


        // retrieve the indices of the columns where the ID, title, etc. of the song are
        int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);

        // add each song to the library
        do {
        	listener.onNewSongFound(new LocalSong(
					   			cur.getString(artistColumn),
						   		   cur.getString(albumColumn),
					   			cur.getString(titleColumn),
        					   ContentUris.withAppendedId(
        	                            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cur.getInt(idColumn))
        						));
        	Log.d("DEBUG", new LocalSong(
		   			cur.getString(artistColumn),
			   		   cur.getString(albumColumn),
		   			cur.getString(titleColumn),
				   ContentUris.withAppendedId(
                         android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cur.getInt(idColumn))
					).toJSONString() + "\nID : " + cur.getInt(idColumn));

        } while (cur.moveToNext());

    }

    
    public ContentResolver getContentResolver() {
        return mContentResolver;
    }
}