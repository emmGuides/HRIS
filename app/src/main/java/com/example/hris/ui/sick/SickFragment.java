package com.example.hris.ui.sick;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.databinding.FragmentSickBinding;
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

public class SickFragment extends Fragment {

    final Calendar myCalendar = Calendar.getInstance();


    private FragmentSickBinding binding;
    EditText editTextStart = null;
    EditText editTextEnd = null;
    EditText details = null;
    EditText approvedBy;
    TextView numberOfDays, medFormLabel, availmentLabel, startDateLabel, endDateLabel;
    String startDate;
    String endDate;
    String additionalDetails;
    Thread thread, threadRadio;
    int differenceInDates = 0;
    Date formattedStart = null;
    Date formattedEnd = null;

    RadioGroup medForm_group, availment_group;
    RadioButton medForm_button, availment_button;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateWord = new SimpleDateFormat("MMMM dd, yyyy");
    String dateToday = dateFormat.format(myCalendar.getTime());

    private DatabaseReference reference, masterList;
    private FirebaseUser user;
    Button applyButton = null;
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SickViewModel sickViewModel =
                new ViewModelProvider(this).get(SickViewModel.class);

        binding = FragmentSickBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //radio buttons groups
        medForm_group = binding.medFormRadioGroup;
        availment_group = binding.availmentRadioGroup;
        medFormLabel = binding.medFormRadioGroupLabel;
        availmentLabel = binding.availmentRadioGroupLabel;

        approvedBy = binding.sickApprovedBy;

        startDateLabel = binding.startDateLabel;
        endDateLabel = binding.endDateLabel;

        // calendar popup
        editTextStart = binding.sickStartDate;
        editTextEnd =  binding.sickEndDate;

        // button
        applyButton = binding.sickApply;

        //location
        details =  binding.sickAdditionalDetails;

        // days duration
        numberOfDays = binding.textSickDays;

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();
        masterList = reference.child(userID).child("Sick Leaves");

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
                startDateLabel.setError(null);
                new DatePickerDialog(getContext(),dateStart,myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // calendar popup
        editTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDateLabel.setError(null);
                new DatePickerDialog(getContext(),dateEnd,myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // applyButton
        applyButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                String startDate = editTextStart.getText().toString().trim();
                String endDate = editTextEnd.getText().toString().trim();
                String additionalDetails = details.getText().toString().trim();
                String approvedByS = approvedBy.getText().toString().trim();

                if(startDate.isEmpty()){
                    startDateLabel.setError("Start Date Required");
                    startDateLabel.requestFocus();
                    return;
                }

                if(endDate.isEmpty()){
                    endDateLabel.setError("End Date Required");
                    endDateLabel.requestFocus();
                    return;
                }

                if(additionalDetails.isEmpty()){
                    details.setError("Details are Required");
                    details.requestFocus();
                    return;
                }

                if(medForm_group.getCheckedRadioButtonId() == -1){
                    medFormLabel.setError("Required");
                    medFormLabel.requestFocus();
                    return;
                }

                if(availment_group.getCheckedRadioButtonId() == -1){
                    availmentLabel.setError("Required");
                    availmentLabel.requestFocus();
                    return;
                }

                if(approvedByS.isEmpty()){
                    approvedBy.setError("Approval required");
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
        threadRadio = new Thread(){
            @Override
            public void run(){
                try{
                    while(!thread.isInterrupted()) {
                        Thread.sleep(5000);
                        requireActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                startDateLabel.setError(null);
                                endDateLabel.setError(null);
                                medFormLabel.setError(null);
                                availmentLabel.setError(null);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        threadRadio.start();
        return root;
    }



    // calendar pop up
    private void updateLabelStart(){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editTextStart.setText(dateFormat.format(myCalendar.getTime()));
    }

    // calendar pop up
    private void updateLabelEnd(){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
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

        List<String> toAdd = new ArrayList<>();
        HashMap<String, String> toAddMap = new HashMap<String, String>();

        medForm_button =  getActivity().findViewById(medForm_group.getCheckedRadioButtonId());
        availment_button = getActivity().findViewById(availment_group.getCheckedRadioButtonId());

        toAddMap.put("Date of Request", dateToday);
        toAddMap.put("User ID", user.getUid());
        toAddMap.put("Start Date", startDate);
        toAddMap.put("End Date", endDate);
        toAddMap.put("Details", additionalDetails);
        toAddMap.put("Medical Certificate", (String) medForm_button.getText());
        toAddMap.put("Availment", (String) availment_button.getText());
        toAddMap.put("Approved By", approvedBy.getText().toString().trim());
        toAddMap.put("Leave Duration", String.valueOf(differenceInDates));

        toAdd.add(dateToday);
        toAdd.add(startDate);
        toAdd.add(endDate);
        toAdd.add(additionalDetails);
        toAdd.add(String.valueOf(differenceInDates));

        masterList.child(dateWord.format(Calendar.getInstance().getTime())).setValue(toAddMap);
        toAdd.clear();

        Toast.makeText(getContext(), "Sick Leave Applied!", Toast.LENGTH_LONG).show();
        editTextStart.setText(""); editTextEnd.setText(""); details.setText(""); approvedBy.setText("");
        medForm_group.clearCheck();
        availment_group.clearCheck();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        thread.interrupt();
        threadRadio.interrupt();
    }
}
