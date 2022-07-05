package com.example.hris.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.Employee;
import com.example.hris.HomeScreen;
import com.example.hris.databinding.FragmentCalendarBinding;
import com.example.hris.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    TextView name, age, email, password;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        name = binding.nameActual;
        age = binding.ageActual;
        email = binding.emailActual;
        password = binding.passwordActual;

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        reference.child(userID).child("User Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Employee userProfile = snapshot.getValue(Employee.class);

                if(userProfile != null){
                    String fullName = userProfile.fullName;
                    String fullEmail = userProfile.email;
                    String Age = userProfile.age;

                    name.setText(fullName);
                    email.setText(fullEmail);
                    age.setText(Age);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something Wrong Happened", Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}