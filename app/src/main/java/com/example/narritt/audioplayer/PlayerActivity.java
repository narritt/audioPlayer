package com.example.narritt.audioplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends Activity {
    private static final String TAG = "MyAudioPlayer";
    static PlayerActivity instance;

    public static MediaPlayer mediaPlayer;
    public PlayerCurrentState playerState = new PlayerCurrentState();

    ImageButton btnPlay, btnRand, btnLoop, btnNext, btnPrev;
    TextView artistName, albumName, songName, songDuration, currentSongPosition;
    SeekBar progressControl;
    double startTime, finalTime;

    FileMaster fileMaster;
    Handler myHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Log.i(TAG, "OnCreate in " + this.getLocalClassName());
        instance = this;
        checkPermission();

        //Finding elements in R
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnLoop = (ImageButton) findViewById(R.id.btnLoop);
        btnRand = (ImageButton) findViewById(R.id.btnRand);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrev = (ImageButton) findViewById(R.id.btnPrev);
        artistName = (TextView) findViewById(R.id.strArtistName);
        albumName = (TextView) findViewById(R.id.strAlbumName);
        songName = (TextView) findViewById(R.id.strSongName);
        songDuration = (TextView) findViewById(R.id.strSongDuration);
        currentSongPosition = (TextView) findViewById(R.id.strCurrentSongPosition);
        progressControl = (SeekBar) findViewById(R.id.progressControl);

        //getting written current song from file
        fileMaster = new FileMaster(getApplicationContext());
        playerState.setCurrentPlaylistAndSong(fileMaster.readCurrentPlaylist());
        if (playerState.getCurrentSong() == null)
            mediaPlayer = MediaPlayer.create(this, R.raw.music);
        else {
            mediaPlayer = MediaPlayer.create(this, playerState.getCurrentSong().getPath());
            prepareInterface(playerState.getCurrentSong());
        }

        progressControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean pausedOnTouch;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);
                    UpdateSongTimeManualy();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    pausedOnTouch = true;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if( (!mediaPlayer.isPlaying()) && (pausedOnTouch)){
                    mediaPlayer.start();
                    pausedOnTouch = false;
                }
            }
        });
    }

    /*
     * MEDIAPLAYER LOGIC
     */
    public void play(ArrayList<Song> songs, int position){
        playerState.isMusicPlaying = true;
        mediaPlayer.stop();
        playerState.setCurrentPlaylistAndSong(songs, position);
        mediaPlayer = MediaPlayer.create(this, songs.get(position).getPath());
        mediaPlayer.start();
        prepareInterface(songs.get(position));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "Track is completed");
                if(playerState.getCurrentPlaylist() == null) {
                    Log.i(TAG, "MP.onCompletion : currPlayList is empty.");
                    stopPlay();
                } else {
                    int indexCurrSong = playerState.getCurrentSongIndex();
                    if(!playerState.isRandom) {
                        if (indexCurrSong >= playerState.getPlaylistSize() - 1) {
                            if(playerState.isLooping)
                                play(playerState.getCurrentPlaylist(), 0);
                            else {
                                play(playerState.getCurrentPlaylist(), 0);
                                stopPlay();
                            }
                        }
                        else
                            play(playerState.getCurrentPlaylist(), indexCurrSong + 1);
                    } else {
                        // TODO : 02.03.18 create normal randomizer
                        play(playerState.getCurrentPlaylist(), new Random().nextInt(playerState.getPlaylistSize()));
                    }
                }
            }
        });
    }
    public void pausePlay(){
        mediaPlayer.pause();
        playerState.isMusicPlaying = false;
        btnPlay.setImageResource(R.drawable.play);
    }
    public void resumePlay(){
        mediaPlayer.start();
        playerState.isMusicPlaying = true;
        btnPlay.setImageResource(R.drawable.pause);
        myHandler.postDelayed(UpdateSongTime,100);
    }
    private void stopPlay(){
        mediaPlayer.stop();
        playerState.isMusicPlaying = false;
        btnPlay.setImageResource(R.drawable.play);
        try {
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * INTERFACE LOGIC
     */
    public void prepareInterface(Song song){
        if(playerState.isMusicPlaying)
            btnPlay.setImageResource(R.drawable.pause);
        else
            btnPlay.setImageResource(R.drawable.play);

        artistName.setText(song.getArtist());
        albumName.setText(song.getAlbum());
        songName.setText(song.getTitle());

        startTime = mediaPlayer.getCurrentPosition();
        finalTime = mediaPlayer.getDuration();

        //defense for 0-9 seconds duration and position for string like 1:9, should be 1:09
        if((TimeUnit.MILLISECONDS.toSeconds((long) startTime) % 60) < 10)
            songDuration.setText(String.format("%d:0%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
        else
            songDuration.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));

        if((TimeUnit.MILLISECONDS.toSeconds((long) finalTime) % 60) < 10)
            songDuration.setText(String.format("%d:0%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
        else
            songDuration.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));

        progressControl.setMax((int)finalTime);
        progressControl.setProgress((int)startTime);
        if(playerState.isMusicPlaying)
            myHandler.postDelayed(UpdateSongTime,100);
    }
    private void UpdateSongTimeManualy(){
        startTime = mediaPlayer.getCurrentPosition();
        //defense for 0-9 seconds position for string like 1:9, should be 1:09
        if ((TimeUnit.MILLISECONDS.toSeconds((long) startTime) % 60) < 10)
            currentSongPosition.setText(String.format("%d:0%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
        else
            currentSongPosition.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
        progressControl.setProgress((int) startTime);
    }
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            if(!playerState.isApplicationDestroying) {
                startTime = mediaPlayer.getCurrentPosition();
                //defense for 0-9 seconds position for string like 1:9, should be 1:09
                if ((TimeUnit.MILLISECONDS.toSeconds((long) startTime) % 60) < 10)
                    currentSongPosition.setText(String.format("%d:0%d",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
                else
                    currentSongPosition.setText(String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
                progressControl.setProgress((int) startTime);
                myHandler.postDelayed(this, 100);
            }
        }
    };

    /*
     * BUTTON LOGIC
     */
    public void btnPlayClick(View view){
        if(/*currPlaylist*/ playerState.getCurrentPlaylist() == null) {
            Toast.makeText(this, "Сначала нужно выбрать трек!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (playerState.isMusicPlaying)
                pausePlay();
            else
                resumePlay();
        }
    }
    public void btnPrevClick(View view){
        if (mediaPlayer.getCurrentPosition() > 1500) {
            mediaPlayer.seekTo(0);
            UpdateSongTimeManualy();
        }
        else{
            if(playerState.getCurrentPlaylist() != null) {
                int indexCurrSong = playerState.getCurrentSongIndex();
                if (indexCurrSong == 0)
                    play(playerState.getCurrentPlaylist(), playerState.getPlaylistSize() - 1);
                else
                    play(playerState.getCurrentPlaylist(), indexCurrSong - 1);
                if (!playerState.isMusicPlaying)
                    stopPlay();
            }
        }
    }
    public void btnNextClick(View view){
        if(playerState.getCurrentPlaylist() != null) {
            int indexCurrSong = playerState.getCurrentSongIndex(); //currPlaylist.indexOf(currSong);
            if (indexCurrSong == playerState.getPlaylistSize() - 1) {
                if (!playerState.isMusicPlaying) {
                    play(playerState.getCurrentPlaylist(), 0);
                    stopPlay();
                } else
                    play(playerState.getCurrentPlaylist(), 0);
                if (!playerState.isLooping)
                    stopPlay();
            } else{
                if(!playerState.isMusicPlaying) {
                    play(playerState.getCurrentPlaylist(), indexCurrSong + 1);
                    stopPlay();
                } else
                    play(playerState.getCurrentPlaylist(), indexCurrSong + 1);
            }
        }
    }
    public void btnLoopClick(View view){
        //TODO : 02.03.18 one song looping
        if (playerState.isLooping) {
            btnLoop.setImageResource(R.drawable.loop_off);
        }
        else {
            btnLoop.setImageResource(R.drawable.loop_on);
        }
        playerState.isLooping = !playerState.isLooping;
    }
    public void btnRandClick(View view){
        if (playerState.isRandom)
            btnRand.setImageResource(R.drawable.rand_off);
        else
            btnRand.setImageResource(R.drawable.rand_on);
        playerState.isRandom = !playerState.isRandom;
    }
    public void btnBack(View view){

        Intent intent = new Intent(PlayerActivity.this, SongListActivity.class);
        startActivity(intent);
    }


    /*
     * SYSTEM LOGIC
     */
    private void checkPermission(){
        int requestCode = 0;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                // requestCode is an app-defined int constant. The callback method gets the result of the request.
            }
        }
    }
    @Override
    protected void onDestroy() {
        playerState.isApplicationDestroying = true;
        releaseMP();
        Log.i(TAG, "On destroy, trying to write current song");
        //fileMaster.writeCurrentSong(playerState.getCurrentSong());
        fileMaster.writeCurrentPlaylist(playerState);
        super.onDestroy();
    }

    /*  Освобождает используемые проигрывателем ресурсы, его рекомендуется вызывать когда вы закончили работу с плеером.
        Более того, хелп рекомендует вызывать этот метод и при onPause/onStop, если нет острой необходимости держать объект.    */
    private void releaseMP() {
        if (mediaPlayer != null) {
            stopPlay();
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static PlayerActivity getInstance() {
        return instance;
    }
}
