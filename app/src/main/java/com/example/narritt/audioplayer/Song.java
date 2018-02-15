package com.example.narritt.audioplayer;

/**
 * Created by Narritt on 28.11.2017.
 */

public class Song {
    private long id;
    private String title;
    private String album;
    private String artist;
    private int position;

    public Song(long songID, String songTitle, String songAlbum, String songArtist, int position) {
        id = songID;
        title = songTitle;
        album = songAlbum;
        artist = songArtist;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getAlbum() {return album;}
    public String getArtist(){return artist;}
    public int getPosition() {return position;}
}
