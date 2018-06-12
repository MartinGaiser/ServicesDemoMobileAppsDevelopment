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

    MediaPlayer mediaPlayer;

    boolean useService = false;

    boolean startSticky = false;

    boolean startIntent = false;

    boolean startIntentLoop = false;

    Switch serviceSwitch;

    Switch stickySwitch;

    Switch intentSwitch;

    Switch intentLoopSwitch;

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

        stickySetting();

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
                if (!b) {
                    stickySwitch.setChecked(false);
                    intentSwitch.setChecked(false);
                    intentLoopSwitch.setChecked(false);
                    stickySwitch.setEnabled(false);
                    intentSwitch.setEnabled(false);
                } else {
                    stickySwitch.setEnabled(true);
                    intentSwitch.setEnabled(true);
                }
            }
        });
    }

    private void stickySetting() {
        //Switch to enable StickyService
        stickySwitch = findViewById(R.id.startSticky);
        stickySwitch.setEnabled(false);
        stickySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                startSticky = b;
                intentSwitch.setEnabled(!b);

            }
        });
    }

    private void intentSetting() {
        //Switch to enable IntentService.
        intentSwitch = findViewById(R.id.useIntent);
        intentSwitch.setEnabled(false);
        intentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                startIntent = b;
                stickySwitch.setEnabled(!b);
                intentLoopSwitch.setEnabled(b);
            }
        });
    }

    private void intentLoopSetting() {
        //Switch to enable IntentService with Loop so the MediaPlayer won't be discarded immediately.
        intentLoopSwitch = findViewById(R.id.useIntentLoop);
        intentLoopSwitch.setEnabled(false);
        intentLoopSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                startIntentLoop = b;
            }
        });
    }

    private void startMusic() {

        //Define Button.
        startButton = findViewById(R.id.startService);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toastText;
                //Disable Switches until the Stop button was Pressed.
                serviceSwitch.setEnabled(false);
                intentSwitch.setEnabled(false);
                stickySwitch.setEnabled(false);
                intentLoopSwitch.setEnabled(false);

                //Determine what Service to use... or no Service at all.
                if (!useService) {

                    //Create new MediaPlayer and start it.
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.music);
                        mediaPlayer.setLooping(true);
                        mediaPlayer.setVolume(0.5f, 0.5f);
                    }
                    mediaPlayer.start();
                    toastText = "Player started without Service!";
                } else {

                    //Create new Intent depending on which switches are checked.
                    Intent intent;
                    if (startIntentLoop) {
                        intent = new Intent(MainActivity.this, ServiceDemoIntentWithLoop.class);
                        toastText = "Player started with intent Service with Loop!";
                    } else if (startSticky) {
                        intent = new Intent(MainActivity.this, ServiceDemoSticky.class);
                        toastText = "Player started with sticky Service!";
                    } else if (startIntent) {
                        intent = new Intent(MainActivity.this, ServiceDemoIntent.class);
                        toastText = "Player started with intent Service!";
                    } else {
                        intent = new Intent(MainActivity.this, ServiceDemo.class);
                        toastText = "Player started with normal Service!";
                    }

                    //Start as ForegroundService if Android O or greater.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                }
                //Show Toast depending of the start Method.
                Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopMusic() {

        //Define Button
        stopButton = findViewById(R.id.stopService);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toastText;

                //Determine what to disable.
                if (!useService) {

                    //Stop and release the MediaPlayer if one was running.
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    toastText = "MediaPlayer Stopped";
                } else {

                    //Create new Intent depending on which switches are checked.
                    Intent intent;
                    if (startIntentLoop) {
                        intent = new Intent(MainActivity.this, ServiceDemoIntentWithLoop.class);
                        toastText = "Intent Service with Loop stopped!";
                    } else if (startSticky) {
                        intent = new Intent(MainActivity.this, ServiceDemoSticky.class);
                        toastText = "Sticky Service Stopped!";
                    } else if (startIntent) {
                        intent = new Intent(MainActivity.this, ServiceDemoIntent.class);
                        toastText = "Intent Service Stopped!";
                    } else {
                        intent = new Intent(MainActivity.this, ServiceDemo.class);
                        toastText = "Normal Service Stopped!";
                    }

                    //Send the Service the Stop Signal.
                    stopService(intent);

                    //Re-Enable the Switches.
                    if (!startSticky && !startIntent) {
                        stickySwitch.setEnabled(true);
                        intentSwitch.setEnabled(true);
                    } else {
                        stickySwitch.setEnabled(startSticky);
                        intentSwitch.setEnabled(startIntent);
                    }
                    if (intentSwitch.isEnabled()) {
                        intentLoopSwitch.setEnabled(true);
                    }
                }
                Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                serviceSwitch.setEnabled(true);
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
