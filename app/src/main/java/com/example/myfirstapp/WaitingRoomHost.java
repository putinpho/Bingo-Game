package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class WaitingRoomHost extends AppCompatActivity {
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 5;
    private int numRefreshes = 2;
    private String roomIdString;
    private String roomKey;
    private DatabaseReference myRefRoom;
    private DatabaseReference userRef;
    private long numReadyPlayers;
    private Long numparticipants;
    static char[] num = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private boolean isInRoom;

    TextView roomId;
    TextView participants;
    Button[][] arrBingoBoard = new Button[NUM_ROWS][NUM_COLS]; // Sets up array of buttons for grid
    String[] arrButtonNum  = new String[25];
    ImageButton imgbtnRefresh;
    ImageButton returnButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room_host);

        // Updates the current room user is in within database
        userRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.child("currentRoom").setValue(roomIdString);

        participants = findViewById(R.id.txtParticipants);

        isInRoom = true;

        createNewGameSession();
        createBoard(); // Creates first board

        //Room ID text view
        roomId = findViewById(R.id.txtRoomID);
        roomId.setText("Room ID: " + roomIdString);


        //Return button
        returnButton = findViewById(R.id.returnButton);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myRef.removeValue();
                //Return to the login UI
                Intent menuIntent = new Intent();
                myRefRoom.child("roomState").setValue("Destroyed");
                menuIntent.setClass(WaitingRoomHost.this, MainMenu.class);
                isInRoom = false;
                startActivity(menuIntent);
                myRefRoom.removeValue();

            }
        });

        //Refresh button
        imgbtnRefresh = findViewById(R.id.imgbtnSwitchBoard);

        // Checks if image button (refresh button) is clicked
        imgbtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshBoard();
            }
        });

        gotoGameScreen();

    }

    public static char randomNum() {
        return num[(int) Math.floor(Math.random() * 10)];
    }

    //Create RoomID of length 5
    private void createNewGameSession() {
        int len = 5;
        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            strBuilder.append(randomNum());
        }

        // Generates room number
        this.roomIdString = ""+ strBuilder.toString();

        RoomInformation roomInfo = new RoomInformation(roomIdString);

        //Create a new room session on firebase realtime database
        myRefRoom = FirebaseDatabase.getInstance().getReference("Rooms").push();
        myRefRoom.setValue(roomInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Print confirm message
                    Toast.makeText(WaitingRoomHost.this, "Create new room successfully", Toast.LENGTH_SHORT).show();

                }
                else{
                    //Print error message
                    Toast.makeText(WaitingRoomHost.this, "Failed to create room", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myRefRoom.child("readyPlayers").setValue(1);
        myRefRoom.child("roomState").setValue("open");

        myRefRoom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numparticipants = (Long) snapshot.child("numParticipants").getValue();
               if(isInRoom){
                   numReadyPlayers = (Long) snapshot.child("readyPlayers").getValue();
                   participants.setText("Participants: " + numparticipants);
                   roomKey = snapshot.getKey();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    /*
    This function creates a 5x5 bingo board by iterating through each row and column and adding a button to a TableLayout
    It also displays the initial bingo board
     */
    private void createBoard(){
        int count = -1;
        GameBoard newBoard = new GameBoard(NUM_ROWS,NUM_COLS);
        TableLayout table = findViewById(R.id.tblBingoBoard);


        for(int row = 0; row < NUM_ROWS; row++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams( // Sets Table row parameters
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    0.5f
            ));

            table.addView(tableRow);

            for(int col = 0; col < NUM_COLS; col++){
                count++;
                Button button = new Button(this); // New button
                button.setLayoutParams(new TableRow.LayoutParams( // Sets Button parameters
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        0.5f
                ));

                tableRow.addView(button); // Adds button to table layout
                arrBingoBoard[row][col] = button; // Stores button in array

                arrBingoBoard[row][col].setText(String.valueOf(newBoard.board[count].number));
            }
        }
    }


    /*
        This function refreshes the Bingo Board preview
        numRefreshes keeps trash of number of refreshes left
        User gets 2 refreshes max
     */
    private void refreshBoard(){
        if(numRefreshes == 0){ // Checks if user has refreshes left
            imgbtnRefresh.setEnabled(false); // Stops user from being able to click refresh button
        }else{ // Has refreshes available

            // Updates board
            GameBoard newBoard = new GameBoard(NUM_ROWS,NUM_COLS);
            int index = -1; // Variable that will be used to keep track of board index
            for(int r = 0; r < NUM_ROWS; r++){
                for(int c = 0; c < NUM_COLS; c++){
                    index++;
                    arrBingoBoard[r][c].setText(String.valueOf(newBoard.board[index].number));
                }
            }

            numRefreshes--; // Updates number of refreshes
        }

    }

    // This function  will go the the Bingo game screen once the ready button is clicked
    private void gotoGameScreen(){
        //Start button
        Button btnGoToGame = findViewById(R.id.btnStartButton);
        btnGoToGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // Ready button is clicked
                if(numReadyPlayers == numparticipants ){
                    Intent intent = new  Intent(WaitingRoomHost.this , BingoGame.class);
                    fillArr();
                    intent.putExtra("BingoBoardArr", arrButtonNum);
                    intent.putExtra("GameRoomKey", roomKey);
                    intent.putExtra("GameRoomID", roomIdString);
                    isInRoom = false;
                    myRefRoom.child("roomState").setValue("Started");
                    startActivity(intent); // Switch to MainActivity
                }else{
                    Toast.makeText(WaitingRoomHost.this, "Waiting for all players to be ready", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void fillArr(){
        int index = -1;

        for(int r = 0; r < NUM_ROWS; r++){
            for(int c = 0; c < NUM_COLS; c++){
                index++;
                arrButtonNum[index] = (String) arrBingoBoard[r][c].getText();
            }
        }
    }


}