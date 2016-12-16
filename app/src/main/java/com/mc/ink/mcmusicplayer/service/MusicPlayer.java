package com.mc.ink.mcmusicplayer.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mc.ink.mcmusicplayer.R;
import com.mc.ink.mcmusicplayer.domain.Song;
import com.mc.ink.mcmusicplayer.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 主音乐播放器
 * Created by INK
 * on 2016/12/8.
 */

public class MusicPlayer {
    public static final int PLAY_WITH_SIGNAL = 1;
    public static final int PLAY_WITH_SIGNAL_LOOPING = 2;
    public static final int PLAY_WITH_RANDOM = 3;
    public static final int PLAY_WITH_SONG_LIST = 4;
    public static final int PLAY_WITH_SONG_LIST_LOOPING = 5;

    public static final int PLAYING = 10;
    public static final int PAUSE = 11;
    public static final int STOP = 12;
    private String TAG;

    private static MusicPlayer musicPlayer;
    private MediaPlayer mediaPlayer;
    private List<Song> songList;
    private int playMode;
    private char playStatus;
    private int position;
    private Context context;
    private List<OnCompletionListener> onCompletionListenerList;
    private List<OnSeekCompleteListener> onSeekCompleteListenerList;
    private List<OnPauseListener> onPauseListenerList;
    private List<OnPlayListener> onPlayListenerList;
    private List<OnPlayModeChangeListener> onPlayModeChangeListenerList;
    private boolean playByUserChoice;

    private MusicPlayer(Context context) {
        mediaPlayer = new MediaPlayer();
        playMode = PLAY_WITH_SIGNAL;
        playStatus = STOP;
        position = -1;
        this.context = context;
        setListener();
        playByUserChoice = true;
        TAG = context.getString(R.string.app_name) + " --- MusicPlayer -- ";

    }

    public static MusicPlayer getMusicPlayer(Context context) {
        if (musicPlayer == null)
            musicPlayer = new MusicPlayer(context);
        return musicPlayer;
    }

    /**
     * 设置歌曲列表
     *
     * @param songList
     */
    public void setPlayList(List<Song> songList) {
        Log.i(TAG, "setPlayList(List<Song> songList) 一共加载了" + songList.size() + "首歌曲");
        this.songList = songList;
    }

    /**
     * 设置播放模式
     * 单曲播放 Constant.PLAY_WITH_SIGNAL
     * 单曲循环 Constant.PLAY_WITH_SIGNAL_LOOPING
     * 随机播放 Constant.PLAY_WITH_RANDOM
     * 列表播放 Constant.PLAY_WITH_SONG_LIST
     * 列表循环 Constant.PLAY_WITH_SONG_LIST_LOOPING
     *
     * @param playMode
     */
    public void setPlayMode(int playMode) {
        LogUtil.i(TAG, "setPlayMode(int playMode) 设置播放模式为" + playMode);
        LogUtil.i(TAG, "                          1.单曲播放");
        LogUtil.i(TAG, "                          2.单曲循环");
        LogUtil.i(TAG, "                          3.随机播放");
        LogUtil.i(TAG, "                          4.列表播放");
        LogUtil.i(TAG, "                          5.列表循环");
        this.playMode = playMode;
        if (this.onPlayModeChangeListenerList != null) {
            for (OnPlayModeChangeListener onPlayModeChangeListener : onPlayModeChangeListenerList) {
                onPlayModeChangeListener.onChange();
            }
        }
    }

    /**
     * 获取播放模式
     * 单曲播放 Constant.PLAY_WITH_SIGNAL
     * 单曲循环 Constant.PLAY_WITH_SIGNAL_LOOPING
     * 随机播放 Constant.PLAY_WITH_RANDOM
     * 列表播放 Constant.PLAY_WITH_SONG_LIST
     * 列表循环 Constant.PLAY_WITH_SONG_LIST_LOOPING
     */
    public int getPlayMode() {
        return playMode;
    }

