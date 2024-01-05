package com.example.myfirstapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

//Class used to play the background music throughout all activities in the app
public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    MediaPlayer player;
    //Start music
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.bgm);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

    }
    //Function that starts the BGM when startService() is called.
    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return 1;
    }

    //Function that stops the music player and frees resources allocated to the MediaPlayer
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
