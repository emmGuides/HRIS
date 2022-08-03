package com.example.hris.ui.reminders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.databinding.FragmentRemindersBinding;
import com.example.hris.databinding.FragmentTeamsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RemindersFragment extends Fragment {

    private FragmentRemindersBinding binding;

    private FirebaseUser user;
    private DatabaseReference reference;
    String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRemindersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // get user and DB
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        // start
        try {

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