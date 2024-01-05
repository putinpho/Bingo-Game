package com.example.myfirstapp;

public class RoomInformation {
    public String roomID;
    public long numParticipants;

    //Default RoomInformation constructor
    public RoomInformation(){

    }

    //Constructor RoomInformation with room ID parameter
    public RoomInformation(String roomID){
        this.roomID = roomID;
        this.numParticipants = 1;
    }


}
