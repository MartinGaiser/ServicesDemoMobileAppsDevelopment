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
        doAndroidOreoStuff();
        //Start new Thread with Workload so the Main activity does not freeze.
        new Thread() {
            public void run() {
                mediaPlayer.start();
                //Can stop itself if...
                //onDestroy();
                //is executed.
            }
        }.start();
        return START_NOT_STICKY;
    }

    private void doAndroidOreoStuff() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //Define new Notification (minimum requirement: SmallIcon.)
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.NotificationChannelID));
            notificationBuilder
                    .setContentText(getString(R.string.NotificationTextContent))
                    .setContentTitle(getString(R.string.NotificationTitleContent))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this, 0, MainActivity.notificationIntent, 0));

            //Notify Notification Manager of new Notification.
            Notification notification = notificationBuilder.build();
            ((NotificationManager) Objects.requireNonNull(getSystemService(Context.NOTIFICATION_SERVICE))).notify(1, notification);

            //Call StartForeground so Android O wont kill the Service.
            startForeground(1, notification);
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

    //Method must be overwritten. Return null so no binding is possible.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
