package com.example.hris.ui.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.Employee;
import com.example.hris.HomeScreen;
import com.example.hris.R;
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
    ImageView editName, editAge, editEmail, editPassword;
    Dialog changeName, changeAge, changeEmail, changePassword;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @SuppressLint("UseCompatLoadingForDrawables")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        name = binding.nameActual;
        age = binding.ageActual;
        email = binding.emailActual;
        password = binding.passwordActual;

        editName = binding.editName;
        editAge = binding.editAge;
        editEmail = binding.editEmail;
        editPassword = binding.editPassword;

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        // change name dialog
        changeName = new Dialog(getContext());
        changeName.setContentView(R.layout.custom_dialog_change_name);
        changeName.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        changeName.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeName.setCancelable(true);
        changeName.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // change age dialog
        changeAge = new Dialog(getContext());
        changeAge.setContentView(R.layout.custom_dialog_change_age);
        changeAge.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        changeAge.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeAge.setCancelable(true);
        changeAge.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // change email dialog
        changeEmail = new Dialog(getContext());
        changeEmail.setContentView(R.layout.custom_dialog_change_email);
        changeEmail.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        changeEmail.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeEmail.setCancelable(true);
        changeEmail.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // change password dialog
        changePassword = new Dialog(getContext());
        changePassword.setContentView(R.layout.custom_dialog_change_password);
        changePassword.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        changePassword.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changePassword.setCancelable(true);
        changePassword.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

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

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "EDIT NAME", Toast.LENGTH_SHORT).show();
                changeName.show();
            }
        });

        editAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "EDIT AGE", Toast.LENGTH_SHORT).show();
                changeAge.show();
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "EDIT EMAIL", Toast.LENGTH_SHORT).show();
                changeEmail.show();
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "EDIT PASSSWORD", Toast.LENGTH_SHORT).show();
                changePassword.show();
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