package com.example.musicplayer.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.classes.AlbumClass;
import com.example.musicplayer.R;
import com.example.musicplayer.adapters.SongsAdapter;

public class ContainerFragment extends Fragment implements AlbumFragment.swInterface {

    AlbumFragment.swInterface listener;

    public ContainerFragment(AlbumFragment.swInterface listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container, container, false);
        getFragmentManager().beginTransaction().replace(R.id.container,new AlbumFragment(this)).commit();

        return view;
    }



    @Override
    public void switchNow(AlbumClass album) {
        FragmentTransaction manager = getFragmentManager().beginTransaction();
        manager.setCustomAnimations(R.anim.fadein,R.anim.fadeout);
        manager.replace(R.id.container,new album_profile(album,this)).commit();
    }

    @Override
    public void goBack() {
        FragmentTransaction manager = getFragmentManager().beginTransaction();
        manager.setCustomAnimations(R.anim.fadein,R.anim.fadeout);
        manager.replace(R.id.container,new AlbumFragment(this)).commit();
    }

    @Override
    public void playMusic(int position) {
        listener.playMusic(position);
    }

    @Override
    public void giveInstance(AlbumFragment.swInterface listener, SongsAdapter.SongListener songListener) {

    }


}