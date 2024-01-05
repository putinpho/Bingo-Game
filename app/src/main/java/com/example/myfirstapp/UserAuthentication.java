package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserAuthentication extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private Button createNewAccount;
    private Button resetPasswordButton;
    private Button returnWelcomeScreen;
    private FirebaseAuth mAuth;
    CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authentication);

        //Email input field
        editTextEmail = (EditText) findViewById(R.id.emailAddressField);

        //Password input field
        editTextPassword = (EditText) findViewById(R.id.passwordField);

        //Sign in button
        signIn = findViewById(R.id.signInButton);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Return to the Bingo Game UI
                signInUser();
            }
        });

        //Create new account button
        createNewAccount = findViewById(R.id.signUpButton);
        createNewAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(UserAuthentication.this, CreateUserAccount.class);
                startActivity(it);
            }
        });

        //Reset button
        resetPasswordButton = findViewById(R.id.passwordReset);
        resetPasswordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(UserAuthentication.this, ResetPassword.class);
                startActivity(it);
            }
        });

        //Quit game button
        returnWelcomeScreen = findViewById(R.id.returnWelcomeScreen);
        returnWelcomeScreen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent();
                it.setClass(UserAuthentication.this, WelcomeScreen.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        rememberMe = findViewById(R.id.chkRememberMe);

        // Checks to see if checkbox is checked or not
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });


        // Accesses Shared Preferences file on device to check if "remember me" was checked previously or not
        SharedPreferences sharedPreferences = getSharedPreferences("remember me", MODE_PRIVATE);
        String checkboxState = sharedPreferences.getString("remember", "");
        if(checkboxState.equals("true")){ // Was checked
            Intent intent = new Intent(UserAuthentication.this, MainMenu.class);
            startActivity(intent); // Go to main menu without having to log in
        }else{ // Not checked
            System.out.println("Unchecked");
        }
    }

    //Method use for user to sign into their account
    private void signInUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        //Begin sign in user
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //When all credential input correctly
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){

                        // Creates a Shared Preferences file locally on the device to store whether remember me was checked or not
                        if(rememberMe.isChecked()){ // Is checked
                            SharedPreferences sharedPreferences = getSharedPreferences("remember me", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("remember", "true"); // Store that "remember me" is true
                            editor.apply(); // Send to local file
                        }else if(!rememberMe.isChecked()){
                            SharedPreferences sharedPreferences = getSharedPreferences("remember me", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("remember", "false"); // Store that "remember me" is false
                            editor.apply(); // Send to local file

                        }

                        //Redirect to main menu
                        Intent it = new Intent();
                        it.setClass(UserAuthentication.this, MainMenu.class);
                        startActivity(it);
                    }
                    else{
                        user.sendEmailVerification();
                        Toast.makeText(UserAuthentication.this, "Please check your email to verify your account", Toast.LENGTH_LONG).show();
                    }
                }
                //Show error
                else{
                    Toast.makeText(UserAuthentication.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}