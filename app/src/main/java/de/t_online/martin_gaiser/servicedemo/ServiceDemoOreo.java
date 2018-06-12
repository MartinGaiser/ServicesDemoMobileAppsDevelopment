package de.t_online.martin_gaiser.servicedemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Objects;

public class ServiceDemoOreo extends ServiceDemo {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //start MediaPlayer
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            doAndroidOreoStuff();
        }

        //Start_Sticky will restart the Service once it has been destroyed.
        return START_STICKY;
    }

    private void doAndroidOreoStuff() {


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
