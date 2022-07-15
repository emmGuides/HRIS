package com.example.hris.ui.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    Dialog changeName, changeAge, changeEmail, changePassword, changeProfile;
    Button okayName, cancelName, okayAge, cancelAge, okayEmail, cancelEmail, okayPassword, cancelPassword,
            okayProfile, cancelProfile;
    EditText emailEditText, nameEditText, ageEditText, passwordEditText_New, passwordEditText_Old,
            emailEditTextProfile, ageEditTextProfile,nameEditTextProfile, passwordEditText_NewProfile, passwordEditText_OldProfile;
    Button editProfile;
    Thread thread;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    String user_oldFullName, user_oldEmail, user_oldAge, user_oldPassword;

    @SuppressLint("UseCompatLoadingForDrawables")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        name = binding.nameActual;
        age = binding.ageActual;
        email = binding.emailActual;
        password = binding.passwordActual;

        editProfile = binding.editProfileBUTTON;

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        // change Entire Profile dialog
        changeProfile = new Dialog(getContext());
        changeProfile.setContentView(R.layout.custom_dialog_change_profile);
        changeProfile.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        changeProfile.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeProfile.setCancelable(false);
        changeProfile.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


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

        // Dialog Confirm and Cancel buttons
        okayName = changeName.findViewById(R.id.btn_okay);
        cancelName = changeName.findViewById(R.id.btn_cancel);

        okayAge = changeAge.findViewById(R.id.btn_okay);
        cancelAge = changeAge.findViewById(R.id.btn_cancel);

        okayEmail = changeEmail.findViewById(R.id.btn_okay);
        cancelEmail = changeEmail.findViewById(R.id.btn_cancel);

        okayPassword = changePassword.findViewById(R.id.btn_okay);
        cancelPassword = changePassword.findViewById(R.id.btn_cancel);

        okayProfile = changeProfile.findViewById(R.id.btn_okay);
        cancelProfile = changeProfile.findViewById(R.id.btn_cancel);

        // Dialog EditTexts
        emailEditText = changeEmail.findViewById(R.id.newEmail_input);
        nameEditText = changeName.findViewById(R.id.newName_input);
        ageEditText = changeAge.findViewById(R.id.newAge_input);
        passwordEditText_New = changePassword.findViewById(R.id.newPassword_input);
        passwordEditText_Old = changePassword.findViewById(R.id.oldPassword_input);

        emailEditTextProfile = changeProfile.findViewById(R.id.newEmail_input);
        nameEditTextProfile = changeProfile.findViewById(R.id.newName_input);
        ageEditTextProfile = changeProfile.findViewById(R.id.newAge_input);
        passwordEditText_NewProfile = changeProfile.findViewById(R.id.newPassword_input);
        passwordEditText_OldProfile = changeProfile.findViewById(R.id.oldPassword_input);


        thread = new Thread(){
          @Override
          public void run(){
              try {
                  while (!thread.isInterrupted()) {
                      Thread.sleep(250);
                      requireActivity().runOnUiThread(new Runnable() {
                          @SuppressLint("SetTextI18n")
                          @Override
                          public void run() {
                              reference.child(userID).child("User Details").addListenerForSingleValueEvent(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                                      Employee userProfile = snapshot.getValue(Employee.class);

                                      if(userProfile != null){
                                          user_oldFullName = userProfile.fullName;
                                          user_oldEmail = userProfile.email;
                                          user_oldAge = userProfile.age;

                                          name.setText(user_oldFullName);
                                          email.setText(user_oldEmail);
                                          age.setText(user_oldAge);


                                          nameEditTextProfile.setHint(user_oldFullName);
                                          emailEditTextProfile.setHint(user_oldEmail);
                                          ageEditTextProfile.setHint(user_oldAge);
                                      }
                                  }

                                  @Override
                                  public void onCancelled(@NonNull DatabaseError error) {
                                      Toast.makeText(getContext(), "Something Wrong Happened", Toast.LENGTH_LONG).show();
                                  }
                              });
                          }
                      });
                  }
              }
              catch(InterruptedException x){
                  x.printStackTrace();
              }
          }
        };
        thread.start();



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

        password.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changePassword.show();
                return false;
            }
        });

        // Name buttons
        okayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = nameEditText.getText().toString().trim();
                if(newName.isEmpty()){
                    nameEditText.setError("Name should not be empty!");
                    nameEditText.requestFocus();
                    return;
                }
                String disp = "Name updated to "+newName;
                reference.child(userID).child("User Details").child("fullName").setValue(newName);
                Toast.makeText(getContext(), disp, Toast.LENGTH_LONG).show();
                changeName.dismiss();
            }
        });

        cancelName.setOnClickListener(view -> changeName.dismiss());

        // Age buttons
        okayAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newAge = ageEditText.getText().toString().trim();
                if(newAge.isEmpty()){
                    ageEditText.setError("Age cannot be empty");
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

        okayEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String oldEmail = user_oldEmail;

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEditText.setError("Invalid Email Format");
                    emailEditText.requestFocus();
                    return;
                }
                if(email.equals(oldEmail)){
                    emailEditText.setError("New and Old email should not be the same.");
                    emailEditText.requestFocus();
                    return;
                }
                Toast.makeText(getContext(), "Emails will be able to be updated in Future Updates", Toast.LENGTH_SHORT).show();
                changeEmail.dismiss();
            }
        });

        cancelEmail.setOnClickListener(view -> changeEmail.dismiss());

        okayPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Passwords will be able to be updated in Future Updates", Toast.LENGTH_SHORT).show();
                changePassword.dismiss();
            }
        });

        cancelPassword.setOnClickListener(view -> changePassword.dismiss());

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


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        thread.interrupt();
    }
}