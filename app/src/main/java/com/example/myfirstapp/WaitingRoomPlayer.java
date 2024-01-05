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
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class WaitingRoomPlayer extends AppCompatActivity {
    // Initialize values
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 5;
    private int numRefreshes = 2;
    private DatabaseReference userRef;
    private DatabaseReference roomRef;
    private DatabaseReference newRoomRef;
    private Long numParticipants;
    private Long numReadyPlayers;
    private String roomIDString;
    private String roomKey;
    private String roomStatus;
    private boolean isInRoom;
    private boolean isReadyClicked;


    Button[][] arrBingoBoard = new Button[NUM_ROWS][NUM_COLS]; // Sets up array of buttons for grid
    String[] arrButtonNum  = new String[25];
    ImageButton imgbtnRefresh;
    ImageButton returnButton;
    TextView participants;
    TextView roomId;
    Button btnReadyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room_player);

        isInRoom = true; // Player is in room
        isReadyClicked = false; // Ready button not clicked

        participants = findViewById(R.id.txtParticipants);
        roomId = findViewById(R.id.txtRoomID);

        // Gets and displays room ID
        roomIDString = getIntent().getExtras().getString("Room ID");
        roomId.setText("Room ID: " + roomIDString);

        // Gets user reference and updates current room
        userRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.child("currentRoom").setValue(roomIDString);

        roomRef = FirebaseDatabase.getInstance().getReference("Rooms");

        // Checks through each room to see if it matches with room ID
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp;
                for(DataSnapshot ds : snapshot.getChildren()){
                    temp =  ds.child("roomID").getValue(String.class);
                    if(temp != null && temp.equals(roomIDString)){ // Room matches room ID
                        roomKey = ds.getKey();
                        newRoomInstance(roomKey);
                        numParticipants = (Long) ds.child("numParticipants").getValue(); // Gets number of people in room

                        // New player joins room
                        if(numParticipants != null){
                            numParticipants++;
                            newRoomRef.child("numParticipants").setValue(numParticipants);
                        }

                    }else{
                        System.out.println("Failed");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        createBoard(); // Creates first board

        btnReadyButton = findViewById(R.id.btnReadyButton);

        //Ready button
        btnReadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // Ready button is clicked

                if(!isReadyClicked){
                    numReadyPlayers++;
                    newRoomRef.child("readyPlayers").setValue(numReadyPlayers);
                    isReadyClicked = true;
                    Toast.makeText(WaitingRoomPlayer.this, "You are now ready", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(WaitingRoomPlayer.this, "You are already ready", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Return button
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Return to the login UI
                Intent it = new Intent();
                it.setClass(WaitingRoomPlayer.this, MainMenu.class);
                if(isReadyClicked){
                    numReadyPlayers--;
                    newRoomRef.child("readyPlayers").setValue(numReadyPlayers);
                }
                numParticipants--;
                newRoomRef.child("numParticipants").setValue(numParticipants);
                updateParticipants();
                isInRoom = false;
                startActivity(it);
                roomKey = "";
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

    private void fillArr(){
        int index = -1;

        for(int r = 0; r < NUM_ROWS; r++){
            for(int c = 0; c < NUM_COLS; c++){
                index++;
                arrButtonNum[index] = (String) arrBingoBoard[r][c].getText();
            }
        }
    }


    private void newRoomInstance(String currentRoomKey){
        newRoomRef = FirebaseDatabase.getInstance().getReference("Rooms/"+currentRoomKey);
        newRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!isInRoom){
                    roomRef.removeEventListener(this);
                    newRoomRef.removeEventListener(this);
                }

                // Checks room status
                roomStatus = snapshot.child("roomState").getValue(String.class);

                // If room status changes
                if(roomStatus.equals("Destroyed")){ // Host leaves room
                    newRoomRef.removeEventListener(this);
                    roomDestroyed();
                    isInRoom = false;
                }else if(roomStatus.equals("Started")){ // Game starts
                    gotoGameScreen();
                }
                // Updates participants and number of players ready
                numReadyPlayers = (Long)snapshot.child("readyPlayers").getValue();
                numParticipants = (Long) snapshot.child("numParticipants").getValue();
                updateParticipants();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Updates number of participants on screen
    private void updateParticipants(){
        if(isInRoom){
            participants.setText("Participants: " + numParticipants);
        }

    }

    // Goes to game screen
    private void gotoGameScreen(){
        fillArr();
        Intent intent = new  Intent(WaitingRoomPlayer.this , BingoGamePlayer.class);
        intent.putExtra("BingoBoardArr", arrButtonNum);
        intent.putExtra("GameRoomKey", roomKey);
        intent.putExtra("GameRoomID", roomIDString);
        startActivity(intent); // Switch to MainActivity
        isInRoom = false;
    }

    // Sends user to main menu
    private void roomDestroyed(){
        Intent mainMenuIntent= new Intent(WaitingRoomPlayer.this , MainMenu.class);
        Toast.makeText(WaitingRoomPlayer.this, "Host has left the room. Room closed", Toast.LENGTH_SHORT).show();
        startActivity(mainMenuIntent);
    }


}