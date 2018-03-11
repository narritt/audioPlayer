package com.example.narritt.audioplayer.misc;

import android.content.Context;
import android.util.Log;

import com.example.narritt.audioplayer.items.Song;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class FileMaster {
    private static final String TAG = "MyAudioPlayer";

    private Context context;
    private String currentPlaylistFileName  = "CurrentPlaylist.txt";
    private File  currentPlaylistFile;

    public FileMaster(Context ctx){
        context = ctx;
        currentPlaylistFile = new File(context.getFilesDir() + currentPlaylistFileName);
        //Log.i(TAG, "Create a FileMaster, context:  " + ctx.toString() + "; FilesDir path: "  + ctx.getFilesDir());
    }

    public void writeCurrentPlaylist(PlayerCurrentState plState){
        try {
            //Log.i(TAG, "Trying to create current playlist file");
            currentPlaylistFile.createNewFile();    //if it does not exist

            FileOutputStream outputStream = new FileOutputStream(currentPlaylistFile);
            for(Song song : plState.getCurrentPlaylist()) {
                //Log.i(TAG, "Writing " + plState.getCurrentPlaylist().indexOf(song) + " song from playlist : " + song.toString());
                outputStream.write((song.toString() + "_-_-_").getBytes());
            }
            int indexOfCurrentSong = plState.getCurrentSongIndex();
            if(indexOfCurrentSong == -1) {
                Log.e(TAG, "Writing current playlist: current song does not exist in current playlist!");
                indexOfCurrentSong = 0;
            }
            outputStream.write( ( String.valueOf(indexOfCurrentSong) ).getBytes() ); //index of current song
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "ERROR WRITING CURRENT PLAYLIST FILE: " + e.toString() + " - " + e.getMessage());
        }
    }
    public PlayerCurrentState readCurrentPlaylist(){
        ArrayList<Song> songs = new ArrayList<>();
        int currPosition = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(currentPlaylistFile)));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                //Производим построчное считывание данных из файла в конструктор строки, после того, как данные закончились, создаём объект Song
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                String[] allSongsFromFile = stringBuilder.toString().split("_-_-_");
                for(String s : allSongsFromFile) {
                    if(s.length() > 3) {
                        songs.add(new Song(s));
                    }  else
                        currPosition = Integer.parseInt(s);
                }
            } catch (IOException e) {
                Log.e(TAG, "ERROR READING CURRENT SONG FILE: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "CURRENT SONG FILE NOT FOUND WHILE READING: " + e.getMessage());
        } catch (NullPointerException e){
            Log.e(TAG, "Can't read current playlist - its null : " + e.toString());
        } catch (Exception e) {
            Log.e(TAG, "Freaking exception while reading current playlist : " + e.toString());
        }
        if(songs.isEmpty())
            songs = null;
        PlayerCurrentState plState = new PlayerCurrentState(songs, currPosition);
        return plState;
    }
    // TODO : 02.03.18 creating and reading user playlist
}
