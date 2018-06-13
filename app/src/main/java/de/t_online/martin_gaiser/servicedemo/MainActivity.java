package de.t_online.martin_gaiser.servicedemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    //MediaPlayer that will be started if no service is selected.
    MediaPlayer mediaPlayer;

    //if set to true the app will start a non oreo compatible Service.
    boolean useService = false;

    //if set to true the app will start a oreo compatible Service.
    boolean useOreoService = false;

    //if set to true the app will start a intent service with just MediaPlayer.start in it. (service will stop itself immediately.
    boolean useIntentService = false;

    //if set to true the app will start a intent service with MediaPlayer.start and a loop with Thread.sleep(1000) in it.... so it wont close immediately.
    boolean useIntentServiceLoop = false;

    Switch serviceSwitch;

    Switch serviceWithOreoSwitch;

    Switch intentServiceSwitch;

    Switch intentServiceLoopSwitch;

    Button startButton;

    Button stopButton;

    static Intent notificationIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Must call super.onCreate(...)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create Notification stuff only if Android Version equals or is bigger than Android O.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Create Notification Intent => bring back Activity in Foreground or create new Task with Activity, if the Activity is not on Stack.
            notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Create Notification Channel and register it to the NotificationManager.
            NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.NotificationChannelID),
                    getString(R.string.NotificationChannelName), NotificationManager.IMPORTANCE_MIN);
            Objects.requireNonNull(getSystemService(NotificationManager.class)).createNotificationChannel(notificationChannel);
        }

        settings();
        stopMusic();
        startMusic();

    }

    private void settings() {
        serviceSetting();

        serviceOreoSetting();

        intentSetting();

        intentLoopSetting();
    }

    private void serviceSetting() {
        //Switch to enable Service.
        serviceSwitch = findViewById(R.id.useService);
        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                useService = b;
                intentServiceSwitch.setEnabled(!b);
                serviceWithOreoSwitch.setEnabled(!b);
                intentServiceLoopSwitch.setEnabled(!b);
            }
        });
    }

    private void serviceOreoSetting() {
        //Switch to enable StickyService
        serviceWithOreoSwitch = findViewById(R.id.startOreoService);
        serviceWithOreoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                useOreoService = b;
                serviceSwitch.setEnabled(!b);
                intentServiceSwitch.setEnabled(!b);
                intentServiceLoopSwitch.setEnabled(!b);

            }
        });
    }

    private void intentSetting() {
        //Switch to enable IntentService.
        intentServiceSwitch = findViewById(R.id.useIntent);
        intentServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                useIntentService = b;
                serviceSwitch.setEnabled(!b);
                serviceWithOreoSwitch.setEnabled(!b);
                intentServiceLoopSwitch.setEnabled(!b);
            }
        });
    }

    private void intentLoopSetting() {
        //Switch to enable IntentService with Loop so the MediaPlayer won't be discarded immediately.
        intentServiceLoopSwitch = findViewById(R.id.useIntentLoop);
        intentServiceLoopSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                useIntentServiceLoop = b;
                serviceSwitch.setEnabled(!b);
                serviceWithOreoSwitch.setEnabled(!b);
                intentServiceSwitch.setEnabled(!b);
            }
        });
    }

    private void startMusic() {

        //Define Button.
        startButton = findViewById(R.id.startService);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Disable Switches until the Stop button was Pressed.
                serviceSwitch.setEnabled(false);
                intentServiceSwitch.setEnabled(false);
                serviceWithOreoSwitch.setEnabled(false);
                intentServiceLoopSwitch.setEnabled(false);

                //Determine what Service to use... or no Service at all.

                    //Create new MediaPlayer and start it.


                    //Create new Intent depending on which switches are checked.
                    Intent intent = null;
                    if (useIntentServiceLoop) {
                        intent = new Intent(MainActivity.this, ServiceDemoIntentWithLoop.class);
                    } else if (useOreoService) {
                        intent = new Intent(MainActivity.this, ServiceDemoOreo.class);
                    } else if (useIntentService) {
                        intent = new Intent(MainActivity.this, ServiceDemoIntent.class);
                    } else if(useService){
                        intent = new Intent(MainActivity.this, ServiceDemo.class);
                    }else {
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.music);
                            mediaPlayer.setLooping(true);
                            mediaPlayer.setVolume(0.5f, 0.5f);
                        }
                        mediaPlayer.start();
                        //Show Toast depending of the start Method.
                        Toast.makeText(MainActivity.this,"MediaPlayer started!", Toast.LENGTH_SHORT).show();
                    }

                    //Start as ForegroundService if Android O or greater.
                    if(intent != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        } else {
                            startService(intent);
                        }
                    }
            }
        });
    }

    private void stopMusic() {

        //Define Button
        stopButton = findViewById(R.id.stopService);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    //Determine what to disable.
                    //Create new Intent depending on which switches are checked and send the Stop Signal.
                    if (useIntentServiceLoop) {
                        Intent intent = new Intent(MainActivity.this, ServiceDemoIntentWithLoop.class);
                        stopService(intent);
                    }else if (useOreoService) {
                        Intent intent = new Intent(MainActivity.this, ServiceDemoOreo.class);
                        stopService(intent);
                    }else if (useIntentService) {
                        Intent intent = new Intent(MainActivity.this, ServiceDemoIntent.class);
                        stopService(intent);
                    }else if(useService){
                        Intent intent = new Intent(MainActivity.this, ServiceDemo.class);
                        stopService(intent);
                    }else{

                        //Stop and release the MediaPlayer if one was running.
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        Toast.makeText(MainActivity.this, "MediaPlayer Stopped",Toast.LENGTH_SHORT).show();
                    }


                //Re-Enable the Switches.
                if(!useService && !useOreoService && !useIntentService && !useIntentServiceLoop){
                    serviceSwitch.setEnabled(true);
                    serviceWithOreoSwitch.setEnabled(true);
                    intentServiceSwitch.setEnabled(true);
                    intentServiceLoopSwitch.setEnabled(true);
                }else {
                    serviceSwitch.setEnabled(useService);
                    serviceWithOreoSwitch.setEnabled(useOreoService);
                    intentServiceSwitch.setEnabled(useIntentService);
                    intentServiceLoopSwitch.setEnabled(useIntentServiceLoop);
                }
            }
        });
    }


    @Override
    protected void onResume() {

        //Resume Media Player if one was Running.
        super.onResume();
        if (!useService && mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        //Pause MediaPlayer if one was running.
        if (!useService && mediaPlayer != null) {
            mediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //Release Resources if still allocated.
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
