package com.example.myfirstapp;

import java.util.Random;

public class GameBoard {

    BingoNumber[] board;

    //Generates a bingo board with 15 possible numbers per column
    //Ensures that there will be no duplicate values in the bingo board
    public GameBoard(int rows, int columns){
        Random rand = new Random();
        int randInt = 0;
        int upperBound = 15;
        int[] dupCheck = new int[75];
        int numsGenerated = 0;
        boolean newValue;

        board = new BingoNumber[rows * columns];

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                newValue = false;
                while(!newValue){
                    randInt = rand.nextInt(upperBound);
                    randInt = (15 * i) + 1 + randInt;
                    newValue = true;
                    for(int k = 0; k < numsGenerated; k++){
                        if(dupCheck[k] == randInt){
                            newValue = false;
                            break;
                        }
                    }
                }
                dupCheck[numsGenerated] = randInt;
                numsGenerated++;
                board[j * rows + i] = new BingoNumber(randInt);
            }
        }
    }
}
