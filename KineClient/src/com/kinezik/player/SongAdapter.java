package com.kinezik.player;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
		LinearLayout linearView;
	}

	@Override

	public View getView(int position, View convertView, ViewGroup parent) {
		if (position>=songs.size()){
			throw new IllegalArgumentException();
		}
		ViewHolder holder;
		if(convertView == null) {

			holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.song_item, null);
			if (position == 0){
				holder.linearView = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
				holder.linearView.setBackgroundColor(R.color.bluetransparent);
			}
			
			holder.titleView = (TextView) convertView.findViewById(R.id.titleTextView);
			holder.artistView = (TextView) convertView.findViewById(R.id.artistTextView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.titleView.setText(songs.get(position).getTitle());
		holder.artistView.setText(songs.get(position).getArtist());

		return convertView;

	}

	
}
