package com.mc.ink.mcmusicplayer.activity;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 马坚铭
 * on 2016/12/19.
 */

public class SongTextView extends TextView {
    public SongTextView(Context context) {
        super(context);
    }

    public SongTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SongTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return super.isFocused();
    }
}
