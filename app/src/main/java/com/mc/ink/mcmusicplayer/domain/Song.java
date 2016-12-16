package com.mc.ink.mcmusicplayer.domain;

import com.mc.ink.mcmusicplayer.util.TimeUtil;

import org.litepal.crud.DataSupport;

/**
 * Created by INK on 2016/11/30.
 */
public class Song extends DataSupport {
    private int id;
    private String displayName;//包含后缀名
    private String title;//歌曲名
    private long size;//文件大小
    private long addedDate;//添加时间
    private long modified;//添加时间
    private String artistName;//歌手
    private int artistId;//歌手id
    private String mimeType;//mime类型
    private String album;//专辑
    private String data;//放置位置
    private long duration;//长度
    private String str_duration;//数值显示的长度


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        this.str_duration = TimeUtil.timeParse(duration);
    }

    public String getStrDuration() {
        return str_duration;
    }
}