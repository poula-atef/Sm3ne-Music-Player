package com.example.musicplayer.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.sqlite.SongDB;
import com.example.musicplayer.sqlite.FavoriteDB;
import com.example.musicplayer.R;
import com.example.musicplayer.classes.SongClass;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.example.musicplayer.ui.MainActivity.likedSonges;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongHolder> {
    private static ArrayList<SongClass> songs;
    Context context;
    boolean startUse = false;
    SongListener listener;
    task backTask;
    int clicked=0;
    FavoriteDB fs;
    public SongsAdapter(ArrayList<SongClass> songs, Context context, SongListener listener) {
        this.songs = songs;
        this.context = context;
        this.listener = listener;
    }

    public SongsAdapter() {
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        backTask = new task();
        backTask.execute();
        if(likedSonges == null) {
            fs = new FavoriteDB(context);
            likedSonges = fs.getLikedSongs();
        }
        return new SongHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int i) {
        if(likedSonges!=null && likedSonges.contains(songs.get(i).getTitle()))
        {
            holder.fav.setBackground(context.getResources().getDrawable(R.drawable.fav));
            holder.fav.setContentDescription("Fav");
        }
        else{
            holder.fav.setBackground(context.getResources().getDrawable(R.drawable.not_fav));
            holder.fav.setContentDescription("notFav");
        }
        if(songs.get(i).isClicked()) {
            holder.title.setTextColor(context.getResources().getColor(R.color.whiTeColor));
            clicked = i;
        }
        else {
            holder.title.setTextColor(context.getResources().getColor(R.color.itemColor));
        }
        holder.title.setText(songs.get(i).getTitle());
        if(startUse)
            Glide.with(context).asBitmap().load(songs.get(i).getImage()).into(holder.image);
        else {
         byte[]im = getSongImage(songs.get(i).getPath());
         if(im!= null)
            Glide.with(context).asBitmap().load(im).into(holder.image);
         else
             Glide.with(context).asBitmap().load(getDefaultImage(context)).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        if(songs == null)
            return 0;
        return songs.size();
    }

    public static ArrayList<SongClass> getSongs() {
        return songs;
    }

    public interface SongListener{
        void onClick(int position);
    }

    public class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView image;
        ImageButton fav;
        public SongHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            image = itemView.findViewById(R.id.im_song);
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

            chooseSong(getLayoutPosition());

            listener.onClick(getLayoutPosition());
        }
    }

    public void chooseSong(int pos) {

        songs.get(clicked).setClicked(false);
        notifyItemChanged(clicked);


        songs.get(pos).setClicked(true);
        notifyItemChanged(pos);
    }

    public static byte[] getSongImage(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] image = retriever.getEmbeddedPicture();
        retriever.release();
        return  image;
    }

    public static byte[] getDefaultImage(Context context){
        Drawable d = context.getResources().getDrawable(R.mipmap.music_icon);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }

    class task extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            SongDB songDb = new SongDB(context);
            for(int i=0;i<songs.size();i++){
                byte[] im = songDb.getImage(songs.get(i).getTitle());
                if(im == null){
                    byte[]im1 = getSongImage(songs.get(i).getPath());
                    if(im1 != null) {
                        songDb.addImage(songs.get(i).getTitle(), im1);
                        songs.get(i).setImage(im1);
                    }
                    else {
                        byte[]im2 = getDefaultImage(context);
                        songDb.addImage(songs.get(i).getTitle(), im2);
                        songs.get(i).setImage(im2);
                    }
                }
                else
                    songs.get(i).setImage(im);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startUse = true;
        }
    }
}
