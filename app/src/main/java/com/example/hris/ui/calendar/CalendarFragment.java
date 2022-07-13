package com.example.hris.ui.calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.HomeScreen;
import com.example.hris.R;
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
import java.util.HashMap;

public class CalendarFragment extends Fragment {


    private FragmentCalendarBinding binding;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID, timeIn, timeOut, totalTimedIn;
    ListView timeInOutLog, vacationLeaveLog, sickLeaveLog, overtimeLog;
    String ret_timeInOut, ret_vacation, ret_sick, ret_overtime;
    Dialog timeInOutLogDialog, vacationLeaveLogDialog, sickLeaveLogDialog, overtimeLogDialog, offsetLogDialog;
    Button timeInOutLog_BUTTON, vacationLeaveLog_BUTTON, sickLeaveLog_BUTTON, overtimeLog_BUTTON, offsetLog_BUTTON;

    ArrayList<String> timeInOutList = new ArrayList<>();
    ArrayList<String> vacationLeavesList = new ArrayList<>();
    ArrayList<String> sickLeavesList = new ArrayList<>();
    ArrayList<String> overtimeList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter_timeInOut, arrayAdapter_vacation, arrayAdapter_sick, arrayAdapter_overtime;

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Authenticated user ID and Firebase Reference
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        // overtime Dialog
        overtimeLog_BUTTON = binding.overtimeLogBUTTON;
        overtimeLogDialog = new Dialog(getContext());
        overtimeLogDialog.setContentView(R.layout.custom_dialog_overtime);
        overtimeLogDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        overtimeLogDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        overtimeLogDialog.setCancelable(true);
        overtimeLogDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        // overtime ListView
        overtimeLog = overtimeLogDialog.findViewById(R.id.overtimeLOG_inDialog);
        // set up and adapter for overtimes
        arrayAdapter_overtime = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, overtimeList);
        overtimeLog.setAdapter(arrayAdapter_overtime);
        overtimeLog.setEmptyView(overtimeLogDialog.findViewById(R.id.emptyListOvertime_TextView));
        // confirm button in overtime dialog
        overtimeLogDialog.findViewById(R.id.btn_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overtimeLogDialog.dismiss();
            }
        });

        overtimeLog_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overtimeLogDialog.show();
            }
        });


        // timeInOutLog Dialog
        timeInOutLog_BUTTON = binding.timeInOutLogBUTTON;
        timeInOutLogDialog = new Dialog(getContext());
        timeInOutLogDialog.setContentView(R.layout.custom_dialog_time_in_out_log);
        timeInOutLogDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        timeInOutLogDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeInOutLogDialog.setCancelable(true);
        timeInOutLogDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        //ListView
        timeInOutLog = timeInOutLogDialog.findViewById(R.id.timeInsOutsLOG_inDialog);
        // set up and adapter for time ins and outs
        arrayAdapter_timeInOut = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, timeInOutList);
        timeInOutLog.setAdapter(arrayAdapter_timeInOut);
        timeInOutLog.setEmptyView(timeInOutLogDialog.findViewById(R.id.emptyListTimeInOut_TextView));
        // Confirm
        timeInOutLogDialog.findViewById(R.id.btn_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeInOutLogDialog.dismiss();
            }
        });

        // show dialog button on click time in and out
        timeInOutLog_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeInOutLogDialog.show();
            }
        });


        // Vacation Leave log Dialog
        vacationLeaveLog_BUTTON = binding.VacationLogBUTTON;
        vacationLeaveLogDialog = new Dialog(getContext());
        vacationLeaveLogDialog.setContentView(R.layout.custom_dialog_vacation_leave_log);
        vacationLeaveLogDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        vacationLeaveLogDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vacationLeaveLogDialog.setCancelable(true);
        vacationLeaveLogDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        //List View
        vacationLeaveLog = vacationLeaveLogDialog.findViewById(R.id.vacationLeaveLOG_inDialog);
        // Set up adapter for vacation leave log
        arrayAdapter_vacation = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, vacationLeavesList);
        vacationLeaveLog.setAdapter(arrayAdapter_vacation);
        vacationLeaveLog.setEmptyView(vacationLeaveLogDialog.findViewById(R.id.emptyListVacation_TextView));
        // Confirm
        vacationLeaveLogDialog.findViewById(R.id.btn_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vacationLeaveLogDialog.dismiss();
            }
        });

        vacationLeaveLog_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vacationLeaveLogDialog.show();
            }
        });

        // Sick Leave log Dialog
        sickLeaveLog_BUTTON = binding.SickLogBUTTON;
        sickLeaveLogDialog = new Dialog(getContext());
        sickLeaveLogDialog.setContentView(R.layout.custom_dialog_sick_leave_log);
        sickLeaveLogDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        sickLeaveLogDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sickLeaveLogDialog.setCancelable(true);
        sickLeaveLogDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        //List View
        sickLeaveLog = sickLeaveLogDialog.findViewById(R.id.sickLeaveLOG_inDialog);
        // Set up adapter for vacation leave log
        arrayAdapter_sick = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sickLeavesList);
        sickLeaveLog.setAdapter(arrayAdapter_sick);
        sickLeaveLog.setEmptyView(sickLeaveLogDialog.findViewById(R.id.emptyListSick_TextView));
        // Confirm
        sickLeaveLogDialog.findViewById(R.id.btn_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sickLeaveLogDialog.dismiss();
            }
        });

        sickLeaveLog_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sickLeaveLogDialog.show();
            }
        });


        // overtime list view reference
        reference.child(userID).child("Overtimes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // HashMap<String,String> master = new HashMap<String,String>();
                // master = (HashMap<String, String>) snapshot.getValue();
                //ArrayList<String> master = (ArrayList<String>) snapshot.getValue();
                // String dateAdded = TextUtils.join(", ", (Iterable) snapshot.getValue());

                try{
                    ret_overtime = "\nDate: " + snapshot.getKey()
                            + "\n\n\t\t\tDate of Overtime: " + ((HashMap<?, ?>) snapshot.getValue()).get("Date of Overtime")
                            + "\n\t\t\tHours: " + ((HashMap<?, ?>) snapshot.getValue()).get("Hours")
                            + "\n\t\t\tReason: " + ((HashMap<?, ?>) snapshot.getValue()).get("Reason")
                            + "\n\t\t\tTitle/Position: " + ((HashMap<?, ?>) snapshot.getValue()).get("Title")
                            + "\n\t\t\tTeam: " + ((HashMap<?, ?>) snapshot.getValue()).get("Team")
                            + "\n";
                } catch (Exception e) {
                    ret_timeInOut = "Retrieval Error";
                }


                overtimeList.add(ret_overtime);
                arrayAdapter_overtime.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayAdapter_overtime.notifyDataSetChanged();
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


        // time ins and outs list view reference
        reference.child(userID).child("Time Ins and Outs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ArrayList<String> master = (ArrayList<String>) snapshot.getValue();
                // String dateAdded = TextUtils.join(", ", (Iterable) snapshot.getValue());

                try{
                    ret_timeInOut = "\nDate: " + snapshot.getKey()
                                + "\n\n\t\t\tTime In: " + master.get(0)
                                + "\n\t\t\tTime Out: " + master.get(2)
                                + "\n\t\t\tDuration: " + master.get(3) + "\n";
                } catch (Exception e) {
                    ret_timeInOut = "\nDate: " + snapshot.getKey()
                                + "\n\n\t\t\tTime In: " + master.get(0)
                                + "\n\t\t\tTime Out: Not Timed out.\n";
                }


                timeInOutList.add(ret_timeInOut);
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


        reference.child(userID).child("Vacation Leaves").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ArrayList<String> master = (ArrayList<String>) snapshot.getValue();

                try{
                    ret_vacation = "\nDate Filed: " + snapshot.getKey()
                            + "\n\n\n\t\t\tFrom: " + master.get(1)
                            + "\n\t\t\tTo: " + master.get(2)
                            + "\n\t\t\tNumber of Days: " + master.get(4)
                            + "\n\n\t\t\tAdditional Details: \n\t\t\t" + master.get(3) +"\n";
                } catch (Exception e) {
                    ret_vacation = "Something wrong happened.";
                }


                vacationLeavesList.add(ret_vacation);
                arrayAdapter_vacation.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayAdapter_vacation.notifyDataSetChanged();
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

        // fix scrolling of vacation leave list view
        vacationLeaveLog.setOnTouchListener(new ListView.OnTouchListener() {
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

        reference.child(userID).child("Sick Leaves").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ArrayList<String> master = (ArrayList<String>) snapshot.getValue();

                try{
                    ret_sick = "\nDate Filed: " + snapshot.getKey()
                            + "\n\n\n\t\t\tFrom: " + master.get(1)
                            + "\n\t\t\tTo: " + master.get(2)
                            + "\n\t\t\tNumber of Days: " + master.get(4)
                            + "\n\n\t\t\tAdditional Details: \n\t\t\t" + master.get(3) +"\n";
                } catch (Exception e) {
                    ret_sick = "Something wrong happened.";
                }


                sickLeavesList.add(ret_sick);
                arrayAdapter_sick.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayAdapter_sick.notifyDataSetChanged();
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

        // fix scrolling of Sick leave list view
        sickLeaveLog.setOnTouchListener(new ListView.OnTouchListener() {
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

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}