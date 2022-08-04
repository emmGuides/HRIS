package com.example.hris.ui.reminders;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.ListAdapter;
import com.example.hris.ListAdapter_Reminders;
import com.example.hris.R;
import com.example.hris.Reminders;
import com.example.hris.databinding.FragmentRemindersBinding;
import com.example.hris.databinding.FragmentTeamsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RemindersFragment extends Fragment {

    private FragmentRemindersBinding binding;

    private FirebaseUser user;
    private DatabaseReference reference;
    String userID;
    ListView remindersList;
    ArrayList<Reminders> remindersArrayList = new ArrayList<>();
    ListAdapter_Reminders listAdapter_Reminders;
    TextView emptyDisplayList_Reminders;

    ArrayList<String> accomplished_list = new ArrayList<>();
    ArrayList<String> assignedBy_list = new ArrayList<>();
    ArrayList<String> assigneeID_list = new ArrayList<>();
    ArrayList<String> details_list = new ArrayList<>();
    ArrayList<String> importantDate_list = new ArrayList<>();
    ArrayList<String> reminderType_list = new ArrayList<>();
    ArrayList<String> reminderTypeContext_list = new ArrayList<>();
    ArrayList<String> header_list = new ArrayList<>();

    Dialog additionalDetails;
    TextView header_addtlDetails, importantDate_addtlDetails, assignee_addtlDetails, details_addtlDetails;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRemindersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        remindersList = binding.listViewShowReminders;
        emptyDisplayList_Reminders = binding.noReminders;

        // get user and DB
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        // additional details
        additionalDetails = new Dialog(getContext());
        additionalDetails.setContentView(R.layout.custom_dialog_reminder_addtl_details);
        additionalDetails.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        additionalDetails.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        additionalDetails.setCancelable(true);
        additionalDetails.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        assignee_addtlDetails = additionalDetails.findViewById(R.id.assignee);
        details_addtlDetails = additionalDetails.findViewById(R.id.details);
        header_addtlDetails = additionalDetails.findViewById(R.id.header);
        importantDate_addtlDetails = additionalDetails.findViewById(R.id.date);
        additionalDetails.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additionalDetails.dismiss();
            }
        });

        // start
        try {
            reference.child(userID).child("Reminders").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    for (DataSnapshot ds : snapshot.getChildren()){

                        String accomplished = String.valueOf(ds.child("Accomplished").getValue());
                        String assignedBy = String.valueOf(ds.child("Assigned By").getValue());
                        String assigneeID = String.valueOf(ds.child("Assignee ID").getValue());
                        String details = String.valueOf(ds.child("Details").getValue());
                        String importantDate = String.valueOf(ds.child("Important Date").getValue());
                        String reminderType = String.valueOf(ds.child("Reminder Type").getValue());
                        String reminderTypeContext = String.valueOf(ds.child("Reminder Type Context").getValue());
                        String header = String.valueOf(ds.child("Header").getValue());

                        if(accomplished.equals("false")){
                            Reminders reminderToAdd = new Reminders(accomplished, assignedBy, assigneeID, details, importantDate, reminderType, reminderTypeContext, header);
                            remindersArrayList.add(reminderToAdd);

                            accomplished_list.add(accomplished);
                            assignedBy_list.add(assignedBy);
                            assigneeID_list.add(assigneeID);
                            details_list.add(details);
                            importantDate_list.add(importantDate);
                            reminderType_list.add(reminderType);
                            reminderTypeContext_list.add(reminderTypeContext);
                            header_list.add(header);
                        }

                        if(getActivity() != null){
                            listAdapter_Reminders = new ListAdapter_Reminders(getActivity(), remindersArrayList);
                            remindersList.setAdapter(listAdapter_Reminders);
                            remindersList.setEmptyView(emptyDisplayList_Reminders);
                            listAdapter_Reminders.notifyDataSetChanged();
                            remindersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    //TODO do stuff when employee taps on a reminder item
                                    assignee_addtlDetails.setText("From: " + assignedBy_list.get(i));
                                    details_addtlDetails.setText(details_list.get(i));
                                    importantDate_addtlDetails.setText("Comply by: " + importantDate_list.get(i));
                                    header_addtlDetails.setText(header_list.get(i));
                                    additionalDetails.show();
//                                    try{
//                                        Snackbar.make(requireView(), "You tapped a " + reminderType_list.get(i) + " " +  reminderTypeContext_list.get(i) + " reminder, given by " + assignedBy_list.get(i), Snackbar.LENGTH_LONG).show();
//                                    } catch (Exception snackBarError){
//                                        Toast.makeText(getActivity(), "You tapped a " + reminderType_list.get(i) + " " +  reminderTypeContext_list.get(i) + " reminder, given by " + assignedBy_list.get(i), Toast.LENGTH_LONG).show();
//                                    }
                                }

                            });
                            listAdapter_Reminders.notifyDataSetChanged();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception userHasNoReminders) {
            // TODO let peepo know they do not have pending reminders
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}