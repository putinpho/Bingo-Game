package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    private EditText editTextEmail;
    private Button resetButton;
    private ImageButton returnButton;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //Return button
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Return to the login UI
                Intent it = new Intent();
                it.setClass(ResetPassword.this, UserAuthentication.class);
                startActivity(it);
            }
        });

        //Email input field
        editTextEmail = (EditText) findViewById(R.id.emailAddressField);

        //Reset button
        resetButton = findViewById(R.id.resetPasswordButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Return to the login UI
                resetPassword();
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    //Method for user to rest password
    private void resetPassword() {
        String email = editTextEmail.getText().toString().trim();
        //Check for email input
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        //Check for valid form email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide a valid email");
            editTextEmail.requestFocus();
            return;
        }

        //Rest password
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Check if the email match on database
                if(task.isSuccessful()){
                    Toast.makeText(ResetPassword.this, "Reset link has been send through email", Toast.LENGTH_LONG).show();
                    //Redirect to login menu
                    Intent it = new Intent();
                    it.setClass(ResetPassword.this, UserAuthentication.class);
                    startActivity(it);
                }
                //Show error
                else{
                    Toast.makeText(ResetPassword.this, "Failed to reset password! Please check your credential!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}