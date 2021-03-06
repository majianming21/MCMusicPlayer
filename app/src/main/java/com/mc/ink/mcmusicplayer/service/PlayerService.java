package com.mc.ink.mcmusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mc.ink.mcmusicplayer.domain.Song;
import com.mc.ink.mcmusicplayer.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 马坚铭
 * on 2016/12/18.
 */

public class PlayerService extends Service {
    private IBinder iBinder;
    private MusicPlayer musicPlayer;
    private List<OnCompletionListener> onCompletionListenerList;
    private List<OnSeekCompleteListener> onSeekCompleteListenerList;
    private List<OnPauseListener> onPauseListenerList;
    private List<OnPlayListener> onPlayListenerList;
    private List<OnPlayModeChangeListener> onPlayModeChangeListenerList;
    private List<OnErrorListener> onErrorListenerList;
    private List<OnPlayPositionChangeListener> onPlayPositionChangeListenerList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public class PlayBinder extends Binder {
        public Service getService() {
            return PlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        iBinder = new PlayBinder();
        musicPlayer = MusicPlayer.getMusicPlayer(PlayerService.this);
        musicPlayer.setOnCompletionListener(new MusicPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                if (onCompletionListenerList != null) {
                    for (OnCompletionListener onCompletionListener : onCompletionListenerList) {
                        if (onCompletionListener != null) {
                            onCompletionListener.onCompletion();
                        }
                    }
                }
            }
        });
        musicPlayer.setOnPlayListener(new MusicPlayer.OnPlayListener() {
            @Override
            public void onPlay(int position, int song_duration, boolean fromUser) {
                if (onPlayListenerList != null) {
                    for (OnPlayListener onPlayListener : onPlayListenerList) {
                        if (onPlayListener != null) {
                            onPlayListener.onPlay(position, song_duration, fromUser);
                        }
                    }
                }
            }
        });
        musicPlayer.setOnErrorListener(new MusicPlayer.OnErrorListener() {
            @Override
            public void onError() {
                if (onErrorListenerList != null) {
                    for (OnErrorListener onErrorListener : onErrorListenerList) {
                        if (onErrorListener != null) {
                            onErrorListener.onError();
                        }
                    }
                }
            }
        });
        musicPlayer.setOnPauseListener(new MusicPlayer.OnPauseListener() {
            @Override
            public void onPause() {
                if (onPauseListenerList != null) {
                    for (OnPauseListener onPauseListener : onPauseListenerList) {
                        if (onPauseListener != null) {
                            onPauseListener.onPause();
                        }
                    }
                }
            }
        });
        musicPlayer.setOnPlayModeChangeListener(new MusicPlayer.OnPlayModeChangeListener() {
            @Override
            public void onChange(int playMode) {
                if (onPlayModeChangeListenerList != null) {
                    for (OnPlayModeChangeListener onPlayModeChangeListener : onPlayModeChangeListenerList) {
                        if (onPlayModeChangeListener != null) {
                            onPlayModeChangeListener.onChange(playMode);
                        }
                    }
                }
            }
        });

        musicPlayer.setOnPlayPositionChangeListener(new MusicPlayer.OnPlayPositionChangeListener() {
            @Override
            public void onChange(int position) {
                if (onPlayPositionChangeListenerList != null) {
                    String str_position = TimeUtil.timeParse(position);
                    for (OnPlayPositionChangeListener onPlayPostionChangeListener : onPlayPositionChangeListenerList) {
                        if (onPlayPostionChangeListener != null) {
                            onPlayPostionChangeListener.onChange(position, str_position);
                        }
                    }
                }
            }
        });


    }

    public void setSongs(List<Song> songs) {
        musicPlayer.setPlayList(songs);
    }

    public void setPlayMode(int playMode) {
        musicPlayer.setPlayMode(playMode);
    }

    public void play(int position) {
        musicPlayer.play(position);
    }

    public void changePlayStatus() {
        musicPlayer.changePlayStatus();
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
        void onPlay(int position, int song_duration, boolean fromUser);
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
        void onChange(int playMode);
    }


    /**
     * @param onErrorListener 播放器暂停调用接口对象
     */
    public void addOnErrorListener(OnErrorListener onErrorListener) {
        if (onErrorListener == null) {
            return;
        }
        if (onErrorListenerList == null) {
            onErrorListenerList = new ArrayList<>();
        }
        this.onErrorListenerList.add(onErrorListener);
    }

    public interface OnErrorListener {
        void onError();
    }


    /**
     * @param onPlayPositionChangeListener
     */
    public void addOnPlayPositionChangeListener(OnPlayPositionChangeListener onPlayPositionChangeListener) {
        if (onPlayPositionChangeListener == null) {
            return;
        }
        if (onPlayPositionChangeListenerList == null) {
            onPlayPositionChangeListenerList = new ArrayList<>();
        }
        this.onPlayPositionChangeListenerList.add(onPlayPositionChangeListener);
    }

    public interface OnPlayPositionChangeListener {
        void onChange(int max, String str_position);
    }
}
