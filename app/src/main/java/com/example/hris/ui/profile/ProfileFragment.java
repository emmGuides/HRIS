package com.example.hris.ui.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hris.Employee;
import com.example.hris.R;
import com.example.hris.databinding.FragmentProfileBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {


    private FragmentProfileBinding binding;
    TextView name, age, email, position;
    ImageView dummyProfileIcon;
    Animation scaleDownSlow, scaleUpSlow;
    Dialog changeName, changeAge, changeEmail, changeProfile;
    Button okayName, cancelName, okayAge, cancelAge, okayEmail, cancelEmail,
            okayProfile, cancelProfile;

    EditText emailEditText, nameEditText, ageEditText,
            emailEditTextProfile, ageEditTextProfile, nameEditTextProfile,
            passwordEditText_NewProfile, passwordEditText_OldProfile;

    TextInputLayout nameLayout, ageLayout, emailLayout;

    Button editProfile;
    boolean passwordVisible;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    String user_oldFullName, user_oldEmail, user_oldAge, user_oldPosition;

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        name = binding.nameActual;
        age = binding.ageActual;
        email = binding.emailActual;
        position = binding.positionActual;
        dummyProfileIcon = binding.userIcon;

        editProfile = binding.editProfileBUTTON;

        // animation
        scaleUpSlow = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_slow);
        scaleDownSlow = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_slow);

        // get user and DB
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        // change Entire Profile dialog
        changeProfile = new Dialog(getContext());
        changeProfile.setContentView(R.layout.custom_dialog_change_profile);
        changeProfile.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        changeProfile.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeProfile.setCancelable(false);
        changeProfile.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        // change name dialog
        changeName = new Dialog(getContext());
        changeName.setContentView(R.layout.custom_dialog_change_name);
        changeName.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        changeName.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeName.setCancelable(true);
        changeName.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // change age dialog
        changeAge = new Dialog(getContext());
        changeAge.setContentView(R.layout.custom_dialog_change_age);
        changeAge.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        changeAge.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeAge.setCancelable(true);
        changeAge.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // change email dialog
        changeEmail = new Dialog(getContext());
        changeEmail.setContentView(R.layout.custom_dialog_change_email);
        changeEmail.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        changeEmail.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeEmail.setCancelable(true);
        changeEmail.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        // Dialog Confirm and Cancel buttons
        okayName = changeName.findViewById(R.id.btn_okay);
        cancelName = changeName.findViewById(R.id.btn_cancel);

        okayAge = changeAge.findViewById(R.id.btn_okay);
        cancelAge = changeAge.findViewById(R.id.btn_cancel);

        okayEmail = changeEmail.findViewById(R.id.btn_okay);
        cancelEmail = changeEmail.findViewById(R.id.btn_cancel);

        okayProfile = changeProfile.findViewById(R.id.btn_okay);
        cancelProfile = changeProfile.findViewById(R.id.btn_cancel);

        // Dialog EditTexts
        emailEditText = changeEmail.findViewById(R.id.newEmail_input);
        emailLayout = changeEmail.findViewById(R.id.newEmail_input_layout);

        nameEditText = changeName.findViewById(R.id.newName_input);
        nameLayout = changeName.findViewById(R.id.newName_input_layout);

        ageEditText = changeAge.findViewById(R.id.newAge_input);
        ageLayout = changeAge.findViewById(R.id.newAge_input_layout);

        emailEditTextProfile = changeProfile.findViewById(R.id.newEmail_input);
        nameEditTextProfile = changeProfile.findViewById(R.id.newName_input);
        ageEditTextProfile = changeProfile.findViewById(R.id.newAge_input);
        passwordEditText_NewProfile = changeProfile.findViewById(R.id.newPassword_input);
        passwordEditText_OldProfile = changeProfile.findViewById(R.id.oldPassword_input);

        // display user details
        reference.child(userID).child("User Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Employee userProfile = snapshot.getValue(Employee.class);

                if (userProfile != null) {
                    user_oldFullName = userProfile.fullName;
                    user_oldEmail = userProfile.email;
                    user_oldAge = userProfile.age;

                    try{
                        user_oldPosition = userProfile.position;
                    } catch (Exception p){
                        user_oldPosition = "Employee";
                    }


                    name.setText(user_oldFullName);
                    email.setText(user_oldEmail);
                    age.setText(user_oldAge);
                    position.setText(user_oldPosition);

                    nameEditTextProfile.setHint(user_oldFullName);
                    emailEditTextProfile.setHint(user_oldEmail);
                    ageEditTextProfile.setHint(user_oldAge);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireActivity(), "Position cannot be changed as of now", Toast.LENGTH_SHORT).show();
            }
        });

        email.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeEmail.show();
                return false;
            }
        });

        name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeName.show();
                return false;
            }
        });

        age.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeAge.show();
                return false;
            }
        });

        // Name buttons
        okayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = nameEditText.getText().toString().trim();
                if(newName.isEmpty()){
                    nameLayout = changeName.findViewById(R.id.newName_input_layout);
                    nameLayout.setError("Name should not be empty!");
                    nameEditText.requestFocus();
                    return;
                }
                String disp = "Name updated to "+newName;
                reference.child(userID).child("User Details").child("fullName").setValue(newName);
                Toast.makeText(getContext(), disp, Toast.LENGTH_LONG).show();
                changeName.dismiss();
            }
        });

        nameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameLayout.setError(null);
            }
        });

        cancelName.setOnClickListener(view -> changeName.dismiss());

        // Age buttons
        okayAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newAge = ageEditText.getText().toString().trim();
                if(newAge.isEmpty()){
                    ageLayout.setError("Age cannot be empty");
                    ageEditText.requestFocus();
                    return;
                }
                String disp = "Age updated to " + newAge;
                reference.child(userID).child("User Details").child("age").setValue(newAge);
                Toast.makeText(getContext(), disp, Toast.LENGTH_LONG).show();
                changeAge.dismiss();
            }
        });

        cancelAge.setOnClickListener(view -> changeAge.dismiss());

        ageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ageLayout.setError(null);
            }
        });

        okayEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String oldEmail = user_oldEmail;

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailLayout.setError("Invalid Email Format");
                    emailEditText.requestFocus();
                    return;
                }
                if(email.equals(oldEmail)){
                    emailLayout.setError("New and Old email should not be the same.");
                    emailEditText.requestFocus();
                    return;
                }
                Toast.makeText(getContext(), "Emails will be able to be updated in Future Updates", Toast.LENGTH_SHORT).show();
                changeEmail.dismiss();
            }
        });

        cancelEmail.setOnClickListener(view -> changeEmail.dismiss());

        emailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailLayout.setError(null);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfile.show();
            }
        });

        cancelProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfile.dismiss();
            }
        });

        okayProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!nameEditTextProfile.getText().toString().trim().isEmpty()){
                    String newName = nameEditTextProfile.getText().toString().trim();
                    reference.child(userID).child("User Details").child("fullName").setValue(newName);
                }
                if(!ageEditTextProfile.getText().toString().trim().isEmpty()){
                    String newAge = ageEditTextProfile.getText().toString().trim();
                    reference.child(userID).child("User Details").child("age").setValue(newAge);
                }
                if(!emailEditTextProfile.getText().toString().trim().isEmpty()){
                    String email = emailEditTextProfile.getText().toString().trim();
                    String oldEmail = user_oldEmail;

                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        emailEditTextProfile.setError("Invalid Email Format");
                        emailEditTextProfile.requestFocus();
                        return;
                    }
                    if(email.equals(oldEmail)){
                        emailEditTextProfile.setError("New and Old email should not be the same.");
                        emailEditTextProfile.requestFocus();
                        return;
                    }
                }
                if(!passwordEditText_OldProfile.getText().toString().trim().isEmpty()){
                    // change password
                }
                if(!passwordEditText_NewProfile.getText().toString().trim().isEmpty()){
                    // change password
                }
                Toast.makeText(getActivity(), "Non empty fields updated!", Toast.LENGTH_LONG).show();
                changeProfile.dismiss();
            }
        });

        dummyProfileIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try{
                    Snackbar.make(requireView(), "Profile Icon placeholder", Snackbar.LENGTH_SHORT).show();
                }catch(Exception e){
                    Toast.makeText(getActivity(), "Profile Icon placeholder", Toast.LENGTH_SHORT).show();
                }

                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    dummyProfileIcon.startAnimation(scaleDownSlow);
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    dummyProfileIcon.startAnimation(scaleUpSlow);
                }

                return false;
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