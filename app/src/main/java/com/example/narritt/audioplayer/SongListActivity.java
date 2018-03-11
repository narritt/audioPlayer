package com.example.narritt.audioplayer;

import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.narritt.audioplayer.adapters.AlbumAdapter;
import com.example.narritt.audioplayer.adapters.ArtistAdapter;
import com.example.narritt.audioplayer.adapters.SongAdapter;
import com.example.narritt.audioplayer.items.Album;
import com.example.narritt.audioplayer.items.Artist;
import com.example.narritt.audioplayer.items.Song;

public class SongListActivity extends Activity {
    private static final String TAG = "MyAudioPlayer";

    enum ListViewStage{ ARTISTS, ALBUMS, SONGS, ALL_SONGS}

    private ArrayList<Artist> artistList;
    private ArrayList<Album> albumList;
    private ArrayList<Song> thisAlbumSongsList;
    private ArrayList<Song> songList;
    private ListView songView;

    private static ListViewStage currStage = ListViewStage.ARTISTS;
    private static Artist pickedArtist = null;
    private static Album pickedAlbum = null;

    private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (currStage) {
                case ARTISTS:
                    if(position == 0)
                        currStage = ListViewStage.ALL_SONGS;
                    else {
                        currStage = ListViewStage.ALBUMS;
                        pickedArtist = artistList.get(position);
                    }
                    changeStage();
                    break;
                case ALBUMS:
                    currStage = ListViewStage.SONGS;
                    pickedAlbum = albumList.get(position);
                    changeStage();
                    break;
                case SONGS:
                    PlayerActivity plAct = PlayerActivity.getInstance();
                    plAct.play(thisAlbumSongsList, position);
                    finish();
                    break;
                case ALL_SONGS:
                    plAct = PlayerActivity.getInstance();
                    plAct.play(songList, position);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songView = (ListView)findViewById(R.id.song_list);
        songView.setOnItemClickListener(listener);

        artistList = new ArrayList<Artist>();
        albumList = new ArrayList<Album>();
        songList = new ArrayList<Song>();
        thisAlbumSongsList = new ArrayList<Song>();

        getSongList();
        changeStage();
    }

    protected void changeStage(){
        String allSongsPointString = this.getString(R.string.allSongsFromArtistList);
        switch (currStage){
            case ARTISTS:
                artistList.clear();
                getArtistList();
                Collections.sort(artistList, new Comparator<Artist>(){
                    public int compare(Artist a, Artist b){
                        return a.getName().compareTo(b.getName());
                    }
                });
                artistList.add(0, new Artist(allSongsPointString));
                ArtistAdapter artAdt = new ArtistAdapter(this, artistList);
                songView.setAdapter(artAdt);
                break;
            case ALBUMS:
                albumList.clear();
                if (pickedArtist.getName().equals(allSongsPointString)){
                    SongAdapter allSongsAdt = new SongAdapter(this, songList);
                    songView.setAdapter(allSongsAdt);
                } else {
                    getAlbumList(pickedArtist);
                    Collections.sort(albumList, new Comparator<Album>() {
                        public int compare(Album a, Album b) {
                            return a.getTitle().compareTo(b.getTitle());
                        }
                    });
                    AlbumAdapter albAdt = new AlbumAdapter(this, albumList);
                    songView.setAdapter(albAdt);
                }
                break;
            case SONGS:
                thisAlbumSongsList.clear();
                getSongsFromAlbum(pickedAlbum);
                Collections.sort(thisAlbumSongsList, new Comparator<Song>(){
                    public int compare(Song a, Song b){
                        int cmp;
                        if      (a.getPosition() > b.getPosition()) cmp = +1;
                        else if (a.getPosition() < b.getPosition()) cmp = -1;
                        else    cmp = 0;
                        return cmp;
                    }
                });
                SongAdapter songAdt = new SongAdapter(this, thisAlbumSongsList);
                songView.setAdapter(songAdt);
                break;
            case ALL_SONGS:
                Collections.sort(songList, new Comparator<Song>() {
                    public int compare(Song a, Song b) {
                        return a.getTitle().compareTo(b.getTitle());
                    }
                });
                SongAdapter allSongsAdt = new SongAdapter(this, songList);
                songView.setAdapter(allSongsAdt);
                break;
        }
    }

    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
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
            /*int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);*/
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                int thisPosition = Integer.parseInt(musicCursor.getString(posColumn));
                String thisPath = musicCursor.getString(pathColumn);
                //long albumId = musicCursor.getLong(albumIdColumn);
                //String coverPath = getCoverArtPath(albumId);
                //Log.i(TAG, "Cover path is " + coverPath);

                //defense from whatsapp audiorecords and google talk notifications and ringtones
                if((!thisPath.contains("WhatsApp Audio")) && !thisPath.contains("com.google.android.talk"))
                    songList.add(new Song(thisId, thisTitle, thisAlbum, thisArtist, thisPosition, thisPath));
            }
            while (musicCursor.moveToNext());
        }
    }

    /*private String getCoverArtPath(long albumId) {
        Cursor albumCursor = getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }*/

    public void getArtistList() {
        for (Song song:songList) {
            Artist songArtist = new Artist(song.getArtist());
            if (!artistList.contains(songArtist)){
                artistList.add(songArtist);
            }
        }
    }

    public void getAlbumList(Artist pickedArtist) {
        //defense
        if (pickedArtist == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Выбранный артист - Null", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            for (Song song : songList) {
                //Album songAlbum = new Album(song.getAlbum(), song.getArtist());
                Album songAlbum = new Album(song);
                if ((!albumList.contains(songAlbum)) && (songAlbum.getArtist().equals(pickedArtist.getName()))) {
                    albumList.add(songAlbum);
                }
            }
        }
    }

    public void getSongsFromAlbum(Album pickedAlbum) {
        //defense
        if (pickedArtist == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Выбранный альбом - Null", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            thisAlbumSongsList.clear();
            for (Song song : songList) {
                if (song.getAlbum().equals(pickedAlbum.getTitle())) {
                    thisAlbumSongsList.add(song);
                }
            }

        }
    }

    public void btnBackToPlayerClick(View view){
        finish();
    }

    @Override
    public void onBackPressed() {
        switch (currStage){
            case ARTISTS:
                super.onBackPressed();
                break;
            case ALBUMS:
                currStage = ListViewStage.ARTISTS;
                pickedArtist = null;
                changeStage();
                break;
            case SONGS:
                currStage = ListViewStage.ALBUMS;
                pickedAlbum = null;
                changeStage();
                break;
            case ALL_SONGS:
                currStage = ListViewStage.ARTISTS;
                changeStage();
                break;
            default:
                Toast toast = Toast.makeText(getApplicationContext(), "Неверный CurrStage. Сообщите мне об этом!", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onBackPressed();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
}
