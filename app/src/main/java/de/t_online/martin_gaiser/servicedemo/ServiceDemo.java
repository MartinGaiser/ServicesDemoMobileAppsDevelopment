package de.t_online.martin_gaiser.servicedemo;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Objects;

public class ServiceDemo extends Service {
    MediaPlayer mediaPlayer;


    @Override
    public void onCreate() {
        super.onCreate();

        //Instantiate MediaPlayer on start.
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.5f, 0.5f);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Start new Thread with Workload so the Main activity does not freeze.
        new Thread() {
            public void run() {
                mediaPlayer.start();
                //Can stop itself if...
                //onDestroy();
                //is executed.
            }
        }.start();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {

        //Release resources.
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;

        System.out.println("==========================================================");
        System.out.println("Service " + this.getClass().getName() + "was destroyed!");
        System.out.println("==========================================================");
        super.onDestroy();
    }

    //Method must be overwritten. Return null so no binding is possible.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
