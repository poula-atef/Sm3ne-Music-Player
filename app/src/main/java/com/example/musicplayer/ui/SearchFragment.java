package com.example.musicplayer.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.classes.SongClass;
import com.example.musicplayer.adapters.SongsAdapter;
import com.example.musicplayer.adapters.innerAdapter;

import java.util.ArrayList;


public class SearchFragment extends Fragment implements innerAdapter.SongClickListener {

    RecyclerView search_rec;
    EditText search_et;
    innerAdapter adapter;
    innerAdapter.SongClickListener listener = this;
    TextView noItems;
    AlbumFragment.swInterface ManListener;

    public SearchFragment(AlbumFragment.swInterface manListener) {
        ManListener = manListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        search_et = view.findViewById(R.id.search_et);
        search_rec = view.findViewById(R.id.search_rec);
        noItems = view.findViewById(R.id.no_items);
        search_rec.setHasFixedSize(true);
        search_rec.setLayoutManager(new LinearLayoutManager(getContext()));
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<SongClass>songs = new ArrayList<>();
                if(!s.toString().equals("")){
                    noItems.setVisibility(View.GONE);
                    for(int i = 0; i< SongsAdapter.getSongs().size(); i++){
                        if(SongsAdapter.getSongs().get(i).getTitle().toLowerCase().contains(s.toString().toLowerCase())){
                            songs.add(SongsAdapter.getSongs().get(i));
                        }
                    }
                }
                if(songs.size() == 0){
                    noItems.setVisibility(View.VISIBLE);
                }
                adapter = new innerAdapter(songs,getContext(),listener);
                search_rec.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    @Override
    public void onSongClick(SongClass song) {
        int pos=0;
        for(int i=0;i<SongsAdapter.getSongs().size();i++){
            if(SongsAdapter.getSongs().get(i).getTitle().toLowerCase().equals(song.getTitle().toLowerCase())){
                pos=i;
                break;
            }
        }
        ManListener.playMusic(pos);
    }
}