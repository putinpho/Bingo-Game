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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class CreateUserAccount extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword, editTextUserName;
    private Button registerButton;
    private ImageButton returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_account);

        //Return button
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Return to the login UI
                Intent it = new Intent();
                it.setClass(CreateUserAccount.this, UserAuthentication.class);
                startActivity(it);
            }
        });

        //Email input field
        editTextEmail = (EditText) findViewById(R.id.emailAddressField);

        //Password input field
        editTextPassword = (EditText) findViewById(R.id.passwordField);

        //Username input field
        editTextUserName = (EditText) findViewById(R.id.userName);

        //Register button
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    //This method create user data to store on firebase
    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String username = editTextUserName.getText().toString().trim();

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

        //Check for password input
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        //Check password length
        if (password.length() < 6) {
            editTextPassword.setError("Min password length should be 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        //Check username input
        if (username.isEmpty()) {
            editTextUserName.setError("Username is required");
            editTextUserName.requestFocus();
            return;
        }

        //The progress create new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Check if all information fill in the field
                        if(task.isSuccessful()){
                            //User information storage: email, username
                            UserInformation user = new UserInformation(email, username);


                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Show confirmation that new account has been register
                                    if(task.isSuccessful()){
                                        Toast.makeText(CreateUserAccount.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.sendEmailVerification();
                                        Toast.makeText(CreateUserAccount.this, "Please check your email to verify your account", Toast.LENGTH_LONG).show();
                                        //Redirect to login menu
                                        Intent it = new Intent();
                                        it.setClass(CreateUserAccount.this, UserAuthentication.class);
                                        startActivity(it);
                                    }
                                    //Show error
                                    else{
                                        Toast.makeText(CreateUserAccount.this, "Failed to register! Please try again!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        //Show error
                        else{
                            Toast.makeText(CreateUserAccount.this, "Failed to register! Please try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}