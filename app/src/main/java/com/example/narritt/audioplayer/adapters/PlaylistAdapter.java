package com.example.narritt.audioplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.narritt.audioplayer.R;
import java.util.ArrayList;
import com.example.narritt.audioplayer.items.Playlist;

public class PlaylistAdapter extends BaseAdapter {

    private ArrayList<Playlist> playlists;
    private LayoutInflater artistInf;

    public PlaylistAdapter(Context c, ArrayList<Playlist> thePlaylists){
        playlists = thePlaylists;
        artistInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to artist layout
        LinearLayout artistLay = (LinearLayout)artistInf.inflate(R.layout.artist, parent, false);

        TextView artistNameView   = (TextView)artistLay.findViewById(R.id.artistName);

        Playlist currPlaylist = playlists.get(position);

        artistNameView.setText(currPlaylist.getName());

        artistLay.setTag(position);
        return artistLay;
    }
}
