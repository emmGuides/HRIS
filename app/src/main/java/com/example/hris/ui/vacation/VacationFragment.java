package com.example.hris.ui.vacation;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.databinding.FragmentVacationBinding;
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

public class VacationFragment extends Fragment {


    private FragmentVacationBinding binding;
    // calendar popup
    EditText editTextStart;
    EditText editTextEnd;
    EditText details;
    EditText teamName, managerName, approvedBy;
    TextView numberOfDays, startDateTextView, endDateTextView;
    ProgressBar progressBar;

    String startDate;
    String endDate;
    String additionalDetails;
    Thread thread, threadFields;
    int differenceInDates = 0;
    Date formattedStart;
    Date formattedEnd;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateWord = new SimpleDateFormat("MMMM dd, yyyy");

    Calendar myCalendar = Calendar.getInstance();
    String dateToday = dateFormat.format(myCalendar.getTime());

    private FirebaseUser user;
    private DatabaseReference reference, masterList;
    private String userID;

    Button applyButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVacationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        teamName = binding.vacationTeam;
        managerName = binding.vacationProjectManager;
        approvedBy = binding.vacationApprovedBy;

        startDateTextView = binding.vacStartDateLabel;
        endDateTextView = binding.vacEndDateLabel;

        // Button apply
        applyButton = binding.vacationApply;

        // location
        details = binding.vacationAdditionalDetails;

        // calendar popup
        editTextStart = binding.vacationStartDate;
        editTextEnd = binding.vacationEndDate;

        // days duration
        numberOfDays = binding.textVacationDays;


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();
        masterList = reference.child(userID).child("Vacation Leaves");

        // calendar popup
        DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabelStart();
            }
        };

        // calendar popup
        DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabelEnd();
            }
        };

        // calendar popup
        editTextStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDateTextView.setError(null);
                new DatePickerDialog(getContext(),
                        dateStart,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        // calendar popup
        editTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDateTextView.setError(null);
                new DatePickerDialog(getContext(),
                        dateEnd,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(startDate.isEmpty()){
                    startDateTextView.setError("Start Date is required");
                    startDateTextView.requestFocus();
                    return;
                }

                if(endDate.isEmpty()){
                    endDateTextView.setError("End Date is required");
                    endDateTextView.requestFocus();
                    return;
                }

                if(additionalDetails.isEmpty()){
                    details.setError("Details are Required");
                    details.requestFocus();
                    return;
                }

                if(teamName.getText().toString().trim().isEmpty()){
                    teamName.setError("Team Name cannot be empty");
                    teamName.requestFocus();
                    return;
                }

                if(managerName.getText().toString().trim().isEmpty()){
                    managerName.setError("Manager name cannot be empty");
                    managerName.requestFocus();
                    return;
                }

                if(approvedBy.getText().toString().trim().isEmpty()){
                    approvedBy.setError("You need to indicate who approved this leave.");
                    approvedBy.requestFocus();
                    return;
                }

                sendToDatabase();
            }

        });

        thread = new Thread() {
            @Override
            public void run(){
                try{
                    while(!thread.isInterrupted()) {
                        Thread.sleep(500);
                        requireActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {

                                startDate = editTextStart.getText().toString().trim();
                                endDate = editTextEnd.getText().toString().trim();
                                additionalDetails = details.getText().toString().trim();

                                if(isEditTextEmpty()){
                                    numberOfDays.setText("Dates incomplete");
                                }
                                updateDuration();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        threadFields = new Thread(){
            @Override
            public void run(){
                try{
                    while(!thread.isInterrupted()) {
                        Thread.sleep(5000);
                        requireActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                startDateTextView.setError(null);
                                endDateTextView.setError(null);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadFields.start();
        thread.start();
        return root;

    }
    // calendar popup
    private void updateLabelStart(){
        String myFormat = "MM/dd/yyyy";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat);
        editTextStart.setText(dateFormat.format(myCalendar.getTime()));
    }

    // calendar popup
    private void updateLabelEnd(){
        String myFormat = "MM/dd/yyyy";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat);
        editTextEnd.setText(dateFormat.format(myCalendar.getTime()));
    }

    private Boolean isEditTextEmpty(){
        return (TextUtils.isEmpty(editTextStart.getText().toString()) || TextUtils.isEmpty(editTextEnd.getText().toString()));
    }

    @SuppressLint("SetTextI18n")
    private void updateDuration(){

            try {
                formattedStart = dateFormat.parse(startDate);
                formattedEnd = dateFormat.parse(endDate);
                formattedStart.getTime();
                differenceInDates = (int) ((formattedEnd.getTime() - formattedStart.getTime()) / 86400000 );
                if(differenceInDates < 0){
                    numberOfDays.setText("Invalid dates inputted!\n(You are not a time traveller)");
                }
                else if (differenceInDates == 1){
                    numberOfDays.setText("This leave will take up a single day.");
                }
                else if (differenceInDates == 0) {
                    numberOfDays.setText("This will be an emergency leave.");
                }
                else {
                    numberOfDays.setText("This leave will take up " + differenceInDates + " days.");
                }

            }
            catch (Exception e) {
                numberOfDays.setText("");
            }
    }

    public void sendToDatabase (){

        List <String> toAdd = new ArrayList<>();
        toAdd.add(dateToday);
        toAdd.add(startDate);
        toAdd.add(endDate);
        toAdd.add(additionalDetails);
        toAdd.add(String.valueOf(differenceInDates));

        toAdd.clear();

        HashMap<String, String> toAddMap = new HashMap<>();
        toAddMap.put("Date Requested",dateToday);
        toAddMap.put("User ID", user.getUid());
        toAddMap.put("Start Date", startDate);
        toAddMap.put("End Date", endDate);
        toAddMap.put("Additional Details", additionalDetails);
        toAddMap.put("Team Name", teamName.getText().toString().trim());
        toAddMap.put("Project Manager", managerName.getText().toString().trim());
        toAddMap.put("Approved By", approvedBy.getText().toString().trim());
        toAddMap.put("Leave Duration", String.valueOf(differenceInDates));

        Toast.makeText(getContext(), "Vacation Leave Applied!", Toast.LENGTH_LONG).show();
        masterList.child(dateWord.format(Calendar.getInstance().getTime())).setValue(toAddMap);

        editTextStart.setText(""); editTextEnd.setText(""); details.setText("");
        teamName.setText(null); managerName.setText(null); approvedBy.setText(null);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        threadFields.interrupt();
        thread.interrupt();
    }

}
