package com.example.hris.ui.reminders;

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

        // start
        try {
            reference.child(userID).child("Reminders").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    for (DataSnapshot ds : snapshot.getChildren()){
                        Toast.makeText(getActivity(), ds.getKey(), Toast.LENGTH_SHORT).show();

                        String accomplished = String.valueOf(ds.child("Accomplished").getValue());
                        String assignedBy = String.valueOf(ds.child("Assigned By").getValue());
                        String assigneeID = String.valueOf(ds.child("Assignee ID").getValue());
                        String details = String.valueOf(ds.child("Details").getValue());
                        String importantDate = String.valueOf(ds.child("Important Date").getValue());
                        String reminderType = String.valueOf(ds.child("Reminder Type").getValue());
                        String reminderTypeContext = String.valueOf(ds.child("Reminder Type Context").getValue());

                        if(accomplished.equals("false")){
                            Reminders reminderToAdd = new Reminders(accomplished, assignedBy, assigneeID, details, importantDate, reminderType, reminderTypeContext);
                            remindersArrayList.add(reminderToAdd);

                            accomplished_list.add(accomplished);
                            assignedBy_list.add(assignedBy);
                            assigneeID_list.add(assigneeID);
                            details_list.add(details);
                            importantDate_list.add(importantDate);
                            reminderType_list.add(reminderType);
                            reminderTypeContext_list.add(reminderTypeContext);
                          }

                        if(getActivity() != null){
                            listAdapter_Reminders = new ListAdapter_Reminders(getActivity(), remindersArrayList);
                            remindersList.setAdapter(listAdapter_Reminders);
                            remindersList.setEmptyView(emptyDisplayList_Reminders);
                            listAdapter_Reminders.notifyDataSetChanged();
                            remindersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    //TODO do stuff when employee taps on a reminder item
                                    try{
                                        Snackbar.make(requireView(), "You tapped a " + reminderType_list.get(i) + " " +  reminderTypeContext_list.get(i) + " reminder, given by " + assignedBy_list.get(i), Snackbar.LENGTH_LONG).show();
                                    } catch (Exception snackBarError){
                                        Toast.makeText(getActivity(), "You tapped a " + reminderType_list.get(i) + " " +  reminderTypeContext_list.get(i) + " reminder, given by " + assignedBy_list.get(i), Toast.LENGTH_LONG).show();
                                    }
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