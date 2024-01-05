package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindGame extends AppCompatActivity {
    private Button returnButton;
    private SearchView roomIdSearch;
    private ListView roomListView;

    private  ArrayList<String> roomArrayList;
    private ArrayAdapter adapter;

    private DatabaseReference myRef;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);


        //Room ID search
        roomIdSearch = findViewById(R.id.searchRoomId);
        roomIdSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Filter the room ID with the input ID
                try {
                    adapter.getFilter().filter(s);

                }catch (Exception e){
                    System.out.println("ERROR");
                }
                return false;
            }
        });

        //Room List
        roomListView = findViewById(R.id.roomList);
        //Locate room ID reference on database
        myRef = FirebaseDatabase.getInstance().getReference("Rooms");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    showData(snapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Gets user reference
        userRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it = new Intent();
                it.setClass(FindGame.this, WaitingRoomPlayer.class);
                userRef.child("currentRoom").setValue(roomArrayList.get(i)); // Updates user's current room on firebase
                it.putExtra("Room ID", roomArrayList.get(i)); // Send room ID to activity
                startActivity(it);


            }
        });


        //Return main menu button
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Return to the Bingo Game UI
                finish();
            }
        });
    }

    // Displays list of rooms on screen
    private void showData(DataSnapshot snapshot) {
        //Initialize room list
        roomArrayList = new ArrayList<>();
        String roomID;
        for(DataSnapshot ds : snapshot.getChildren()){

                //Retrieve Room ID
                roomID = ds.child("roomID").getValue(String.class);

                //Store list of room ID from database into arrayList
                roomArrayList.add(roomID);

        }
        //Initialize adapter for display list on the RoomList
        adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, roomArrayList);
        roomListView.setAdapter(adapter);
    }

}
