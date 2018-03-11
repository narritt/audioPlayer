package com.example.narritt.audioplayer.items;

import android.widget.ImageView;

/**
 * Created by Narritt on 13.01.2018.
 */

public class Album {

    private long id;
    private String title;
    private String artist;
    private String path;

    private String genre[];
    private int year;
    private ImageView cover;

    public Album(String albumTitle, String albumArtist){
        title = albumTitle;
        artist = albumArtist;

        year = 1970;
        //cover.setImageResource(R.drawable.note);
    }
    public Album (long albumId, String albumTitle, String albumArtist){
        id = albumId;
        title = albumTitle;
        artist = albumArtist;

        year = 1970;
        //cover.setImageResource(R.drawable.note);
    }
    public Album(Song song){
        title = song.getAlbum();
        artist = song.getArtist();
        path = song.getFolderPathString();
    }

    public long     getID(){return id;}
    public String   getTitle() {return title;}
    public String   getArtist() {return artist;}
    public String[] getGenre() {return genre;}
    public int      getYear() {return year;}
    public ImageView getCover(){return cover;}
    public String   getPath(){return path;}

    @Override
    public boolean equals(Object o){
        if(o instanceof Album) {
            Album alb = (Album) o;
            return ((this.title.equals(alb.title)) && (this.artist.equals(alb.artist)));
        }
        return false;
    }

    @Override
    public int hashCode(){
        return title.hashCode() + artist.hashCode();
    }
}
