package com.mc.ink.mcmusicplayer.adpter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mc.ink.mcmusicplayer.R;
import com.mc.ink.mcmusicplayer.activity.SongListActivity;
import com.mc.ink.mcmusicplayer.domain.Song;

import java.util.List;

/**
 * Created by INK on 2016/12/8.
 */

public class SongListAdpter extends RecyclerView.Adapter<SongListAdpter.ViewHolder> {
    private List<Song> songList;

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
       /* viewHolder.songItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(parent.getContext(),""+viewHolder.getAdapterPosition(),Toast.LENGTH_SHORT).show();

            }
        });*/
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song=songList.get(position);
        holder.data.setText(song.getData());
        holder.name.setText(song.getTitle());
        holder.size.setText(song.getStringDuration());

    }

    public SongListAdpter(List<Song> songList) {
        this.songList = songList;
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View songItemView;
        TextView name;
        TextView data;
        TextView size;

        /**
         *
         * @param view
         */
        public ViewHolder(View view){
            super(view);
            songItemView=view;
            name= (TextView) view.findViewById(R.id.song_name);
            size= (TextView) view.findViewById(R.id.song_size);
            data= (TextView) view.findViewById(R.id.song_data);
        }
    }
}