    public void changePlayStatus() {
        if (playStatus == PLAYING) {
            pause();
            onPause();
        } else if (playStatus == PAUSE) {
            play();
            onPlay(true);
        } else {
            play();
            onPlay(false);
        }
    }
    /**
     * 点击播放按钮
     */
    private void play() {
        LogUtil.i(TAG, "play: 用户点击了play");
        if (position == -1) {
            LogUtil.i(TAG, "play: position 为 -1");
            position = playNext(true);
        } else if (playStatus == MusicPlayer.PAUSE) {
            mediaPlayer.start();
        }
        playStatus = PLAYING;
    }

    /**
     * 暂停
     * 当触发时，如果原来状态为play，则暂停播放,并返回true 否则什么都不做，并返回false
     */
    private void pause() {
        if (mediaPlayer.isPlaying()) {
            LogUtil.i(TAG, "pause() 用户点击暂停，播放器停止播放");
            mediaPlayer.pause();
            playStatus = PAUSE;
        }
    }

    /**
     * 播放下一首
     */
    public int playNext(boolean fromUser) {
        int next_position = this.getNextPosition(fromUser);
        String next_path = getSongPath(next_position);
        LogUtil.i(TAG, "playNext() 播放下一首，当前歌曲为第" + position + "首");
        LogUtil.i(TAG, "playNext() 当前播放模式为" + playMode);
        LogUtil.i(TAG, "           1.单曲播放");
        LogUtil.i(TAG, "           2.单曲循环");
        LogUtil.i(TAG, "           3.随机播放");
        LogUtil.i(TAG, "           4.列表播放");
        LogUtil.i(TAG, "           5.列表循环");
        LogUtil.i(TAG, "playNext() 下一首是第" + next_position + "首");

        if (next_path != null) {
            try {
                this.position = next_position;
                play(next_path, fromUser);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return next_position;
    }

    /**
     * 播放
     * 用户点击歌曲列表得到
     * @param position 播放歌曲列表中偏移值为position的歌曲
     *                 如果偏移值为-1，则播放第-1的下一首，即第0首歌曲
     */
    public void play(int position) {
            String path = getSongPath(position);
        LogUtil.i(TAG, path);
            if (path != null) {
                try {
                    this.position = position;
                    play(path, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }


    /**
     * 播放上一首
     */
    public void playPre() {
        /* TODO */
    }

    /**
     * 获得播放状态
     * @return 播放状态
     *
     */
    public int playStatus() {
        return playStatus;
    }

    /**
     * 通过歌曲列表中偏移值获得歌曲路径
     *
     * @param position 歌曲在列表中的偏移值
     * @return 为歌曲路径
     */
    @Nullable
    private String getSongPath(int position) {
        if (songList == null || songList.isEmpty() || songList.size() <= position) {
            return null;
        } else {
            return songList.get(position).getData();
        }
    }


    /**
     * 获得即将播放的歌曲的下标
     * 提供给自动播放队列
     * @return
     */
    private int getNextPosition(boolean fromUser) {
        int next_position = -1;//当没有下一首时下标为-1
        switch (playMode) {
            case PLAY_WITH_SIGNAL: {
                //单曲播放
                /* TODO */
                if (position == -1) {
                    next_position = 0;
                }
            }
            break;
            case PLAY_WITH_SIGNAL_LOOPING: {
                //单曲循环
                if (position != -1) {
                    next_position = position;
                } else {
                    next_position = 0;
                }
            }
            break;
            case PLAY_WITH_RANDOM: {
                //随机播放
                Random random = new Random();
                next_position = random.nextInt(songList.size());
            }
            break;
            case PLAY_WITH_SONG_LIST: {
                //列表播放
                if (position != -1) {//还没开始播放
                    next_position = 0;
                } else if (position < songList.size() - 1) {//当前播放的歌曲不是列表的最后一首
                    next_position = position + 1;
                } else {//当前播放的歌曲是列表的最后一首，这下一首应该停止
                    next_position = -1;
                }
            }
            break;
            case PLAY_WITH_SONG_LIST_LOOPING: {
                //列表循环
                if (position != -1) {//还没开始播放
                    next_position = 0;
                } else if (position < songList.size() - 1) {//当前播放的歌曲不是列表的最后一首
                    next_position = position + 1;
                } else {//当前播放的歌曲是列表的最后一首，这下一首应该从头开始
                    next_position = 0;
                }
            }
            break;


        }
        return next_position;
    }

    /**
     * 播放
     *
     * @param path 歌曲文件的绝对路径
     * @throws IOException 当文件路径或者播放器状态异常时抛出
     */
    private void play(String path, boolean fromUser) throws IOException {
        if (playStatus == PLAYING || playStatus == PAUSE) {
            //播放/暂停
            mediaPlayer.stop();
            mediaPlayer.reset();
            playStatus = STOP;
        } else {
            //停止情况
            mediaPlayer.reset();
        }
        mediaPlayer.setDataSource(path);
        mediaPlayer.prepare();
        mediaPlayer.start();
        onPlay(fromUser);
        playStatus = PLAYING;
    }

    /**
     * 播放时触发
     * 遍历所有监听播放状态的监听器
     */
    private void onPlay(boolean fromUser) {
        if (this.onPlayListenerList != null) {
            for (OnPlayListener onPlayListener : onPlayListenerList) {
                onPlayListener.onPlay(position, fromUser);
            }
        }
    }

    /**
     * 暂停时触发
     * 遍历所有监听暂停状态的监听器
     */
    private void onPause() {
        if (this.onPauseListenerList != null) {
            for (OnPauseListener onPauseListener : onPauseListenerList) {
                onPauseListener.onPause();
            }
        }
    }

    /**
     * 获取播放进度
     * 获取播放进度
     */

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 获取播放歌曲最大值
     */
    public int getDuration() {
        return mediaPlayer.getDuration();
    }



/*   public void setPlayCurentSong(){
        play(position);
    }
    public void setPlayWithSongListLooping(MediaPlayer mp){
        if(++position>=songAdpter.getCount()){
            position=0;
        }
        play(mp,songAdpter.getItem(position).getData());
    }
    public void setPlayWithSongList(MediaPlayer mp){
        if(++position<songAdpter.getCount()) {
            play(mp, songAdpter.getItem(position).getData());
        }else{
            mp.reset();
            stopTimer();
            setSeekBar(0);
            setSeekBarMax(0);
            return;
        }
    }
   public void setPlayWithSignal(MediaPlayer mp){
        mp.reset();
        stopTimer();
        setSeekBar(0);
        setSeekBarMax(0);
        return;
    }
    // Toast.makeText(context, "播放错误", Toast.LENGTH_SHORT).show();
    public void setPlayWithSignalLooping(){
        setPlayCurentSong(position);
    }
    public void setPlayWithRadom(){
        Random random=new Random();
        int po=position;
        while (po==position) {
            po=random.nextInt(songAdpter.getCount());
        }
        position=po;
        play(mp,songAdpter.getItem(position).getData());
    }
    public void play(int position) throws IOException {
        String path=new String();
        play(path);
    }*/

    /**
     *
     */
    private void setListener() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (onCompletionListenerList != null) {
                    for (OnCompletionListener onCompletionListener : onCompletionListenerList) {
                        onCompletionListener.onCompletion();
                    }
                }
                playNext(false);
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (onSeekCompleteListenerList != null) {
                    for (OnSeekCompleteListener onSeekCompleteListener : onSeekCompleteListenerList)
                        onSeekCompleteListener.onSeekComplete();
                }
            }
        });
    }

    /**
     * @param onCompletionListener 播放器播放完成调用接口对象
     */
    public void addOnCompletionListener(OnCompletionListener onCompletionListener) {
        if (onCompletionListener == null) {
            return;
        }
        if (onCompletionListenerList != null) {
            onCompletionListenerList = new ArrayList<>();
        }
        this.onCompletionListenerList.add(onCompletionListener);
    }

    public interface OnCompletionListener {
        void onCompletion();
    }

    /**
     * @param onSeekCompleteListener 播放器移动到某位置完成调用接口对象
     */

    public void addOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {
        if (onSeekCompleteListener == null) {
            return;
        }
        if (onSeekCompleteListenerList == null) {
            onSeekCompleteListenerList = new ArrayList<>();
        }
        this.onSeekCompleteListenerList.add(onSeekCompleteListener);
    }

    public interface OnSeekCompleteListener {
        void onSeekComplete();
    }

    /**
     * @param onPauseListener 播放器暂停调用接口对象
     */
    public void addOnPauseListener(OnPauseListener onPauseListener) {
        if (onPauseListener == null) {
            return;
        }
        if (onPauseListenerList == null) {
            onPauseListenerList = new ArrayList<>();
        }
        this.onPauseListenerList.add(onPauseListener);
    }

    public interface OnPauseListener {
        void onPause();
    }

    /**
     * @param onPlayListener 播放器播放调用接口对象
     */
    public void addOnPlayListener(OnPlayListener onPlayListener) {
        if (onPlayListener == null) {
            return;
        }
        if (onPlayListenerList == null) {
            onPlayListenerList = new ArrayList<>();
        }
        this.onPlayListenerList.add(onPlayListener);
    }

    /**
     *
     */
    public interface OnPlayListener {
        void onPlay(int posotion, boolean fromUser);
    }


    /**
     * @param onPlayModeChangeListener 播放器播放模式改变调用接口对象
     */
    public void addOnPlayModeChangeListener(OnPlayModeChangeListener onPlayModeChangeListener) {
        if (onPlayModeChangeListener == null) {
            return;
        }
        if (onPlayModeChangeListenerList == null) {
            onPlayModeChangeListenerList = new ArrayList<>();
        }
        this.onPlayModeChangeListenerList.add(onPlayModeChangeListener);
    }

    /**
     *
     */
    public interface OnPlayModeChangeListener {
        void onChange();
    }

}














/*







    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (playStatus == PLAYING) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        }
    });
    final ArrayList<String> sin=new ArrayList<>();
    sin.add("单曲播放");
    sin.add("单曲循环");
    sin.add("随机播放");
    sin.add("列表播放");
    sin.add("列表循环");
    ArrayAdapter<String> adp=new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,sin);
    spinner.setAdapter(adp);

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View
        view, int position, long id) {
            playMode=position;
            editor.putInt("play_mode",playMode);
            Toast.makeText(SongListActivity.this, sin.get(position), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    );



        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);*//*
       *//*     }
        });*//*

    search.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick:current_song_position sharedPreferences.getInt(\\\"current_song_position\\\",-1)"+sharedPreferences.getInt("current_song_position",-1));
        }
    });

    pause.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (playStatus == PLAYING) {
                //播放中
                //应该暂停
                mediaPlayer.pause();
                playStatus = PAUSE;
                //进度条停止
                stopTimer();
            } else if (playStatus == PAUSE) {
                //暂停情况
                //应该继续播放
                mediaPlayer.start();
                playStatus=PLAYING;
                //进度条继续
                startTimer();
            } else {
                //停止了
                //还没播放或者这首歌已经结束
                //无动作
            }
        }
    });*/
/*        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = ((SongAdpter1.SongHolder) view.getTag()).data;
                SongListActivity.this.position=position;
                play(mediaPlayer,textView.getText().toString());
            }
        });
        songList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SongAdpter1.SongHolder songHolder = ((SongAdpter1.SongHolder) view.getTag());
                //Toast.makeText(MainActivity.this,textView.getText().toString(),Toast.LENGTH_SHORT).show();
                // return false;
                Bundle bundle=new Bundle();
                bundle.putString("data",songHolder.data.getText().toString());
                bundle.putString("length",songHolder.size.getText().toString());
                bundle.putString("name",songHolder.name.getText().toString());

                Intent intent=new Intent(SongListActivity.this,SongDetails.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                return true;
            }
        });*/

/*        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(playMode==PLAY_WITH_SONG_LIST_LOOPING){
                    setPlayWithSongListLooping(mp);
                }else if (playMode==PLAY_WITH_SIGNAL){
                    setPlayWithSignal(mp);
                }else if (playMode== PLAY_WITH_SIGNAL_LOOPING){
                    setPlayWithSignalLooping(mp);
                }else if(playMode== PLAY_WITH_RANDOM){
                    setPlayWithRadom(mp);
                }else{
                    //playWithSongList
                    setPlayWithSongList(mp);
                }



            }
        })

    //  getSharedPreferences();
}

    private void getSharedPreferences() {
        if(position!=-1){
            //这个时候表示SharedPreferences data 文件存在 说明之前保存过
            Log.d(TAG, "onCreate: position!=-1"+true+"current_position"+position);
            setPlayCurentSong(mediaPlayer);
            playStatus=PAUSE;
            mediaPlayer.pause();
        }
        Log.d(TAG, "getSharedPreferences: playMode"+playMode);
        spinner.setSelection(playMode,true);
    }*/

