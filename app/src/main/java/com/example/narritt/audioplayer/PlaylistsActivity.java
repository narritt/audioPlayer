package com.example.narritt.audioplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.example.narritt.audioplayer.adapters.AddSongAdapter;
import com.example.narritt.audioplayer.adapters.AlbumAdapter;
import com.example.narritt.audioplayer.adapters.ArtistAdapter;
import com.example.narritt.audioplayer.adapters.PlaylistAdapter;
import com.example.narritt.audioplayer.adapters.SongAdapter;
import com.example.narritt.audioplayer.items.Album;
import com.example.narritt.audioplayer.items.Artist;
import com.example.narritt.audioplayer.items.Playlist;
import com.example.narritt.audioplayer.items.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlaylistsActivity extends AppCompatActivity {

    enum ListViewStage{ ADD_ARTISTS, ADD_ALBUMS, ADD_SONGS, ADD_ALL_SONGS, PLAYLISTS, SONGS}

    private ArrayList<Artist> artistList;
    private ArrayList<Album> albumList;
    private ArrayList<Song> thisAlbumSongsList;
    private ArrayList<Song> songList;
    private ArrayList<Playlist> playlistList;

    private ListView songView;
    private Button btnCreatePlaylist;

    private static ListViewStage currStage = ListViewStage.PLAYLISTS;
    private static Playlist pickedPlaylist = null;
    private static Artist pickedArtist = null;
    private static Album pickedAlbum = null;

    private OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (currStage) {
                case PLAYLISTS:
                    //currStage = ListViewStage.SONGS;
                    //pickedPlaylist = playlistList.get(position);
                    break;
                case SONGS:

                    break;
                case ADD_ARTISTS:
                    if(position == 0)
                        currStage = ListViewStage.ADD_ALL_SONGS;
                    else {
                        currStage = ListViewStage.ADD_ALBUMS;
                        pickedArtist = artistList.get(position);
                    }
                    changeStage();
                    break;
                case ADD_ALBUMS:
                    currStage = ListViewStage.ADD_SONGS;
                    pickedAlbum = albumList.get(position);
                    changeStage();
                    break;
                case ADD_SONGS:
                    Toast toast = Toast.makeText(getApplicationContext(), "Готов добавить трек в плейлист", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case ADD_ALL_SONGS:
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Готов добавить трек в плейлист из полного списка треков", Toast.LENGTH_SHORT);
                    toast1.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);

        songView = (ListView)findViewById(R.id.playlist_list);
        songView.setOnItemClickListener(listener);
        btnCreatePlaylist = (Button) findViewById(R.id.btnAddPlaylist);

        artistList = new ArrayList<Artist>();
        albumList = new ArrayList<Album>();
        songList = new ArrayList<Song>();
        thisAlbumSongsList = new ArrayList<Song>();
        playlistList = new ArrayList<>();

        getSongList();
        changeStage();
    }

    public void btnToPlayerClick(View view){
        finish();
    }
    public void btnAddPlaylistClick(View view){
        /*if(currStage == ListViewStage.PLAYLISTS || currStage == ListViewStage.SONGS) {
            currStage = ListViewStage.ADD_ARTISTS;
            btnCreatePlaylist.setText(R.string.createPlaylist);
        }
        if(currStage == ListViewStage.ADD_ARTISTS || currStage == ListViewStage.ADD_ALBUMS || currStage == ListViewStage.ADD_SONGS || currStage == ListViewStage.ADD_ALL_SONGS) {
            currStage = ListViewStage.PLAYLISTS;
            btnCreatePlaylist.setText(R.string.stopCreatingPlaylist);
        }*/
        currStage = ListViewStage.ADD_ARTISTS;
        changeStage();
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
        }
    }
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

    protected void changeStage(){
        String allSongsPointString = this.getString(R.string.allSongsFromArtistList);
        switch (currStage){
            case PLAYLISTS:
                playlistList.clear();
                //getPlaylistList();
                if (playlistList.isEmpty()){
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет существующих плейлистов", Toast.LENGTH_SHORT);
                    toast.show();
                }
                PlaylistAdapter pllAdt = new PlaylistAdapter(this, playlistList);
                songView.setAdapter(pllAdt);
                break;
            case SONGS:

                break;
            case ADD_ARTISTS:
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
            case ADD_ALBUMS:
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
            case ADD_SONGS:
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
                AddSongAdapter songAdt = new AddSongAdapter(this, thisAlbumSongsList);
                songView.setAdapter(songAdt);
                break;
            case ADD_ALL_SONGS:
                Collections.sort(songList, new Comparator<Song>() {
                    public int compare(Song a, Song b) {
                        return a.getTitle().compareTo(b.getTitle());
                    }
                });
                AddSongAdapter allSongsAdt = new AddSongAdapter(this, songList);
                songView.setAdapter(allSongsAdt);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        switch (currStage){
            case PLAYLISTS:
                super.onBackPressed();
                break;
            case SONGS:
                currStage = ListViewStage.PLAYLISTS;
                break;
            case ADD_ARTISTS:
                currStage = ListViewStage.PLAYLISTS;
                changeStage();
                break;
            case ADD_ALBUMS:
                currStage = ListViewStage.ADD_ARTISTS;
                pickedArtist = null;
                changeStage();
                break;
            case ADD_SONGS:
                currStage = ListViewStage.ADD_ALBUMS;
                pickedAlbum = null;
                changeStage();
                break;
            case ADD_ALL_SONGS:
                currStage = ListViewStage.ADD_ARTISTS;
                changeStage();
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
