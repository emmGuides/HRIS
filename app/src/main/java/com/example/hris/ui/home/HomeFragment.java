package com.example.hris.ui.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.example.hris.Employee;
import com.example.hris.HomeScreen;
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
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    TextView timeInsOuts;
    ImageButton timeInOutButton;
    TextView homeGreeting;
    TextView DateDisplay;
    TextView totalTimedIn;

    FirebaseUser user;
    DatabaseReference reference;
    List<String> toAdd = new ArrayList<>();

    String userID;
    Date currentTime;
    Date timeInTime;
    Date timeOutTime;
    long timeInFromDB = 69; //nice

    Dialog dialog;
    Button cancel, okay;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat date = new SimpleDateFormat("MMMM dd, yyyy");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat greet = new SimpleDateFormat("HH");


    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // HomeViewModel homeViewModel =
        //                new ViewModelProvider(this).get(HomeViewModel.class);
        // homeViewModel format, in case
        // final TextView textView = binding.textHome;
        // homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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


        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_time_in);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        okay = dialog.findViewById(R.id.btn_okay);
        cancel = dialog.findViewById(R.id.btn_cancel);

        reference.child(userID).child("Time Ins and Outs").child(formattedDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    // sets Employee's timed in time from DB
                    String displayers = Objects.requireNonNull(snapshot.getValue()).toString();
                    displayers = displayers.substring(1, displayers.length() - 1);
                    String[] strArray = displayers.split(" ");
                    timeInFromDB = Long.parseLong(strArray[1].substring(0, strArray[1].length()-1));
                }
                catch (Exception e) {
                    // Handle Exception: Not needed as of now.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.child(userID).child("User Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Employee userProfile = snapshot.getValue(Employee.class);

                if(userProfile != null){

                    String fullName = userProfile.fullName;
                    String firstName;

                    try{
                        firstName = fullName.substring(0, fullName.indexOf(' '));
                    } catch (Exception e) {
                        firstName = fullName;
                    }

                    if (Integer.parseInt(greet.format(currentTime.getTime()).toString()) < 12){
                        homeGreeting.setText("Good Morning, "+ firstName);
                    }
                    else if (Integer.parseInt(greet.format(currentTime.getTime()).toString()) < 18) {
                        homeGreeting.setText("Good Afternoon, "+ firstName);
                    }
                    else {
                        homeGreeting.setText("Good Evening, "+ firstName);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something Wrong Happened", Toast.LENGTH_LONG).show();
            }
        });

        try{
            reference.child(userID).child("Time Ins and Outs").child(formattedDate).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{

                        try{
                            // Time in and out
                            String displayers = Objects.requireNonNull(snapshot.getValue()).toString();
                            displayers = displayers.substring(1, displayers.length() - 1);
                            String[] strArray = displayers.split(" ");

                            timeInsOuts.setText("Timed out:  "+strArray[2].substring(0, strArray[2].length() - 1));
                            totalTimedIn.setText("Total Timed In: " + strArray[3] +" "+ strArray[4] + " " + strArray[5]);


                        } catch (Exception e) {

                            try{
                                // on time in only
                                String trim = timeInsOuts.getText().toString().trim();
                                String displayers = Objects.requireNonNull(snapshot.getValue()).toString();
                                displayers = displayers.substring(1, displayers.length() - 1);
                                String[] strArray = displayers.split(" ");

                                timeInsOuts.setText("Timed In:  "+ strArray[0].substring(0, strArray[0].length() - 1));
                                timeInFromDB = Long.parseLong(strArray[1]);

                            } catch (Exception d) {
                                // Handle Exception: Not needed as of now.
                            }

                        }

                    } catch(Exception e) {
                        // Handle Exception: Not needed as of now.
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            // Handle Exception
            Toast.makeText(getContext(), "WHOOPS! something went wrong!", Toast.LENGTH_LONG).show();
        }


        timeInOutButton.setOnClickListener(view -> {

            String trim = timeInsOuts.getText().toString().trim();
            currentTime = Calendar.getInstance().getTime();
            String currTime = time.format(currentTime.getTime());

            if(trim.isEmpty()){

                timeInTime = currentTime;
                timeInsOuts.setText("Timed in:  " + currTime);
                Snackbar.make(requireView(),"Employee Timed In", Snackbar.LENGTH_LONG).show();
                // timeInOutButton.setImageResource(R.drawable.timeintimeout_button_image_green);
                toAdd.add(time.format(timeInTime.getTime()));
                toAdd.add(Long.toString(timeInTime.getTime()));

                // send to DB:

            } else {

                timeOutTime = currentTime;

                long seconds = (timeOutTime.getTime() - timeInFromDB)/(1000);
                long minutes = seconds / 60;
                long hours = minutes / 60;
                Long.toString(seconds);
                Long.toString(minutes);
                Long.toString(hours);
                String timeInDurationDB = hours + "h " + minutes%60 + "m " + seconds%60 + "s ";

                timeInsOuts.setText("Timed out:  "+ currTime);

                Snackbar.make(requireView(),"Timed Out Done", Snackbar.LENGTH_LONG).show();
                // timeInOutButton.setImageResource(R.drawable.timeintimeout_button_image);

                toAdd.add(time.format(timeInFromDB));
                toAdd.add(Long.toString(timeInFromDB));
                toAdd.add(time.format(timeOutTime.getTime()));
                toAdd.add(timeInDurationDB);

                // send to DB:

            }
            reference.child(userID).child("Time Ins and Outs").child(formattedDate).setValue(toAdd);
            toAdd.clear();

        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}