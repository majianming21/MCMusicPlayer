package com.mc.ink.mcmusicplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mc.ink.mcmusicplayer.R;

/**
 * Created by INK on 2016/12/4.
 */

public class SongDetails extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.song_details);
        String data="";
        String length="";
        String name="";
        Bundle bundle = this.getIntent().getBundleExtra("bundle");
        if(bundle!=null){
           data= (String) bundle.get("data");
           name= (String) bundle.get("name");
           length= (String) bundle.get("length");
        }
        TextView nameView= (TextView) findViewById(R.id.title);
        TextView dataView= (TextView) findViewById(R.id.data);
        TextView lengthView= (TextView) findViewById(R.id.length);
        Button button= (Button) findViewById(R.id.button);
        nameView.setText(name);
        lengthView.setText(length);
        dataView.setText(data);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
