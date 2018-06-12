package de.t_online.martin_gaiser.servicedemo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Objects;

public class ServiceDemoIntent extends IntentService {

    MediaPlayer mediaPlayer;

    public ServiceDemoIntent() {
        super("IntentMusicService");
    }

    @Override
    public void onCreate() {
        //Instantiate MediaPlayer on start.
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.5f, 0.5f);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        doAndroidOreoStuff();
        //No need to create a new Thread since a IntentService creates a new WorkerThread.
        mediaPlayer.start();
        //Intent Service will can onDestroy since nothing else needs to be done => MediaPlayer will also be stopped.
    }

    private void doAndroidOreoStuff(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.NotificationChannelID));
            notificationBuilder
                    .setContentText(getString(R.string.NotificationTextContent))
                    .setContentTitle(getString(R.string.NotificationTitleContent))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this,0,MainActivity.notificationIntent,0));

            ((NotificationManager) Objects.requireNonNull(getSystemService(Context.NOTIFICATION_SERVICE))).notify(1,notificationBuilder.build());
            startForeground(1, notificationBuilder.build());
        }
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
}