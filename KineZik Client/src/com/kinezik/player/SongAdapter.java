package com.kinezik.player;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

	public SongAdapter(Context context,List<LocalSong> songs) {
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

	private class ViewHolder{
		TextView titleView;
		TextView artistView;
		TextView fittingView;
		TextView genreView ;
		ImageView albumArtView;
	}

	@Override

	public View getView(int position, View convertView, ViewGroup parent) {
//		if (position>=songs.size()){
//			throw new IllegalArgumentException();
//		}
		ViewHolder holder;
		if(convertView == null) {

			holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.song_item, null);


			holder.titleView = (TextView) convertView.findViewById(R.id.titleTextView);
			holder.artistView = (TextView) convertView.findViewById(R.id.artistTextView);
			holder.fittingView = (TextView) convertView.findViewById(R.id.fittingTextView);
			holder.genreView = (TextView) convertView.findViewById(R.id.genreTextView);
			holder.albumArtView = (ImageView) convertView.findViewById(R.id.albumArtView);
		
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == 0){
			
			holder.titleView.setText("[Lecture] "+songs.get(position).getTitle());
		} else {
			holder.titleView.setText(songs.get(position).getTitle());
		}
		holder.artistView.setText(songs.get(position).getArtist());
		holder.fittingView.setText(songs.get(position).getFit());
		holder.genreView.setText("Genre : " +songs.get(position).getGenre());
		ContentResolver res = parent.getContext().getContentResolver();
		InputStream in;
		try {
			in = res.openInputStream(songs.get(position).getUriAlbum());
			Bitmap artwork = BitmapFactory.decodeStream(in);
			holder.albumArtView.setImageBitmap(artwork);
		} catch (FileNotFoundException e) {
			Log.d("DEBUG","Album art not found");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		return convertView;

	}


}
