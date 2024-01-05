package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameInformation extends AppCompatActivity {
    private TextView infoTitle;
    private TextView infoDetail;
    private ImageButton returnButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_information);

        //Info Title
        infoTitle = findViewById(R.id.titleTextView);

        //Info detail
        infoDetail = findViewById(R.id.infoDetail);
        infoDetail.setText("This project was created by team 3 for CMPT 276" +
                            "\n" + "Hope you enjoy our game." +
                            "\n" + "The song use in this game: Night Life by Michael Kobrin"+
                            "\n" + "https://pixabay.com/music/modern-jazz-nightlife-michael-kobrin-95bpm-3783/"+
                            "\n" + "Pie chart by PhilJay"+
                            "\n" + "https://github.com/PhilJay/MPAndroidChart#quick-start"
        );

        //Return button
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}