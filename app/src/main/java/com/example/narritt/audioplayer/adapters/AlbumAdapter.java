package com.example.narritt.audioplayer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.narritt.audioplayer.R;
import com.example.narritt.audioplayer.items.Album;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Narritt on 09.12.2017.
 */

public class AlbumAdapter extends BaseAdapter {
    private static final String TAG = "MyAudioPlayer";

    private Context context;
    private ArrayList<Album> albums;
    private LayoutInflater albumInf;

    public AlbumAdapter(Context c, ArrayList<Album> theAlbums){
        context = c;
        albums = theAlbums;
        albumInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        //wtf is this method
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        //wtf is this method
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout albumLay = (LinearLayout)albumInf.inflate(R.layout.album, parent, false);
        //get title and artist views
        TextView albumTitleView   = (TextView)albumLay.findViewById(R.id.album_title);
        TextView albumArtistView = (TextView)albumLay.findViewById(R.id.album_artist);
        ImageView cover = (ImageView)albumLay.findViewById(R.id.cover);
        //get song using position
        Album currAlbum = albums.get(position);
        //get title and artist strings
        albumTitleView.setText(currAlbum.getTitle());
        albumArtistView.setText(currAlbum.getArtist());

        //get cover
        loadAlbumCover(currAlbum, cover);

        //set position as tag
        albumLay.setTag(position);
        return albumLay;
    }

    public void loadAlbumCover(Album album, ImageView cover){

        boolean coverFound = false;
        String coverArtPath = getCoverArtPath(album.getID());

        if( coverArtPath != null) {
            Log.i(TAG, "loadAlbumCover: coverArtPath is " + coverArtPath + "; album is " + album.getTitle() );
            File imgFile = new File(coverArtPath);
            Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            if(bmp != null) {
                cover.setImageBitmap(bmp);
                coverFound = true;
            }
        } else Log.i(TAG, "AlbumAdapter: getCoverArtPath returns null");

        if(!coverFound) {
            File dir = new File(album.getPath());
            for (File file : dir.listFiles()) {
                String fileName = file.getName().toLowerCase();
            /* Полная инфа в аналогичном методе playerActivity */
                if (fileName.contains("cover.jpg") || fileName.contains("front.jpg")) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    cover.setImageBitmap(myBitmap);
                    coverFound = true;
                    break;
                }
            }
        }

        if(!coverFound){
            cover.setImageResource(R.drawable.octave_wb);
        }
    }

    private String getCoverArtPath(long albumId) {
        Cursor albumCursor = context.getContentResolver().query(
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

}