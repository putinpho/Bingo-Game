package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Random;

public class BingoGameOffline extends AppCompatActivity {
    private TextView mTextViewCountDown;
    private CountDownTimer countDownTimer;
    private int[] dupCheck;
    private boolean[][] boardState;
    private int numsGenerated;
    private ImageButton gameSettingButton;

    private boolean bingo;

    // Time variables for timer
    private static final int COUNTDOWN_START_VAL = 3000; // 15 seconds
    private static final int COUNTDOWN_BY = 1000; // Count down by 1 second

    // Variables for Bingo Board
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 5;
    private Button arrBingoBoard[][] = new Button[NUM_ROWS][NUM_COLS]; // Sets up array of buttons for grid


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo_game);

        mTextViewCountDown = (TextView)findViewById(R.id.txtCountDown);
        dupCheck = new int[75];
        boardState = new boolean[5][5]; // Array to check states of buttons in bingo board
        numsGenerated = 0;
        bingo = false; // Variable to check for bingo

        // Getting values from waiting room
        Bundle extras = getIntent().getExtras();
        String arrBingoBoardVals[] = extras.getStringArray("BingoBoardArr"); // Values of each button

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
            }

            @Override
            // Timer hits 0
            public void onFinish() {

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
    private void displayBoard(String arrButtonVals[]) {
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

        //If someone has a bingo, the game stops and Bingo! appears
        if(haveWon){
            TextView txtNumGenVal = (TextView)findViewById(R.id.txtNumGen);
            mTextViewCountDown.setVisibility(View.INVISIBLE);
            txtNumGenVal.setText("Bingo!");
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
        }
        else if(numsGenerated == 2){
            drawHistoryNum1.setText("" + dupCheck[numsGenerated - 2]);
            drawHistoryNum2.setText("" + dupCheck[numsGenerated - 1]);
            drawHistoryNum3.setText("NONE");
        }
        else {
            drawHistoryNum1.setText("" + dupCheck[numsGenerated - 3]);
            drawHistoryNum2.setText("" + dupCheck[numsGenerated - 2]);
            drawHistoryNum3.setText("" + dupCheck[numsGenerated - 1]);
        }
    }

    //Open setting menu
    public void openGameSetting(){
        Intent gameSettingIntent= new Intent(this,GameSettingOffline.class);
        startActivity(gameSettingIntent);
    }
}