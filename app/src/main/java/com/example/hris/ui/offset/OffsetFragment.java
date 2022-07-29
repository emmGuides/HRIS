package com.example.hris.ui.offset;

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

import com.example.hris.databinding.FragmentOffsetBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class OffsetFragment extends Fragment {


    private FragmentOffsetBinding binding;
    EditText offset_teamName, offset_teamLeader, offset_hours, offset_date, offset_reason;
    TextInputLayout offset_teamNameLayout, offset_teamLeaderLayout, offset_hoursLayout, offset_dateLayout, offset_reasonLayout;
    String userID, offset_teamNameS, offset_teamLeaderS, offset_hoursS, offset_dateS, offset_reasonS;
    final Calendar myCalendar = Calendar.getInstance();
    TextView offsetDateLabel;
    Thread thread;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateWord = new SimpleDateFormat("MMMM dd, yyyy");
    String dateToday = dateFormat.format(myCalendar.getTime());

    private DatabaseReference reference, masterList;
    private FirebaseUser user;
    Button applyButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOffsetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        offset_teamName = binding.offsetTeam;
        offset_teamNameLayout = binding.offsetTeamLayout;

        offset_teamLeader = binding.offsetTeamLeader;
        offset_teamLeaderLayout = binding.offsetTeamLeaderLayout;

        offset_date = binding.offsetDate;
        offset_dateLayout = binding.offsetDateLayout;

        offset_reason = binding.offsetReason;
        offset_reasonLayout = binding.offsetReasonLayout;

        offset_hours = binding.offsetTotalHours;
        offset_hoursLayout = binding.offsetTotalHoursLayout;

        applyButton = binding.offsetApply;

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();
        masterList = reference.child(userID).child("Offsets");

        // calendar popup
        DatePickerDialog.OnDateSetListener offsetDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabelOffsetDate();
            }
        };

        offset_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset_dateLayout.setError(null);
                new DatePickerDialog(getContext(), offsetDatePicker,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset_teamNameS = offset_teamName.getText().toString().trim();
                offset_teamLeaderS = offset_teamLeader.getText().toString().trim();
                offset_hoursS = offset_hours.getText().toString().trim();
                offset_dateS = offset_date.getText().toString().trim();
                offset_reasonS = offset_reason.getText().toString().trim();

                if(offset_teamNameS.isEmpty()){
                    offset_teamNameLayout.setError("Team name is required!");
                    offset_teamName.requestFocus();
                    return;
                }

                if(offset_teamLeaderS.isEmpty()){
                    offset_teamLeaderLayout.setError("Team Leader's name is required!");
                    offset_teamLeader.requestFocus();
                    return;
                }

                if(offset_hoursS.isEmpty()){
                    offset_hoursLayout.setError("Number Hours required!");
                    offset_hours.requestFocus();
                    return;
                }

                if(offset_dateS.isEmpty()){
                    offset_dateLayout.setError("Date is required!");
                    offsetDateLabel.requestFocus();
                    return;
                }

                if(offset_reasonS.isEmpty()){
                    offset_reasonLayout.setError("Reason is required!");
                    offset_reason.requestFocus();
                    return;
                }

                if(offset_reasonS.length() > 20){
                    offset_reasonLayout.setError("Limit reason to 20 characters");
                    offset_reasonLayout.requestFocus();
                    return;
                }
                sendToDatabase();
                requireView().clearFocus();
            }
        });

        offset_teamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset_teamNameLayout.setError(null);
            }
        });

        offset_teamLeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset_teamLeaderLayout.setError(null);
            }
        });

        offset_hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset_hoursLayout.setError(null);
            }
        });

        offset_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset_reasonLayout.setError(null);
            }
        });

        return root;
    }

    // calendar pop up
    private void updateLabelOffsetDate(){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        offset_date.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void sendToDatabase(){
        HashMap<String,String> toAddMap=new HashMap<String,String>();
        String userName = user.getDisplayName();

        toAddMap.put("Date Applied", dateToday);
        toAddMap.put("Team Name", offset_teamNameS);
        toAddMap.put("Team Leader", offset_teamLeaderS);
        toAddMap.put("Hours", offset_hoursS);
        toAddMap.put("Date of Offset", offset_dateS);
        toAddMap.put("Reason", offset_reasonS);

        masterList.child(dateWord.format(Calendar.getInstance().getTime())).setValue(toAddMap);
        toAddMap.clear();

        Toast.makeText(getActivity(), "Offset Added!", Toast.LENGTH_LONG).show();
        offset_teamName.setText(""); offset_teamLeader.setText(""); offset_hours.setText("");
        offset_date.setText(""); offset_reason.setText("");

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}