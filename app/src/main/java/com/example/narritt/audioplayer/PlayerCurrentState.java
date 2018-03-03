package com.example.narritt.audioplayer;

import java.util.ArrayList;

/**
 * Created by Narritt on 03.03.2018.
 */

public class PlayerCurrentState {
    private ArrayList<Song> currentPlaylist;
    private Song currentSong;

    public boolean
            isMusicPlaying = false,
            isApplicationDestroying = false,
            isRandom,
            isLooping;

    public PlayerCurrentState(ArrayList<Song> songs, Song currSong){
        this.currentPlaylist = songs;
        this.currentSong = currSong;
    }
    public PlayerCurrentState(ArrayList<Song> songs, int pos){
        this.currentPlaylist = songs;
        if(currentPlaylist!=null)
            this.currentSong = songs.get(pos);
        else
            this.currentSong = null;
    }
    public PlayerCurrentState(){
        this.currentPlaylist = null;
        this.currentSong = null;
    }

    //++++ GETTERS ++++
    public ArrayList<Song> getCurrentPlaylist(){
        return currentPlaylist;
    }
    public Song getCurrentSong(){
        return currentSong;
    }
    public Song getSong(int pos) {
        return currentPlaylist.get(pos);
    }
    public int getPlaylistSize(){
        if (currentPlaylist != null)
            return currentPlaylist.size();
        else return 0;
    }
    public int getCurrentSongIndex(){
        return currentPlaylist.indexOf(currentSong);
    }

    /*public boolean isMusicPlaying(){
        return isMusicPlaying;
    }
    public boolean isApplicationDestroying() {
        return isApplicationDestroying;
    }
    public boolean isRandom(){
        return isRandom;
    }
    public boolean isLooping(){
        return isLooping;
    }*/

    //++++ SETTERS ++++
    public void setCurrentPlaylist(ArrayList<Song> songList){
        this.currentPlaylist = songList;
    }
    public void setCurrentSong(Song s){
        this.currentSong = s;
    }
    public void setCurrentPlaylistAndSong(ArrayList<Song> songList, Song s){
        this.currentPlaylist = songList;
        this.currentSong = s;
    }
    public void setCurrentPlaylistAndSong(ArrayList<Song> songList, int pos){
        this.currentPlaylist = songList;
        this.currentSong = songList.get(pos);
    }
    public void setCurrentPlaylistAndSong(PlayerCurrentState plState){
        this.currentPlaylist = plState.getCurrentPlaylist();
        this.currentSong = plState.getCurrentSong();
    }
}
