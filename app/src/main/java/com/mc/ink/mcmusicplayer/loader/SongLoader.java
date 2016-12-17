package com.mc.ink.mcmusicplayer.loader;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.MediaStore;

import com.mc.ink.mcmusicplayer.domain.Song;

import java.util.ArrayList;

/**
 * 歌曲加载
 * Created by 马坚铭
 * on 2016/12/8.
 */

public class SongLoader {
  /*  private List<String> mimeType;

    public SongLoader() {
        mimeType = new ArrayList<>();
    }*/

    public ArrayList<Song> getSongList(Context context) {
        ArrayList<Song> songs = new ArrayList<>();
        Cursor cursor = getCursor(context);
      /*  MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,


                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATA;*/



        if (cursor.moveToFirst()) {
            Song song;
            do {
                song = new Song();
                song.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                song.setDuration(cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                song.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                // MediaStore.Audio.Media.YEAR
                song.setMimeType(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)));
                song.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                song.setData(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }


    private Cursor getCursor(Context context) {
        MediaScannerConnection.scanFile(context, new String[]{Environment
                .getExternalStorageDirectory().getAbsolutePath()}, null, null);
        Cursor cursor = context.getContentResolver().query(
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
                //  MediaStore.Audio.Media.MIME_TYPE + " like ?",new String[]{"%"},null);
                MediaStore.Audio.Media.MIME_TYPE + "=? or " +
                        MediaStore.Audio.Media.MIME_TYPE + "=? or " +
                        MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[]{"audio/mpeg", "audio/x-ms-wma", "audio/quicktime"}, null);
        return cursor;
    }

}
