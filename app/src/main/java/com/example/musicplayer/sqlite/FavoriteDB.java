package com.example.musicplayer.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.musicplayer.classes.SongClass;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDB extends SQLiteOpenHelper {
    private static String databasename="favSongs";
    SQLiteDatabase songsDB;
    int id;

    public FavoriteDB(Context context){
        super(context,databasename,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table favSongs(title text Primary key not null,path text ,artist text ," +
                "album text ,duration text,image blob,clicked text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if Exists favSongs");
        onCreate(db);
    }

    public boolean addNewFav(String title,String path,String artist,String album,String duration,byte[] image,boolean clicked){
        songsDB = this.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put("title",title);
        row.put("path",path);
        row.put("artist",artist);
        row.put("album",album);
        row.put("duration",duration);
        row.put("image",image);
        if(clicked)
            row.put("clicked","true");
        else
            row.put("clicked","false");

        long result = songsDB.insert("favSongs",null, row);
        if(result == -1)
            return false;
        else
            return true;
    }

    public ArrayList<SongClass> getAllSongs(){
        songsDB = this.getReadableDatabase();
        String s="Select * from favSongs";
        Cursor cursor = songsDB.rawQuery(s,null);
        ArrayList<SongClass> list = new ArrayList<>();
        while(cursor.moveToNext()){
            SongClass song = new SongClass(cursor.getString(1),cursor.getString(0)
                    ,cursor.getString(2),cursor.getString(3),cursor.getString(4));
            song.setImage(cursor.getBlob(5));
            if(cursor.getString(6).equals("true"))
                song.setClicked(true);
            else
                song.setClicked(false);

            list.add(song);
        }
        return list;
    }

    public void removeSong(String title){
        songsDB = this.getWritableDatabase();
        songsDB.delete("favSongs", "title = ?", new String[]{title});
    }

    public List<String> getLikedSongs(){
        songsDB = this.getReadableDatabase();
        String s="Select * from favSongs";
        Cursor cursor = songsDB.rawQuery(s,null);
        List<String> list = new ArrayList<>();
        while(cursor.moveToNext()){
            list.add(cursor.getString(0));
        }
        return list;
    }

}
