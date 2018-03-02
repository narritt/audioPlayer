package com.example.narritt.audioplayer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class FileMaster {
    private static final String TAG = "MyAudioPlayer";

    private Context context;
    private String currentSongFileName = "CurrentSong.txt";
    private File currentSongFile;

    protected FileMaster(Context ctx){
        context = ctx;
        currentSongFile = new File(context.getFilesDir() + currentSongFileName);
        Log.i(TAG, "Create a FileMaster, context:  " + ctx.toString() + "; FilesDir path: "  + ctx.getFilesDir());
    }

    protected void writeCurrentSong(Song song){
        try {
            //Log.i(TAG, "Trying to create current song file");
            currentSongFile.createNewFile();    //if it does not exist
            FileOutputStream outputStream = new FileOutputStream(currentSongFile);
            Log.i(TAG, "Trying to write current song: " + song.toString());
            outputStream.write(song.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "ERROR WRITING CURRENT SONG FILE: " + e.getMessage());
        }
    }
    protected Song readCurrentSong(){
        Song song = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(currentSongFile)));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                 //Производим построчное считывание данных из файла в конструктор строки,
                 //После того, как данные закончились, создаём объект Song
                 //Здесь необязательно считывать много строк, но пусть остаётся до создание плейлистов
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                Log.i(TAG, "Reading a song: " + stringBuilder.toString());
                song = new Song(stringBuilder.toString());
            } catch (IOException e) {
                Log.e(TAG, "ERROR READING CURRENT SONG FILE: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "CURRENT SONG FILE NOT FOUND WHILE READING: " + e.getMessage());
        }
        return song;
    }
    // TODO : 02.03.18 saving and reading curent playlist

    // TODO : 02.03.18 creating and reading user playlist
}
