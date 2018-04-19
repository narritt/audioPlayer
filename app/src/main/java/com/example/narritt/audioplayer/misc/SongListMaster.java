package com.example.narritt.audioplayer.misc;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.narritt.audioplayer.R;
import com.example.narritt.audioplayer.items.Album;
import com.example.narritt.audioplayer.items.Artist;
import com.example.narritt.audioplayer.items.Playlist;
import com.example.narritt.audioplayer.items.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SongListMaster {
    //private static final String TAG = "MyAudioPlayer";
    private Context ctx;

    private ArrayList<Artist> artistList;
    private ArrayList<Album> albumList;
    private ArrayList<Song> thisAlbumSongsList;
    private ArrayList<Song> songList;
    private ArrayList<Playlist> playlistList;

    public SongListMaster(Context context){
        ctx = context;
        artistList =            new ArrayList<>();
        albumList =             new ArrayList<>();
        songList =              new ArrayList<>();
        thisAlbumSongsList =    new ArrayList<>();
        playlistList =          new ArrayList<>();
        createSongList();
    }

    public ArrayList<Artist>    getArtistList(){
        return artistList;
    }
    public ArrayList<Album>     getAlbumList(){
        return albumList;
    }
    public ArrayList<Song>      getThisAlbumSongsList(){
        return thisAlbumSongsList;
    }
    public ArrayList<Song>      getSongList(){
        return songList;
    }
    public ArrayList<Playlist>  getPlaylistList(){
        return playlistList;
    }

    private void createSongList() {
        ContentResolver musicResolver = ctx.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int posColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TRACK);
            int pathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                int thisPosition = Integer.parseInt(musicCursor.getString(posColumn));
                String thisPath = musicCursor.getString(pathColumn);
                long albumId = musicCursor.getLong(albumIdColumn);
                //String coverPath = getCoverArtPath(albumId);
                //Log.i(TAG, "getSongList: Cover path is " + coverPath + "; album id is = " + albumId);

                //defense from whatsapp audiorecords and google talk notifications and ringtones
                if((!thisPath.contains("WhatsApp Audio")) && !thisPath.contains("com.google.android.talk"))
                    songList.add(new Song(thisId, thisTitle, thisAlbum, thisArtist, thisPosition, thisPath, albumId));
            }
            while (musicCursor.moveToNext());

            musicCursor.close();
        }
    }

    public void createArtistList() {
        artistList.clear();
        for (Song song:songList) {
            Artist songArtist = new Artist(song.getArtist());
            if (!artistList.contains(songArtist)){
                artistList.add(songArtist);
            }
        }
        Collections.sort(artistList, new Comparator<Artist>(){
            public int compare(Artist a, Artist b){
                return a.getName().compareTo(b.getName());
            }
        });
        String allSongsPointString = ctx.getString(R.string.allSongsFromArtistList);
        artistList.add(0, new Artist(allSongsPointString));
    }
    public void createAlbumList(Artist pickedArtist) {
        albumList.clear();
        for (Song song : songList) {
            Album songAlbum = new Album(song);
            if ((!albumList.contains(songAlbum)) && (songAlbum.getArtist().equals(pickedArtist.getName()))) {
                albumList.add(songAlbum);
            }
        }
        Collections.sort(albumList, new Comparator<Album>() {
            public int compare(Album a, Album b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }
    public void createSongsFromAlbum(Album pickedAlbum) {
        thisAlbumSongsList.clear();
        for (Song song : songList) {
            if (song.getAlbum().equals(pickedAlbum.getTitle())) {
                thisAlbumSongsList.add(song);
            }
        }
        Collections.sort(thisAlbumSongsList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                int cmp;
                if      (a.getPosition() > b.getPosition()) cmp = +1;
                else if (a.getPosition() < b.getPosition()) cmp = -1;
                else    cmp = 0;
                return cmp;
            }
        });
    }
    public boolean createPlaylistsList(){
        //playlistList.clear();
        //TODO method returning playlist[] of created playlists;
        //return FALSE if there is no created playlists
        return playlistList.isEmpty();
    }

    public void clearAlbumList(){
        albumList.clear();
    }
    public void sortSongList(){
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }
}
