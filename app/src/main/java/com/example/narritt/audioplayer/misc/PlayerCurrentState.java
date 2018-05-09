package com.example.narritt.audioplayer.misc;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.util.Log;

import com.example.narritt.audioplayer.PlayerActivity;
import com.example.narritt.audioplayer.PlaylistDialogActivity;
import com.example.narritt.audioplayer.items.Playlist;
import com.example.narritt.audioplayer.items.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlayerCurrentState {
    private static final String TAG = "MyAudioPlayer";
    private static PlayerCurrentState _instance;
    private static MediaPlayer mediaPlayer;
    private static AudioManager audioManager;
    private static Equalizer equalizer;
    private ArrayList<Song>
                currentPlaylist,
                shuffledPlaylist,
                showingInActivityPlaylist;
    private Song currentSong;

    public boolean
            isMusicPlaying = false,
            isApplicationDestroying = false,
            isRandom;
            /*isLooping*/

    public enum Looping {
        OFF, ON, ON_ONE_SONG
    }
    public Looping isLooping;

    public PlayerCurrentState(ArrayList<Song> songs, Song currSong){
        this.currentPlaylist = songs;
        this.currentSong = currSong;
        this._instance = this;
    }
    public PlayerCurrentState(ArrayList<Song> songs, int pos){
        this.currentPlaylist = songs;
        this._instance = this;
        if(currentPlaylist!=null)
            this.currentSong = songs.get(pos);
        else
            this.currentSong = null;
        //this._instance = this;
    }
    public PlayerCurrentState(){
        this.currentPlaylist = null;
        this.currentSong = null;
        //TODO save state of isLooping and Random
        this.isLooping = Looping.OFF;
        this._instance = this;
    }

    //++++ GETTERS ++++
    public ArrayList<Song>  getCurrentPlaylist(){
        return currentPlaylist;
    }
    public ArrayList<Song>  getShuffledPlaylist(){
        shuffledPlaylist = (ArrayList<Song>) currentPlaylist.clone();
        Collections.shuffle(shuffledPlaylist);
        return shuffledPlaylist;
    }
    public ArrayList<Song>  getShowingInActivityPlaylist(){
        return showingInActivityPlaylist;
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
    public MediaPlayer      getMediaPlayer() {
        return mediaPlayer;
    }
    public Equalizer        getEqualizer() {
        return equalizer;
    }

    public static PlayerCurrentState get_instance(){
        return _instance;
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
    public void setCurrentPlaylistAndSong(Playlist playlist){
        this.currentPlaylist = playlist.getPlaylist();
        this.currentSong = playlist.getPlaylist().get(playlist.getCurrentPosition());
    }

    public void destroyMP(){
        mediaPlayer = null;
    }
    public void createNewMPCurrSong(PlayerActivity activity){
        mediaPlayer = MediaPlayer.create(activity, currentSong.getPath());
        equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
    }   //mediaplayer on currentSong
    public void createNewMP(PlayerActivity activity, Uri path){
        mediaPlayer = MediaPlayer.create(activity, path);
        equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
    }      //mediaplayer on custom uri song

    public void openPlaylistActivity(Context context, ArrayList<Song> playlist){
        showingInActivityPlaylist = playlist;
        Intent intent = new Intent(context, PlaylistDialogActivity.class);
        intent.putExtra("PLAYLIST", "OTHER");
        context.startActivity(intent);
    }
    public void openPlaylistActivity(Context context){
        showingInActivityPlaylist = currentPlaylist;
        //Log.i(TAG, "SHOWING PLAYLIST: \n" + showingInActivityPlaylist);
        Intent intent = new Intent(context, PlaylistDialogActivity.class);
        intent.putExtra("PLAYLIST", "CURRENT");
        context.startActivity(intent);
    }

    public void sortCurrentPlaylist(){
        Collections.sort(currentPlaylist, new Comparator<Song>(){
            public int compare(Song a, Song b){
                int cmp;
                if      (a.getPosition() > b.getPosition()) cmp = +1;
                else if (a.getPosition() < b.getPosition()) cmp = -1;
                else    cmp = 0;
                return cmp;
            }
        });
    }
}
