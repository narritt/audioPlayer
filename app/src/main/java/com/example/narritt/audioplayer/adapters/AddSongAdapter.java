package com.example.narritt.audioplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.narritt.audioplayer.R;
import com.example.narritt.audioplayer.items.Song;

import java.util.ArrayList;


public class AddSongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public AddSongAdapter(Context c, ArrayList<Song> theSongs){
        songs = theSongs;
        songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return songs.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //мы пробуем использовать convertView, который идет на вход метода.
        //Это уже созданное ранее View, но неиспользуемое в данный момент.
        //Например, при прокрутке списка, часть пунктов уходит за экран и их уже не надо прорисовывать
        LinearLayout songLay = (LinearLayout) convertView;
        if (songLay == null) {
            songLay = (LinearLayout)songInf.inflate(R.layout.add_song, parent, false);
        }
        //map to song layout
        //get title and artist views
        TextView songView   = songLay.findViewById(R.id.song_title);
        TextView artistView = songLay.findViewById(R.id.song_artist);
        CheckBox chb        = songLay.findViewById(R.id.chbSelected);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }

    /*CheckBox.OnCheckedChangeListener songSelectedCheck = new CheckBox.OnCheckedChangeListener() {
        public void onCheckedChanged(CheckBox checkBox, boolean isChecked) {
        }
    };*/

}