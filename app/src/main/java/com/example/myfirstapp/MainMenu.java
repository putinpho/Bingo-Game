package com.example.myfirstapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class MainMenu extends AppCompatActivity {
    static char num[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Find game button
        Button findGame = findViewById(R.id.findGameButton);
        findGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(MainMenu.this, FindGame.class);
                startActivity(it);
            }
        });

        //Create game button
        final Button createGame = findViewById(R.id.createGameButton);
        createGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(MainMenu.this, WaitingRoomHost.class);
                startActivity(it);
            }
        });

        //Tutorial button
        Button gameTutorial = findViewById(R.id.tutorialButton);
        gameTutorial.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(MainMenu.this, TutorialSelection.class);
                startActivity(it);
            }
        });

        //Setting button
        Button mainMenuSettingButton = findViewById(R.id.settingButton);
        mainMenuSettingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(MainMenu.this, MenuGameSetting.class);
                startActivity(it);
            }
        });

        //Sign out button
        Button signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences sharedPreferences = getSharedPreferences("remember me", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("remember", "false"); // Does not remember user anymore
                editor.apply();

                Intent it = new Intent();
                it.setClass(MainMenu.this, UserAuthentication.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
            }
        });

        // Gets user reference and sets the default room to "0"
        myRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.child("currentRoom").setValue(0);

    }

}
