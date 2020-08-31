package com.example.musicplayer.ui;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.musicplayer.classes.AlbumClass;
import com.example.musicplayer.notificationServices.OnClearFromRecentService;
import com.example.musicplayer.notificationServices.Playable;
import com.example.musicplayer.R;
import com.example.musicplayer.adapters.PagerAdapter;
import com.example.musicplayer.adapters.SongsAdapter;
import com.example.musicplayer.adapters.albumAdapter;
import com.example.musicplayer.notificationServices.createNotification;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.musicplayer.ui.AlbumFragment.albums;
import static com.example.musicplayer.ui.AlbumFragment.names;
import static com.example.musicplayer.ui.AlbumFragment.rec;
import static com.example.musicplayer.ui.AlbumFragment.swlistener;
import static com.example.musicplayer.ui.SongsFragment.big_player;
import static com.example.musicplayer.ui.SongsFragment.songAdapter;

public class MainActivity extends AppCompatActivity implements albumAdapter.onAlbumClickListener, AlbumFragment.swInterface, Playable {
    private static final int REQUEST_CODE = 20;
    TabLayout tabs;
    ViewPager pager;
    public static MediaPlayer mediaPlayer = null;
    public static int playingNow = -1;
    PagerAdapter pagerAdapter;
    albumAdapter.onAlbumClickListener listener = this;
    AlbumFragment.swInterface songSwitchListener;
    public static List<String> likedSonges;
    int position = -1;
    Uri songUri;
    Timer timer;
    boolean replay = false;
    boolean shuffle = false;
    boolean isDestroied = false;

    SongsAdapter.SongListener songListener;


