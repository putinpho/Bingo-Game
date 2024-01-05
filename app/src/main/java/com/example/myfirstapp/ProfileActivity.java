package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private Button changeProfilePictureButton;
    private ImageButton returnButton;

    //Variables to keep track of win loss ratio-
    private int wins;
    private int gamesPlayed;
    private ProgressBar winLossBar;
    private TextView winLossText;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);

        changeProfilePictureButton = findViewById(R.id.profilePic);
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(ProfileActivity.this, ChangeProfilePicture.class);
                startActivity(it);
            }
        });

        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Gets the number of games played and won from the Firebase database
                long wins = (long) snapshot.child("wins").getValue();
                long gamesPlayed = (long)snapshot.child("gamesPlayed").getValue();

                //Calculates number of losses and win loss ratio
                long losses = gamesPlayed - wins;
                int ratio = (int) ((wins/(double)gamesPlayed)*100);

                //Creates two segments in the pie graph
                List<PieEntry> pieEntries = new ArrayList<>();
                pieEntries.add(new PieEntry(wins,"Wins"));
                pieEntries.add(new PieEntry(losses,"Losses"));

                //Creates a pie chart with the win/loss data
                PieDataSet dataSet = new PieDataSet(pieEntries,"");
                dataSet.setColors(Color.rgb(0,255,0), Color.rgb(255,0,0));
                PieData data = new PieData(dataSet);

                //Generate pie chart
                PieChart chart = (PieChart)findViewById(R.id.winLossChart);

                //Makes the description tag invisible
                Description description = chart.getDescription();
                description.setText("");

                //Disables legend
                Legend legend = chart.getLegend();
                legend.setEnabled(false);

                //Displays the data as a pie chart
                chart.setData(data);
                chart.animateY(1000);
                chart.invalidate();

                //Updates profile page with current win loss ratio
                winLossText = findViewById(R.id.textViewWinLoss);
                winLossText.setText("Wins/Losses: " + String.valueOf(wins) +"W/" + String.valueOf(losses) + "L");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}