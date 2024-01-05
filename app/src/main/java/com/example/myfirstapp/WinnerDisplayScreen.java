package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WinnerDisplayScreen extends AppCompatActivity {
    private TextView title;
    private TextView displayWinner;
    private Button returnButton;
    private String winnerUsername;
    private long usersRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_screen);

        //Text view title
        title = findViewById(R.id.titleTextView);

        //Text view winner player display
        displayWinner = findViewById(R.id.playerUserName);

        // Gets retrieves data from other activities
        Bundle extras = getIntent().getExtras();
        String roomKey = extras.getString("GameRoomKey");

        // Reference to current game room
        final DatabaseReference gameRoomRef = FirebaseDatabase.getInstance().getReference("Rooms/"+roomKey);

        gameRoomRef.child("roomState").setValue("Game Complete");

        gameRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    usersRemaining = (long) snapshot.child("numParticipants").getValue(); // Gets number of users in room
                    winnerUsername = snapshot.child("Game Details").child("usernameWinner").getValue(String.class); // Gets winner
                    displayWinner.setText("" + winnerUsername); // Displays winner
                }catch (Exception e){
                    System.out.println("Cannot get number of users");
                }

                // Destroys room when all players leave
                if((int)usersRemaining == 0){ // No players left
                    gameRoomRef.removeEventListener(this);
                    gameRoomRef.removeValue(); // Removes room on firebase
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Return main menu button
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int updatedLeftinRoom = (int) usersRemaining -1;
                gameRoomRef.child("numParticipants").setValue(updatedLeftinRoom);
                //Return to the main menu
                Intent it = new Intent();
                it.setClass(WinnerDisplayScreen.this, MainMenu.class);
                startActivity(it);
            }
        });
    }
}