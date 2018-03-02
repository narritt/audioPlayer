package com.example.narritt.audioplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Narritt on 09.12.2017.
 */

public class AlbumAdapter extends BaseAdapter {

    private ArrayList<Album> albums;
    private LayoutInflater albumInf;

    public AlbumAdapter(Context c, ArrayList<Album> theAlbums){
        albums = theAlbums;
        albumInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        //wtf is this method
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        //wtf is this method
        return 0;
    }

    //ПРОБЛЕМА С ОБЛОЖКОЙ - В КЛАССЕ ХРАНИТСЯ В ImageView, НАДО КАК-ТО ЗАБРАТЬ  Drawable ИЗ НЕГО
    // TODO: начал делать с обложкой что-то здесь, разобраться
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout albumLay = (LinearLayout)albumInf.inflate(R.layout.album, parent, false);
        //get title and artist views
        TextView albumTitleView   = (TextView)albumLay.findViewById(R.id.album_title);
        TextView albumArtistView = (TextView)albumLay.findViewById(R.id.album_artist);
        ImageView cover = (ImageView)albumLay.findViewById(R.id.cover);
        //get song using position
        Album currAlbum = albums.get(position);
        //get title and artist strings
        albumTitleView.setText(currAlbum.getTitle());
        albumArtistView.setText(currAlbum.getArtist());
        //cover.setImageResource(currAlbum.getCover().getDrawable() );
        cover.setImageResource(R.drawable.note);
        //set position as tag
        albumLay.setTag(position);
        return albumLay;
    }

}