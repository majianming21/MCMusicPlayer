package com.mc.ink.mcmusicplayer.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.mc.ink.mcmusicplayer.domain.Song;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * 主音乐播放器
 * Created by INK on 2016/12/8.
 */

public class MusicPlayer {
    public static final int PLAYWITHSIGNAL = 0;
    public static final int PLAYWITHSIGNALLOOPING = 1;
    public static final int PLAYWITHRADOM = 2;
    public static final int PLAYWITHSONGLIST = 3;
    public static final int PLAYWITHSONGLISTLOOPING = 4;

    public static final int PLAYING = 10;
    public static final int PAUSE = 11;
    public static final int STOP = 12;


    private static MusicPlayer musicPlayer;
    private MediaPlayer mediaPlayer;
    private List<Song> songList;
    private int playMode;
    private char playStatus;
    private int position;
    private Context context;
    private OnCompletionListener onCompletionListener;
    private OnSeekCompleteListener onSeekCompleteListener;
    private OnPauseListener onPauseListener;
    private OnPlayListener onPlayListener;
    private boolean playByUserChoice;

    private MusicPlayer(Context context) {
        mediaPlayer = new MediaPlayer();
        playMode = PLAYWITHSIGNAL;
        playStatus = STOP;
        position=-1;
        this.context=context;
        setListener();
        playByUserChoice=true;
    }

    public static MusicPlayer getMusicPlayer(Context context) {
        if (musicPlayer == null)
            musicPlayer = new MusicPlayer(context);
        return musicPlayer;
    }
    /**
     * 设置歌曲列表
     * @param songList
     */
    public void setPlayList(List<Song> songList){
        this.songList=songList;
        Toast.makeText(context,this.songList.size()+"",Toast.LENGTH_SHORT).show();
    }

    /**
     *  设置播放模式
     *  单曲播放 Constant.PLAYWITHSIGNAL
     *  单曲循环 Constant.PLAYWITHSIGNALLOOPING
     *  随机播放 Constant.PLAYWITHRADOM
     *  列表播放 Cosntant.PLAYWITHSONGLIST
     *  列表循环 Cosntant.PLAYWITHSONGLISTLOOPING
     * @param playMode
     */
    public void setPlayMode(int playMode){
        this.playMode=playMode;
    }
    /**
     * 获取播放模式
     *  单曲播放 Constant.PLAYWITHSIGNAL
     *  单曲循环 Constant.PLAYWITHSIGNALLOOPING
     *  随机播放 Constant.PLAYWITHRADOM
     *  列表播放 Cosntant.PLAYWITHSONGLIST
     *  列表循环 Cosntant.PLAYWITHSONGLISTLOOPING
     */
    public int getPlayMode(){
        return playMode;
    }

    public void setPosition(int position){
        this.position=position;
        playByUserChoice=true;
    }

