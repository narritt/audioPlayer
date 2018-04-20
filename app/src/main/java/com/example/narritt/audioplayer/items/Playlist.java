package com.example.narritt.audioplayer.items;

import java.util.ArrayList;

public class Playlist {
    private String name;
    private ArrayList<Song> playlist;

    public Playlist() {
        //name = new String();
        playlist = new ArrayList<>();
    }
    public Playlist(String name) {
        this.name = name;
        playlist = new ArrayList<>();
    }
    public Playlist(String name, ArrayList<Song> playlist) {
        this.name = name;
        this.playlist = playlist;
    }
    public Playlist(ArrayList<Song> playlist){
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
    public void removeSong(int index) {
        playlist.remove(index);
    }
    public void removeSong(Song song) {
        playlist.remove(song);
    }
    public void clear() {
        playlist.clear();
    }
}
