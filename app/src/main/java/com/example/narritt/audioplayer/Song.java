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
    public Uri getFolderPath(){
        Log.i(TAG, "getFolderPath: Song path is " + path);
        String folderPath = path.substring(0, path.lastIndexOf("/"));
        Log.i(TAG, "getFolderPath: Folder path is " + folderPath);
        return Uri.parse("file://" + folderPath);
    }
    public String getFolderPathString(){
        return path.substring(0, path.lastIndexOf("/"));
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;

        Song song = (Song) o;

        if (id != song.id) return false;
        if (position != song.position) return false;
        if (title != null ? !title.equals(song.title) : song.title != null) return false;
        if (album != null ? !album.equals(song.album) : song.album != null) return false;
        if (artist != null ? !artist.equals(song.artist) : song.artist != null) return false;
        return path.equals(song.path);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (album != null ? album.hashCode() : 0);
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + position;
        result = 31 * result + path.hashCode();
        return result;
    }
}
