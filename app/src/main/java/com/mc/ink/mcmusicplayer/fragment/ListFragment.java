package com.mc.ink.mcmusicplayer.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.mc.ink.mcmusicplayer.activity.Main;
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


    private SQLiteDatabase db;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        play = (Button) view.findViewById(R.id.list_play);
        songListView = (RecyclerView) view.findViewById(R.id.list_song_list);
        LitePal.initialize(view.getContext());
        db = LitePal.getDatabase();
        songListView.setLayoutManager(new LinearLayoutManager(getContext()));
        songListView.setItemAnimator(new DefaultItemAnimator());
        songsFromAppDatabse = DataSupport.findAll(Song.class);
        songListAdpter = new SongListAdpter(songsFromAppDatabse);
        songListView.setAdapter(songListAdpter);
        musicPlayer = MusicPlayer.getMusicPlayer(getContext());
        musicPlayer.setPlayList(songsFromAppDatabse);
        musicPlayer.setPlayMode(MusicPlayer.PLAY_WITH_RANDOM);
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
                Toast.makeText(getContext(), "暂停", Toast.LENGTH_SHORT).show();
            }
        });
        musicPlayer.addOnPlayListener(new MusicPlayer.OnPlayListener() {
            @Override
            public void onPlay(int posotion, boolean fromUser) {
                if (fromUser)
                    Toast.makeText(getContext(), "播放", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), songsFromAppDatabse.get(posotion).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        musicPlayer.addOnErrorListener(new MusicPlayer.OnErrorListener() {
            @Override
            public void onError() {
                Toast.makeText(getContext(), "播放列表错误", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, null);
        return view;
    }
}