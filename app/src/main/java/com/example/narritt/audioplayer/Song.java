package com.example.narritt.audioplayer;

import android.net.Uri;
import android.util.Log;

/**
 * Created by Narritt on 28.11.2017.
 */

public class Song {
    private static final String TAG = "MyAudioPlayer";

    private long id;
    private String title;
    private String album;
    private String artist;
    private int position;
    private String path;

    protected Song(long songID, String songTitle, String songAlbum, String songArtist, int songPosition, String songPath) {
        id = songID;
        title = songTitle;
        album = songAlbum;
        artist = songArtist;
        position = songPosition;
        path = songPath;
    }
    protected Song(String data){
        Song tmpSong = this.toSong(data);
        id = tmpSong.id;
        title = tmpSong.title;
        album = tmpSong.album;
        artist = tmpSong.artist;
        position = tmpSong.position;
        path = tmpSong.path;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    protected String getAlbum() {return album;}
    protected String getArtist(){return artist;}
    protected int getPosition() {return position;}
    protected Uri getPath()  {return Uri.parse("file://" + path);}

    public String toString(){
        return ("ID:" + this.id +
                ";TITLE:" + this.title +
                ";ALBUM:" + this.album +
                ";ARTIST:" + this.artist +
                ";POSITION:" + this.position +
                ";PATH:" + this.path);
    }
    protected Song toSong(String str){
        String params[] = str.split(";");
        Song song = new Song(
                Long.parseLong(params[0].substring(3)), //ID
                params[1].substring(6),     //TITLE
                params[2].substring(6),     //ALBUM
                params[3].substring(7),     //ARTIST
                Integer.parseInt(params[4].substring(9)),    //POSITION
                params[5].substring(5)      //PATH
        );
        return song;
    }
}
