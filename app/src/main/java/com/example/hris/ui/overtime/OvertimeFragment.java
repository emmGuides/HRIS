package com.example.hris.ui.overtime;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hris.databinding.FragmentOvertimeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OvertimeFragment extends Fragment {

    EditText overtimeDate, titlePosition, teamOvertime, reasonOvertime, hoursOvertime, approvedByOvertime;
    String userID, overtimeDateS, titlePositionS, teamOvertimeS, reasonOvertimeS, hoursOvertimeS, approvedByS, OvertimeS;
    final Calendar myCalendar = Calendar.getInstance();
    TextView dateOvertimeLabel;
    Thread thread;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateWord = new SimpleDateFormat("MMMM dd, yyyy");
    String dateToday = dateFormat.format(myCalendar.getTime());

    private DatabaseReference reference, masterList;
    private FirebaseUser user;
    Button applyButton;

    private FragmentOvertimeBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOvertimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        overtimeDate = binding.overtimeDate;
        titlePosition = binding.overtimeTitlePosition;
        teamOvertime = binding.overtimeTeam;
        reasonOvertime = binding.overtimeReason;
        hoursOvertime = binding.overtimeTotalHours;
        approvedByOvertime = binding.overtimeApprovedBy;

        applyButton = binding.overtimeApply;
        dateOvertimeLabel = binding.overtimeDateLabel;

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();
        masterList = reference.child(userID).child("Overtimes");


        // calendar popup
        DatePickerDialog.OnDateSetListener overtimeDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabelOvertimeDate();
            }
        };

        overtimeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOvertimeLabel.setError(null);
                new DatePickerDialog(getContext(), overtimeDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overtimeDateS = overtimeDate.getText().toString().trim();
                titlePositionS = titlePosition.getText().toString().trim();
                teamOvertimeS = teamOvertime.getText().toString().trim();
                reasonOvertimeS = reasonOvertime.getText().toString().trim();
                hoursOvertimeS = hoursOvertime.getText().toString().trim();
                approvedByS = approvedByOvertime.getText().toString().trim();

                if(overtimeDateS.isEmpty()){
                    dateOvertimeLabel.setError("Date is Required");
                    dateOvertimeLabel.requestFocus();
                    return;
                }

                if(titlePositionS.isEmpty()){
                    titlePosition.setError("Your Position is required");
                    titlePosition.requestFocus();
                    return;
                }

                if(teamOvertimeS.isEmpty()){
                    teamOvertime.setError("You must indicate your Team.");
                    teamOvertime.requestFocus();
                    return;
                }

                if(hoursOvertimeS.isEmpty()){
                    hoursOvertime.setError("Please indicate the number of hours");
                    hoursOvertime.requestFocus();
                    return;
                }

                if(reasonOvertimeS.isEmpty()){
                    reasonOvertime.setError("Reason for Overtime is Required");
                    reasonOvertime.requestFocus();
                    return;
                }

                if(approvedByS.isEmpty()){
                    approvedByOvertime.setError("Please indicate who approved this overtime.");
                    approvedByOvertime.requestFocus();
                    return;
                }
                sendToDatabase();
            }
        });

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(10000);
                        requireActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                hoursOvertime.setError(null);
                                dateOvertimeLabel.setError(null);
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

    // calendar pop up
    private void updateLabelOvertimeDate(){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        overtimeDate.setText(dateFormat.format(myCalendar.getTime()));
    }



    public void sendToDatabase (){

        HashMap<String,String> toAddMap=new HashMap<String,String>();
        toAddMap.put("Date Applied", dateToday);
        toAddMap.put("Date of Overtime", overtimeDateS);
        toAddMap.put("Title", titlePositionS);
        toAddMap.put("Team", teamOvertimeS);
        toAddMap.put("Reason", reasonOvertimeS);
        toAddMap.put("Hours", hoursOvertimeS);
        toAddMap.put("Approved By", approvedByS);

        masterList.child(dateWord.format(Calendar.getInstance().getTime())).setValue(toAddMap);

        Toast.makeText(getContext(), "Overtime Added!", Toast.LENGTH_LONG).show();

        toAddMap.clear();

        overtimeDate.setText(""); titlePosition.setText(""); teamOvertime.setText("");
        reasonOvertime.setText(""); hoursOvertime.setText(""); approvedByOvertime.setText("");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        thread.interrupt();
        binding = null;
    }
}