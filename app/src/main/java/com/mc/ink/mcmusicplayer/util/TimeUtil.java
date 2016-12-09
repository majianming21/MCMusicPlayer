package com.mc.ink.mcmusicplayer.util;

/**
 * Created by INK on 2016/11/30.
 */

public class TimeUtil {
    public static String timeParse(long duration) {
        StringBuilder time =new StringBuilder() ;
        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;
        //long hour=duration/(60000*24);
        long second = Math.round((float)seconds/1000) ;

        time.append((minute<10?"0":"")+minute).append(":");
        time.append(second<10?"0"+second:String.valueOf(second));

        return time.toString();
    }

}
