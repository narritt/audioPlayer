package com.example.narritt.audioplayer.misc;

import android.content.Context;
import android.util.Log;

import com.example.narritt.audioplayer.items.Playlist;
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
    public String currentPlaylistFileName  = "_currentPlaylist.txt";
    private String SEPARATOR = "_-_-_";
    private File  currentPlaylistFile;
    private File playlistFolder;

    public FileMaster(Context ctx){
        context = ctx;
        playlistFolder = new File(context.getFilesDir() + File.separator + "playlists");
        if (!playlistFolder.exists())
            playlistFolder.mkdir();
        currentPlaylistFile = new File(playlistFolder.getPath() + File.separator + currentPlaylistFileName);
    }

    public void writeCurrentPlaylist(PlayerCurrentState plState){
        try {
            currentPlaylistFile.createNewFile();    //if it does not exist
            //Log.i(TAG, "Writting current playlist: " + currentPlaylistFile);
            FileOutputStream outputStream = new FileOutputStream(currentPlaylistFile);
            for(Song song : plState.getCurrentPlaylist()) {
                outputStream.write((song.toString() + SEPARATOR).getBytes());
            }
            int indexOfCurrentSong = plState.getCurrentSongIndex();
            outputStream.write( ( String.valueOf(indexOfCurrentSong) ).getBytes() ); //index of current song
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "ERROR WRITING CURRENT PLAYLIST FILE: " + e.toString() + " - " + e.getMessage());
        }
    }
    public void writePlaylist(Playlist playlist){
        File playlistFile= new File(playlistFolder.getPath() + File.separator + playlist.getName());
        try {
            playlistFile.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(playlistFile);
            for(Song song : playlist.getPlaylist()) {
                outputStream.write((song.toString() + SEPARATOR).getBytes());
                //в конце файла будет разделитель, после которого песни нет - иметь в виду
            }
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "ERROR WRITING PLAYLIST " + playlist.getName() + " FILE: " + e.toString() + " - " + e.getMessage());
        }

    }

    public Playlist readCurrentPlaylist(){
        ArrayList<Song> songs = new ArrayList<>();
        int currPosition = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(currentPlaylistFile)));
            //Log.i(TAG, "Reading current playlist: " + currentPlaylistFile);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                //Производим построчное считывание данных из файла в конструктор строки, после того, как данные закончились, создаём объект Song
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                String[] allSongsFromFile = stringBuilder.toString().split(SEPARATOR);
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
        } catch (Exception e) {
            Log.e(TAG, "Weird exception while reading current playlist : " + e.toString());
        }
        if(songs.isEmpty())
            songs = null;
        Playlist pl = new Playlist(songs, currPosition);
        return pl;
    }
    // TODO : 02.03.18 creating and reading user playlist

    public Playlist readPlaylist(String name){
        File playlistFile= new File(playlistFolder.getPath() + File.separator + name);
        ArrayList<Song> songs = new ArrayList<>();
        //Log.i(TAG, "Reading user playlist " + name);
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(playlistFile)));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                String[] allSongsFromFile = stringBuilder.toString().split(SEPARATOR);

                for(String s : allSongsFromFile) {
                    songs.add(new Song(s));
                }

            } catch (IOException e) {
                Log.e(TAG, "ERROR READING CURRENT SONG FILE: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "CURRENT SONG FILE NOT FOUND WHILE READING: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Weird exception while reading " + name + " playlist : " + e.toString());
        }

        if(songs.isEmpty())
            songs = null;

        Playlist pl = new Playlist(name, songs);
        return pl;
    }

    public void deletePlaylist(String name) {
        File playlistFile= new File(playlistFolder.getPath() + File.separator + name);
        if (playlistFile.exists())
            playlistFile.delete();
    }
}
