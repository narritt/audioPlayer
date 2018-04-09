package com.example.narritt.audioplayer.items;

import java.util.ArrayList;

/**
 * Created by Narritt on 23.03.2018.
 */

public class Playlist {
    private String name;
    private ArrayList<Song> playlist;

    public Playlist() {
    }
    public Playlist(String name) {
        this.name = name;
        playlist = new ArrayList<Song>();
    }
    public Playlist(String name, ArrayList<Song> playlist) {
        this.name = name;
        this.playlist = playlist;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Song> getPlaylist() {
        return playlist;
    }

    public void addSong(Song song){
        playlist.add(song);
    }
}
