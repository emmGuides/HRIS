package com.example.hris.ui.vacation;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.databinding.FragmentVacationBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class VacationFragment extends Fragment {

    private FragmentVacationBinding binding;
    // calendar popup
    EditText editTextStart = null;
    EditText editTextEnd = null;
    EditText location = null;
    final Calendar myCalendar = Calendar.getInstance();

    Button applyButton = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        VacationViewModel vacationViewModel =
                new ViewModelProvider(this).get(VacationViewModel.class);

        binding = FragmentVacationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Button apply
        applyButton = (Button) binding.vacationApply;

        // location
        location = (EditText) binding.vacationLeaveAddress;

        // calendar popup
        editTextStart = (EditText) binding.vacationStartDate;
        editTextEnd = (EditText) binding.vacationEndDate;

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

                String startDate = editTextStart.getText().toString().trim();
                String endDate = editTextEnd.getText().toString().trim();
                String locationLeave = location.getText().toString().trim();


                if(startDate.isEmpty()){
                    location.setError("Start Date is required");
                    location.requestFocus();
                    return;
                }

                if(endDate.isEmpty()){
                    location.setError("End Date is required");
                    location.requestFocus();
                    return;
                }

                if(locationLeave.isEmpty()){
                    location.setError("Location is Required");
                    location.requestFocus();
                    return;
                }
                //TODO submit dates and location to firebase
                Toast.makeText(getContext(), "Vacation Leave Applied", Toast.LENGTH_LONG).show();
            }
        });

        final TextView textView = binding.textVacationDays;
        vacationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
