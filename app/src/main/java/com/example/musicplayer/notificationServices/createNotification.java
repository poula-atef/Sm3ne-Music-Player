package com.example.musicplayer.notificationServices;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicplayer.R;
import com.example.musicplayer.classes.SongClass;
import com.example.musicplayer.ui.MainActivity;

public class createNotification {
    public static final int REQUEST_CODE = 22;
    public static final String CHANNEL_ID = "MyChannel";
    public static final String ACTION_PREV = "ActionPrev";
    public static final String ACTION_NEXT = "ActionNext";
    public static final String ACTION_PLAY = "ActionPlay";

    public static Notification notification;
    public static void createNotification(Context context, SongClass song, int playButton, int pos, int size){

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context,"tag");

        Bitmap icon = BitmapFactory.decodeByteArray(song.getImage(),0,song.getImage().length);

        PendingIntent pendingIntentPrev;
        int drw_prev;
        if(pos == 0){
            pendingIntentPrev = null;
            drw_prev = 0;
        }
        else{
            Intent prev = new Intent(context,NotificationActionService.class).setAction(ACTION_PREV);
            pendingIntentPrev = PendingIntent.getBroadcast(context,0,prev,PendingIntent.FLAG_UPDATE_CURRENT);
            drw_prev = R.drawable.back_dark;
        }

        Intent play = new Intent(context,NotificationActionService.class).setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context,0,play,PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntentNext;
        int drw_next;
        if(pos == size){
            pendingIntentNext = null;
            drw_next = 0;
        }
        else{
            Intent next = new Intent(context,NotificationActionService.class).setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context,0,next,PendingIntent.FLAG_UPDATE_CURRENT);
            drw_next = R.drawable.forward_dark;
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntentNotification = PendingIntent.getActivity(context,REQUEST_CODE,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_audio)
                        .setContentTitle(song.getTitle())
                        .setContentText(song.getArtist())
                        .setLargeIcon(icon)
                        .setContentIntent(pendingIntentNotification)
                        .setOnlyAlertOnce(true)
                        .addAction(drw_prev,"Previous",pendingIntentPrev)
                        .addAction(playButton,"Play",pendingIntentPlay)
                        .addAction(drw_next,"Next",pendingIntentNext)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .build();
        if(playButton == R.drawable.pause)
            notification.flags |= Notification.FLAG_NO_CLEAR;

        managerCompat.notify(1,notification);
    }
}
