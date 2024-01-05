package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeScreen extends AppCompatActivity {
    private Button playOnline;
    private Button playOffline;
    private Button quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent svc = new Intent(this, BackgroundSoundService.class);
        startService(svc);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        playOnline = findViewById(R.id.playOnlineButton);
        playOnline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(WelcomeScreen.this, UserAuthentication.class);
                startActivity(it);
            }
        });

        playOffline = findViewById(R.id.playOfflineButton);
        playOffline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(WelcomeScreen.this, MainMenuOffline.class);
                startActivity(it);
            }
        });

        quitButton = findViewById(R.id.quitGameButton);
        quitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setAction(Intent.ACTION_MAIN);
                it.addCategory(Intent.CATEGORY_HOME);
                startActivity(it);
                System.exit(0);
            }
        });

    }
}