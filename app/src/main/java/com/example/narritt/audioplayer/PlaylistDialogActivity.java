package com.example.narritt.audioplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.narritt.audioplayer.items.Song;
import com.example.narritt.audioplayer.misc.PlayerCurrentState;

import java.util.ArrayList;

public class PlaylistDialogActivity extends Activity {
    private static final String TAG = "MyAudioPlayer";

    Bundle extras;
    ListView songListView;
    TextView TVnum_and_name;

    private PlayerCurrentState pcs = PlayerCurrentState.get_instance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_dialog);
        extras = getIntent().getExtras();
        findViews();
        reloadListView();
        songListView.setOnItemClickListener(listener);
        registerForContextMenu(songListView);
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pcs.getPlayerActivity().play(pcs.getShowingInActivityPlaylist(), position);
                finish();
            }
        };
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist_dialog_act_context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.context_menu_song_edit:
                //editItem(info.position); // метод, выполняющий действие при редактировании пункта меню
                return true;
            case R.id.context_menu_song_delete:
                pcs.getShowingInActivityPlaylist().remove(info.position);   //метод, выполняющий действие при удалении пункта меню
                reloadListView();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void reloadListView(){
        ArrayList<String> playlist = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
        try {
            for (Song s : pcs.getShowingInActivityPlaylist()){
                playlist.add(s.getPosition() + getString(R.string.tab) + s.getTitle());
            }

        } catch (NullPointerException e){
            TVnum_and_name.setVisibility(View.GONE);
            playlist.add(getApplicationContext().getString(R.string.error_playlist_empty));
        } catch (Exception e){
            TVnum_and_name.setVisibility(View.GONE);
            playlist.add(e.getMessage());
        } finally {
            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, playlist);
            songListView.setAdapter(adapter);
        }

    }

    private void findViews(){
        TVnum_and_name = findViewById(R.id.numandname);
        TVnum_and_name.setText("№" + getString(R.string.tab) + getString(R.string.curr_playlist_message_songname));
        songListView = findViewById(R.id.playlist_song_list);
    }

    @Override
    protected void onStop() {
        //if (extras.getString("PLAYLIST").equals("CURRENT"))
            //pcs.setCurrentPlaylist(pcs.getShowingInActivityPlaylist());
        super.onStop();
    }
}
