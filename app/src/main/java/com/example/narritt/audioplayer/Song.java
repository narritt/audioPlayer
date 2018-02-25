package com.example.narritt.audioplayer;

import android.net.Uri;

/**
 * Created by Narritt on 28.11.2017.
 */

public class Song {
    private long id;
    private String title;
    private String album;
    private String artist;
    private int position;
    private String path;

    public Song(long songID, String songTitle, String songAlbum, String songArtist, int songPosition, String songPath) {
        id = songID;
        title = songTitle;
        album = songAlbum;
        artist = songArtist;
        position = songPosition;
        path = songPath;
    }
    public Song(String data){
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
    public String getAlbum() {return album;}
    public String getArtist(){return artist;}
    public int getPosition() {return position;}
    public Uri getPath()  {return Uri.parse("file://" + path);}

    public String toString(){
        return ("ID:" + this.id +
                ";TITLE:" + this.title +
                ";ALBUM:" + this.album +
                ";ARTIST:" + this.artist +
                ";POSITION:" + this.position +
                ";PATH:" + this.path);
    }
    public Song toSong(String str){
        String params[] = str.split(";");
        Song song = new Song(
                Long.parseLong(params[0].substring(4)), //ID
                params[1].substring(7),     //TITLE
                params[2].substring(7),     //ALBUM
                params[3].substring(8),     //ARTIST
                Integer.parseInt(params[4].substring(10)),    //POSITION
                params[5].substring(6)      //PATH
        );
        return song;
    }
}
