package com.mc.ink.mcmusicplayer.fragment;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mc.ink.mcmusicplayer.service.PlayerService;
import com.mc.ink.mcmusicplayer.util.LogUtil;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 马坚铭
 * on 2016/12/18.
 */

public class ListFragment extends Fragment {
    private View view;


    private int playStatus;
    private int playMode;
    private SongLoader songLoader;


    private Spinner spinner;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private TextView max, current, currentSongName;
    private RecyclerView songListView;
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
    private PlayerService playerService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.PlayBinder playBinder = (PlayerService.PlayBinder) service;
            playerService = (PlayerService) playBinder.getService();
            playerService.setSongs(songsFromAppDatabse);
            playerService.setPlayMode(MusicPlayer.PLAY_WITH_RANDOM);
            LogUtil.d(Tag, "onServiceConnected 服务连接");
            playerService.addOnPauseListener(new PlayerService.OnPauseListener() {
                @Override
                public void onPause() {
                    Toast.makeText(getContext(), "暂停", Toast.LENGTH_SHORT).show();
                }
            });
            playerService.addOnPlayListener(new PlayerService.OnPlayListener() {
                @Override
                public void onPlay(int posotion, boolean fromUser) {
                    if (fromUser)
                        Toast.makeText(getContext(), "播放", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), songsFromAppDatabse.get(posotion).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
            playerService.addOnErrorListener(new PlayerService.OnErrorListener() {
                @Override
                public void onError() {
                    Toast.makeText(getContext(), "播放列表错误", Toast.LENGTH_SHORT).show();
                }
            });
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerService.changePlayStatus();
                    LogUtil.d(Tag, "用户点击播放歌曲");
                }
            });
            songListAdpter.setOnClickListener(new SongListAdpter.OnClickListener() {
                @Override
                public void onClick(View view, int position) {
                    playerService.play(position);
                    LogUtil.d(Tag, "用户点击,开始播放第" + position + "首歌曲");
                }
            });
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private SQLiteDatabase db;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent bindIntent = new Intent(getContext(), PlayerService.class);
        getContext().bindService(bindIntent, serviceConnection, getContext().BIND_AUTO_CREATE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LitePal.initialize(getContext());
        db = LitePal.getDatabase();

        songsFromAppDatabse = DataSupport.findAll(Song.class);
        songListAdpter = new SongListAdpter(songsFromAppDatabse);
        songListView.setAdapter(songListAdpter);
        songListAdpter.setOnDetailsButtonClickListener(new SongListAdpter.OnDetailsButtonClickListener() {
            @Override
            public void onClick(View v, final int position) {
                LogUtil.d(Tag, "用户点击,第" + position + "首歌曲详情按钮");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
                builder.setMessage("确认删除 " + songsFromAppDatabse.get(position).getTitle() + " 吗");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int count = 0;
                        db.beginTransaction();
                        count = db.delete("song", "title =?", new String[]{songsFromAppDatabse.get(position).getTitle()});
                        songListAdpter.notifyDataSetChanged();
                        songsFromAppDatabse.remove(position);
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        Toast.makeText(getContext(), "移除" + count + "首歌曲成功", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.setCancelable(false);
                builder.show();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, null);
        initView();
        return view;
    }

    public void initView() {
        play = (Button) view.findViewById(R.id.list_play);
        songListView = (RecyclerView) view.findViewById(R.id.list_song_list);
        songListView.setLayoutManager(new LinearLayoutManager(getContext()));
        songListView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}