package com.example.narritt.audioplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.narritt.audioplayer.items.Artist;
import com.example.narritt.audioplayer.items.Song;

import java.util.ArrayList;

public class PlaylistsActivity extends AppCompatActivity {

    enum ListViewStage{ PLAYLISTS, SONGS}

    private ArrayList<Song> songList;
    private ListView songView;

    private static SongListActivity.ListViewStage currStage = SongListActivity.ListViewStage.ARTISTS;
    private static Artist pickedArtist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
    }

    public void btnToPlayerClick(View view){
        finish();
    }
    public void btnAddPlaylistClick(View view){
        //TODO: creating playlist logic
    }
}
