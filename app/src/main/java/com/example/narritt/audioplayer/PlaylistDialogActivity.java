package com.example.narritt.audioplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.narritt.audioplayer.items.Song;
import com.example.narritt.audioplayer.misc.PlayerCurrentState;

import java.util.ArrayList;

public class PlaylistDialogActivity extends Activity {
    private static final String TAG = "MyAudioPlayer";

    ListView songListView;

    private PlayerCurrentState pcs = PlayerCurrentState.get_instance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_dialog);

        songListView = findViewById(R.id.playlist_song_list);
        TextView TVnum_and_name = findViewById(R.id.numandname);
        TVnum_and_name.setText("№" + getString(R.string.tab) + getString(R.string.curr_playlist_message_songname));

        ArrayList<String> playlist = new ArrayList<>();
        for (Song s : pcs.getCurrentPlaylist()){
            playlist.add(s.getPosition() + getString(R.string.tab) + s.getTitle());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, playlist);
        songListView.setAdapter(adapter);
        songListView.setOnItemClickListener(listener);
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PlaylistDialogActivity.this, "Click on " + pcs.getCurrentPlaylist().get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        };
}
