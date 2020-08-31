package com.example.musicplayer.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SongDB extends SQLiteOpenHelper {
    private static final String DBName = "musicdb";
    private SQLiteDatabase MusicDB;

    public SongDB(Context context) {
        super(context, DBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DBName + "(title text Primary key not null,image blob)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if Exists " + DBName);
        onCreate(db);
    }

    public boolean addImage(String title,byte[] image){
        MusicDB = this.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put("title",title);
        row.put("image",image);

        long result = MusicDB.insert(DBName,null, row);
        if(result == -1)
            return false;
        else
            return true;
    }

    public byte[] getImage(String title){
        MusicDB = this.getReadableDatabase();
        String s="Select * from " + DBName + " where title = ?";
        Cursor cursor = MusicDB.rawQuery(s,new String[]{title});
        while(cursor.moveToNext()){
            return cursor.getBlob(1);
        }
        return null;
    }
}
