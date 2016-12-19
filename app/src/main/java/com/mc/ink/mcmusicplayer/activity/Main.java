package com.mc.ink.mcmusicplayer.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.mc.ink.mcmusicplayer.util.LogUtil;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by INK
 * on 2016/12/9.
 */


/**
 * https://github.com/LitePalFramework/LitePal
 */
public class Main extends Activity {
    private int playStatus;
    private int playMode;
    private SongLoader songLoader;


    private Spinner spinner;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private TextView max, current, currentSongName;
    // private RecyclerView songListView;

    private Button play;
    private Button search;
    private EditText searchText;
    private Timer timer;
    private TimerTask timerTask;
    private int position;

    private Intent playServiceIntent;
    private Bundle playServiceBundle = new Bundle();
    private List<Song> songsFromSystemMedieDatabse;
    private List<Song> songsFromAppDatabse;
    private SongListAdpter songListAdpter;
    private MusicPlayer musicPlayer;
    private String Tag = "Main";


    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.initialize(this);
  /*      db = LitePal.getDatabase();
        setContentView(R.layout.activity_main);
        initUi();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        songListView.setLayoutManager(linearLayoutManager);
        songListView.setItemAnimator(new DefaultItemAnimator());

       songLoader=new SongLoader();
       songsFromSystemMedieDatabse=songLoader.getSongList(this);
        DataSupport.deleteAll(Song.class);
        for(Song song:songsFromSystemMedieDatabse){
            song.save();
           }*/
/*

        songsFromAppDatabse = DataSupport.findAll(Song.class);
        songListAdpter = new SongListAdpter(songsFromAppDatabse);
        songListView.setAdapter(songListAdpter);
        musicPlayer = MusicPlayer.getMusicPlayer(this);
        musicPlayer.setPlayList(songsFromAppDatabse);
        musicPlayer.setPlayMode(MusicPlayer.PLAY_WITH_RANDOM);
        songListAdpter.setOnDetailsButtonClickListener(new SongListAdpter.OnDetailsButtonClickListener() {
            @Override
            public void onClick(View v, final int position) {
                LogUtil.d(Tag, "用户点击,第" + position + "首歌曲详情按钮");
                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this, R.style.AlertDialogCustom);
                builder.setMessage("确认删除 " + songsFromAppDatabse.get(position).getTitle() + " 吗");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int count = 0;
                        //DataSupport.delete(Song.class,position);
                        db.beginTransaction();
                        count = db.delete("song", "title =?", new String[]{songsFromAppDatabse.get(position).getTitle()});
                        songListAdpter.notifyDataSetChanged();
                        songsFromAppDatabse.remove(position);
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        Toast.makeText(Main.this, "移除" + count + "首歌曲成功", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.setCancelable(false);
                builder.show();
            }
        });
        songListAdpter.setOnClickListener(new SongListAdpter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                musicPlayer.play(position);
                LogUtil.d(Tag, "用户点击,开始播放第" + position + "首歌曲");
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(Main.this, songsFromAppDatabse.get(posotion).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        musicPlayer.addOnErrorListener(new MusicPlayer.OnErrorListener() {
            @Override
            public void onError() {
                Toast.makeText(Main.this, "播放列表错误", Toast.LENGTH_SHORT).show();
            }
        });*/


    }


    public void initUi() {
        //seekBar = (SeekBar) findViewById(R.id.pro);
        // searchText = (EditText) findViewById(R.id.search);
        //  max = (TextView) findViewById(R.id.max);
        //  current = (TextView) findViewById(R.id.curr);
        play = (Button) findViewById(R.id.list_play);
        //    songListView = (RecyclerView) findViewById(R.id.list_song_list);
        //  spinner = (Spinner) findViewById(R.id.spinner);
        //   currentSongName = (TextView) findViewById(R.id.current_song_name);
        //   search = (Button) findViewById(R.id.btn_search);
    }
}
