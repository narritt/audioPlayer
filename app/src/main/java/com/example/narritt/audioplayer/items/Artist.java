package com.example.narritt.audioplayer.items;



/**
 * Created by Narritt on 13.01.2018.
 */

public class Artist {
    private long id;
    private String name;

    public Artist (String artistName){
        name = artistName;
    }
    public Artist (long artistId, String artistName){
        id = artistId;
        name = artistName;
    }

    public long getID(){return id;}
    public String getName() {return name;}

    @Override
    public boolean equals(Object o){
        if(o instanceof Artist) {
            Artist art = (Artist) o;
            return this.name.equals(art.name);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }
}
