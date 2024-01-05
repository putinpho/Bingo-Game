package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuOffline extends AppCompatActivity {
    private Button playGame;
    private Button tutorialButton;
    private Button settingButton;
    private Button returnWelcomeScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_offline);

        //Play game button
        playGame = findViewById(R.id.playGame);
        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(MainMenuOffline.this, SelectBoardOffline.class);
                startActivity(it);
            }
        });

        //Tutorial button
        tutorialButton = findViewById(R.id.tutorialButton);
        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(MainMenuOffline.this, TutorialSelection.class);
                startActivity(it);
            }
        });

        //Setting button
        settingButton = findViewById(R.id.settingButton);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(MainMenuOffline.this, MenuGameSettingOffline.class);
                startActivity(it);
            }
        });

        //Return home screen button
        returnWelcomeScreen = findViewById(R.id.returnWelcomeScreen);
        returnWelcomeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(MainMenuOffline.this, WelcomeScreen.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
            }
        });
    }
}