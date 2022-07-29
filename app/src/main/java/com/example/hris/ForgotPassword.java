package com.example.hris;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEditText;
    private TextInputLayout email_layout;
    private ProgressBar progressBar;
    private Button resetPasswordBUTTON;
    private TextView logInInstead;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = (EditText) findViewById(R.id.email);
        email_layout = findViewById(R.id.email_layout);

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

        emailEditText.setOnClickListener(view -> email_layout.setError(null));
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                email_layout.setStartIconDrawable(b ? R.drawable.email_on : R.drawable.email_icon);
            }
        });

    }

    private void resetPassword(){

        String extractedEmail = emailEditText.getText().toString().trim();

        if(extractedEmail.isEmpty()){
            email_layout.setError("Email cannot be empty!");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(extractedEmail).matches()){
            email_layout.setError("Invalid email format!");
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