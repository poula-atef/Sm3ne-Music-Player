package com.example.musicplayer.classes;

import java.util.ArrayList;

public class AlbumClass {
    private String name;
    private byte[] image;
    private String path;
    private ArrayList<SongClass> songs = new ArrayList<>();


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SongClass> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<SongClass> songs) {
        this.songs = songs;
    }
}
