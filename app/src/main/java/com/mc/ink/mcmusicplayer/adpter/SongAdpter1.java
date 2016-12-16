package com.mc.ink.mcmusicplayer.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mc.ink.mcmusicplayer.R;
import com.mc.ink.mcmusicplayer.domain.Song;

import java.util.List;
import java.util.Map;

/**
 * Created by INK on 2016/12/3.
 */




public class SongAdpter1 extends ArrayAdapter<Song> {
    private int resource;
    private Map<Integer,Song> songMap;
    public SongAdpter1(Context context, int resource, List<Song> objects) {
        super(context, resource, objects);
        this.resource=resource;
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song=getItem(position);
        SongHolder songHolder=null;
        View view=null;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resource,null);
            songHolder=new SongHolder();
           /// songHolder.data=
            songHolder.name= (TextView) view.findViewById(R.id.song_name);
            songHolder.size= (TextView) view.findViewById(R.id.song_size);
            songHolder.data= (TextView) view.findViewById(R.id.song_data);
            view.setTag(songHolder);
        }else{
            view=convertView;
            songHolder= (SongHolder) view.getTag();
        }
        songHolder.size.setText(song.getStrDuration());
        songHolder.name.setText(song.getTitle());
        songHolder.data.setText(song.getData());
        return view;
    }
    class SongHolder{
        TextView name;
        TextView data;
        TextView size;
    }
}
