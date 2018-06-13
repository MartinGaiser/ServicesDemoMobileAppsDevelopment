package de.t_online.martin_gaiser.servicedemo;



import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ServiceDemo extends Service {
    MediaPlayer mediaPlayer;


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, this.getClass().getSimpleName() + " started.", Toast.LENGTH_SHORT).show();

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
                //stopSelf();
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

        Toast.makeText(this, this.getClass().getSimpleName() + " stopped.", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    //Method must be overwritten. Return null so no binding is possible.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