   /* public void setPlayCurentSong(MediaPlayer mp){
        play(mp,songAdpter.getItem(position).getData());
    }
    public void setPlayWithSongListLooping(MediaPlayer mp){
        if(++position>=songAdpter.getCount()){
            position=0;
        }
        play(mp,songAdpter.getItem(position).getData());
    }
    public void setPlayWithSongList(MediaPlayer mp){
        if(++position<songAdpter.getCount()) {
            play(mp, songAdpter.getItem(position).getData());
        }else{
            mp.reset();
            stopTimer();
            setSeekBar(0);
            setSeekBarMax(0);
            return;
        }
    }*/
  /*  public void setPlayWithSignal(MediaPlayer mp){
        mp.reset();
        stopTimer();
        setSeekBar(0);
        setSeekBarMax(0);
        return;
    }
    public void setPlayWithSignalLooping(MediaPlayer mp){
        setPlayCurentSong(mp);
    }
    public void setPlayWithRadom(MediaPlayer mp){
        Random random=new Random();
        int po=position;
        while (po==position) {
            po=random.nextInt(songAdpter.getCount());
        }
        position=po;
        play(mp,songAdpter.getItem(position).getData());
    }

*/

  /*  public void play(MediaPlayer mediaPlayer,String dataSource){
        if (playStatus == PLAYING || playStatus == PAUSE) {
            //播放/暂停
            mediaPlayer.stop();
            mediaPlayer.reset();
            // stopTimer();
        } else {
            //停止情况
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentSongName.setText(songAdpter.getItem(position).getTitle());
        // Toast.makeText(MainActivity.this,songAdpter.getItem(position).getTitle(), Toast.LENGTH_SHORT).show();
        mediaPlayer.start();
        playStatus = PLAYING;
        setSeekBar(0);
        setSeekBarMax(mediaPlayer.getDuration());
        startTimer();
        editor.putInt("current_song_position",position);
        editor.commit();
        Log.d(TAG, "onCompletion play: current_song_position"+position);
    }*/
/*

    public void startTimer(){
        if(timer==null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSeekBar(mediaPlayer.getCurrentPosition());
                            service();

                        }
                    });
                }
            };
            timer.schedule(timerTask, 0, 100);
        }
    }
    public void stopTimer(){
        if(timer!=null) {
            timer.cancel();
            timer=null;
            timerTask.cancel();
            timerTask=null;
        }
    }

    public void setSeekBarMax(int prosses) {
        seekBar.setMax(prosses);
        max.setText(TimeUtil.timeParse(prosses));
        playServiceBundle.putInt("max",prosses);
    }


    public void setSeekBar(int prosses) {
        seekBar.setProgress(prosses);
        current.setText(TimeUtil.timeParse(prosses));
        playServiceBundle.putInt("prosses",prosses);
    }


    public void service() {
        playServiceIntent.putExtras(playServiceBundle);
        startService(playServiceIntent);
    }

    public SongAdpter1 getSongAdpter() {
        SongAdpter1 songAdpter = new SongAdpter1(this, R.layout.song_item, getSongList());
        return songAdpter;
    }


}
*/
