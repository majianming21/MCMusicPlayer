package com.mc.ink.mcmusicplayer.fragment;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mc.ink.mcmusicplayer.R;
import com.mc.ink.mcmusicplayer.activity.SongTextView;
import com.mc.ink.mcmusicplayer.adpter.SongListAdpter;
import com.mc.ink.mcmusicplayer.domain.Song;
import com.mc.ink.mcmusicplayer.loader.SongLoader;
import com.mc.ink.mcmusicplayer.service.MusicPlayer;
import com.mc.ink.mcmusicplayer.service.PlayerService;
import com.mc.ink.mcmusicplayer.util.LogUtil;
import com.mc.ink.mcmusicplayer.util.TimeUtil;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.logging.LogRecord;

/**
 * Created by 马坚铭
 * on 2016/12/18.
 */

public class ListFragment extends Fragment {
    private View view;


    //  private int playStatus;
    private int playMode;
    private SongLoader songLoader;
    private FloatingActionButton playModeButton;
    private AppCompatSeekBar seekBar;
    //   private Spinner spinner;
//    private MediaPlayer mediaPlayer;
//    private SeekBar seekBar;
    //   private SharedPreferences.Editor editor;
    //   private SharedPreferences sharedPreferences;
    //   private TextView max, current, currentSongName;
    private RecyclerView songListView;
    private Button play;
    /*    private Button search;
        private EditText searchText;
        private Timer timer;
        private TimerTask timerTask;
        private int position;
        private Intent playServiceIntent;
        private Bundle playServiceBundle = new Bundle();*/
    private TextView songCurrentDuration, songDuration;
    private TextView currentSongName;
    private List<Song> songsFromSystemMedieDatabse;
    private List<Song> songsFromAppDatabse;
    private SongListAdpter songListAdpter;
    private String Tag = "Main";
    private PlayerService playerService;
    private Message message;
    private Bundle bundle;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle;
            int song_duration;
                    /*switch (msg.what){
                        case 0x123:{
                            bundle=msg.getData();
                            song_duration=bundle.getInt("song_duration");
                            seekBar.setMax(song_duration);
                            songDuration.setText(TimeUtil.timeParse(song_duration));
                        }break;
                        case 0x124:{
                            bundle=msg.getData();
                            song_duration=bundle.getInt("song_current_duration");
                            seekBar.setProgress(song_duration);
                            songCurrentDuration.setText(TimeUtil.timeParse(song_duration));
                        }break;
                    }*/
        }
    };

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
                    currentSongName.clearFocus();
                }
            });

            playerService.addOnPlayListener(new PlayerService.OnPlayListener() {
                @Override
                public void onPlay(int position, int song_duration, boolean fromUser) {
                    message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("song_duration", song_duration);
                    message.what = 0x123;
                    message.setData(bundle);
                    handler.sendMessage(message);

                    if (fromUser)
                        Toast.makeText(getContext(), "播放", Toast.LENGTH_SHORT).show();
                    else {
                        //String songsFromAppDatabse.get(position).getTitle()
                        //   Toast.makeText(getContext(), songsFromAppDatabse.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                        currentSongName.setText(songsFromAppDatabse.get(position).getTitle());
                    }
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
            playerService.addOnPlayPositionChangeListener(new PlayerService.OnPlayPositionChangeListener() {
                @Override
                public void onChange(int position, String str_position) {
                    message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("song_current_duration", position);
                    message.what = 0x124;
                    message.setData(bundle);
                    handler.sendMessage(message);
                    //LogUtil.d(Tag,"播放状态改变");
                }
            });
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //playerService.set
                    // seekBar.getProgress();
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
        playMode = MusicPlayer.PLAY_WITH_RANDOM;
        Intent bindIntent = new Intent(getContext(), PlayerService.class);
        getContext().bindService(bindIntent, serviceConnection, getContext().BIND_AUTO_CREATE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LitePal.initialize(getContext());
        db = LitePal.getDatabase();


        songLoader = new SongLoader();
        songsFromSystemMedieDatabse=songLoader.getSongList(getContext());
        DataSupport.deleteAll(Song.class);
        for(Song song:songsFromSystemMedieDatabse){
            song.save();
        }


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
                        int count;
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
        seekBar = (AppCompatSeekBar) view.findViewById(R.id.list_seekbar);
        songCurrentDuration = (TextView) view.findViewById(R.id.list_play_current_position);
        songDuration = (TextView) view.findViewById(R.id.list_play_max_position);
        currentSongName = (TextView) view.findViewById(R.id.list_play_current_song_name);
        // currentSongName.setMovementMethod(new ScrollingMovementMethod());
        //   currentSongName.setSelected(true);
        // currentSongName.requestFocus();
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