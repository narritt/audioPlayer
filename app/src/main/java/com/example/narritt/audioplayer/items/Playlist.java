package com.example.narritt.audioplayer.items;

import java.util.ArrayList;

public class Playlist {
    private String name;
    private ArrayList<Song> playlist;
    private int currentPosition;

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
    public Playlist(ArrayList<Song> playlist, int currentPosition){
        this.playlist = playlist;
        this.currentPosition = currentPosition;
    }

    public void setName(String n){
        name = n;
    }

    public String getName() {
        return name;
    }
    public int getCurrentPosition() {
        return currentPosition;
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

    @Override
    public String toString() {
        return "Playlist{" +
                "name='" + name + '\'' +
                ", playlist=" + playlist +
                ", currentPosition=" + currentPosition +
                '}';
    }
}
