package com.kinezik.player;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinezik.R;
import com.kinezik.music.LocalSong;

public class SongAdapter extends BaseAdapter {

	
	List<LocalSong> songs;
	LayoutInflater inflater;

	public SongAdapter(Context context, List<LocalSong> songs) {
		inflater = LayoutInflater.from(context);
		this.songs = songs;
	}

	@Override
	public int getCount() {
		return songs.size();
	}

	@Override
	public Object getItem(int position) {
		return songs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		TextView titleView;
		TextView artistView;
		TextView fittingView;
		TextView genreView;
		ImageView albumArtView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("DEBUG", "Call to getView for the position : " + position + " and the song : " + songs.get(position).getTitle());
		
		
		View vi = convertView;
		ViewHolder vh;
		
		if( convertView == null ) {
			vh = new ViewHolder();

			vi = inflater.inflate(R.layout.song_item, null);
			vh.titleView = (TextView) vi.findViewById(R.id.titleTextView);
			vh.artistView = (TextView) vi.findViewById(R.id.artistTextView);
			vh.fittingView = (TextView) vi.findViewById(R.id.fittingTextView);
			vh.genreView = (TextView) vi.findViewById(R.id.genreTextView);
			vh.albumArtView = (ImageView) vi.findViewById(R.id.albumArtView);
			vi.setTag(vh);
		} else {
			vh = (ViewHolder) vi.getTag();
		}

		
		int fade = 255;
		if(position != 0){
			fade = Math.max(90, 150 - 20*position);
		}
		vh.titleView.setText(songs.get(position).getTitle());
		vh.albumArtView.setAlpha(fade);
		vh.titleView.setTextColor(fadeColor(vh.titleView.getCurrentTextColor(), fade));
		vh.artistView.setTextColor(fadeColor(vh.artistView.getCurrentTextColor(), fade));
		vh.fittingView.setTextColor(fadeColor(vh.fittingView.getCurrentTextColor(), fade));
		vh.genreView.setTextColor(fadeColor(vh.genreView.getCurrentTextColor(), fade));

		vh.artistView.setText(songs.get(position).getArtist());
		vh.fittingView.setText(songs.get(position).getFit());
		vh.genreView.setText("Genre : " + songs.get(position).getGenre());
		ContentResolver res = parent.getContext().getContentResolver();
		InputStream in;
		try {
			in = res.openInputStream(songs.get(position).getUriAlbum());
			Bitmap artwork = BitmapFactory.decodeStream(in);
			vh.albumArtView.setImageBitmap(artwork);
		} catch (FileNotFoundException e) {
			Log.d("DEBUG", "Album art not found");
			e.printStackTrace();
		}

		return vi;

	}
	
	int fadeColor(int color, int alpha) {
		return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
	}
}
