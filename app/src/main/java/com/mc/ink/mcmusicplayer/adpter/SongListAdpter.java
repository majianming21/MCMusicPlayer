package com.mc.ink.mcmusicplayer.adpter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mc.ink.mcmusicplayer.R;
import com.mc.ink.mcmusicplayer.domain.Song;

import java.util.List;

/**
 * Created by INK on 2016/12/8.
 */

public class SongListAdpter extends RecyclerView.Adapter<SongListAdpter.ViewHolder> {
    private List<Song> songList;
    private OnClickListener onClickListener;
    private OnDetailsButtonClickListener onDetailsButtonClickListener;

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Song song=songList.get(position);
        holder.data.setText(song.getData());
        holder.name.setText(song.getTitle());
        holder.size.setText(song.getStrDuration());
        holder.songItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v, holder.getAdapterPosition());
                }
            }
        });
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDetailsButtonClickListener != null) {
                    onDetailsButtonClickListener.onClick(v, holder.getAdapterPosition());
                }
            }
        });
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
        Button details;

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
            details = (Button) view.findViewById(R.id.details);

        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener {
        void onClick(View view, int position);
    }

    public interface OnDetailsButtonClickListener {
        void onClick(View view, int position);
    }


    public void setOnDetailsButtonClickListener(OnDetailsButtonClickListener onDetailsButtonClickListener) {
        this.onDetailsButtonClickListener = onDetailsButtonClickListener;
    }
}
