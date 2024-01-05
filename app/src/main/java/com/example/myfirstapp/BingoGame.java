package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class BingoGame extends AppCompatActivity {

    private TextView mTextViewCountDown;
    private CountDownTimer countDownTimer;
    private int[] dupCheck;
    private boolean[][] boardState;
    private int numsGenerated;
    private long numUserWins;
    private long userGamesPlayed;
    private ImageButton gameSettingButton;
    private String roomKey;
    private String roomIDString;
    private String username;
    private DatabaseReference gameRoomRef;
    private DatabaseReference roomRef;
    private DatabaseReference userRef;

    // Booleans for game
    private boolean bingo;
    private boolean isbingoOpponent;
    private boolean isInRoom;

    // Time variables for timer
    private static final int COUNTDOWN_START_VAL = 10000; // 10 seconds
    private static final int COUNTDOWN_BY = 1000; // Count down by 1 second

    // Variables for Bingo Board
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 5;
    private Button[][] arrBingoBoard = new Button[NUM_ROWS][NUM_COLS]; // Sets up array of buttons for grid


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo_game);

        mTextViewCountDown = (TextView)findViewById(R.id.txtCountDown);
        dupCheck = new int[75];
        boardState = new boolean[5][5]; // Array to check states of buttons in bingo board
        numsGenerated = 0;
        bingo = false; // Variable to check for bingo
        isbingoOpponent = false;
        isInRoom = true;

        // Getting values from waiting room
        Bundle extras = getIntent().getExtras();
        String[]  arrBingoBoardVals = extras.getStringArray("BingoBoardArr"); // Values of each button

        roomKey = extras.getString("GameRoomKey");
        roomIDString = extras.getString("GameRoomID");

        // Gets user reference from firebase
        userRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //Gets user's win amount
        getUserStats();

        // Gets reference to game room
        roomRef = FirebaseDatabase.getInstance().getReference("Rooms/"+roomKey);
        gameRoomRef = roomRef.child("Game Details"); // Reference to game details within game room

        // Setting initial values
        gameRoomRef.child("timer").setValue(15);
        gameRoomRef.child("hasBingo").setValue(false);
        gameRoomRef.child("usernameWinner").setValue("");

        // Gets current user's username
        getUsername();

        // This listener checks the state of the room and if any other player gets bingo
        gameRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!isInRoom){ // Player leaves room
                    roomRef.child("roomState").setValue("Destroyed");
                    gameRoomRef.removeEventListener(this);
                    roomRef.removeValue();
                    countDownTimer.cancel();
                }

                isbingoOpponent = (Boolean) snapshot.child("hasBingo").getValue();
                if(isbingoOpponent){
                    goWinnerScreen();
                    gameRoomRef.removeEventListener(this);
                    countDownTimer.cancel(); // Stops the timer
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // This listener checks to see if the host (player in this room) has left the bingo game or if users are in winner screen
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String roomStatus = snapshot.child("roomState").getValue(String.class); // Gets room state
                if(roomStatus.equals("Host left") || roomStatus.equals("Game Complete")){
                    isInRoom = false; // Game host is not in room
                    roomRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Create board
        createBoard();

        //Display board
        displayBoard(arrBingoBoardVals);

        // Gets random number
        int firstRandNum = getRandomInt(dupCheck);

        setNewRandomNumText(firstRandNum);

        //This function creates a clock which counts down from 30 until 0
        countDownTimer = new CountDownTimer(COUNTDOWN_START_VAL, COUNTDOWN_BY) {
            @Override
            public void onTick(long millisUntilFinished) { // Timer ticks down
                mTextViewCountDown.setText(""+ millisUntilFinished/1000); // Updates number displayed on screen
                gameRoomRef.child("timer").setValue(millisUntilFinished/1000);
            }

            @Override
            // Timer hits 0
            public void onFinish() {
                gameRoomRef.child("timer").setValue(0);
                //Restarts the timer and generates a new number
                countDownTimer.start();
                setDrawHistory(numsGenerated, dupCheck);
                int newRandomNum = getRandomInt(dupCheck);
                setNewRandomNumText(newRandomNum);
            }
        };
        // Starts the timer
        countDownTimer.start();

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
        boolean numCheck = false;
        Button uniqueButton = arrBingoBoard[row][col];
        String buttonText = uniqueButton.getText().toString();
        TextView ranNum = findViewById(R.id.txtNumGen);
        String numText = ranNum.getText().toString();

        // Checks to see if number matches values in prev 3 numbers
        if(numsGenerated <= 3){
            for (int i = 0; i < numsGenerated; i++) {
                if (buttonText.equals( String.valueOf(dupCheck[i]))) {
                    numCheck = true;
                }
            }
        }
        else if(numsGenerated > 3) {
            for (int i = numsGenerated - 4; i < numsGenerated - 1; i++) {
                if (buttonText.equals(String.valueOf(dupCheck[i]))) {
                    numCheck = true;
                }
            }
        }

        // Checks to see if button matches number generated
        if (buttonText.equals(numText) || numCheck) {
            ViewCompat.setBackgroundTintList(uniqueButton, ContextCompat.getColorStateList(this, android.R.color.holo_green_light)); // Changes background to green
            boardState[row][col] = true; // Sets state to be true
        }

        boolean haveWon = checkBingo(row,col); // Checks to see if there is a bingo

        //If someone has a bingo, the game stops and sends users to winner screen
        if(haveWon){
            TextView txtNumGenVal = (TextView)findViewById(R.id.txtNumGen);
            mTextViewCountDown.setVisibility(View.INVISIBLE);
            txtNumGenVal.setText("Bingo!");
            gameRoomRef.child("hasBingo").setValue(true); // Tells other users bingo user has bingo
            gameRoomRef.child("usernameWinner").setValue(username); // username of winner
            int updatedWins = (int)numUserWins +1;
            userRef.child("wins").setValue(updatedWins); // Updates number of wins
            countDownTimer.cancel(); // Stops the timer
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


    /*
     This function returns a random integer from 0 to the number defined by randupperbound
     The functions ensures there will be no duplicates
     */
    private int getRandomInt(int[] dupArray){
        Random randNum = new Random();
        int randUpperBound = 75; // Limit for random number
        int randInt = 0;
        boolean newValue = false;
        while(!newValue){
            randInt = randNum.nextInt(randUpperBound)+1;
            newValue = true;
            for(int i = 0; i < numsGenerated; i++){
                if(dupArray[i] == randInt){
                    newValue = false;
                    break;
                }
            }
        }
        dupCheck[numsGenerated] = randInt;
        numsGenerated++;
        gameRoomRef.child("numsGen").setValue(numsGenerated);
        gameRoomRef.child("randNum").setValue(randInt);

        return randInt;
    }


    // This function takes a random integer and displays it on the screen
    private void setNewRandomNumText(int num){
        TextView txtNumGenVal = (TextView)findViewById(R.id.txtNumGen); // The textview for random number
        txtNumGenVal.setText("" + num); // Sets text on screen
    }


    //this is function to set drawHistory buttons
    private void setDrawHistory(int numsGenerated, int[] dupCheck){
        TextView drawHistoryNum1 = (TextView)findViewById(R.id.drawHistory1);
        TextView drawHistoryNum2 = (TextView)findViewById(R.id.drawHistory2);
        TextView drawHistoryNum3 = (TextView)findViewById(R.id.drawHistory3);

        if(numsGenerated == 1){
            drawHistoryNum1.setText("" + dupCheck[numsGenerated - 1]);
            drawHistoryNum2.setText("NONE");
            drawHistoryNum3.setText("NONE");
            gameRoomRef.child("prev1").setValue(dupCheck[numsGenerated - 1]);

        }
        else if(numsGenerated == 2){
            drawHistoryNum1.setText("" + dupCheck[numsGenerated - 2]);
            drawHistoryNum2.setText("" + dupCheck[numsGenerated - 1]);
            drawHistoryNum3.setText("NONE");
            gameRoomRef.child("prev1").setValue(dupCheck[numsGenerated - 2]);
            gameRoomRef.child("prev2").setValue(dupCheck[numsGenerated - 1]);
        }
        else {
            drawHistoryNum1.setText("" + dupCheck[numsGenerated - 3]);
            drawHistoryNum2.setText("" + dupCheck[numsGenerated - 2]);
            drawHistoryNum3.setText("" + dupCheck[numsGenerated - 1]);

            //Sends the previous 3 numbers to firebase for other players to retrieve
            gameRoomRef.child("prev1").setValue(dupCheck[numsGenerated - 3]);
            gameRoomRef.child("prev2").setValue(dupCheck[numsGenerated - 2]);
            gameRoomRef.child("prev3").setValue(dupCheck[numsGenerated - 1]);
        }
    }

    // This function gets the username of the user
    private void getUsername(){
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.child("userUsername").getValue(String.class); // Retrieves username from firebase
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Open setting menu
    private void openGameSetting(){
        Intent gameSettingIntent= new Intent(this,GameSettingHost.class);
        gameSettingIntent.putExtra("RoomID", roomIDString);
        gameSettingIntent.putExtra("RoomKey", roomKey);
        startActivity(gameSettingIntent);
    }

    // Goes to Winner display screen when called
    private void goWinnerScreen(){
        Intent winnerDisplayIntent= new Intent(this, WinnerDisplayScreen.class);

        winnerDisplayIntent.putExtra("GameRoomKey", roomKey);

        int updatedGamePlayed = (int)userGamesPlayed + 1;
        userRef.child("gamesPlayed").setValue(updatedGamePlayed); // Updates games played

        startActivity(winnerDisplayIntent);
        isInRoom = false; // Not in room anymore
    }

    // This function gets the users wins and games played
    private void getUserStats(){
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
