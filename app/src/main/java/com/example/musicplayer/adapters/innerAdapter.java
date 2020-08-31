package com.example.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.sqlite.FavoriteDB;
import com.example.musicplayer.R;
import com.example.musicplayer.classes.SongClass;

import java.util.ArrayList;

import static com.example.musicplayer.ui.MainActivity.likedSonges;
import static com.example.musicplayer.adapters.SongsAdapter.getDefaultImage;
import static com.example.musicplayer.adapters.SongsAdapter.getSongImage;

public class innerAdapter extends RecyclerView.Adapter<innerAdapter.itemViewHolder> {
    private ArrayList<SongClass> songs;
    SongClickListener listener;
    Context context;
    private int clicked=-1;
    private String type;
    public innerAdapter(ArrayList<SongClass> songs,Context context, SongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
        this.context = context;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new itemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        if(type != null){
            holder.fav.setBackground(context.getResources().getDrawable(R.drawable.fav));
            holder.fav.setContentDescription("Fav");
        }
        else{
            if(likedSonges.contains(songs.get(position).getTitle())) {
                holder.fav.setBackground(context.getResources().getDrawable(R.drawable.fav));
                holder.fav.setContentDescription("Fav");
            }
            else{
                holder.fav.setBackground(context.getResources().getDrawable(R.drawable.not_fav));
                holder.fav.setContentDescription("notFav");
            }
        }
        if(songs.get(position).isClicked()) {
            holder.name.setTextColor(context.getResources().getColor(R.color.whiTeColor));
            clicked = position;
        }
        else {
            holder.name.setTextColor(context.getResources().getColor(R.color.itemColor));
        }
        holder.name.setText(songs.get(position).getTitle());
        if(songs.get(position).getImage() != null)
            Glide.with(context).asBitmap().load(songs.get(position).getImage()).into(holder.img);
        else
        {
            byte[]im = getSongImage(songs.get(position).getPath());
            if(im!= null)
                Glide.with(context).asBitmap().load(im).into(holder.img);
            else
                Glide.with(context).asBitmap().load(getDefaultImage(context)).into(holder.img);
        }
    }

    @Override
    public int getItemCount() {
        if(songs != null)
            return songs.size();
        return 0;
    }

    public interface SongClickListener{
        void onSongClick(SongClass song);
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView name;
        ImageButton fav;
        public itemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_title);
            img = itemView.findViewById(R.id.im_song);
            fav = itemView.findViewById(R.id.favBtn);

            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FavoriteDB db = new FavoriteDB(context);
                    if(fav.getContentDescription() == "Fav")
                    {
                        fav.setBackground(context.getResources().getDrawable(R.drawable.not_fav));
                        fav.setContentDescription("notFav");
                        db.removeSong(songs.get(getLayoutPosition()).getTitle());
                        likedSonges.remove(songs.get(getLayoutPosition()).getTitle());
                        songs.remove(songs.get(getLayoutPosition()));
                        notifyItemRemoved(getLayoutPosition());
                    }
                    else{
                        fav.setBackground(context.getResources().getDrawable(R.drawable.fav));
                        fav.setContentDescription("Fav");
                        db.addNewFav(songs.get(getLayoutPosition()).getTitle(),songs.get(getLayoutPosition()).getPath()
                                ,songs.get(getLayoutPosition()).getArtist(),songs.get(getLayoutPosition()).getAlbum()
                                ,songs.get(getLayoutPosition()).getDuration(),songs.get(getLayoutPosition()).getImage()
                                ,songs.get(getLayoutPosition()).isClicked());
                        likedSonges.add(songs.get(getLayoutPosition()).getTitle());
                    }
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            if(clicked != -1) {
                songs.get(clicked).setClicked(false);
                notifyItemChanged(clicked);
            }

            songs.get(pos).setClicked(true);
            notifyItemChanged(pos);

            listener.onSongClick(songs.get(getLayoutPosition()));
        }
    }
}
