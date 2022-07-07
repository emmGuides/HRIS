package com.example.hris.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.HomeScreen;
import com.example.hris.databinding.FragmentCalendarBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID, timeIn, timeOut, totalTimedIn;
    TextView timeInOutLog;
    String disp;
    ArrayList<String> timeInOutList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /*
        CalendarViewModel galleryViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);
        final TextView textView = binding.textCalendar;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

         */

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        timeInOutLog = binding.timeInsOutsLOG;

        reference.child(userID).child("Time Ins and Outs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot1  : snapshot.getChildren()){
                    String dateThisSnap = String.valueOf(dataSnapshot1.getKey());
                    String[] dateThisSnapperino = String.valueOf(dataSnapshot1.getValue()).split(", ");

                    timeIn = dateThisSnapperino[0].substring(1);
                    try{
                        timeOut = dateThisSnapperino[2];
                        totalTimedIn = dateThisSnapperino[3].substring(0, dateThisSnapperino[3].length() - 1);
                    } catch(Exception e){
                        timeOut = "Not Timed Out";
                        totalTimedIn = "-";
                    }
                    disp = "Date: "+ dateThisSnap + "\nTime In: " + timeIn + "\nTime Out: " + timeOut
                            + "\nTotal Timed in: " + totalTimedIn;
                    try{
                        //Toast.makeText(requireContext(), disp,Toast.LENGTH_SHORT).show();
                    } catch (Exception ignored) {

                    }
                    timeInOutList.add(disp);
                    disp = "";
                }
            ///////
            try{
                Toast.makeText(getContext(), timeInOutList.toString().substring(1, timeInOutList.toString().length() - 1) , Toast.LENGTH_SHORT).show();
            } catch (Exception ignored){

            }
            //////

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        timeInOutLog.setText(timeInOutList.toString().substring(1, timeInOutList.toString().length() - 1));
        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}