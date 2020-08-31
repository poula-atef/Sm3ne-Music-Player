package com.example.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.classes.AlbumClass;
import com.example.musicplayer.R;

import java.util.ArrayList;

import static com.example.musicplayer.adapters.SongsAdapter.getDefaultImage;
import static com.example.musicplayer.adapters.SongsAdapter.getSongImage;

public class albumAdapter extends RecyclerView.Adapter<albumAdapter.albumHolder> {
    ArrayList<AlbumClass>albums;
    Context context;
    onAlbumClickListener listener;
    public albumAdapter(Context context,ArrayList<AlbumClass>albums,onAlbumClickListener listener) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public albumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new albumHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.album_element,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull albumHolder holder, int position) {
        holder.name.setText(albums.get(position).getName());
        if(albums.get(position).getImage() != null)
            Glide.with(context).asBitmap().load(albums.get(position).getImage()).into(holder.image);
        else
        {
            byte[]im = getSongImage(albums.get(position).getPath());
            if(im!= null)
                Glide.with(context).asBitmap().load(im).into(holder.image);
            else
                Glide.with(context).asBitmap().load(getDefaultImage(context)).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        if(albums == null)
            return 0;
        return albums.size();
    }

    public interface onAlbumClickListener{
        void onAlbumClick(AlbumClass album);
    }

    public class albumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView name;
        public albumHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.album_img);
            name = itemView.findViewById(R.id.album_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onAlbumClick(albums.get(getLayoutPosition()));
        }
    }
}
