package com.kinezik.music;

import org.json.JSONException;

import android.net.Uri;

/** Represents a song on a device
 * 
 * @author Victor Mours
 *
 */
public class LocalSong extends Song implements Comparable<LocalSong>{
	
	private Uri uri;
	private float fit = (float) 0;
	
	public LocalSong(String artist, String album, String title, Uri uri){
		super(artist, album, title);
		this.setUri(uri);
	}
	
	public LocalSong(String json) throws JSONException{
		super(json);
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}
	
	public void setFit(float fit){
		this.fit = fit;
	}

	@Override
	public int compareTo(LocalSong song) {
		if(this.fit >= song.fit){
			return 1;
		}else{
			return -1;
		}
	}

}
