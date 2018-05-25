package com.example.narritt.audioplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.example.narritt.audioplayer.adapters.AlbumAdapter;
import com.example.narritt.audioplayer.adapters.ArtistAdapter;
import com.example.narritt.audioplayer.adapters.PlaylistAdapter;
import com.example.narritt.audioplayer.adapters.SongAdapter;
import com.example.narritt.audioplayer.items.Album;
import com.example.narritt.audioplayer.items.Artist;
import com.example.narritt.audioplayer.items.Playlist;
import com.example.narritt.audioplayer.misc.FileMaster;
import com.example.narritt.audioplayer.misc.SongListMaster;

public class PlaylistsActivity extends AppCompatActivity {

    private static final String TAG = "MyAudioPlayer";

    enum ListViewStage{ ADD_ARTISTS, ADD_ALBUMS, ADD_SONGS, ADD_ALL_SONGS, PLAYLISTS, SONGS}

    private SongListMaster slm;

    private ListView songView;
    private Button btnCreatePlaylist, btnBackToPlayer;

    private static ListViewStage currStage = ListViewStage.PLAYLISTS;
    private static Playlist pickedPlaylist = null;
    private static Artist pickedArtist = null;
    private static Album pickedAlbum = null;
    private static Playlist creatingPlaylist = new Playlist();

    private FileMaster FM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
        findViews();
        registerForContextMenu(songView);

        FM = new FileMaster(this);

        try {
            slm = new SongListMaster(this);
            changeStage();
        } catch (SecurityException e){
            Toast.makeText(this, getString(R.string.error_getting_permission_read), Toast.LENGTH_LONG).show();
            PlayerActivity plAct = PlayerActivity.getInstance();
            plAct.checkPermission();
        }
    }

    protected void changeStage(){
        String allSongsPointString = this.getString(R.string.allSongsFromArtistList);
        switch (currStage){
            case PLAYLISTS:
                if (!slm.createPlaylistsList()){
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет существующих плейлистов", Toast.LENGTH_SHORT);
                    toast.show();
                }
                creatingPlaylist.clear();
                btnCreatePlaylist.setText(R.string.createPlaylist);
                btnBackToPlayer.setText(R.string.backToPlayer);
                PlaylistAdapter pllAdt = new PlaylistAdapter(this, slm.getPlaylistList());
                songView.setAdapter(pllAdt);
                break;
            case SONGS:
                SongAdapter sAdt = new SongAdapter(this, pickedPlaylist.getPlaylist());
                songView.setAdapter(sAdt);
                break;
            case ADD_ARTISTS:
                slm.createArtistList();
                ArtistAdapter artAdt = new ArtistAdapter(this, slm.getArtistList());
                songView.setAdapter(artAdt);
                btnCreatePlaylist.setText(R.string.saveCurrentPlaylist);
                btnBackToPlayer.setText(R.string.stopCreatingPlaylist);
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
                Log.i(TAG, "Готов добавить трек в плейлист");
                slm.createSongsFromAlbum(pickedAlbum);
                SongAdapter songAdt = new SongAdapter(this, slm.getThisAlbumSongsList());
                songView.setAdapter(songAdt);
                break;
            case ADD_ALL_SONGS:
                Log.i(TAG, "Готов добавить трек в плейлист");
                slm.sortSongList();
                SongAdapter allSongsAdt = new SongAdapter(this, slm.getSongList());
                songView.setAdapter(allSongsAdt);
                break;
        }
    }

    /*
     * Interface logic
     */
    public void btnToPlayerClick(View view){
        if(currStage == ListViewStage.PLAYLISTS || currStage == ListViewStage.SONGS) {
            finish();
        } else
        if(currStage == ListViewStage.ADD_ARTISTS || currStage == ListViewStage.ADD_ALBUMS || currStage == ListViewStage.ADD_SONGS || currStage == ListViewStage.ADD_ALL_SONGS) {
            creatingPlaylist.clear();
            currStage = ListViewStage.PLAYLISTS;
            changeStage();
        }
    }
    public void btnAddPlaylistClick(View view){
        if(currStage == ListViewStage.PLAYLISTS || currStage == ListViewStage.SONGS) {
            currStage = ListViewStage.ADD_ARTISTS;
            changeStage();
        } else
        if(currStage == ListViewStage.ADD_ARTISTS || currStage == ListViewStage.ADD_ALBUMS || currStage == ListViewStage.ADD_SONGS || currStage == ListViewStage.ADD_ALL_SONGS) {
            createPlaylistNameDialog();
            currStage = ListViewStage.PLAYLISTS;
        }
    }

    private void createPlaylistNameDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.playlistname_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(promptsView);
        final EditText userInput = promptsView.findViewById(R.id.input_text);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                creatingPlaylist.setName(userInput.getText().toString());
                                FM.writePlaylist(creatingPlaylist);
                                changeStage();
                            }
                        })
                .setNegativeButton(R.string.playlistnamedialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    /*
     * SYSTEM LOGIC
     */

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        switch (currStage){
            case PLAYLISTS:
                inflater.inflate(R.menu.playlist_context_menu, menu);
                break;
            case SONGS:
                inflater.inflate(R.menu.playlist_song_context_menu, menu);
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.context_menu_playlist_delete:
                slm.deletePlaylist(info.position);
                changeStage();
                return true;
            case R.id.context_menu_playlist_song_delete:
                Playlist pl = slm.getPlaylist(pickedPlaylist);
                pl.removeSong(info.position);
                FM.writePlaylist(pl);
                changeStage();
                return true;
            default:
                return super.onContextItemSelected(item);
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
                changeStage();
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
    private void findViews(){
        songView = findViewById(R.id.playlist_list);
        songView.setOnItemClickListener(listener);
        btnCreatePlaylist = findViewById(R.id.btnAddPlaylist);
        btnBackToPlayer = findViewById(R.id.btnBackToPlayer);
    }

    private OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (currStage) {
                case PLAYLISTS:
                    currStage = ListViewStage.SONGS;
                    pickedPlaylist = slm.getPlaylistList().get(position);
                    changeStage();
                    break;
                case SONGS:
                    PlayerActivity.getInstance().play(pickedPlaylist.getPlaylist(), 0);
                    finish();
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
                    creatingPlaylist.addSong(slm.getThisAlbumSongsList().get(position));
                    Log.i(TAG, "Adding song to playlist; playlist now: " + creatingPlaylist.toString());
                    break;
                case ADD_ALL_SONGS:
                    creatingPlaylist.addSong(slm.getSongList().get(position));
                    Log.i(TAG, "Adding song to playlist; playlist now: " + creatingPlaylist.toString());
                    break;
            }
        }
    };
}
