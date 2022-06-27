package com.example.hris.ui.sick;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.databinding.FragmentSickBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SickFragment extends Fragment {

    final Calendar myCalendar = Calendar.getInstance();


    private FragmentSickBinding binding;
    EditText editTextStart = null;
    EditText editTextEnd = null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SickViewModel sickViewModel =
                new ViewModelProvider(this).get(SickViewModel.class);

        binding = FragmentSickBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editTextStart = (EditText) binding.sickStartDate;
        editTextEnd = (EditText) binding.sickEndDate;

        DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabelStart();
            }
        };

        DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabelEnd();
            }
        };

        editTextStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),dateStart,myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),dateEnd,myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TextView textView = binding.textSickDays;
        sickViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void updateLabelStart(){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editTextStart.setText(dateFormat.format(myCalendar.getTime()));
    }

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
