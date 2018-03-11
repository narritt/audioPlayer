package com.example.narritt.audioplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.narritt.audioplayer.R;
import com.example.narritt.audioplayer.items.Artist;

import java.util.ArrayList;

/**
 * Created by Narritt on 13.01.2018.
 */

public class ArtistAdapter extends BaseAdapter {

    private ArrayList<Artist> artists;
    private LayoutInflater artistInf;

    public ArtistAdapter(Context c, ArrayList<Artist> theArtists){
        artists = theArtists;
        artistInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return artists.size();
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

        Artist currArtist = artists.get(position);

        artistNameView.setText(currArtist.getName());

        artistLay.setTag(position);
        return artistLay;
    }
}
