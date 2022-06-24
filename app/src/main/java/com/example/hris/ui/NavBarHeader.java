package com.example.hris.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hris.Employee;
import com.example.hris.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NavBarHeader extends AppCompatActivity implements View.OnClickListener{


    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_home_screen);

        final TextView userNameDisplay = (TextView) findViewById(R.id.navBarName);
        final TextView userEmailDisplay = (TextView) findViewById(R.id.navBarEmail);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Employees");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Employee userProfile = snapshot.getValue(Employee.class);
                if(userProfile != null){
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;
                    Toast.makeText(getApplicationContext(), "USER PROFILE NOT NULL", Toast.LENGTH_LONG).show();
                    userNameDisplay.setText(fullName);
                    userEmailDisplay.setText(email);
                }
                else {
                    Toast.makeText(getApplicationContext(), "USER PROFILE NULL", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userNameDisplay.setText("onCancelled proc");
                userEmailDisplay.setText("onCancelled proc");
                Toast.makeText(getApplicationContext(), "Something wrong happened", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
