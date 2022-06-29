package com.example.hris.ui.home;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.Employee;
import com.example.hris.R;
import com.example.hris.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    TextView timeInsOuts;
    ImageButton timeInOutButton;
    TextView homeGreeting;
    FirebaseUser user;
    DatabaseReference reference;
    String userID;
    Date currentTime;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        timeInsOuts = binding.TimeInsOuts;
        timeInOutButton = binding.TimeInsOutsButton;
        homeGreeting = binding.greetUser;

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();



        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Employee userProfile = snapshot.getValue(Employee.class);

                if(userProfile != null){

                    String fullName = userProfile.fullName;
                    String fullEmail = userProfile.email;
                    String firstName = fullName.substring(0, fullName.indexOf(' '));

                    homeGreeting.setText("Good day, "+ firstName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something Wrong Happened", Toast.LENGTH_LONG).show();
            }


        });

        timeInOutButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String trim = timeInsOuts.getText().toString().trim();
                currentTime = Calendar.getInstance().getTime();
                if(trim.isEmpty()){
                    String currTime = sdf.format(currentTime.getTime());
                    timeInsOuts.setText("Timed in:  "+currTime);
                    Snackbar.make(getView(),"Timed In Done", Snackbar.LENGTH_LONG).show();
                    timeInOutButton.setImageResource(R.drawable.timeintimeout_button_image_green);
                } else {
                    String currTime = sdf.format(currentTime.getTime());
                    timeInsOuts.setText("Timed out:  "+currTime);
                    Snackbar.make(getView(),"Timed Out Done", Snackbar.LENGTH_LONG).show();
                    timeInOutButton.setImageResource(R.drawable.timeintimeout_button_image);
                }

            }
        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}