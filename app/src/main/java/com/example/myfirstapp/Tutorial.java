package com.example.myfirstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

public class Tutorial extends AppCompatActivity {

    private ImageButton returnButton;
    Button tutBingoBoard[] = new Button[25];

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        //Return to the main menu, quitting the tutorial
        returnButton = findViewById(R.id.tutorialReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Exits the tutorial if user clicks on return button
                finish();
            }
        });

        //Creates a predetermined board for tutorial purposes
        generateBoard();
        runTutorial(1);
    }

    //Create demo board
    private void generateBoard(){

        TableLayout table = findViewById(R.id.tutorialBingoBoard);

        //Counter to keep track of what index is being generated for the board
        int count = 0;

        //Array that stores predetermined numbers for a bingo board for the tutorial
        int[] tutorialArray = {14,25,33,48,69,
                12,27,31,50,65,
                8,19,34,55,75,
                11,26,42,52,64,
                7,24,38,60,62};

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

    //Start tutorial
    private void runTutorial(int part){

        //Tutorial function separated into multiple parts
        //The tutorial will not move on until the user clicks the correct button
        //Which will then call this function but with the next part.

        final TextView numGen = (TextView)findViewById(R.id.tutNumGen);
        TextView tutText = (TextView)findViewById(R.id.tutorialText);
        if(part == 1){

            tutText.setText("Welcome to the Tutorial!"
                    + " The number 14 has been drawn. "
                    + "Find the number on the board!");

            numGen.setText("14");

            tutBingoBoard[0].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    String buttonText = tutBingoBoard[0].getText().toString();
                    String numText = numGen.getText().toString();

                    if(buttonText.equals(numText)){
                        setColour(0);
                        runTutorial(2);
                    }

                }
            } );
        }
        else if(part == 2){

            tutText.setText("A new number has been drawn. Try to find it on your board!");

            numGen.setText("52");

            tutBingoBoard[18].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    String buttonText = tutBingoBoard[18].getText().toString();
                    String numText = numGen.getText().toString();

                    if(buttonText.equals(numText)){
                        setColour(18);
                        runTutorial(3);

                    }

                }
            } );

        }

        else if(part == 3){

            tutText.setText("The goal is to match 5 in a row. This can be done horizontally, vertically, or diagonally. ");

            numGen.setText("34");
            tutBingoBoard[12].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    String buttonText = tutBingoBoard[12].getText().toString();
                    String numText = numGen.getText().toString();

                    if(buttonText.equals(numText)){
                        setColour(12);
                        runTutorial(4);

                    }
                }
            } );
        }
        else if(part == 4){
            tutText.setText("During the actual game however, the numbers won't line up perfectly, it would be similar to this 65.");
            numGen.setText("65");
            tutBingoBoard[9].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    String buttonText = tutBingoBoard[9].getText().toString();
                    String numText = numGen.getText().toString();

                    if(buttonText.equals(numText)){
                        setColour(9);
                        runTutorial(5);
                    }
                }
            } );
        }
        else if(part == 5){
            tutText.setText("You're getting close to a Bingo!");
            numGen.setText("62");
            tutBingoBoard[24].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    String buttonText = tutBingoBoard[24].getText().toString();
                    String numText = numGen.getText().toString();

                    if(buttonText.equals(numText)){
                        setColour(24);
                        runTutorial(6);
                    }
                }
            } );
        }
        else if(part == 6){
            tutText.setText("Click the last number for the Bingo!");
            numGen.setText("27");
            tutBingoBoard[6].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    String buttonText = tutBingoBoard[6].getText().toString();
                    String numText = numGen.getText().toString();

                    if(buttonText.equals(numText)){
                        setColour(6);
                        runTutorial(7);
                    }
                }
            } );
        }
        else if(part == 7){
            tutText.setText("Congratulations on the Bingo! You have finished the tutorial! Click on the button on the top right to leave.");
        }

    }

    //Changes the colour for the button at the given index to green
    //Used to mark that the button has been pressed correctly.
    private void setColour(int index){
        ViewCompat.setBackgroundTintList(tutBingoBoard[index], ContextCompat.getColorStateList(this, android.R.color.holo_green_light));
    }
}
