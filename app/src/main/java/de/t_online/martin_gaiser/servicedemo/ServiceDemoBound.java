package de.t_online.martin_gaiser.servicedemo;


import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class ServiceDemoBound extends ServiceDemoOreo {

    private final IBinder binder = new ServiceBinder();

    public class ServiceBinder extends Binder {
        ServiceDemoBound getService(){
            return ServiceDemoBound.this;
        }
    }

    public String callTheService(){
        return "I am the Service";
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        new Thread(){
            @Override
            public void run() {
                mediaPlayer.start();
            }
        }.start();

        return binder;
    }
}