    /**
     * 播放
     */
    public void play(){
        if(playByUserChoice){
            //点击列表后播放某首歌曲
            play(position);
        }else{
            //暂停或者停止点击的播放
            //TODO
            play(position);
        }


    }
    /**
     * 暂停
     * 当触发时，如果原来状态为play，则暂停播放,并返回true 否则什么都不做，并返回false
     */
    public boolean pause(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playStatus = PAUSE;
            onPause();
            return true;
        }else{
            return false;
        }
    }
    /**
     * 播放下一首
     */
    public void playNext(){
        int next_position=this.getNextPosition();
        String next_path=getSongPath(next_position);
        if(next_path!=null){
            try {
                play(next_path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 播放
     */
    public void play(int position){
        if(position==-1){
            playNext();
        }else{
            String path=getSongPath(position);
            Toast.makeText(context,path+"",Toast.LENGTH_SHORT).show();
            if(path!=null){
                try {
                    play(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 播放上一首
     */
    public void playPre(){

    }
    /**
     * 返回播放状态
     */
    public int playStatus(){
        return playStatus;
    }

    /**
     * 通过歌曲列表中偏移值获得歌曲路径
     * @param position
     * @return
     */
    private String getSongPath(int position){
        if(songList==null||songList.isEmpty()||songList.size()<=position){
            return null;
        }else{
            return songList.get(position).getData();
        }
    }
    /**
     * 获得即将播放的歌曲的下标
     *  单曲播放 Constant.PLAYWITHSIGNAL
     *  单曲循环 Constant.PLAYWITHSIGNALLOOPING
     *  随机播放 Constant.PLAYWITHRADOM
     *  列表播放 Cosntant.PLAYWITHSONGLIST
     *  列表循环 Cosntant.PLAYWITHSONGLISTLOOPING
     * @return
     */
    private int getNextPosition(){
        int next_position=-1;//当没有下一首时下标为-1
        switch (playMode){
            case PLAYWITHSIGNAL: {
                //单曲播放
                if(position==-1){
                    next_position=0;
                }
            }break;
            case PLAYWITHSIGNALLOOPING: {
                //单曲循环
                if(position!=-1) {
                    next_position=position;
                }else{
                    next_position=0;
                }
            }break;
            case PLAYWITHRADOM: {
                //随机播放
                Random random=new Random();
                next_position=random.nextInt(songList.size());
            }break;
            case PLAYWITHSONGLIST: {
                //列表播放
                if(position!=-1) {//还没开始播放
                    next_position=0;
                }else if(position<songList.size()-1){//当前播放的歌曲不是列表的最后一首
                    next_position=position+1;
                }else{//当前播放的歌曲是列表的最后一首，这下一首应该停止
                    next_position=-1;
                }
            }break;
            case PLAYWITHSONGLISTLOOPING: {
                //列表循环
                if(position!=-1) {//还没开始播放
                    next_position=0;
                }else if(position<songList.size()-1){//当前播放的歌曲不是列表的最后一首
                    next_position=position+1;
                }else{//当前播放的歌曲是列表的最后一首，这下一首应该从头开始
                    next_position=0;
                }
            }break;


        }
        return next_position;
    }

    /**
     * 播放
     * @param path
     * @throws IOException
     */
    private void play(String path) throws IOException {
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
        onPlay();
        playStatus = PLAYING;
    }

    private void onPlay() {
        if(onPlayListener!=null){
            onPlayListener.onPlay();
        }
    }
    private void onPause() {
        if(onPauseListener!=null){
            onPauseListener.onPause();
        }
    }

    /**
     * 获取播放进度
     * 获取播放进度
     */
    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }
    /**
     * 获取播放歌曲最大值
     */
    public int getDuration(){
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
    public void setListener(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(onCompletionListener!=null)
                    onCompletionListener.onCompletion();
                playNext();
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if(onSeekCompleteListener!=null){
                    onSeekCompleteListener.onSeekComplete();
                }
            }
        });
    }

    /**
     *
     * @param onCompletionListener
     */
    public void setOnCompletionListener(OnCompletionListener onCompletionListener){
        this.onCompletionListener=onCompletionListener;
    }
    public interface OnCompletionListener{
        void onCompletion();
    }

    /**
     *
     * @param onSeekCompleteListener
     */

    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener){
        this.onSeekCompleteListener=onSeekCompleteListener;
    }
    public interface OnSeekCompleteListener{
        void onSeekComplete();
    }

    /**
     *
     * @param onPauseListener
     */
    public void setOnPauseListener(OnPauseListener onPauseListener){
        this.onPauseListener=onPauseListener;
    }
    public interface OnPauseListener{
        void onPause();
    }

    /**
     *
     * @param onPlayListener
     */
    public void setOnPlayListener(OnPlayListener onPlayListener){
        this.onPlayListener=onPlayListener;
    }

    /**
     *
     */
    public interface OnPlayListener{
        void onPlay();
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
                if(playMode==PLAYWITHSONGLISTLOOPING){
                    setPlayWithSongListLooping(mp);
                }else if (playMode==PLAYWITHSIGNAL){
                    setPlayWithSignal(mp);
                }else if (playMode== PLAYWITHSIGNALLOOPING){
                    setPlayWithSignalLooping(mp);
                }else if(playMode== PLAYWITHRADOM){
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
