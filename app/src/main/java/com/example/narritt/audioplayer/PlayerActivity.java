package com.example.narritt.audioplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
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

import java.util.concurrent.TimeUnit;

public class PlayerActivity extends Activity {
    private static final String TAG = "MyAudioPlayer";
    static PlayerActivity instance;

    public static MediaPlayer mediaPlayer;
    Song currSong;

    ImageButton btnPlay, btnRand, btnLoop, btnNext, btnPrev;
    TextView artistName, albumName, songName, songDuration, currentSongPosition;
    SeekBar progressControl;
    double startTime, finalTime;
    boolean isMusicPlaying = false,
            isRandom,
            isLooping,
            isApplicationDestroying = false;

    FileMaster fileMaster;
    Handler myHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Log.i(TAG, "OnCreate");
        instance = this;
        checkPermission();

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

        fileMaster = new FileMaster(getApplicationContext());
        currSong = fileMaster.readCurrentSong();
        if (currSong == null)
            mediaPlayer = MediaPlayer.create(this, R.raw.music);
        else {
            mediaPlayer = MediaPlayer.create(this, currSong.getPath());
            prepareInterface(currSong);
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlay();
            }
        });

        progressControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean pausedOnTouch;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    mediaPlayer.seekTo(progress);
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

    public void btnPlayClick(View view){
        if(isMusicPlaying) {
            pausePlay();
        }
        else {
            resumePlay();
        }
    }

    public void play(Song song){
        isMusicPlaying = true;
        mediaPlayer.stop();
        mediaPlayer = MediaPlayer.create(this, song.getPath());
        currSong = song;
        mediaPlayer.start();
        prepareInterface(song);
    }

    public void prepareInterface(Song song){
        if(isMusicPlaying)
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
        if(isMusicPlaying)
            myHandler.postDelayed(UpdateSongTime,100);
    }

    public void pausePlay(){
        mediaPlayer.pause();
        isMusicPlaying = false;
        btnPlay.setImageResource(R.drawable.play);
    }
    public void resumePlay(){
        mediaPlayer.start();
        isMusicPlaying = true;
        btnPlay.setImageResource(R.drawable.pause);
    }
    private void stopPlay(){
        mediaPlayer.stop();
        isMusicPlaying = false;
        btnPlay.setImageResource(R.drawable.play);
        try {
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
        }
        catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            if(!isApplicationDestroying) {
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

    public void btnPrevClick(View view){
        mediaPlayer.seekTo(0);
    }
    public void btnNextClick(View view){
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
    }

    public void btnLoopClick(View view){
        if (isLooping) {
            btnLoop.setImageResource(R.drawable.loop_off);
            mediaPlayer.setLooping(false);
        }
        else {
            btnLoop.setImageResource(R.drawable.loop_on);
            mediaPlayer.setLooping(true);
        }
        isLooping = !isLooping;
    }
    public void btnRandClick(View view){
        if (isRandom)
            btnRand.setImageResource(R.drawable.rand_off);
        else
            btnRand.setImageResource(R.drawable.rand_on);
        isRandom = !isRandom;
    }

    public void btnBack(View view){

        Intent intent = new Intent(PlayerActivity.this, SongListActivity.class);
        startActivity(intent);
    }

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
        isApplicationDestroying = true;
        releaseMP();
        Log.i(TAG, "On destroy, trying to write current song");
        fileMaster.writeCurrentSong(currSong);
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
