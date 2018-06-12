package de.t_online.martin_gaiser.servicedemo;

import android.content.Intent;
import android.support.annotation.Nullable;

public class ServiceDemoIntentWithLoop extends ServiceDemoIntent {

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //doAndroidOreoStuff and start MediaPlayer.
        super.onHandleIntent(intent);

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
}
