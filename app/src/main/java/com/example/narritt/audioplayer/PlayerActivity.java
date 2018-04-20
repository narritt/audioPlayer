package com.example.narritt.audioplayer;

import android.Manifest;
import android.app.Activity;
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

import com.example.narritt.audioplayer.items.Song;
import com.example.narritt.audioplayer.misc.FileMaster;
import com.example.narritt.audioplayer.misc.PlayerCurrentState;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//import static android.app.Notification.FLAG_ONGOING_EVENT;

public class PlayerActivity extends Activity {
    private static final String TAG = "MyAudioPlayer";
    static PlayerActivity instance;

    public static MediaPlayer mediaPlayer;
    public PlayerCurrentState pcs = new PlayerCurrentState();

    ImageButton btnPlay, btnRand, btnLoop, btnNext, btnPrev;
    TextView artistName, albumName, songName, songDuration, currentSongPosition;
    ImageView imgAlbum;
    SeekBar progressControl;
    double startTime, finalTime;

    FileMaster fileMaster;
    Handler myHandler = new Handler();

    //NotificationManager nm;


    //TODO notification
    //todo WTF THIS BUG стартует иузыка при переключении песен на короткое время

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Log.i(TAG, "OnCreate in " + this.getLocalClassName());
        instance = this;
        checkPermission();

        //Finding elements in R
        btnPlay =               findViewById(R.id.btnPlay);
        btnLoop =               findViewById(R.id.btnLoop);
        btnRand =               findViewById(R.id.btnRand);
        btnNext =               findViewById(R.id.btnNext);
        btnPrev =               findViewById(R.id.btnPrev);
        artistName =            findViewById(R.id.strArtistName);
        albumName =             findViewById(R.id.strAlbumName);
        songName =              findViewById(R.id.strSongName);
        songDuration =          findViewById(R.id.strSongDuration);
        currentSongPosition =   findViewById(R.id.strCurrentSongPosition);
        progressControl =       findViewById(R.id.progressControl);
        imgAlbum =              findViewById(R.id.imgAlbum);

        //getting written current song from file
        fileMaster = new FileMaster(getApplicationContext());
        pcs.setCurrentPlaylistAndSong(fileMaster.readCurrentPlaylist());
        if (pcs.getCurrentSong() != null) {
            mediaPlayer = MediaPlayer.create(this, pcs.getCurrentSong().getPath());
            prepareInterface(pcs.getCurrentSong());
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

        //nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /*
     * MEDIAPLAYER LOGIC
     */
    public void play(ArrayList<Song> songs, int position){
        pcs.isMusicPlaying = true;
        if(mediaPlayer != null) //if its a first start of application
            mediaPlayer.stop();
        pcs.setCurrentPlaylistAndSong(songs, position);
        mediaPlayer = MediaPlayer.create(this, songs.get(position).getPath());
        prepareInterface(songs.get(position));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int indexCurrSong = pcs.getCurrentSongIndex();
                if(!pcs.isRandom) {
                    if (indexCurrSong >= pcs.getPlaylistSize() - 1) {
                        if(pcs.isLooping)
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), pcs.getCurrentPlaylist().get(0).getPath());
                        else {
                            stopPlay();
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), pcs.getCurrentPlaylist().get(0).getPath());
                        }
                    }
                    else
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), pcs.getCurrentPlaylist().get(indexCurrSong + 1).getPath());
                } else {
                    //TODO normal randomizer
                    play(pcs.getShuffledPlaylist(), new Random().nextInt(pcs.getPlaylistSize()));
                }
            }
        });

        mediaPlayer.start();
        
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
        mediaPlayer.pause();
        pcs.isMusicPlaying = false;
        btnPlay.setImageResource(R.drawable.play);
        //nm.cancel(1);
    }
    public void resumePlay(){
        mediaPlayer.start();
        pcs.isMusicPlaying = true;
        btnPlay.setImageResource(R.drawable.pause);
        myHandler.postDelayed(UpdateSongTime,100);
    }
    private void stopPlay(){
        mediaPlayer.stop();
        pcs.isMusicPlaying = false;
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
        if(pcs.isMusicPlaying)
            btnPlay.setImageResource(R.drawable.pause);
        else
            btnPlay.setImageResource(R.drawable.play);

        artistName.setText(song.getArtist());
        albumName.setText(song.getAlbum());
        //artistName.setText(song.getArtist() + " — " + song.getAlbum());
        songName.setText(song.getTitle());

        startTime = mediaPlayer.getCurrentPosition();
        finalTime = mediaPlayer.getDuration();

        loadAlbumCover(song);

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
        if(pcs.isMusicPlaying)
            myHandler.postDelayed(UpdateSongTime,100);
    }
    public void loadAlbumCover(Song song){
        /*
         * Необходима сторонняя библиотека для чтения данных
         * из ID3 тегов. Можно использовать библиотеку сразу для записи,
         * как, например Jaudiotagger (http://www.jthink.net/jaudiotagger/),
         * с этим надо разобраться.
         * http://qaru.site/questions/327594/how-to-use-the-java-mp3-id3-tag-library-to-retrieve-album-artwork
         */
        //float height = imgAlbum.getHeight();
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
            if(!pcs.isApplicationDestroying) {
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
        if(/*currPlaylist*/ pcs.getCurrentPlaylist() == null) {
            Toast.makeText(this, "Сначала нужно выбрать трек!", Toast.LENGTH_SHORT).show();
        }
        else {
            if (pcs.isMusicPlaying)
                pausePlay();
            else
                resumePlay();
        }
    }
    public void btnPrevClick(View view){
        if (mediaPlayer.getCurrentPosition() > 1500) {
            mediaPlayer.seekTo(0);
            UpdateSongTimeManualy();
        } else {
            if(pcs.getCurrentPlaylist() != null) {
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
                if (!pcs.isLooping)
                    stopPlay();
            } else {
                playDependingOn_isMusicPlaying(pcs.getCurrentPlaylist(), indexCurrSong + 1);
            }
        }
    }
    public void btnLoopClick(View view){
        //TODO : 02.03.18 one song looping
        if (pcs.isLooping) {
            btnLoop.setImageResource(R.drawable.loop_off);
        }
        else {
            btnLoop.setImageResource(R.drawable.loop_on);
        }
        pcs.isLooping = !pcs.isLooping;
    }
    public void btnRandClick(View view){
        if (pcs.isRandom)
            btnRand.setImageResource(R.drawable.rand_off);
        else
            btnRand.setImageResource(R.drawable.rand_on);
        pcs.isRandom = !pcs.isRandom;
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
    protected void onStop() {
        fileMaster.writeCurrentPlaylist(pcs);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        Log.i(TAG, "On destroy, trying to write current playlist");
        pcs.isApplicationDestroying = true;
        releaseMP();
        super.onDestroy();
    }

    /*  Освобождает используемые проигрывателем ресурсы */
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
