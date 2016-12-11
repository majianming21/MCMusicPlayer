package com.mc.ink.mcmusicplayer.util;

import android.util.Log;

/**
 * Created by INK on 2016/12/6.
 */

public class LogUtil{
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static final int ASSERT = 6;

    // private static final int EVERYTHING=0;
    private static final int NOTHING = 7;

    private static int level = VERBOSE;



    /**
     * 打印verbose日志
     * @param tag
     * @param msg
     */
    public static void v(String tag,String msg){
        if(level<=VERBOSE){
            Log.v(tag,msg);
        }
    }
    /**
     * 打印debug日志
     * @param tag
     * @param msg
     */
    public static void d(String tag,String msg){
        if(level<=DEBUG){
            Log.d(tag,msg);
        }
    }
    /**
     * info
     * @param tag
     * @param msg
     */
    public static void i(String tag,String msg){
        if(level<=INFO){
            Log.i(tag,msg);
        }
    }
    /**
     * 打印warn日志
     * @param tag
     * @param msg
     */
    public static void w(String tag,String msg){
        if(level<=WARN){
            Log.w(tag,msg);
        }
    }
    /**
     * 打印error日志
     * @param tag
     * @param msg
     */
    public static void e(String tag,String msg){
        if(level<=ERROR){
            Log.e(tag,msg);
        }
    }
}