    @BindView(R.id.big_song_title)
    TextView songTitle;
    @BindView(R.id.big_song_image)
    ImageView songImage;
    @BindView(R.id.song_artist)
    TextView songArtist;
    @BindView(R.id.current_time)
    TextView currentTime;
    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.shuffle_btn)
    ImageView shuffleBtn;
    @BindView(R.id.prev_btn)
    Button prevBtn;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton play;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.replay_btn)
    ImageView replayBtn;
    @BindView(R.id.seekBar)
    SeekBar seekBar;


    static TextView song_title;
    static ImageView song_image;
    ImageView play_song;
    AudioManager audioManager;
    SeekBar volume;
    ImageView close;
    static ConstraintLayout player_layout;

    static BottomSheetBehavior bottom;
    boolean running = false;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpComponents();
        checkPermissions();

    }

    private void checkPermissions() {
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }
        else{
            setupPagerWithTab();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted !!", Toast.LENGTH_SHORT).show();
            setupPagerWithTab();
        }
        else{
            Toast.makeText(this, "Permission Denied !!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPagerWithTab() {
        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new SongsFragment(this),"Songs");
        pagerAdapter.addFragment(new ContainerFragment(this),"Albums");
        pagerAdapter.addFragment(new FavoriteFragment(this),"Favorite");
        pagerAdapter.addFragment(new SearchFragment(this),"Search");
        pager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    boolean stop = false;
                    for(int i=0;i<albums.size();i++)
                        if(albums.get(i).getImage() != null){
                            stop = true;
                            break;
                        }

                        if(!stop) {
                            for (int i = 0; i < SongsAdapter.getSongs().size(); i++) {
                                if (names.contains(SongsAdapter.getSongs().get(i).getAlbum())) {
                                    int index = names.indexOf(SongsAdapter.getSongs().get(i).getAlbum());
                                    albums.get(index).getSongs().add(SongsAdapter.getSongs().get(i));
                                } else {
                                    names.add(SongsAdapter.getSongs().get(i).getAlbum());
                                    AlbumClass al = new AlbumClass();
                                    al.setName(SongsAdapter.getSongs().get(i).getAlbum());

                                    if (SongsAdapter.getSongs().get(i).getImage() != null)
                                        al.setImage(SongsAdapter.getSongs().get(i).getImage());
                                    else
                                        al.setPath(SongsAdapter.getSongs().get(i).getPath());

                                    al.getSongs().add(SongsAdapter.getSongs().get(i));
                                    albums.add(al);
                                }
                            }
                            albumAdapter albumAdapter = new albumAdapter(getApplicationContext(), albums,listener);
                            rec.setAdapter(albumAdapter);
                        }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setBottomPlayer() {
        if(mediaPlayer != null && playingNow != -1){
            if(bottom.getState() == BottomSheetBehavior.STATE_HIDDEN && playingNow != -1)
                bottom.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if(SongsAdapter.getSongs().get(playingNow).getImage() != null)
                Glide.with(getApplicationContext()).asBitmap().load(SongsAdapter.getSongs().get(playingNow).getImage()).into(song_image);
            else{
                byte[] im = SongsAdapter.getSongImage(SongsAdapter.getSongs().get(playingNow).getPath());
                if(im != null)
                    Glide.with(getApplicationContext()).asBitmap().load(im).into(song_image);
                else
                    Glide.with(getApplicationContext()).asBitmap().load(SongsAdapter.getDefaultImage(getApplicationContext())).into(song_image);
            }

            song_title.setText(SongsAdapter.getSongs().get(playingNow).getTitle());
        }
        else{
            bottom.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }




    private void setButtonsActions() {

        if(!mediaPlayer.isPlaying()){
            play.setImageResource(R.drawable.play);
            play_song.setImageResource(R.drawable.play_white);
            running = false;
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSongNext();
            }
        });

        nextBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                seekBar.setProgress(mediaPlayer.getCurrentPosition()/1000);
                currentTime.setText(convertTime(mediaPlayer.getCurrentPosition()/1000));
                return true;
            }
        });

        prevBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mediaPlayer.getCurrentPosition() - 10000 >= 0) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                    seekBar.setProgress(mediaPlayer.getCurrentPosition() / 1000);
                    currentTime.setText(convertTime(mediaPlayer.getCurrentPosition() / 1000));
                }
                return true;
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSongPrev();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running){
                    play.setImageResource(R.drawable.play);
                    play_song.setImageResource(R.drawable.play_white);
                    mediaPlayer.pause();
                    running = false;
                    onSongPlay();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    play_song.setImageResource(R.drawable.pause_white);
                    mediaPlayer.start();
                    running = true;
                    onSongPause();
                }
            }
        });

        play.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);
                currentTime.setText(convertTime(0));
                return true;
            }
        });

        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(replay)
                {
                    replay = false;
                    replayBtn.setImageResource(R.drawable.repeat_off);
                }
                else
                {
                    replay = true;
                    replayBtn.setImageResource(R.drawable.repeat_on);
                }
            }
        });

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffle)
                {
                    shuffle = false;
                    shuffleBtn.setImageResource(R.drawable.shuffle_off);
                }
                else
                {
                    shuffle = true;
                    shuffleBtn.setImageResource(R.drawable.shuffle_on);
                }
            }
        });
    }

    private void startNewSong() {
        setUpMediaPlayer();

        setFullScreenPlayer();

        setBottomPlayer();
    }

    private void setSongInfo() {
        songTitle.setSelected(true);
        if(mediaPlayer.isPlaying())
            play.setImageResource(R.drawable.pause);
        if(SongsAdapter.getSongs().get(position).getImage() != null)
            Glide.with(getApplicationContext()).asBitmap().load(SongsAdapter.getSongs().get(position).getImage()).into(songImage);
        else {
            byte[] im = SongsAdapter.getSongImage(SongsAdapter.getSongs().get(position).getPath());
            if(im != null)
                Glide.with(this).asBitmap().load(im).into(songImage);
            else
                Glide.with(this).asBitmap().load(SongsAdapter.getDefaultImage(this)).into(songImage);
        }
        songTitle.setText(SongsAdapter.getSongs().get(position).getTitle());
        songArtist.setText(SongsAdapter.getSongs().get(position).getArtist());
    }

    private void setFullScreenPlayer() {
        duration.setText(convertTime(mediaPlayer.getDuration() / 1000));
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                    currentTime.setText(convertTime(mediaPlayer.getCurrentPosition() / 1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        timer = new Timer();
        if(running){
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(this != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mediaPlayer != null){
                                    int current = mediaPlayer.getCurrentPosition()/1000;
                                    Log.i("TAG", "time now is ==> "+current);
                                    seekBar.setProgress(current);
                                    currentTime.setText(convertTime(current));
                                    if((int)(mediaPlayer.getCurrentPosition()/1000) == (int)(mediaPlayer.getDuration()/1000) - 1
                                    || (int)(mediaPlayer.getCurrentPosition()/1000) == (int)(mediaPlayer.getDuration()/1000)){
                                        if(replay)
                                            startNewSong();
                                        else
                                            onSongNext();

                                    }
                                }
                            }
                        });
                    }

                }
            },1000,1000);
        }

        setButtonsActions();

        setSongInfo();

    }

    private void startShuffledSong() {
        position = new Random().nextInt(SongsAdapter.getSongs().size());
        startNewSong();
        setBottomPlayer();

    }

    private void setUpComponents() {
        song_title = findViewById(R.id.song_title);
        song_image = findViewById(R.id.song_image);
        play_song = findViewById(R.id.play_song);
        close = findViewById(R.id.close_song);
        player_layout = findViewById(R.id.player_layout);
        big_player = findViewById(R.id.big_player);
        bottom = BottomSheetBehavior.from(findViewById(R.id.nested_view));
        song_title.setSelected(true);
        big_player.setAlpha(0);
        volume = findViewById(R.id.volume);
        audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        volume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        song_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        song_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        play_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running){
                    running = false;
                    play_song.setImageResource(R.drawable.play_white);
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                    onSongPlay();
                }
                else{
                    running = true;
                    play_song.setImageResource(R.drawable.pause_white);
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                    onSongPause();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notificationManager!=null)
                    notificationManager.cancelAll();
                running = false;
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                play_song.setImageResource(R.drawable.pause_white);
                bottom.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        bottom.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    running = false;
                    if(mediaPlayer != null) {
                        if(notificationManager!=null)
                            notificationManager.cancelAll();
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        play_song.setImageResource(R.drawable.pause_white);
                    }
                }


            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                player_layout.setAlpha(1.0f - 2*slideOffset);
                big_player.setAlpha(2*slideOffset);
            }
        });

        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void startNextSong() {
        if(position + 1 == SongsAdapter.getSongs().size())
            position = 0;
        else
            position++;
        startNewSong();
        setBottomPlayer();

    }

    private void startPrevSong() {
        if(position - 1 < 0)
            position = SongsAdapter.getSongs().size() - 1;
        else
            position--;

        startNewSong();
        setBottomPlayer();

    }

    private void setUpMediaPlayer() {
        playingNow = position;
        songUri = Uri.parse(SongsAdapter.getSongs().get(position).getPath());

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(),songUri);
        mediaPlayer.start();
        running = true;
    }

    private String convertTime(int current) {
        String min = String.valueOf((int)current / 60);
        String sec = String.valueOf((int)current % 60);
        if((current%60) <= 9){
            return (min + ":0" + sec);
        }
        return (min + ":" + sec);
    }


    @Override
    public void onBackPressed() {
        if(bottom != null && bottom.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottom.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else{
            SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(this).edit();
            if(mediaPlayer != null)
                preferences.putInt("lastPosition",playingNow);
            else
                preferences.putInt("lastPosition",-1);
            preferences.commit();
            super.onBackPressed();
        }
    }

    @Override
    public void onAlbumClick(AlbumClass album) {
        swlistener.switchNow(album);
    }

    @Override
    public void switchNow(AlbumClass album) {

    }

    @Override
    public void goBack() {

    }

    void createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(createNotification.CHANNEL_ID,"poula atef", NotificationManager.IMPORTANCE_LOW);
            if(notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void playMusic(int position) {
        this.position = position;
        playingNow = position;
        songAdapter.chooseSong(position);
        startNewSong();
        createChannel();
        registerReceiver(broadcastReceiver,new IntentFilter("tracks-tracks"));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        if(running)
            onSongPause();
        else
            onSongPlay();
    }

    @Override
    public void giveInstance(AlbumFragment.swInterface listener, SongsAdapter.SongListener songListener) {
        this.songSwitchListener = listener;
        this.songListener = songListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mediaPlayer != null ){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            playingNow = preferences.getInt("lastPosition",-1);
            position = playingNow;
            if(playingNow != -1){
                running=true;
                setBottomPlayer();
                setFullScreenPlayer();
                songAdapter.chooseSong(position);
            }
        }
        else
            setBottomPlayer();
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            if(action.equals(createNotification.ACTION_NEXT))
                onSongNext();
            else if(action.equals(createNotification.ACTION_PREV))
                onSongPrev();
            else if(action.equals(createNotification.ACTION_PLAY)){
                if(!running)
                    onSongPause();
                else
                    onSongPlay();
            }

        }
    };

    @Override
    public void onSongPrev() {
        if(shuffle)
            startShuffledSong();
        else
            startPrevSong();
        playingNow = position;
        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if(mediaPlayer != null)
            preferences.putInt("lastPosition",position);
        preferences.commit();
        createNotification.createNotification(this,SongsAdapter.getSongs().get(position),R.drawable.pause,position,SongsAdapter.getSongs().size()-1);
    }

    @Override
    public void onSongNext() {
        if(shuffle)
            startShuffledSong();
        else
            startNextSong();

        playingNow = position;
        SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if(mediaPlayer != null)
            preferences.putInt("lastPosition",position);
        preferences.commit();
        createNotification.createNotification(this,SongsAdapter.getSongs().get(position),R.drawable.pause,position,SongsAdapter.getSongs().size()-1);

    }

    @Override
    public void onSongPause() {
        createNotification.createNotification(this,SongsAdapter.getSongs().get(position),R.drawable.pause,position,SongsAdapter.getSongs().size()-1);
        running = true;
        play.setImageResource(R.drawable.pause);
        play_song.setImageResource(R.drawable.pause_white);
        mediaPlayer.start();
    }

    @Override
    public void onSongPlay() {
        createNotification.createNotification(this,SongsAdapter.getSongs().get(position),R.drawable.play,position,SongsAdapter.getSongs().size()-1);
        running = false;
        play.setImageResource(R.drawable.play);
        play_song.setImageResource(R.drawable.play_white);
        mediaPlayer.pause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroied = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
        return super.onKeyDown(keyCode, event);
    }
}