package com.example.hris.ui.calendar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.HomeScreen;
import com.example.hris.databinding.FragmentCalendarBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
    ListView timeInOutLog, vacationLeaveLog, sickLeaveLog;
    String disp, ret;
    ArrayList<String> timeInOutList = new ArrayList<>();
    ArrayList<String> vacationLeavesList = new ArrayList<>();
    ArrayList<String> sickLeavesList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter_timeInOut, arrayAdapter_vacation;
    TextView emptyListTimeInOut_TextView, emptyListVacation_TextView;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        emptyListTimeInOut_TextView = binding.emptyListTimeInOut;
        emptyListVacation_TextView = binding.emptyListVacation;

        // Authenticated user ID and Firebase Reference
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        // set up and adapter for time ins and outs
        timeInOutLog = binding.timeInsOutsLOG;
        arrayAdapter_timeInOut = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, timeInOutList);
        timeInOutLog.setAdapter(arrayAdapter_timeInOut);
        timeInOutLog.setEmptyView(emptyListTimeInOut_TextView);

        // set up and adapter for vacation leave requests
        vacationLeaveLog = binding.VacationLeavesLog;
        arrayAdapter_vacation = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, vacationLeavesList);
        vacationLeaveLog.setAdapter(arrayAdapter_vacation);
        vacationLeaveLog.setEmptyView(emptyListVacation_TextView);

        reference.child(userID).child("Time Ins and Outs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ArrayList<String> master = (ArrayList<String>) snapshot.getValue();
                // String dateAdded = TextUtils.join(", ", (Iterable) snapshot.getValue());

                try{
                    ret = "\nDate: " + snapshot.getKey()
                                + "\n\n\t\t\tTime In: " + master.get(0)
                                + "\n\t\t\tTime Out: " + master.get(2)
                                + "\n\t\t\tDuration: " + master.get(3) + "\n";
                } catch (Exception e) {
                    ret = "\nDate: " + snapshot.getKey()
                                + "\n\t\t\tTime In: " + master.get(0)
                                + "\n\t\t\tTime Out: Not Timed out yet\n";
                }


                timeInOutList.add(ret);
                arrayAdapter_timeInOut.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    arrayAdapter_timeInOut.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // fix scrolling of time ins and outs list view
        timeInOutLog.setOnTouchListener(new ListView.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


        //addValueEventListener HERE


        // timeInOutLog.setText(timeInOutList.toString().substring(1, timeInOutList.toString().length() - 1));
        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}