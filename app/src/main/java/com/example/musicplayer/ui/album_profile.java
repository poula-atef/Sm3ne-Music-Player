package com.example.musicplayer.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.classes.AlbumClass;
import com.example.musicplayer.R;
import com.example.musicplayer.classes.SongClass;
import com.example.musicplayer.adapters.SongsAdapter;
import com.example.musicplayer.adapters.innerAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class album_profile extends Fragment implements  innerAdapter.SongClickListener {

    ImageView image;
    TextView name,numOfTracks;
    RecyclerView rec;
    AlbumClass album;
    ImageButton backBtn;
    BottomSheetBehavior sheet;
    AlbumFragment.swInterface listener;
    public album_profile(AlbumClass album, AlbumFragment.swInterface listener) {
        this.album = album;
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_profile, container, false);
        image = view.findViewById(R.id.album_img);
        backBtn = view.findViewById(R.id.back_btn);
        name = view.findViewById(R.id.album_name);
        numOfTracks = view.findViewById(R.id.num_of_tracks);
        sheet = BottomSheetBehavior.from(view.findViewById(R.id.nestedScrollView));
        sheet.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                image.setAlpha(1.0f - 2*slideOffset);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.goBack();
            }
        });


        Glide.with(getContext()).asBitmap().load(album.getImage()).into(image);
        name.setText(album.getName());
        name.setSelected(true);
        numOfTracks.setText(album.getSongs().size()+" Tracks");

        rec = view.findViewById(R.id.rec);

        innerAdapter adapter = new innerAdapter(album.getSongs(),getContext(),this);
        rec.setAdapter(adapter);
        rec.setHasFixedSize(true);
        rec.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    @Override
    public void onSongClick(SongClass song) {
        int pos = 0;
        for(int i = 0; i< SongsAdapter.getSongs().size(); i++){
            if(SongsAdapter.getSongs().get(i).getTitle().equals(song.getTitle())){
                pos = i;
                break;
            }
        }
        listener.playMusic(pos);
    }
}