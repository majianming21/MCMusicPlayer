package com.mc.ink.mcmusicplayer.domain;

import com.mc.ink.mcmusicplayer.util.TimeUtil;

/**
 * Created by INK on 2016/11/30.
 */
public class Song {
    private String displayName;//包含后缀名
    private String title;//歌曲名
    private String artistName;//歌手
    private int artistId;//歌手id
    private String data;//放置位置
    private long size;//文件大小
    private long duration;//长度
    private String str_duration;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getDuration() {
        return duration;
    }
    public String getStringDuration() {
        return str_duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        this.str_duration= TimeUtil.timeParse(duration);
    }



    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
/*    MediaStore.Audio.Media.DISPLAY_NAME,
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.DURATION,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.YEAR,
    MediaStore.Audio.Media.MIME_TYPE,
    MediaStore.Audio.Media.SIZE,
    MediaStore.Audio.Media.DATA*/



}
