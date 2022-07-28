package com.example.hris;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword;
    TextInputLayout email_layout, password_layout, fullName_layout, age_layout;
    private ProgressBar progressBar;
    boolean passwordVisible;
    private TextView goBackLogIn, positionLabel;
    RadioGroup positionGroup;
    RadioButton positionSelected;

    @SuppressLint({"SourceLockedOrientationActivity", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // portrait lock
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        positionGroup = findViewById(R.id.register_radioGroup);
        positionLabel = findViewById(R.id.employeePositionLabel);

        mAuth = FirebaseAuth.getInstance();
        goBackLogIn = (TextView) findViewById(R.id.gobackLogIn);
        goBackLogIn.setOnClickListener(this);

        Button registerUser = (Button) findViewById((R.id.register));
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        fullName_layout = findViewById(R.id.fullName_Layout);

        editTextAge = (EditText) findViewById(R.id.age);
        age_layout = findViewById(R.id.age_layout);

        editTextEmail = (EditText) findViewById(R.id.email);
        email_layout = findViewById(R.id.email_layout);

        editTextPassword = (EditText) findViewById(R.id.password);
        password_layout = findViewById(R.id.password_layout);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        editTextFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullName_layout.setError(null);
            }
        });

        editTextEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email_layout.setError(null);
            }
        });

        editTextPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password_layout.setError(null);
            }
        });

        editTextAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                age_layout.setError(null);
            }
        });

        positionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                positionLabel.setError(null);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.gobackLogIn:
                startActivity(new Intent(this, LogIn.class));
                break;
            case R.id.register:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String teams = "No Team";


        if(fullName.isEmpty()){
            fullName_layout.setError("Full name is required");
            editTextFullName.requestFocus();
            return;
        }

        if(age.isEmpty()){
            age_layout.setError("Age is required");
            editTextAge.requestFocus();
            return;
        }

        if(email.isEmpty()){
            email_layout.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_layout.setError("Invalid Email");
            editTextEmail.requestFocus();
            return;
        }

        if(positionGroup.getCheckedRadioButtonId() == -1){
            positionLabel.setError("Employee Position Required");
            positionLabel.requestFocus();
            return;
        } else {
            positionSelected = findViewById(positionGroup.getCheckedRadioButtonId());
        }

        if(password.isEmpty()){
            password_layout.setError("Password Required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            password_layout.setError("Password too small");
            editTextPassword.requestFocus();
            return;
        }

        String position = positionSelected.getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Employee employee = new Employee(fullName, age, email, position, teams);

                            FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("User Details")
                                    .setValue(employee).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterUser.this, "Employee Registered Successfully", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(RegisterUser.this, LogIn.class));
                                            }
                                            else{
                                                Toast.makeText(RegisterUser.this, "Employee Registration Failed", Toast.LENGTH_LONG).show();
                                            }
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(RegisterUser.this, "Employee Registration Failed", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

}