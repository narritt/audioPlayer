package com.example.narritt.audioplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
        TextView TVnum_and_name = findViewById(R.id.numandname);
        TVnum_and_name.setText("№" + getString(R.string.tab) + getString(R.string.curr_playlist_message_songname));

        songListView = findViewById(R.id.playlist_song_list);

        ArrayList<String> playlist = new ArrayList<>();
        Log.i(TAG, "SECOND ACTIVITY SHOW PLAYLIST \n" + pcs.getShowingInActivityPlaylist());
        for (Song s : pcs.getShowingInActivityPlaylist()){
            playlist.add(s.getPosition() + getString(R.string.tab) + s.getTitle());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, playlist);
        songListView.setAdapter(adapter);
        songListView.setOnItemClickListener(listener);
        registerForContextMenu(songListView);
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PlaylistDialogActivity.this, "Click on " + pcs.getCurrentPlaylist().get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        };
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist_context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.context_menu_edit:
                //editItem(info.position); // метод, выполняющий действие при редактировании пункта меню
                return true;
            case R.id.context_menu_delete:
                //pcs.getShowingInActivityPlaylist().remove(info.position);   //метод, выполняющий действие при удалении пункта меню
                Log.i(TAG, "Song#" + info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
