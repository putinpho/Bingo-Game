package com.example.myfirstapp;

public class UserInformation {
    public String userEmail, userUsername;
    public int currentRoom;
    public int wins;
    public int gamesPlayed;

    //Default constructor
    public UserInformation(){

    }

    //Constructor with email, username as parameter
    public UserInformation(String userEmail, String userUsername){
        this.userEmail = userEmail;
        this.userUsername = userUsername;
        this.currentRoom = 0;
        this.wins = 0;
        this.gamesPlayed = 0;
    }

}
