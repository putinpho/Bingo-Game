package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialSelection extends AppCompatActivity {

    private ImageButton returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_selection);

        //Return to the main menu, quitting the tutorial
        returnButton = findViewById(R.id.tutorialReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Exits the tutorial if user clicks on return button
                finish();
            }
        });

        //Basic bingo tutorial button
        ImageView gameTutorial = findViewById(R.id.tutorialBingoImage);
        gameTutorial.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(TutorialSelection.this, Tutorial.class);
                startActivity(it);
            }
        });

        //Board swapping tutorial
        ImageButton swapTutorial = findViewById(R.id.tutorialSwapButton);
        swapTutorial.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(TutorialSelection.this, TutorialBoardSwap.class);
                startActivity(it);
            }
        });



    }


}
