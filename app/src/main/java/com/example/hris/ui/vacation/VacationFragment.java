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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class VacationFragment extends Fragment {

    private FragmentVacationBinding binding;
    // calendar popup
    EditText editTextStart;
    EditText editTextEnd;
    EditText details;
    TextView numberOfDays;
    ProgressBar progressBar;

    String startDate;
    String endDate;
    String additionalDetails;
    Thread thread;
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
        VacationViewModel vacationViewModel =
                new ViewModelProvider(this).get(VacationViewModel.class);

        binding = FragmentVacationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
        masterList = reference.child(userID).child("vacationLeaves");

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
                    details.setError("Start Date is required");
                    details.requestFocus();
                    return;
                }

                if(endDate.isEmpty()){
                    details.setError("End Date is required");
                    details.requestFocus();
                    return;
                }

                if(additionalDetails.isEmpty()){
                    details.setError("Details are Required");
                    details.requestFocus();
                    return;
                }

                //TODO submit dates and details to firebase
                // Toast.makeText(getContext(), "Vacation Leave Applied", Toast.LENGTH_LONG).show();
                //progressBar.setVisibility(View.VISIBLE);
                sendToDatabase();
            }

        });

        /* TODO: use ViewModelProperly
        final TextView textView = binding.textVacationDays;
        vacationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        */


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
        masterList.child(dateWord.format(Calendar.getInstance().getTime())).setValue(toAdd);
        toAdd.clear();

        // Snackbar.make(requireView(), "Vacation Leave Applied!", Snackbar.LENGTH_LONG).show();
        Toast.makeText(getContext(), "Vacation Leave Applied!", Toast.LENGTH_LONG).show();

        editTextStart.setText(""); editTextEnd.setText(""); details.setText("");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        thread.interrupt();
        Toast.makeText(getContext(), "thread interrupted", Toast.LENGTH_SHORT).show();
    }

}
