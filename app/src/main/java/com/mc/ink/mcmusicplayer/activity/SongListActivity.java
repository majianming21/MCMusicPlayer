package com.mc.ink.mcmusicplayer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mc.ink.mcmusicplayer.R;
import com.mc.ink.mcmusicplayer.adpter.SongAdpter1;
import com.mc.ink.mcmusicplayer.adpter.SongListAdpter;
import com.mc.ink.mcmusicplayer.domain.Song;
import com.mc.ink.mcmusicplayer.service.MusicPlayer;
import com.mc.ink.mcmusicplayer.util.TimeUtil;
import com.mc.ink.mcmusicplayer.service.PlayServiceNotic;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by INK on 2016/12/6.
 */

public class SongListActivity extends Activity {

    private int playStatus;
    private int playMode;


    private Spinner spinner;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private TextView max, current,currentSongName;
    private RecyclerView songList;

    private Button pause;
    private EditText searchText;
    private Timer timer;
    private TimerTask timerTask;
    private int position;
   // private SongAdpter1 songAdpter;
    private Intent playServiceIntent;
    private Bundle playServiceBundle=new Bundle();

    private Button search;

    private final String TAG = "MainActivity";

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                    Toast.makeText(SongListActivity.this, "CALL_PHONE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        songList.setLayoutManager(linearLayoutManager);
        SongListAdpter adpter=new SongListAdpter(getSongList());
        //songAdpter=getSongAdpter();
        songList.setAdapter(adpter);
        mediaPlayer = new MediaPlayer();
        sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        position=sharedPreferences.getInt("current_song_position",-1);
        playMode = sharedPreferences.getInt("play_mode", MusicPlayer.PLAY_WITH_SIGNAL);
        playServiceIntent=new Intent(this,PlayServiceNotic.class);
    /*    MusicPlayer musicPlayer=MusicPlayer.getMusicPlayer(this);
        musicPlayer.setOnCompletionListener(new MusicPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {

            }
        });*/

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (playStatus == MusicPlayer.PLAYING) {
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
        ArrayAdapter<String> adp=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,sin);
        spinner.setAdapter(adp);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                              @Override
                                              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                  playMode=position;
                                                  editor.putInt("play_mode",playMode);
                                                  Toast.makeText(SongListActivity.this, sin.get(position), Toast.LENGTH_SHORT).show();
                                              }

                                              @Override
                                              public void onNothingSelected(AdapterView<?> parent) {

                                              }
                                          }
        );



        /*btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);*/
       /*     }
        });*/

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:current_song_position sharedPreferences.getInt(\\\"current_song_position\\\",-1)"+sharedPreferences.getInt("current_song_position",-1));
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playStatus == MusicPlayer.PLAYING) {
                    //播放中
                    //应该暂停
                    mediaPlayer.pause();
                    playStatus = MusicPlayer.PAUSE;
                    //进度条停止
                    stopTimer();
                } else if (playStatus == MusicPlayer.PAUSE) {
                    //暂停情况
                    //应该继续播放
                    mediaPlayer.start();
                    playStatus = MusicPlayer.PLAYING;
                    //进度条继续
                    startTimer();
                } else {
                    //停止了
                    //还没播放或者这首歌已经结束
                    //无动作
                }
            }
        });
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
        })

      mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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
        });*/

      //  getSharedPreferences();
    }
/*
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

    public ArrayList<Song> getSongList() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(SongListActivity.this, Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SongListActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
               // finish();
            }
        }
        ArrayList<Song> songs = new ArrayList<>();
        MediaScannerConnection.scanFile(this, new String[]{Environment
                .getExternalStorageDirectory().getAbsolutePath()}, null, null);
        Cursor cursor = this.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA
                },
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[]{"audio/mpeg", "audio/x-ms-wma"}, null);
        if (cursor.moveToFirst()) {
            Song song;
            do {
                song = new Song();
                song.setTitle(cursor.getString(2));
                song.setData(cursor.getString(9));
                song.setDuration(cursor.getLong(3));
                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }

}
