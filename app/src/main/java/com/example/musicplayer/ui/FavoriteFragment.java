package com.example.musicplayer.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.sqlite.FavoriteDB;
import com.example.musicplayer.R;
import com.example.musicplayer.classes.SongClass;
import com.example.musicplayer.adapters.SongsAdapter;
import com.example.musicplayer.adapters.innerAdapter;

public class FavoriteFragment extends Fragment implements innerAdapter.SongClickListener {
    View view;
    RecyclerView rec;
    AlbumFragment.swInterface listener;
    SwipeRefreshLayout srl;
    public FavoriteFragment(AlbumFragment.swInterface listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        rec = view.findViewById(R.id.fav_rec);
        srl = view.findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setVal(view);
                srl.setRefreshing(false);
            }
        });
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        setVal(view);
    }

    private void setVal(View view) {
        FavoriteDB fs = new FavoriteDB(getContext());
        innerAdapter adapter = new innerAdapter(fs.getAllSongs(),getContext(),this);
        adapter.setType("fav");
        rec.setAdapter(adapter);
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        rec.setHasFixedSize(true);
    }

    @Override
    public void onSongClick(SongClass song) {
        int pos = 0;
        for(int i = 0; i< SongsAdapter.getSongs().size(); i++) {
            if (SongsAdapter.getSongs().get(i).getTitle().equals(song.getTitle()))
            {
                pos = i;
                break;
            }
        }
        listener.playMusic(pos);
    }
}