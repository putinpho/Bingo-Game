package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BingoGamePlayer extends AppCompatActivity {


    private boolean[][] boardState;
    private long numsGenerated;
    private int numsGeneratedInt;
    private ImageButton gameSettingButton;

    // Initializing string variables
    private String roomKey;
    private String roomIDString;
    private String roomState;
    private String username;

    // Database references
    private DatabaseReference gameRoomRef;
    private DatabaseReference roomRef;
    private DatabaseReference userRef;

    private TextView txtNumGenVal;
    private TextView drawHistoryNum1;
    private TextView drawHistoryNum2;
    private TextView drawHistoryNum3;
    private TextView mTextViewCountDown;

    // Initializing values to be retrieved from firebase
    private Long prevNum1;
    private Long prevNum2;
    private Long prevNum3;
    private Long ranNum;
    private Long timer;
    private long numUserWins;
    private long userGamesPlayed;

    private boolean bingo;
    private boolean isbingoOpponent;

    // Variables for Bingo Board
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 5;
    private Button[][] arrBingoBoard = new Button[NUM_ROWS][NUM_COLS]; // Sets up array of buttons for grid


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo_game_player);

        numsGenerated = 0;

        mTextViewCountDown = (TextView)findViewById(R.id.txtCountDown);
        txtNumGenVal = (TextView)findViewById(R.id.txtNumGen); // The textview for random number
        boardState = new boolean[5][5]; // Array to check states of buttons in bingo board
        bingo = false; // Variable to check for bingo
        isbingoOpponent = false;

        // Getting values from waiting room
        Bundle extras = getIntent().getExtras();
        String[] arrBingoBoardVals = extras.getStringArray("BingoBoardArr"); // Values of each button

        //Create board
        createBoard();

        //Display board
        displayBoard(arrBingoBoardVals);

        roomKey = extras.getString("GameRoomKey"); // Gets room key (hash value)
        roomIDString = extras.getString("GameRoomID"); // Gets roomID (number value)

        //
        userRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        getUsername();

        getUserWins();

        roomRef = FirebaseDatabase.getInstance().getReference("Rooms/"+roomKey);
        gameRoomRef = roomRef.child("Game Details");

        gameRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Gets values from host room to make sure values are synced
                prevNum1 = (Long) snapshot.child("prev1").getValue();
                prevNum2 = (Long) snapshot.child("prev2").getValue();
                prevNum3 = (Long) snapshot.child("prev3").getValue();

                timer = (Long) snapshot.child("timer").getValue();
                ranNum = (Long) snapshot.child("randNum").getValue();
                try {
                    numsGenerated = (long) snapshot.child("numsGen").getValue();
                    numsGeneratedInt = (int) numsGenerated - 1;
                    isbingoOpponent = (Boolean) snapshot.child("hasBingo").getValue();
                }catch (Exception exception){
                    System.out.println("PROBLEM HERE");
                }


                mTextViewCountDown.setText(""+ timer);
                txtNumGenVal.setText("" + ranNum); // Sets text on screen

                setDrawHistory();

                if(isbingoOpponent){
                    goWinnerScreen();
                    gameRoomRef.removeEventListener(this);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomState = snapshot.child("roomState").getValue(String.class);
                if(roomState.equals("Destroyed")){
                    roomDestroyed();
                    roomRef.removeEventListener(this);
                    gameRoomRef.removeEventListener(this);
                }else if(roomState.equals("Game Complete")){
                    roomRef.removeEventListener(this);
                    gameRoomRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Game Setting Button
        gameSettingButton = findViewById(R.id.settingButton);
        gameSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGameSetting();
            }
        });
    }

    /*
     This function creates the bingo board by using a TableLayout
     The
     */
    private void createBoard(){
        //GameBoard newBoard = new GameBoard(NUM_ROWS,NUM_COLS);
        TableLayout table = findViewById(R.id.tblGameBingoCard);

        for(int row = 0; row < NUM_ROWS; row++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams( // Sets Table row parameters
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    10.0f
            ));

            table.addView(tableRow);

            for(int col = 0; col < NUM_COLS; col++){
                final int FINAL_ROW = row;
                final int FINAL_COL = col;
                final Button button = new Button(this); // New button
                button.setLayoutParams(new TableRow.LayoutParams( // Sets Button parameters
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        0.5f
                ));

                tableRow.addView(button); // Adds button to table layout
                arrBingoBoard[row][col] = button; // Stores button in array

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkClicked(FINAL_ROW, FINAL_COL);
                    }
                });

            }
        }

    }


    /*
     Updates the buttons to display the generated Gameboard
     Button values match values from Bingo Board preview in Waitingroom
     */
    private void displayBoard(String[] arrButtonVals) {
        int index = -1;
        for (int r = 0; r < NUM_ROWS; r++) {
            for (int c = 0; c < NUM_COLS; c++) {
                index++;
                arrBingoBoard[r][c].setText(arrButtonVals[index]); // Sets bingo board text
            }
        }
    }


    /*
    This function checks to see if the number clicked on bingo board matches randomly generated number
    If it matches then the function makes the button green and changes the state to true
     */
    private void checkClicked(int row, int col){
        Button uniqueButton = arrBingoBoard[row][col];
        String buttonText = uniqueButton.getText().toString();
        String numText = txtNumGenVal.getText().toString();

        if(buttonText.equals(drawHistoryNum1.getText()) || buttonText.equals(drawHistoryNum2.getText()) || buttonText.equals(drawHistoryNum3.getText()) || buttonText.equals(numText) ){
            ViewCompat.setBackgroundTintList(uniqueButton, ContextCompat.getColorStateList(this, android.R.color.holo_green_light)); // Changes background to green
            boardState[row][col] = true; // Sets state to be true
        }


        boolean haveWon = checkBingo(row,col); // Checks to see if there is a bingo

        //If someone has a bingo, the game stops and Bingo! appears
        if(haveWon){
            TextView txtNumGenVal = (TextView)findViewById(R.id.txtNumGen);
            mTextViewCountDown.setVisibility(View.INVISIBLE);
            txtNumGenVal.setText("Bingo!");
            gameRoomRef.child("hasBingo").setValue(true);
            System.out.println("USERNAME" + username);
            gameRoomRef.child("usernameWinner").setValue(username);
            int updatedWins = (int)numUserWins +1;
            userRef.child("wins").setValue(updatedWins);
            goWinnerScreen();
        }
    }

    //Checks each horizontal, vertical, and diagonal states of the boards
    private boolean checkBingo(int row, int col){

        // Checks for any horizontal bingo
        if(boardState[row][0] && boardState[row][1] && boardState[row][2] && boardState[row][3] && boardState[row][4]){
            bingo = true;
        }

        // Checks for vertical bingo
        if(boardState[0][col] && boardState[1][col] && boardState[2][col] && boardState[3][col] && boardState[4][col]){
            bingo = true;
        }

        // Checks for downward diagonal bingo
        if(boardState[0][0] && boardState[1][1] && boardState[2][2] && boardState[3][3] && boardState[4][4]){
            bingo = true;
        }

        // Checks for upward diagonal bingo
        if(boardState[4][0] && boardState[3][1] && boardState[2][2] && boardState[1][3] && boardState[0][4]){
            bingo = true;
        }

        return bingo;
    }


    //this is function to set drawHistory buttons
    private void setDrawHistory(){
        drawHistoryNum1 = (TextView)findViewById(R.id.drawHistory1);
        drawHistoryNum2 = (TextView)findViewById(R.id.drawHistory2);
        drawHistoryNum3 = (TextView)findViewById(R.id.drawHistory3);

        if(numsGeneratedInt == 1){
            drawHistoryNum1.setText("" + prevNum1);
            drawHistoryNum2.setText("NONE");
            drawHistoryNum3.setText("NONE");
        }
        else if(numsGeneratedInt == 2){
            drawHistoryNum1.setText("" + prevNum1);
            drawHistoryNum2.setText("" + prevNum2);
            drawHistoryNum3.setText("NONE");
        }
        else {
            drawHistoryNum1.setText("" + prevNum1);
            drawHistoryNum2.setText("" + prevNum2);
            drawHistoryNum3.setText("" + prevNum3);
        }
    }

    //Open setting menu
    private void openGameSetting(){
        Intent gameSettingIntent= new Intent(this,GameSetting.class);
        gameSettingIntent.putExtra("RoomID", roomIDString);
        startActivity(gameSettingIntent);
    }

    private void getUsername(){
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.child("userUsername").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void roomDestroyed(){
        Intent mainMenuIntent= new Intent(BingoGamePlayer.this, MainMenu.class);
        Toast.makeText(BingoGamePlayer.this, "Host has left the game. Room closed", Toast.LENGTH_SHORT).show();
        startActivity(mainMenuIntent);
    }

    private void goWinnerScreen(){
        Intent winnerDisplayIntent= new Intent(this, WinnerDisplayScreen.class);
        //winnerDisplayIntent.putExtra("RoomID", roomIDString);

        int updatedGamePlayed = (int)userGamesPlayed + 1;
        userRef.child("gamesPlayed").setValue(updatedGamePlayed);

        winnerDisplayIntent.putExtra("GameRoomKey", roomKey);
        startActivity(winnerDisplayIntent);
    }

    private void getUserWins(){
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    numUserWins = (long) snapshot.child("wins").getValue();
                    userGamesPlayed = (long) snapshot.child("gamesPlayed").getValue();
                }catch (Exception e){
                    System.out.println("Can't get number of wins");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}