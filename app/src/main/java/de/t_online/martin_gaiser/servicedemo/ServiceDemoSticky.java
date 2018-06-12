package de.t_online.martin_gaiser.servicedemo;

import android.content.Intent;

public class ServiceDemoSticky extends ServiceDemo {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //doAndroid8Stuff and start MediaPlayer.
        super.onStartCommand(intent,flags,startId);

        //Start_Sticky will restart the Service once it has been destroyed.
        return START_STICKY;
    }
}
