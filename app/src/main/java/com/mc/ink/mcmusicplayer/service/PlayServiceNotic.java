package com.mc.ink.mcmusicplayer.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.mc.ink.mcmusicplayer.R;
import com.mc.ink.mcmusicplayer.activity.SongListActivity;

/**
 * Created by INK on 2016/12/6.
 */

public class PlayServiceNotic extends Service {

    RemoteViews remoteViews;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;
    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent =new Intent(this,SongListActivity.class);
        PendingIntent pend=PendingIntent.getActivity(this,0,intent,0);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);

        remoteViews =new RemoteViews(getPackageName(), R.layout.play_notic);
      //  remoteViews.setProgressBar(R.id.progress,100,0,false);
        mBuilder.setContent(remoteViews);
        mBuilder.setContentIntent(pend);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON*/;
        mBuilder.setOngoing(true);
    //    mNotificationManager.notify(1, mBuilder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle=intent.getExtras();
      //  LogUtil.i(TAG,"intent_bundle"+bundle.toString());
        int max=bundle.getInt("max");
        int prosses=bundle.getInt("prosses");

        remoteViews.setProgressBar(R.id.progress,max,prosses,false);
        mBuilder.setContent(remoteViews);
        mNotificationManager.notify(1, mBuilder.build());
        return super.onStartCommand(intent, flags, startId);
    }
}
