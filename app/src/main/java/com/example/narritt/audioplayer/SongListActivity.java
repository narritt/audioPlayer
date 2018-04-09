package com.example.narritt.audioplayer;

import android.app.Activity;
import android.os.Bundle;
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
import com.example.narritt.audioplayer.misc.SongListMaster;

public class SongListActivity extends Activity {
    //private static final String TAG = "MyAudioPlayer";

    enum ListViewStage{ ARTISTS, ALBUMS, SONGS, ALL_SONGS}

    private ListView songView;

    private SongListMaster slm;

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
                        pickedArtist = slm.getArtistList().get(position);
                    }
                    changeStage();
                    break;
                case ALBUMS:
                    currStage = ListViewStage.SONGS;
                    pickedAlbum = slm.getAlbumList().get(position);
                    changeStage();
                    break;
                case SONGS:
                    PlayerActivity plAct = PlayerActivity.getInstance();
                    plAct.play(slm.getThisAlbumSongsList(), position);
                    finish();
                    break;
                case ALL_SONGS:
                    plAct = PlayerActivity.getInstance();
                    plAct.play(slm.getSongList(), position);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songView = findViewById(R.id.song_list);
        songView.setOnItemClickListener(listener);

        slm = new SongListMaster(this);

        changeStage();
    }

    protected void changeStage(){
        String allSongsPointString = this.getString(R.string.allSongsFromArtistList);
        switch (currStage){
            case ARTISTS:
                slm.createArtistList();
                ArtistAdapter artAdt = new ArtistAdapter(this, slm.getArtistList());
                songView.setAdapter(artAdt);
                break;
            case ALBUMS:
                slm.clearAlbumList();
                if (pickedArtist.getName().equals(allSongsPointString)){
                    SongAdapter allSongsAdt = new SongAdapter(this, slm.getSongList());
                    songView.setAdapter(allSongsAdt);
                } else {
                    slm.createAlbumList(pickedArtist);
                    AlbumAdapter albAdt = new AlbumAdapter(this, slm.getAlbumList());
                    songView.setAdapter(albAdt);
                }
                break;
            case SONGS:
                slm.createSongsFromAlbum(pickedAlbum);
                SongAdapter songAdt = new SongAdapter(this, slm.getThisAlbumSongsList());
                songView.setAdapter(songAdt);
                break;
            case ALL_SONGS:
                slm.sortSongList();
                SongAdapter allSongsAdt = new SongAdapter(this, slm.getSongList());
                songView.setAdapter(allSongsAdt);
                break;
        }
    }

    /*
     * Interface logic
     */
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
