package com.example.hris.ui.teams;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hris.Employee;
import com.example.hris.ListAdapter;
import com.example.hris.R;
import com.example.hris.databinding.FragmentTeamsBinding;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeamsFragment extends Fragment {


    private FragmentTeamsBinding binding;

    private FirebaseUser user;
    private DatabaseReference reference, referenceForTeams;
    private String userID, userName, teamName;
    Dialog createTeam, browseMembers;
    TextInputLayout teamNameLayout, lookForMember_layout;
    ConstraintLayout employeeView, managerView,
            employeeNoTeamView, managerNoTeamView,
            employeeHasTeamView, managerHasTeamView;
    Button createTeam_asManager, addMembers_asManager;
    TextView managerHasTeam_TeamName;
    ListView listViewBrowse;

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> emails = new ArrayList<>();
    ArrayList<String> lastTimeInS = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTeamsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        employeeView = binding.employeeLayout;
        employeeNoTeamView = binding.noTeamsLayoutEMPLOYEE;
        employeeHasTeamView = binding.hasTeamsLayoutEMPLOYEE;

        managerView = binding.managerLayout;
        managerNoTeamView = binding.noTeamsLayoutMANAGER;
        managerHasTeamView = binding.hasTeamsLayoutMANAGER;

        createTeam_asManager = binding.createTeamAsManager;
        addMembers_asManager = binding.managerHasTeamsAddMember;

        managerHasTeam_TeamName = binding.managerHasTeamsTeamNameTitle;

        // get user and DB
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        referenceForTeams = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Teams");
        userID = user.getUid();


        // Look for members dialog
        browseMembers = new Dialog(getContext());
        browseMembers.setContentView(R.layout.custom_dialog_teams_browse_for_members);
        browseMembers.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        browseMembers.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        browseMembers.setCancelable(false);
        browseMembers.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        listViewBrowse = browseMembers.findViewById(R.id.listView_lookForEmployees);
        listViewBrowse.setEmptyView(browseMembers.findViewById(R.id.emptyListAddMembers));
        lookForMember_layout = browseMembers.findViewById(R.id.lookForEmployee_layout);


        ValueEventListener getEmployees = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    String name = Objects.requireNonNull(ds.child("User Details").child("fullName").getValue()).toString();
                    String email = Objects.requireNonNull(ds.child("User Details").child("email").getValue()).toString();
                    String position = Objects.requireNonNull(ds.child("User Details").child("position").getValue()).toString();
                    String lastTimeIn = Objects.requireNonNull(ds.child("User Details").child("lastTimeIn").getValue()).toString();
                    String teams = Objects.requireNonNull(ds.child("User Details").child("teams").child("0").getValue()).toString();

                    if(!position.equals("Manager") && teams.equals("No team")){
                        names.add(name);
                        emails.add(email);
                        lastTimeInS.add(lastTimeIn);
                    }

                }

                ArrayList<Employee> employeeArrayList = new ArrayList<>();
                for(int i = 0; i < names.size(); i++){

                    Employee employee = new Employee(names.get(i),null,emails.get(i),null,null,lastTimeInS.get(i));
                    employeeArrayList.add(employee);

                }

                if(getActivity() != null){
                    ListAdapter listAdapter = new ListAdapter(getActivity(),employeeArrayList);
                    listViewBrowse = browseMembers.findViewById(R.id.listView_lookForEmployees);
                    listViewBrowse.setAdapter(listAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // listViewBrowse.setAdapter(listAdapter);

            reference.child(userID).child("User Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Employee userProfile = snapshot.getValue(Employee.class);
                if (userProfile != null) {
                    String user_position = userProfile.position;
                    userName = userProfile.fullName;
                    teamName = userProfile.teams.get(0);

                    // if user is just an employee
                    if(user_position.equals("Employee")){
                        employeeView.setVisibility(View.VISIBLE);

                        // if user is just an employee and does NOT have a team
                        if(Objects.equals(userProfile.teams.get(0), "No team")){
                            employeeNoTeamView.setVisibility(View.VISIBLE);
                        }

                        // if user is just an employee and does have a team
                        else
                        {
                            employeeHasTeamView.setVisibility(View.VISIBLE);
                        }
                    }

                    // if user is a MANAGER
                    else{
                        managerView.setVisibility(View.VISIBLE);

                        // if user is a MANAGER and does NOT have a team
                        if(Objects.equals(userProfile.teams.get(0), "No team")){
                            managerNoTeamView.setVisibility(View.VISIBLE);
                        }

                        // if user is a MANAGER and does have a team
                        else
                        {
                            managerHasTeam_TeamName.setText(userProfile.teams.get(0));
                            managerHasTeamView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // create team button
        createTeam_asManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTeam.show();
            }
        });

        // Create Team dialog
        createTeam = new Dialog(getContext());
        createTeam.setContentView(R.layout.custom_dialog_teams_create_team);
        createTeam.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        createTeam.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        createTeam.setCancelable(false);
        createTeam.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        teamNameLayout = createTeam.findViewById(R.id.new_team_layout);

        // dialog okay button
        createTeam.findViewById(R.id.btn_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                EditText userTeamNameInput = createTeam.findViewById(R.id.newTeam_name_input);
                String newTeamName = userTeamNameInput.getText().toString().trim();
                if(newTeamName.isEmpty()) {
                    teamNameLayout.setError("New team name should not be empty!");
                } else {
                    create_Team_Method(userTeamNameInput.getText().toString());
                    Toast.makeText(getActivity(), "Team "+newTeamName+" has been created", Toast.LENGTH_LONG).show();
                    managerNoTeamView.setVisibility(View.GONE);
                    createTeam.dismiss();
                }

            }
        });

        // dialog cancel button
        createTeam.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTeam.dismiss();
            }
        });

        // remove errors
        createTeam.findViewById(R.id.newTeam_name_input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teamNameLayout.setError(null);
            }
        });

        browseMembers.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseMembers.dismiss();
            }
        });

        browseMembers.findViewById(R.id.lookForEmployee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lookForMember_layout.setError(null);
            }
        });

        // show add members dialog
        addMembers_asManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseMembers.show();
            }
        });

        // show details
        addMembers_asManager.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getActivity(), "Add new members to team '" + teamName + "'", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        return root;
    }

    public void create_Team_Method(String newTeamNameParam){
        List<String> newTeamName = new ArrayList<>();
        String creatorName = userID + " (" + userName + ")";

        newTeamName.add(newTeamNameParam);
        reference.child(userID).child("User Details").child("teams").setValue(newTeamName);
        referenceForTeams.child(newTeamNameParam).child("Team Leader").setValue(creatorName);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}