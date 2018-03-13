package com.example.narritt.audioplayer.misc;

import android.util.Log;

import com.example.narritt.audioplayer.items.Song;

import java.util.ArrayList;

public class PlayerCurrentState {
    private static final String TAG = "MyAudioPlayer";
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
    public ArrayList<Song>  getCurrentPlaylist(){
        return currentPlaylist;
    }
    public Song             getCurrentSong(){
        return currentSong;
    }
    public Song             getSong(int pos) {
        return currentPlaylist.get(pos);
    }
    public int              getPlaylistSize(){
        if (currentPlaylist != null)
            return currentPlaylist.size();
        else return 0;
    }
    public int              getCurrentSongIndex(){
        return currentPlaylist.indexOf(currentSong);
    }
    public long             getCurrentAlbumID() {
        Log.i(TAG, "Current album id is " + currentSong.getAlbumId());
        return currentSong.getAlbumId();
    }

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
        //Log.i(TAG, "PlayerCurrentState, setCurrentPlaylistAndSong, songlist is null : " + Boolean.toString(songList == null ? true : false));
        if(songList != null) {
            this.currentSong = songList.get(pos);
        }
        else{
            currentSong = null;
            Log.e(TAG, "PlayerCurrentState, setCurrentPlaylistAndSong ERROR: playlist = null");
        }
    }
    public void setCurrentPlaylistAndSong(PlayerCurrentState plState){
        this.currentPlaylist = plState.getCurrentPlaylist();
        this.currentSong = plState.getCurrentSong();
    }
}
