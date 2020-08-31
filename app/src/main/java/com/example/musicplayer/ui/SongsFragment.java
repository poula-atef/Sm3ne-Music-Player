package com.example.musicplayer.ui;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.classes.AlbumClass;
import com.example.musicplayer.R;
import com.example.musicplayer.classes.SongClass;
import com.example.musicplayer.adapters.SongsAdapter;
import com.example.musicplayer.sqlite.SongDB;

import java.util.ArrayList;


public class SongsFragment extends Fragment implements SongsAdapter.SongListener, AlbumFragment.swInterface {

    AlbumFragment.swInterface listener;
    static ConstraintLayout big_player;
    RecyclerView songs_recc;
    ArrayList<SongClass> songs;
    SongDB musicSongDB;
    static SongsAdapter songAdapter;

    public SongsFragment(AlbumFragment.swInterface listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        songs_recc = view.findViewById(R.id.songs_rec);

        listener.giveInstance(this,this);

        setUpMusicList();

        return view;
    }

    public void setUpMusicList() {
        songs = getAllSongs();
        songAdapter = new SongsAdapter(songs,getContext(),this);
        songs_recc.setAdapter(songAdapter);
        songs_recc.setHasFixedSize(true);
        songs_recc.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private ArrayList<SongClass> getAllSongs(){
        ArrayList<SongClass>songs = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String [] dataWanted = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };
        Cursor cursor = getContext().getContentResolver().query(uri,dataWanted,null,null,null);
        String album,title,duration,artist,path;
        musicSongDB = new SongDB(getContext());
        while(cursor != null && cursor.moveToNext()){
            album = cursor.getString(0);
            title = cursor.getString(1);
            duration = cursor.getString(2);
            path = cursor.getString(3);;
            artist = cursor.getString(4);
            songs.add(new SongClass(path,title,artist,album,duration));
        }
        cursor.close();
        return songs;
    }




    @Override
    public void onClick(int position) {
        listener.playMusic(position);
    }


    @Override
    public void switchNow(AlbumClass album) {

    }

    @Override
    public void goBack() {

    }

    @Override
    public void playMusic(int position) {
    }

    @Override
    public void giveInstance(AlbumFragment.swInterface listener, SongsAdapter.SongListener songListener) {

    }
}