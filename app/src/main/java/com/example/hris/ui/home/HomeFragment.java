package com.example.hris.ui.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    TextView timeInsOuts;
    ImageButton timeInOutButton;
    ImageView announcementDummy;
    TextView homeGreeting;
    TextView DateDisplay;
    TextView totalTimedIn;
    Animation scaleUp, scaleDown, scaleUpSlow, scaleDownSlow;

    FirebaseUser user;
    DatabaseReference reference;
    List<String> toAdd = new ArrayList<>();

    String userID;
    Date currentTime;
    Date timeInTime;
    Date timeOutTime;
    String formattedDate;
    long timeInFromDB = 69; //nice

    Dialog dialog_timeIn, dialog_timeOut;
    Button cancel_timeIn, cancel_timeOut, okay_timeIn, okay_timeOut;
    Thread thread;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat date = new SimpleDateFormat("MMMM dd, yyyy");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat greet = new SimpleDateFormat("HH");


    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        scaleUp = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down);
        scaleUpSlow = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_slow);
        scaleDownSlow = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_slow);

        announcementDummy = binding.mockAnnouncements;
        timeInsOuts = binding.TimeInsOuts;
        timeInOutButton = binding.TimeInsOutsButton;
        DateDisplay = binding.dateDisplay;
        homeGreeting = binding.greetUser;

        currentTime = Calendar.getInstance().getTime();
        formattedDate = date.format(currentTime);
        DateDisplay.setText("Today is " + formattedDate);

        totalTimedIn = binding.totalTimedIn;

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        checkBtnStatus();

        // dialog time in
        dialog_timeIn = new Dialog(getContext());
        dialog_timeIn.setContentView(R.layout.custom_dialog_time_in);
        dialog_timeIn.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        dialog_timeIn.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_timeIn.setCancelable(true);
        dialog_timeIn.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // dialog time out
        dialog_timeOut = new Dialog(getContext());
        dialog_timeOut.setContentView(R.layout.custom_dialog_time_out);
        dialog_timeOut.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        dialog_timeOut.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_timeOut.setCancelable(true);
        dialog_timeOut.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // dialog buttons
        okay_timeIn = dialog_timeIn.findViewById(R.id.btn_okay);
        cancel_timeIn = dialog_timeIn.findViewById(R.id.btn_cancel);

        okay_timeOut = dialog_timeOut.findViewById(R.id.btn_okay);
        cancel_timeOut = dialog_timeOut.findViewById(R.id.btn_cancel);

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


        reference.child(userID).child("User Details").addValueEventListener(new ValueEventListener() {
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

                    if (Integer.parseInt(greet.format(currentTime.getTime())) < 13){
                        homeGreeting.setText("Good Morning, "+ firstName+"!");
                    }
                    else if (Integer.parseInt(greet.format(currentTime.getTime())) < 18) {
                        homeGreeting.setText("Good Afternoon, "+ firstName+"!");
                    }
                    else {
                        homeGreeting.setText("Good Evening, "+ firstName+"!");
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


        announcementDummy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    announcementDummy.startAnimation(scaleDownSlow);
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    announcementDummy.startAnimation(scaleUpSlow);
                }

                try{
                    Snackbar.make(requireView(), "This is a placeholder for a working Announcements Widget", Snackbar.LENGTH_LONG).show();
                } catch (Exception s) {
                    Toast.makeText(requireActivity(), "This is a placeholder for a working Announcements Widget", Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        timeInOutButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(!timeInsOuts.getText().toString().trim().isEmpty()
                        && !totalTimedIn.getText().toString().trim().isEmpty())
                {
                    try{
                        Snackbar.make(requireView(), "You already timed out", Snackbar.LENGTH_SHORT).show();
                    } catch (Exception t){
                        Toast.makeText(getActivity(), "You already timed out", Toast.LENGTH_SHORT).show();
                    }

                    return false;
                }

                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    timeInOutButton.startAnimation(scaleUp);
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    timeInOutButton.startAnimation(scaleDown);
                }

                String trim = timeInsOuts.getText().toString().trim();
                if(trim.isEmpty()){
                    dialog_timeIn.show();
                } else {
                    dialog_timeOut.show();
                }

                return false;
            }
        });


        okay_timeIn.setOnClickListener(view -> {
            timeInOutFunction();
            dialog_timeIn.dismiss();
        });

        cancel_timeIn.setOnClickListener(view -> dialog_timeIn.dismiss());

        okay_timeOut.setOnClickListener(view -> {
            timeInOutFunction();
            dialog_timeOut.dismiss();
        });

        cancel_timeOut.setOnClickListener(view -> dialog_timeOut.dismiss());

        thread = new Thread(){
            @Override
            public void run(){
                try{
                    while(!thread.isInterrupted()) {
                        Thread.sleep(200);
                        requireActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                checkBtnStatus();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        return root;
    }

    @SuppressLint("SetTextI18n")
    public void timeInOutFunction() {
        String trim = timeInsOuts.getText().toString().trim();
        currentTime = Calendar.getInstance().getTime();
        String currTime = time.format(currentTime.getTime());

        if(trim.isEmpty()){


            timeInTime = currentTime;
            timeInsOuts.setText("Timed in:  " + currTime);
            Snackbar.make(requireView(),"Employee Timed In", Snackbar.LENGTH_LONG).show();

            toAdd.add(time.format(timeInTime.getTime()));
            toAdd.add(Long.toString(timeInTime.getTime()));

        } else {

            timeOutTime = currentTime;

            long seconds = (timeOutTime.getTime() - timeInFromDB)/(1000);
            long minutes = seconds / 60;
            long hours = minutes / 60;
            String timeInDurationDB = hours + "h " + minutes%60 + "m " + seconds%60 + "s ";

            timeInsOuts.setText("Timed out:  "+ currTime);

            Snackbar.make(requireView(),"Timed Out Done", Snackbar.LENGTH_LONG).show();

            toAdd.add(time.format(timeInFromDB));
            toAdd.add(Long.toString(timeInFromDB));
            toAdd.add(time.format(timeOutTime.getTime()));
            toAdd.add(timeInDurationDB);

        }
        // send to DB:
        reference.child(userID).child("Time Ins and Outs").child(formattedDate).setValue(toAdd);
        reference.child(userID).child("User Details").child("lastTimeIn").setValue(formattedDate);
        toAdd.clear();
    }

    public void checkBtnStatus (){
        if(timeInsOuts.getText().toString().trim().isEmpty()){
            timeInOutButton.setImageResource(R.drawable.time_in);
        }else if(totalTimedIn.getText().toString().trim().isEmpty() && !(timeInsOuts.getText().toString().trim().isEmpty())){
            timeInOutButton.setImageResource(R.drawable.time_in_filled);
        }else{
            timeInOutButton.setImageResource(R.drawable.time_in_done);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        thread.interrupt();
        binding = null;
    }

}