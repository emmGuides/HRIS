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
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.databinding.FragmentVacationBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class VacationFragment extends Fragment {

    private FragmentVacationBinding binding;
    // calendar popup
    EditText editTextStart = null;
    EditText editTextEnd = null;
    EditText details = null;
    TextView numberOfDays;
    String startDate;
    String endDate;
    String additionalDetails;
    Thread thread;
    int differenceInDates = 0;
    Date formattedStart = null;
    Date formattedEnd = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    final Calendar myCalendar = Calendar.getInstance();

    Button applyButton = null;

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
                new DatePickerDialog(getContext(),dateStart,myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // calendar popup
        editTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),dateEnd,myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startDate = editTextStart.getText().toString().trim();
                endDate = editTextEnd.getText().toString().trim();
                additionalDetails = details.getText().toString().trim();


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
                }

                //TODO submit dates and details to firebase
                //updateDuration();
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
                        Thread.sleep(1000);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "YEAH", Toast.LENGTH_SHORT).show();
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
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editTextStart.setText(dateFormat.format(myCalendar.getTime()));
    }

    // calendar popup
    private void updateLabelEnd(){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editTextEnd.setText(dateFormat.format(myCalendar.getTime()));
    }

    /*
    @SuppressLint("SetTextI18n")
    private void updateDuration(){
        if (editTextStart.getText().toString().trim() != null && editTextEnd.getText().toString().trim() != null) {
            try {
                formattedStart = dateFormat.parse(startDate);
                formattedEnd = dateFormat.parse(endDate);
                differenceInDates = (int) ((formattedEnd.getTime() - formattedStart.getTime()) / 86400000);
                numberOfDays.setText("This leave will take up "+ differenceInDates + " days.");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            numberOfDays.setText("");
        }
    }*/

    private int getDateDifference(String start, String end) throws ParseException {
        formattedStart = dateFormat.parse(start);
        formattedEnd = dateFormat.parse(end);
        differenceInDates = (int) ((formattedEnd.getTime() - formattedStart.getTime()) / 86400000);
        return differenceInDates;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        thread.interrupt();
        Toast.makeText(getContext(), "thread interrupted", Toast.LENGTH_SHORT).show();
    }

}
