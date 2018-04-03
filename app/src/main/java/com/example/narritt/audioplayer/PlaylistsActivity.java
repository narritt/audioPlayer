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
import com.example.narritt.audioplayer.misc.SongListMaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlaylistsActivity extends AppCompatActivity {

    enum ListViewStage{ ADD_ARTISTS, ADD_ALBUMS, ADD_SONGS, ADD_ALL_SONGS, PLAYLISTS, SONGS}

    private SongListMaster slm;

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
                        pickedArtist = slm.getArtistList().get(position);
                    }
                    changeStage();
                    break;
                case ADD_ALBUMS:
                    currStage = ListViewStage.ADD_SONGS;
                    pickedAlbum = slm.getAlbumList().get(position);
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

        slm = new SongListMaster(this);

        songView = (ListView)findViewById(R.id.playlist_list);
        songView.setOnItemClickListener(listener);
        btnCreatePlaylist = (Button) findViewById(R.id.btnAddPlaylist);

        changeStage();
    }



    protected void changeStage(){
        String allSongsPointString = this.getString(R.string.allSongsFromArtistList);
        switch (currStage){
            case PLAYLISTS:
                if (!slm.createPlaylistsList()){
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет существующих плейлистов", Toast.LENGTH_SHORT);
                    toast.show();
                }
                PlaylistAdapter pllAdt = new PlaylistAdapter(this, slm.getPlaylistList());
                songView.setAdapter(pllAdt);
                break;
            case SONGS:

                break;
            case ADD_ARTISTS:
                slm.createArtistList();
                ArtistAdapter artAdt = new ArtistAdapter(this, slm.getArtistList());
                songView.setAdapter(artAdt);
                break;
            case ADD_ALBUMS:
                if (pickedArtist.getName().equals(allSongsPointString)){
                    SongAdapter allSongsAdt = new SongAdapter(this, slm.getSongList());
                    songView.setAdapter(allSongsAdt);
                } else {
                    slm.createAlbumList(pickedArtist);
                    AlbumAdapter albAdt = new AlbumAdapter(this, slm.getAlbumList());
                    songView.setAdapter(albAdt);
                }
                break;
            case ADD_SONGS:
                slm.createSongsFromAlbum(pickedAlbum);
                AddSongAdapter songAdt = new AddSongAdapter(this, slm.getThisAlbumSongsList());
                songView.setAdapter(songAdt);
                break;
            case ADD_ALL_SONGS:
                slm.sortSongList();
                AddSongAdapter allSongsAdt = new AddSongAdapter(this, slm.getSongList());
                songView.setAdapter(allSongsAdt);
                break;
        }
    }

    /*
     * Interface logic
     */
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
