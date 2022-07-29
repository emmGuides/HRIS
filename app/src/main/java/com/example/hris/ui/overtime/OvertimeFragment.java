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
import com.google.android.material.textfield.TextInputLayout;
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
    TextInputLayout overtimeDateLayout, titlePositionLayout, teamOvertimeLayout, reasonOvertimeLayout,
            hoursOvertimeLayout, approvedByOvertimeLayout;
    String userID, overtimeDateS, titlePositionS, teamOvertimeS, reasonOvertimeS, hoursOvertimeS, approvedByS, OvertimeS;
    final Calendar myCalendar = Calendar.getInstance();

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
        overtimeDateLayout = binding.overtimeDateLayout;

        titlePosition = binding.overtimeTitlePosition;
        titlePositionLayout = binding.overtimeTitlePositionLayout;

        teamOvertime = binding.overtimeTeam;
        teamOvertimeLayout = binding.overtimeTeamLayout;

        reasonOvertime = binding.overtimeReason;
        reasonOvertimeLayout = binding.overtimeReasonLayout;

        hoursOvertime = binding.overtimeTotalHours;
        hoursOvertimeLayout = binding.overtimeTotalHoursLayout;

        approvedByOvertime = binding.overtimeApprovedBy;
        approvedByOvertimeLayout = binding.overtimeApprovedByLayout;

        applyButton = binding.overtimeApply;

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

        // calendar pop up
        overtimeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overtimeDateLayout.setError(null);
                new DatePickerDialog(getContext(), overtimeDatePicker,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // clear errors
        titlePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titlePositionLayout.setError(null);
            }
        });

        teamOvertime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teamOvertimeLayout.setError(null);
            }
        });

        hoursOvertime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hoursOvertimeLayout.setError(null);
            }
        });

        reasonOvertime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reasonOvertimeLayout.setError(null);
            }
        });

        approvedByOvertime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approvedByOvertimeLayout.setError(null);
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
                    overtimeDateLayout.setError("Date is Required");
                    overtimeDate.requestFocus();
                    return;
                }

                if(titlePositionS.isEmpty()){
                    titlePositionLayout.setError("Your Position is required");
                    titlePosition.requestFocus();
                    return;
                }

                if(teamOvertimeS.isEmpty()){
                    teamOvertimeLayout.setError("You must indicate your Team.");
                    teamOvertime.requestFocus();
                    return;
                }

                if(hoursOvertimeS.isEmpty()){
                    hoursOvertimeLayout.setError("Please indicate the number of hours");
                    hoursOvertime.requestFocus();
                    return;
                }

                if(reasonOvertimeS.isEmpty()){
                    reasonOvertimeLayout.setError("Reason for Overtime is Required");
                    reasonOvertime.requestFocus();
                    return;
                }

                if(reasonOvertimeS.length() > 20){
                    reasonOvertimeLayout.setError("Limit reason to 20 characters");
                    reasonOvertime.requestFocus();
                    return;
                }

                if(approvedByS.isEmpty()){
                    approvedByOvertimeLayout.setError("Please indicate who approved this overtime.");
                    approvedByOvertime.requestFocus();
                    return;
                }
                sendToDatabase();
                requireView().clearFocus();
            }
        });

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
        binding = null;
    }
}