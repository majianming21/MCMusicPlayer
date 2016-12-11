package com.mc.ink.mcmusicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mc.ink.mcmusicplayer.R;
import com.mc.ink.mcmusicplayer.adpter.SongListAdpter;
import com.mc.ink.mcmusicplayer.domain.Song;
import com.mc.ink.mcmusicplayer.loader.SongLoader;
import com.mc.ink.mcmusicplayer.service.MusicPlayer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by INK on 2016/12/9.
 */

public class Main  extends Activity{
    private int playStatus;
    private int playMode;
    private SongLoader songLoader;


    private Spinner spinner;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private TextView max, current,currentSongName;
    private RecyclerView songListView;

    private Button pause;
    private EditText searchText;
    private Timer timer;
    private TimerTask timerTask;
    private int position;

    private Intent playServiceIntent;
    private Bundle playServiceBundle=new Bundle();
    private List<Song> songs;
    private SongListAdpter songListAdpter;
    private MusicPlayer musicPlayer;
    private String Tag="Main";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initUi();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        songListView.setLayoutManager(linearLayoutManager);
//        songListView.setItemAnimator(new DefaultItemAnimator());
        songLoader=new SongLoader();
        songs=songLoader.getSongList(this);
        songListAdpter=new SongListAdpter(songs);
        songListView.setAdapter(songListAdpter);
        /*Toast.makeText(this,"一共加载了"+songListAdpter.getItemCount()+"首歌曲",Toast.LENGTH_SHORT).show();
        LogUtil.d(Tag,"一共加载了"+songListAdpter.getItemCount()+"首歌曲");
        LogUtil.d(Tag,"一共加载了"+songs.size()+"首歌曲");*/
        musicPlayer=MusicPlayer.getMusicPlayer(this);
        musicPlayer.setPlayList(songs);

        /*musicPlayer.setPosition(new Random().nextInt(songs.size()));*/
        musicPlayer.play(2);
        musicPlayer.setPlayMode(MusicPlayer.PLAY_WITH_RANDOM);
        songListAdpter.setOnClickListener(new SongListAdpter.OnClickListener() {
            @Override
            public void onClick(View v, int position) {
                musicPlayer.play(position);
                Toast.makeText(Main.this, v.toString() + " " + position, Toast.LENGTH_SHORT).show();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicPlayer.changePlayStatus();
            }
        });
        musicPlayer.addOnPauseListener(new MusicPlayer.OnPauseListener() {
            @Override
            public void onPause() {
                Toast.makeText(Main.this, "暂停", Toast.LENGTH_SHORT).show();
            }
        });
        musicPlayer.addOnPlayListener(new MusicPlayer.OnPlayListener() {
            @Override
            public void onPlay(int posotion, boolean fromUser) {
                if (fromUser)
                    Toast.makeText(Main.this, "播放", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Main.this, songs.get(posotion).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void initUi() {
        seekBar = (SeekBar) findViewById(R.id.pro);
        searchText = (EditText) findViewById(R.id.search);
        max = (TextView) findViewById(R.id.max);
        current = (TextView) findViewById(R.id.curr);
        pause = (Button) findViewById(R.id.pause);
        songListView = (RecyclerView) findViewById(R.id.db);
        spinner= (Spinner) findViewById(R.id.spinner);
        currentSongName= (TextView) findViewById(R.id.current_song_name);
       // search= (Button) findViewById(R.id.btn_search);
    }
}
