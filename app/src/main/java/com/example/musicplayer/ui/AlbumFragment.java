package com.example.musicplayer.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.classes.AlbumClass;
import com.example.musicplayer.R;
import com.example.musicplayer.adapters.SongsAdapter;
import com.example.musicplayer.adapters.albumAdapter;

import java.util.ArrayList;


public class AlbumFragment extends Fragment implements albumAdapter.onAlbumClickListener {

    public static RecyclerView rec;
    public static ArrayList<AlbumClass>albums = new ArrayList<>();
    public static ArrayList<String>names = new ArrayList<>();
    public static swInterface swlistener;
    public AlbumFragment(swInterface listener) {
    this.swlistener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        rec = view.findViewById(R.id.album_rec);


        rec.setLayoutManager(new GridLayoutManager(getContext(),2));
        rec.setHasFixedSize(true);
        albumAdapter albumAdapter = new albumAdapter(getContext(),albums,this);
        rec.setAdapter(albumAdapter);
        return view;
    }

    public interface swInterface{
        void switchNow(AlbumClass album);
        void goBack();
        void playMusic(int position);
        void giveInstance(swInterface listener, SongsAdapter.SongListener songListener);
    }

    @Override
    public void onAlbumClick(AlbumClass album) {
        swlistener.switchNow(album);
    }
}