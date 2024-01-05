package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

public class SelectBoardOffline extends AppCompatActivity {
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 5;
    private int numRefreshes = 2;

    Button arrBingoBoard[][] = new Button[NUM_ROWS][NUM_COLS]; // Sets up array of buttons for grid
    String arrButtonNum [] = new String[25];
    ImageButton imgbtnRefresh;
    ImageButton returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_board_offline);

        //Return button
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Return to the login UI
                Intent it = new Intent();
                it.setClass(SelectBoardOffline.this, MainMenuOffline.class);
                startActivity(it);
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
        gotoGameScreen();

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

                Intent intent = new  Intent(SelectBoardOffline.this , BingoGameOffline.class);
                fillArr();
                intent.putExtra("BingoBoardArr", arrButtonNum);
                startActivity(intent); // Switch to MainActivity
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