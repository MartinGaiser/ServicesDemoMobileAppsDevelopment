package de.t_online.martin_gaiser.servicedemo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

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
        Toast.makeText(this, this.getClass().getSimpleName() + " started.", Toast.LENGTH_SHORT).show();
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.5f, 0.5f);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        doAndroidOreoStuff();

        //No need to create a new Thread since a IntentService creates a new WorkerThread.
        mediaPlayer.start();

        //Make worker thread Idle so onDestroy won't be called.
        //When the Service will be stopped from the MainActivity the worker-Thread will also be stopped.
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
            Notification notification = notificationBuilder.build();

            //Notify Notification Manager of new Notification.
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
        Toast.makeText(this, this.getClass().getSimpleName() + " stopped.", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
