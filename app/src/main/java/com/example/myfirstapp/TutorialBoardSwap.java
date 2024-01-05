package com.example.myfirstapp;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialBoardSwap extends AppCompatActivity {

    ImageButton imgbtnRefresh;
    private ImageButton returnButton;
    Button tutBingoBoard[] = new Button[25];
    private TextView tutText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_board_swap);

        //Return to the main menu, quitting the tutorial
        returnButton = findViewById(R.id.tutorialReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Exits the tutorial if user clicks on return button
                finish();
            }
        });

        generateTutorialBoards(1);


    }

    private void generateTutorialBoards(int part){

        //Tutorial boards to show the user how swapping the board works.
        int[] tutorialArray = {14,25,33,48,69,
                12,27,31,50,65,
                8,19,34,55,75,
                11,26,42,52,64,
                7,24,38,60,62};

        int[] tutorialArray2 = {4,19,37,47,65,
                9,23,39,50,75,
                7,22,38,51,68,
                2,16,45,60,70,
                14,18,35,55,71};

        int[] tutorialArray3 = {14,30,43,59,64,
                7,24,44,53,72,
                10,25,33,47,74,
                12,27,42,50,65,
                2,16,32,56,67};

        imgbtnRefresh = findViewById(R.id.imgbtnSwitchBoard);


        //Splits tutorial into parts
        final TextView tutText = (TextView)findViewById(R.id.tutorialSwapText);
        if(part == 1){
            generateBoard(tutorialArray);
            tutText.setText("If you are unsatisfied with your board, swap it with the button above! Give it a try!");
            imgbtnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generateTutorialBoards(2);
                }
            });
        }
        if(part == 2){
            //Refreshes the board after the user presses the refresh button
            int index = 0;
            for(int r = 0; r < 5; r++){
                for(int c = 0; c < 5; c++){
                    tutBingoBoard[index].setText(String.valueOf(tutorialArray2[index]));
                    index++;
                }
            }
            tutText.setText("You get a total of 2 board refreshes! You have 1 more refresh!");
            imgbtnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generateTutorialBoards(3);
                }
            });
        }
        else if(part == 3){
            //Refreshes the board after the user presses the refresh button
            int index = 0;
            for(int r = 0; r < 5; r++){
                for(int c = 0; c < 5; c++){
                    tutBingoBoard[index].setText(String.valueOf(tutorialArray3[index]));
                    index++;
                }
            }
            //End of the tutorial, user can press return button to leave
            tutText.setText("That was your last reset! I hope you're satisfied with your board.");
            imgbtnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tutText.setText("Nice try! You're finished with the tutorial. Press the button on the top right to leave");
                }
            });
        }
    }

    //Create demo board
    private void generateBoard(int[] bingoArray){

        TableLayout table = findViewById(R.id.tutorialSwapBingoBoard);

        //Counter to keep track of what index is being generated for the board
        int count = 0;

        //Array that stores predetermined numbers for a bingo board for the tutorial
        int[] tutorialArray = bingoArray;

        for(int row = 0; row < 5; row++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    0.5f
            ));

            table.addView(tableRow);

            for(int col = 0; col < 5; col++){

                Button button = new Button(this);
                button.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        0.5f
                ));

                tableRow.addView(button);
                tutBingoBoard[count] = button;
                tutBingoBoard[count].setText(String.valueOf(tutorialArray[count]));
                count++;
            }
        }
    }


}
