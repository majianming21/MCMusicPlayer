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
        if (cursor.moveToFirst()) {
            Song song;
            do {
                song = new Song();
                song.setTitle(cursor.getString(2));
                song.setData(cursor.getString(9));
                song.setDuration(cursor.getLong(3));
                song.setMimeType(cursor.getString(7));
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
