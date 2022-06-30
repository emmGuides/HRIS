package com.example.hris.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    TextView timeInsOuts;
    ImageButton timeInOutButton;
    TextView homeGreeting;
    TextView DateDisplay;
    TextView totalTimedIn;

    FirebaseUser user;
    DatabaseReference reference, timeInOutDBReference;
    List<String> toAdd = new ArrayList<>();

    String userID;
    Date currentTime;
    Date timeInTime;
    Date timeOutTime;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat date = new SimpleDateFormat("MMMM dd, yyyy");


    @SuppressLint("SetTextI18n")
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
        DateDisplay = binding.dateDisplay;
        homeGreeting = binding.greetUser;

        currentTime = Calendar.getInstance().getTime();
        String formattedDate = date.format(currentTime);
        DateDisplay.setText("Today is " + formattedDate);

        totalTimedIn = binding.totalTimedIn;

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();
        // timeInOutDBReference = reference.child(userID).child("Time_ins_outs");

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
                String currTime = time.format(currentTime.getTime());

                if(trim.isEmpty()){

                    timeInTime = currentTime;
                    timeInsOuts.setText("Timed in:  "+currTime);
                    Snackbar.make(requireView(),"Timed In Done", Snackbar.LENGTH_LONG).show();
                    timeInOutButton.setImageResource(R.drawable.timeintimeout_button_image_green);

                } else {

                    timeOutTime = currentTime;
                    long seconds = (timeOutTime.getTime() - timeInTime.getTime())/(1000);
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    Long.toString(seconds);
                    Long.toString(minutes);
                    Long.toString(hours);
                    String display = "Timed in for: " + hours + "h " + minutes%60 + "m " + seconds%60 + "s";
                    // String timeInDurationDB = hours+" "+minutes+" "+seconds;

                    totalTimedIn.setText(display);
                    timeInsOuts.setText("Timed out:  "+currTime);


                    Snackbar.make(requireView(),"Timed Out Done", Snackbar.LENGTH_LONG).show();
                    timeInOutButton.setImageResource(R.drawable.timeintimeout_button_image);
                    /*
                    toAdd.add(timeInTime.toString());
                    toAdd.add(timeOutTime.toString());
                    toAdd.add(timeInDurationDB);

                     */

                    // send to DB:
                    //reference.child(userID).child("Time_ins_outs").child(formattedDate).setValue(toAdd);


                    // testing out homeViewModel
                    // LiveData<String> test = homeViewModel.getText();
                    // homeViewModel.getText().observe(getViewLifecycleOwner(), totalTimedIn::setText);
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