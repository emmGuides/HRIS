package com.example.hris.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hris.R;

public class NavBarHeader extends AppCompatActivity implements View.OnClickListener{
    private TextView userNameDisplay;
    private TextView userEmailDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_home_screen);

        userNameDisplay = (TextView) findViewById(R.id.navBarName);
        userEmailDisplay = (TextView) findViewById(R.id.navBarEmail);

        userEmailDisplay.setText("BRUH.EMAIL.COM");

    }

    @Override
    public void onClick(View view) {

    }
}
