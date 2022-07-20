package com.example.hris;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEditText;
    private ProgressBar progressBar;
    private Button resetPasswordBUTTON;
    private TextView logInInstead;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = (EditText) findViewById(R.id.email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        resetPasswordBUTTON = (Button) findViewById(R.id.resetPassword_BUTTON);
        logInInstead = (TextView) findViewById(R.id.gobackLogIn);

        auth = FirebaseAuth.getInstance();

        resetPasswordBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        logInInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPassword.this, LogIn.class));
            }
        });

    }

    private void resetPassword(){

        String extractedEmail = emailEditText.getText().toString().trim();

        if(extractedEmail.isEmpty()){
            emailEditText.setError("Email cannot be empty!");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(extractedEmail).matches()){
            emailEditText.setError("Invalid email format!");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(extractedEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Check Email for password reset", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPassword.this, LogIn.class));
                }
                else{
                    Toast.makeText(ForgotPassword.this, "Something wrong happened", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}