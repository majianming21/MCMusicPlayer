package com.mc.ink.mcmusicplayer.fragment;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private FloatingActionButton playModeButton;

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
    private String Tag = "Main";
    private PlayerService playerService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.PlayBinder playBinder = (PlayerService.PlayBinder) service;
            playerService = (PlayerService) playBinder.getService();
            LogUtil.i(Tag, "onServiceConnected 服务连接");
            playerService.setSongs(songsFromAppDatabse);
            playerService.setPlayMode(playMode);
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
                    LogUtil.i(Tag, "用户点击播放歌曲");
                }
            });
            songListAdpter.setOnClickListener(new SongListAdpter.OnClickListener() {
                @Override
                public void onClick(View view, int position) {
                    playerService.play(position);
                    LogUtil.i(Tag, "用户点击,开始播放第" + position + "首歌曲");
                }
            });
            playerService.addOnPlayModeChangeListener(new PlayerService.OnPlayModeChangeListener() {
                @Override
                public void onChange(int playMode) {
                    switch (playMode) {
                        case MusicPlayer.PLAY_WITH_RANDOM: {
                            //随机播放
                            playModeButton.setImageResource(R.drawable.ic_playmode_random);
                            LogUtil.i(Tag, "改变播放模式--随机播放");
                        }
                        break;
                        case MusicPlayer.PLAY_WITH_SIGNAL: {
                            //单曲播放
                            playModeButton.setImageResource(R.drawable.ic_playmode_signal);
                            LogUtil.i(Tag, "改变播放模式--单曲播放");
                        }
                        break;
                        case MusicPlayer.PLAY_WITH_SIGNAL_LOOPING: {
                            //单曲循环
                            playModeButton.setImageResource(R.drawable.ic_playmode_signal_looping);
                            LogUtil.i(Tag, "改变播放模式--单曲循环");
                        }
                        break;
                        case MusicPlayer.PLAY_WITH_SONG_LIST: {
                            //列表播放
                            playModeButton.setImageResource(R.drawable.ic_playmode_list);
                            LogUtil.i(Tag, "改变播放模式--列表播放");
                        }
                        break;
                        case MusicPlayer.PLAY_WITH_SONG_LIST_LOOPING: {
                            //列表循环
                            playModeButton.setImageResource(R.drawable.ic_playmode_list_looping);
                            LogUtil.i(Tag, "改变播放模式--列表循环");
                        }
                        break;
                    }
                }
            });
            playerService.setPlayMode(playMode);
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private SQLiteDatabase db;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playMode = MusicPlayer.PLAY_WITH_RANDOM;
        Intent bindIntent = new Intent(getContext(), PlayerService.class);
        getContext().bindService(bindIntent, serviceConnection, getContext().BIND_AUTO_CREATE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LitePal.initialize(getContext());
        db = LitePal.getDatabase();


/*        songLoader=new SongLoader();
        songsFromSystemMedieDatabse=songLoader.getSongList(getContext());
        DataSupport.deleteAll(Song.class);
        for(Song song:songsFromSystemMedieDatabse){
            song.save();
        }*/


        songsFromAppDatabse = DataSupport.findAll(Song.class);
        songListAdpter = new SongListAdpter(songsFromAppDatabse);
        songListView.setAdapter(songListAdpter);
        songListAdpter.setOnDetailsButtonClickListener(new SongListAdpter.OnDetailsButtonClickListener() {
            @Override
            public void onClick(View v, final int position) {
                LogUtil.i(Tag, "用户点击,第" + position + "首歌曲删除按钮");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
                builder.setMessage("确认删除 " + songsFromAppDatabse.get(position).getTitle() + " 吗");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int count = 0;
                        db.beginTransaction();
                        count = db.delete("song", "title =?", new String[]{songsFromAppDatabse.get(position).getTitle()});
                        songListAdpter.notifyItemRemoved(position);
                        // songListAdpter.notifyDataSetChanged();
                        songsFromAppDatabse.remove(position);
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        LogUtil.i(Tag, "删除歌曲--用户选择确定");
                        Toast.makeText(getContext(), "移除" + count + "首歌曲成功", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtil.i(Tag, "删除歌曲--用户选择取消");
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });
        /*playModeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getEventTime()>2000){
              //      LogUtil.d(Tag,"onTouch x="+event.getRawX()+"y="+event.getRawY());
               //     LogUtil.d(Tag,"onTouch x"+(event.getRawY()-event.getYPrecision()));
                    int result = 0;
                    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                    if (resourceId > 0) {
                        result = getResources().getDimensionPixelSize(resourceId);
                    }
                    playModeButton.setX(event.getRawX()- playModeButton.getSize()/2);
                    playModeButton.setY(event.getRawY()- playModeButton.getSize()/2-40);
                    return true;
                }
                else{
                    return false;
                }
            }
        });
        playModeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        playModeButton.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                LogUtil.d(Tag,"onDrag x="+event.getX()+"y="+event.getY());

                return false;
            }
        });*/

        playModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*public static final int PLAY_WITH_SIGNAL = 1;
                public static final int PLAY_WITH_SIGNAL_LOOPING = 2;
                public static final int PLAY_WITH_RANDOM = 3;
                public static final int PLAY_WITH_SONG_LIST = 4;
                public static final int PLAY_WITH_SONG_LIST_LOOPING = 5;*/
                if (playMode < MusicPlayer.PLAY_WITH_SONG_LIST_LOOPING) {
                    playMode++;
                } else {
                    playMode = MusicPlayer.PLAY_WITH_SIGNAL;
                }
                playerService.setPlayMode(playMode);
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
        playModeButton = (FloatingActionButton) view.findViewById(R.id.list_play_mode);
        songListView.setLayoutManager(new LinearLayoutManager(getContext()));
        songListView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


 /*   class PlayerListener implements PlayerService.OnPlayListener,
            SongListAdpter.OnDetailsButtonClickListener,
           SongListAdpter.OnClickListener,
            View.OnClickListener,
            PlayerService.OnPauseListener,
            PlayerService.OnErrorListener,
            PlayerService.OnPlayModeChangeListener {

        @Override
        public void onPlay(int posotion, boolean fromUser) {
            if (fromUser)
                Toast.makeText(getContext(), "播放", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), songsFromAppDatabse.get(posotion).getTitle(), Toast.LENGTH_SHORT).show();
        }

        *//*
         * SongListAdpter.OnDetailsButtonClickListener
         * @param view
         * @param position
         *//*
        @Override
        public void onClick(View view,final int position) {
            switch (view.getId()){
                case R.id.details:{
                    LogUtil.d(Tag, "用户点击,第" + position + "首歌曲详情按钮");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
                    builder.setMessage("确认删除 " + songsFromAppDatabse.get(position).getTitle() + " 吗");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int count = 0;
                            db.beginTransaction();
                            count = db.delete("song", "title =?", new String[]{songsFromAppDatabse.get(position).getTitle()});
                            songListAdpter.notifyItemRemoved(position);
                            // songListAdpter.notifyDataSetChanged();
                            songsFromAppDatabse.remove(position);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            Toast.makeText(getContext(), "移除" + count + "首歌曲成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.setCancelable(false);
                    builder.show();
                }break;
            }
        }

        *//*
         * View.OnClickListener
         * @param v
         *//*
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button: {
                    *//*public static final int PLAY_WITH_SIGNAL = 1;
                        public static final int PLAY_WITH_SIGNAL_LOOPING = 2;
                        public static final int PLAY_WITH_RANDOM = 3;
                        public static final int PLAY_WITH_SONG_LIST = 4;
                        public static final int PLAY_WITH_SONG_LIST_LOOPING = 5;*//*
                    if (playMode < MusicPlayer.PLAY_WITH_SONG_LIST_LOOPING) {
                        playMode++;
                    } else {
                        playMode = MusicPlayer.PLAY_WITH_SIGNAL;
                    }
                    playerService.setPlayMode(playMode);
                }break;
                case R.id.list_play:{
                    playerService.changePlayStatus();
                    LogUtil.d(Tag, "用户点击播放歌曲");
                }break;
            }
        }

        @Override
        public void onChange(int playMode) {
            switch(playMode){
                case MusicPlayer.PLAY_WITH_RANDOM: {
                    //随机播放
                    playModeButton.setImageResource(R.drawable.ic_playmode_random);
                    LogUtil.i(Tag,"改变播放模式--随机播放");
                }break;
                case MusicPlayer.PLAY_WITH_SIGNAL: {
                    //单曲播放
                    playModeButton.setImageResource(R.drawable.ic_playmode_signal);
                    LogUtil.i(Tag,"改变播放模式--单曲播放");
                }break;
                case MusicPlayer.PLAY_WITH_SIGNAL_LOOPING: {
                    //单曲循环
                    playModeButton.setImageResource(R.drawable.ic_playmode_signal_looping);
                    LogUtil.i(Tag,"改变播放模式--单曲循环");
                }break;
                case MusicPlayer.PLAY_WITH_SONG_LIST: {
                    //列表播放
                    playModeButton.setImageResource(R.drawable.ic_playmode_list);
                    LogUtil.i(Tag,"改变播放模式--列表播放");
                }break;
                case MusicPlayer.PLAY_WITH_SONG_LIST_LOOPING: {
                    //列表循环
                    playModeButton.setImageResource(R.drawable.ic_playmode_list_looping);
                    LogUtil.i(Tag,"改变播放模式--列表循环");
                }break;
            }
        }

        @Override
        public void onPause() {
            Toast.makeText(getContext(), "暂停", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError() {
            Toast.makeText(getContext(), "播放列表错误", Toast.LENGTH_SHORT).show();
        }
    }
*/
}