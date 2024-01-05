package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class GameSettingOffline extends AppCompatActivity {
    private ImageButton returnButton;
    private Button quitGameButton;
    AudioManager audioManager;
    CheckBox muteButton;
    boolean isMuted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setting_offline);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //Retrieves the max volume from the device and the current volume
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        //Return button
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Return to the bingo game
                finish();
            }
        });

        //Quit button
        quitGameButton = findViewById(R.id.quitGameButton);
        quitGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Return to the main menu
                Intent it = new Intent();
                it.setClass(GameSettingOffline.this, MainMenuOffline.class);
                startActivity(it);
            }
        });

        //Wires seekbar to allow user to control volume using it!
        final SeekBar seekBar = (SeekBar)findViewById(R.id.musicSeekBar);
        seekBar.setMax(maxVolume);
        seekBar.setProgress(currentVolume);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //When the seek bar gets moved, the volume changes accordingly
                if(!isMuted){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        muteButton = (CheckBox)findViewById(R.id.muteButton);

        muteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    //When the mute button is clicked, the volume is set to 0
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,0);
                    isMuted = true;
                }
                else{
                    //When the mute button is unclicked, it will set the volume to whatever the seekbar is
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(),0);
                    isMuted = false;
                }
            }
        });
    }
}