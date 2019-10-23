package com.example.narritt.audioplayer;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.narritt.audioplayer.Preferences.PrefActivity;
import com.example.narritt.audioplayer.items.Song;
import com.example.narritt.audioplayer.misc.FileMaster;
import com.example.narritt.audioplayer.misc.PlayerCurrentState;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//import static android.app.Notification.FLAG_ONGOING_EVENT;



//GIT BRANCH TEST 11

public class PlayerActivity extends Activity {
    private static final String TAG = "MyAudioPlayer";
    static PlayerActivity instance;

    public PlayerCurrentState pcs = new PlayerCurrentState(this);

    ImageButton btnPlay, btnShuffle, btnLoop, btnNext, btnPrev;
    TextView artistName, albumName, songName, songDuration, currentSongPosition;
    ImageView imgAlbum;
    SeekBar progressControl;

    double startTime, finalTime;

    FileMaster fileMaster;
    Handler myHandler = new Handler();      //handler for runnable UpdateSongTime method

    //TODO notification
    NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Log.i(TAG, "OnCreate in " + this.getLocalClassName());
        instance = this;
        checkPermission();
        findViews();

        //getting written current song from file
        fileMaster = new FileMaster(getApplicationContext());
        pcs.setCurrentPlaylistAndSong(fileMaster.readCurrentPlaylist());
        if (pcs.isMPNull() && pcs.getCurrentSong() != null) {
            pcs.createNewMPCurrSong(this);
            prepareInterface(pcs.getCurrentSong());
        }
        if(!pcs.isMPNull())
            pcs.getMediaPlayer().setOnCompletionListener(MPCompletionListener);

        progressControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean pausedOnTouch;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!pcs.isMPNull()) {
                    if (fromUser) {
                        pcs.getMediaPlayer().seekTo(progress);
                        UpdateSongTimeManualy();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(!pcs.isMPNull()) {
                    if (pcs.getMediaPlayer().isPlaying()) {
                        pcs.getMediaPlayer().pause();
                        pausedOnTouch = true;
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!pcs.isMPNull()) {
                    if ((!pcs.getMediaPlayer().isPlaying()) && (pausedOnTouch)) {
                        pcs.getMediaPlayer().start();
                        pausedOnTouch = false;
                    }
                }
            }
        });

        resetDemensionsImgAlbum();

        //nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /*
     * MEDIAPLAYER LOGIC
     */
    public void play(ArrayList<Song> songs, int position){
        pcs.isMusicPlaying = true;
        if(!pcs.isMPNull()) //if its a first start of application
            pcs.getMediaPlayer().stop();
        pcs.setCurrentPlaylistAndSong(songs, position);
        pcs.createNewMP(this, songs.get(position).getPath());
        prepareInterface(songs.get(position));

        pcs.getMediaPlayer().setOnCompletionListener(MPCompletionListener);
        pcs.getMediaPlayer().start();

        //TODO notification widget
        /*if(pcs.isMusicPlaying){
            Log.i(TAG, "Pull notification");
            Notification notif = new Notification(R.mipmap.ic_launcher, "Song", System.currentTimeMillis());
            notif.flags |= FLAG_ONGOING_EVENT;
            nm.notify(1, notif);
        }*/
    }
    public void playDependingOn_isMusicPlaying(ArrayList<Song> playlist, int pos){
        if(pcs.isMusicPlaying)
            play(playlist, pos);
        else{
            play(playlist, pos);
            stopPlay();
        }
    }
    public void pausePlay(){
        pcs.getMediaPlayer().pause();
        pcs.isMusicPlaying = false;
        btnPlay.setImageResource(R.drawable.play);
        //nm.cancel(1);
    }
    public void resumePlay(){
        pcs.getMediaPlayer().start();
        pcs.isMusicPlaying = true;
        btnPlay.setImageResource(R.drawable.pause);
        myHandler.postDelayed(UpdateSongTime,100);
    }
    private void stopPlay(){
        pcs.getMediaPlayer().stop();
        pcs.isMusicPlaying = false;
        btnPlay.setImageResource(R.drawable.play);
        try {
            pcs.getMediaPlayer().prepare();
            pcs.getMediaPlayer().seekTo(0);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * INTERFACE LOGIC
     */
    public void prepareInterface(Song song){
        if(pcs.isMusicPlaying)
            btnPlay.setImageResource(R.drawable.pause);
        else
            btnPlay.setImageResource(R.drawable.play);

        artistName.setText(song.getArtist());
        albumName.setText(song.getAlbum());
        songName.setText(song.getTitle());

        startTime = pcs.getMediaPlayer().getCurrentPosition();
        finalTime = pcs.getMediaPlayer().getDuration();

        loadAlbumCover(song);

        setTextCurrentSongTime(startTime);
        setTextFinalSongTime(finalTime);

        progressControl.setMax((int)finalTime);
        progressControl.setProgress((int)startTime);
        if(pcs.isMusicPlaying)
            myHandler.postDelayed(UpdateSongTime,100);
    }
    public void setTextCurrentSongTime(double currentTime){
        if ((TimeUnit.MILLISECONDS.toSeconds((long) currentTime) % 60) < 10)
            currentSongPosition.setText(String.format(Locale.ENGLISH, "%d:0%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) currentTime))));
        else
            currentSongPosition.setText(String.format(Locale.ENGLISH, "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) currentTime))));
    }
    public void setTextFinalSongTime(double _finalTime){
        if((TimeUnit.MILLISECONDS.toSeconds((long) _finalTime) % 60) < 10)
            songDuration.setText(String.format(Locale.ENGLISH, "%d:0%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) _finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) _finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) _finalTime))));
        else
            songDuration.setText(String.format(Locale.ENGLISH, "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) _finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) _finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) _finalTime))));
    }
    public void loadAlbumCover(Song song){
        boolean coverFound = false;
        String coverArtPath = getCoverArtPath(song.getAlbumId());

        if( coverArtPath != null) {
            File imgFile = new File(coverArtPath);
            Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            if(bmp != null) {
                imgAlbum.setImageBitmap(bmp);
                coverFound = true;
            }
        }

        if(!coverFound) {
            File dir = new File(song.getFolderPathString());
            for (File file : dir.listFiles()) {
                String fileName = file.getName().toLowerCase();
                if (fileName.contains("cover.jpg") || fileName.contains("front.jpg")) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imgAlbum.setImageBitmap(myBitmap);
                    coverFound = true;
                    break;
                }
            }
        }

        if(!coverFound){
            imgAlbum.setImageResource(R.drawable.octave_wb);
        }

        //DIMENSIONS OF IMGALBUM
        resetDemensionsImgAlbum();
    }
    private void resetDemensionsImgAlbum(){
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgAlbum.getLayoutParams();
        params.width = point.x;
        params.height = point.x;
        imgAlbum.setLayoutParams(params);
    }
    private String getCoverArtPath(long albumId) {
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
    }
    private void UpdateSongTimeManualy(){
        startTime = pcs.getMediaPlayer().getCurrentPosition();
        setTextCurrentSongTime(startTime);
        progressControl.setProgress((int) startTime);
    }
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            if(!pcs.isApplicationDestroying) {
                startTime = pcs.getMediaPlayer().getCurrentPosition();
                setTextCurrentSongTime(startTime);
                progressControl.setProgress((int) startTime);
                myHandler.postDelayed(this, 100);
            }
        }
    };

    /*
     * BUTTON LOGIC
     */
    public void btnPlayClick(View view){
        try {
            if (pcs.isMusicPlaying)
                pausePlay();
            else
                resumePlay();
        } catch (Exception e) {
            if(pcs.getCurrentPlaylist() == null)
                Log.e(TAG, "Current Playlist == null. It's okey, if its first start of application");
            Log.e(TAG, e.getClass() + e.getMessage());
            Toast.makeText(this, R.string.error_play_pick_track, Toast.LENGTH_SHORT).show();
        }
    }
    public void btnPrevClick(View view){
        if(pcs.getCurrentPlaylist() != null) {
            if (pcs.getMediaPlayer().getCurrentPosition() > 1500) {
                pcs.getMediaPlayer().seekTo(0);
                UpdateSongTimeManualy();
            } else {
                int indexCurrSong = pcs.getCurrentSongIndex();
                if (indexCurrSong == 0) {
                    playDependingOn_isMusicPlaying(pcs.getCurrentPlaylist(), pcs.getPlaylistSize() - 1);
                } else {
                    playDependingOn_isMusicPlaying(pcs.getCurrentPlaylist(), indexCurrSong - 1);
                }
            }
        }
    }
    public void btnNextClick(View view){
        if(pcs.getCurrentPlaylist() != null) {
            int indexCurrSong = pcs.getCurrentSongIndex();
            if (indexCurrSong == pcs.getPlaylistSize() - 1) {   //if this is last song of playlist
                playDependingOn_isMusicPlaying(pcs.getCurrentPlaylist(), 0);
                if (pcs.isLooping == PlayerCurrentState.Looping.OFF)
                    stopPlay();
            } else {
                playDependingOn_isMusicPlaying(pcs.getCurrentPlaylist(), indexCurrSong + 1);
            }
        }
    }
    public void btnLoopClick(View view){
        if (pcs.isLooping == PlayerCurrentState.Looping.ON) {
            btnLoop.setImageResource(R.drawable.loop_on_one);
            pcs.isLooping = PlayerCurrentState.Looping.ON_ONE_SONG;
            return;
        }
        if (pcs.isLooping == PlayerCurrentState.Looping.ON_ONE_SONG){
            btnLoop.setImageResource(R.drawable.loop_off1);
            pcs.isLooping = PlayerCurrentState.Looping.OFF;
            return;
        }
        if (pcs.isLooping == PlayerCurrentState.Looping.OFF){
            btnLoop.setImageResource(R.drawable.loop_on1);
            pcs.isLooping = PlayerCurrentState.Looping.ON;
            return;
        }
    }
    public void btnRandClick(View view){
        try {
            if (pcs.isRandom) {
                pcs.sortCurrentPlaylist();
            }
            else {
                pcs.setCurrentPlaylist(pcs.getShuffledPlaylist());
            }
        } catch (NullPointerException e){
            //empty current playlist
        }
        catch (Exception e) {
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (pcs.isRandom)
                btnShuffle.setImageResource(R.drawable.rand_off);
            else
                btnShuffle.setImageResource(R.drawable.rand_on);
            pcs.isRandom = !pcs.isRandom;
        }

    }
    public void btnToArtists(View view){
        Intent intent = new Intent(PlayerActivity.this, SongListActivity.class);
        startActivity(intent);
    }
    public void btnToPlaylists(View view){
        Intent intent = new Intent(PlayerActivity.this, PlaylistsActivity.class);
        startActivity(intent);
    }
    public void btnPrefClick(View view){
        Intent intent = new Intent(PlayerActivity.this, PrefActivity.class);
        startActivity(intent);
    }
    public void btnCurrPlaylistClick(View view){
        //Intent intent = new Intent(PlayerActivity.this, PlaylistDialogActivity.class);
        //startActivity(intent);
        pcs.openPlaylistActivity(this);
    }

    /*
     * SYSTEM LOGIC
     */
    private void findViews(){
        btnPlay =               findViewById(R.id.btnPlay);
        btnLoop =               findViewById(R.id.btnLoop);
        btnShuffle =               findViewById(R.id.btnRand);
        btnNext =               findViewById(R.id.btnNext);
        btnPrev =               findViewById(R.id.btnPrev);
        artistName =            findViewById(R.id.strArtistName);
        albumName =             findViewById(R.id.strAlbumName);
        songName =              findViewById(R.id.strSongName);
        songDuration =          findViewById(R.id.strSongDuration);
        currentSongPosition =   findViewById(R.id.strCurrentSongPosition);
        progressControl =       findViewById(R.id.progressControl);
        imgAlbum =              findViewById(R.id.imgAlbum);
    }
    public void checkPermission(){
        int requestCode = 0;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block this thread waiting for the user's response! After the user sees the explanation, try again to request the permission.
                Toast.makeText(this, getString(R.string.permission_read), Toast.LENGTH_LONG).show();
            } else {
                // No explanation needed, we can request the permission.
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                // requestCode is an app-defined int constant. The callback method gets the result of the request.
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        }
    }
    @Override
    protected void onStop() {
        fileMaster.writeCurrentPlaylist(pcs);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        Log.i(TAG, "On destroy");
        pcs.isApplicationDestroying = true;
        releaseMP();
        super.onDestroy();
    }

    /*  Освобождает используемые проигрывателем ресурсы */
    private void releaseMP() {
        if (!pcs.isMPNull()) {
            stopPlay();
            try {
                pcs.getMediaPlayer().release();
                pcs.destroyMP();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static PlayerActivity getInstance() {
        return instance;
    }

    MediaPlayer.OnCompletionListener MPCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

            int indexCurrSong = pcs.getCurrentSongIndex();

            if(pcs.isLooping == PlayerCurrentState.Looping.ON_ONE_SONG){
                Log.i(TAG, "Looping is: ONE_SONG");
                play(pcs.getCurrentPlaylist(), indexCurrSong);
                return;
            }

            if (indexCurrSong >= pcs.getPlaylistSize() - 1) {
                play(pcs.getCurrentPlaylist(), 0);
                if (pcs.isLooping == PlayerCurrentState.Looping.OFF)
                    stopPlay();
            } else {
                play(pcs.getCurrentPlaylist(), indexCurrSong + 1);
            }
        }
    };
}
