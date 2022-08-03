package com.example.hris.ui.teams;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TeamsFragment extends Fragment {

    private FragmentTeamsBinding binding;

    private FirebaseUser user;
    private DatabaseReference reference, referenceForTeams;
    SimpleDateFormat date = new SimpleDateFormat("MMMM dd, yyyy");

    private String userID, userName, teamName, userEmail;
    Dialog createTeam, browseMembers, verifyAdding, sendReminderDialog;
    TextInputLayout teamNameLayout, lookForMember_layout;
    ConstraintLayout employeeView, managerView,
            employeeNoTeamView, managerNoTeamView,
            employeeHasTeamView, managerHasTeamView;

    Button createTeam_asManager, addMembers_asManager;
    TextView managerHasTeam_TeamName, emptyDisplayList_Manager, employeeHasTeam_TeamName, emptyDisplayList_Employee, sendReminderTo;
    EditText sendReminderDate, sendReminderDetails;
    TextInputLayout sendReminderDateLayout, sendReminderDetailsLayout;
    ListView listViewBrowse, listViewShowEmployees_Manager, listViewShowEmployees_Employee;
    ListAdapter listAdapter, listAdapter_display;
    Employee userProfile;
    String managerName = "None", managerID = "None";

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> emails = new ArrayList<>();
    ArrayList<String> lastTimeInS = new ArrayList<>();
    ArrayList<String> IDs = new ArrayList<>();
    ArrayList<Employee> employeeArrayList = new ArrayList<>();
    HashMap<String, String> toBeAdded = new HashMap<>();

    ArrayList<String> namesForDisplay = new ArrayList<>();
    ArrayList<String> emailsForDisplay = new ArrayList<>();
    ArrayList<String> lastTimeInSforDisplay = new ArrayList<>();
    ArrayList<String> IDsForDisplay = new ArrayList<>();
    ArrayList<Employee> employeeArrayListForDisplay = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTeamsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        employeeView = binding.employeeLayout;
        employeeNoTeamView = binding.noTeamsLayoutEMPLOYEE;
        employeeHasTeamView = binding.hasTeamsLayoutEMPLOYEE;
        listViewShowEmployees_Employee = binding.listViewShowEmployeesEmployee;

        managerView = binding.managerLayout;
        managerNoTeamView = binding.noTeamsLayoutMANAGER;
        managerHasTeamView = binding.hasTeamsLayoutMANAGER;
        listViewShowEmployees_Manager = binding.listViewShowEmployeesManager;

        createTeam_asManager = binding.createTeamAsManager;
        addMembers_asManager = binding.managerHasTeamsAddMember;

        managerHasTeam_TeamName = binding.managerHasTeamsTeamNameTitle;
        emptyDisplayList_Manager = binding.emptyDisplayList;

        employeeHasTeam_TeamName = binding.employeeHasTeamsTeamNameTitle;
        emptyDisplayList_Employee = binding.emptyDisplayListEmployee;

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

        // verify adding member
        verifyAdding = new Dialog(getContext());
        verifyAdding.setContentView(R.layout.custom_dialog_add_member);
        verifyAdding.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        verifyAdding.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        verifyAdding.setCancelable(false);
        verifyAdding.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        verifyAdding.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyAdding.dismiss();
            }
        });

        // sendReminders dialog
        sendReminderDialog = new Dialog(getContext());
        sendReminderDialog.setContentView(R.layout.custom_dialog_teams_send_reminder);
        sendReminderDialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_backgroud);
        sendReminderDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sendReminderDialog.setCancelable(true);
        sendReminderDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        sendReminderDate = sendReminderDialog.findViewById(R.id.sendReminder_Date);
        sendReminderDateLayout = sendReminderDialog.findViewById(R.id.sendReminder_Date_Layout);
        sendReminderTo = sendReminderDialog.findViewById(R.id.sendReminderTo);
        sendReminderDetails = sendReminderDialog.findViewById(R.id.reminder_details);
        sendReminderDetailsLayout = sendReminderDialog.findViewById(R.id.reminder_details_layout);
        sendReminderDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReminderDialog.dismiss();
            }
        });

        sendReminderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReminderDetailsLayout.setError(null);
            }
        });

        sendReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReminderDateLayout.setError(null);
            }
        });

        final Calendar myCalendar = Calendar.getInstance();

        // calendar popup
        DatePickerDialog.OnDateSetListener overtimeDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat = "MM/dd/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                sendReminderDate.setText(dateFormat.format(myCalendar.getTime()));
            }
        };

        // calendar pop up
        sendReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReminderDateLayout.setError(null);
                new DatePickerDialog(getContext(), overtimeDatePicker,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // conditional rendering
        reference.child(userID).child("User Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProfile = snapshot.getValue(Employee.class);
                if (userProfile != null) {
                    String user_position = userProfile.position;
                    userName = userProfile.fullName;
                    teamName = userProfile.teams.get(0);
                    userEmail = userProfile.email;

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
                            employeeHasTeam_TeamName.setText(userProfile.teams.get(0));

                            //
                            try{

                                referenceForTeams.child(teamName).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for(DataSnapshot ds : snapshot.getChildren()){

                                            //Toast.makeText(getActivity(), Objects.requireNonNull(ds.getValue()).toString(), Toast.LENGTH_LONG).show();
                                            String name = Objects.requireNonNull(ds.child("Name").getValue()).toString();
                                            String email = Objects.requireNonNull(ds.child("Email").getValue()).toString();
                                            String ID = Objects.requireNonNull(ds.child("ID").getValue()).toString();
                                            String position = ds.getKey();

                                            if(!Objects.requireNonNull(ID).trim().equals(userID)){
                                                namesForDisplay.add(name);
                                                emailsForDisplay.add(email);
                                                IDsForDisplay.add(ID);
                                            }

                                        }


                                        for(int i = 0; i < namesForDisplay.size(); i++){

                                            Employee employee = new Employee(namesForDisplay.get(i),null, emailsForDisplay.get(i),null,null, "to be implemented");
                                            employeeArrayListForDisplay.add(employee);

                                        }

                                        if(getActivity() != null){
                                            listAdapter_display = new ListAdapter(getActivity(), employeeArrayListForDisplay);
                                            listViewShowEmployees_Employee.setAdapter(listAdapter_display);
                                            listViewShowEmployees_Employee.setEmptyView(emptyDisplayList_Employee);
                                            listAdapter_display.notifyDataSetChanged();
                                            listViewShowEmployees_Employee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                    //TODO do stuff when employee taps on a team member's item
                                                    try{
                                                        Snackbar.make(requireView(), "You tapped '" + namesForDisplay.get(i) + "', functionality will be implemented soon!", Snackbar.LENGTH_LONG).show();
                                                    } catch (Exception snackBarError){
                                                        Toast.makeText(getActivity(), "You tapped '" + namesForDisplay.get(i) + "', functionality will be implemented soon!", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                            });
                                            listAdapter_display.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } catch (Exception noTeamsYet){
                                Toast.makeText(getActivity(), "oops something went wrong " + teamName, Toast.LENGTH_LONG).show();
                            }

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

                            //
                            try{

                                referenceForTeams.child(teamName).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for(DataSnapshot ds : snapshot.getChildren()){

                                            //Toast.makeText(getActivity(), Objects.requireNonNull(ds.getValue()).toString(), Toast.LENGTH_LONG).show();
                                            String name = Objects.requireNonNull(ds.child("Name").getValue()).toString();
                                            String email = Objects.requireNonNull(ds.child("Email").getValue()).toString();
                                            String ID = Objects.requireNonNull(ds.child("ID").getValue()).toString();
                                            String position = ds.getKey();

                                            if(!Objects.requireNonNull(position).trim().equals("Team Leader")){
                                                namesForDisplay.add(name);
                                                emailsForDisplay.add(email);
                                                IDsForDisplay.add(ID);
                                            } else {
                                                managerName = name;
                                                managerID = ID;
                                            }

                                        }


                                        for(int i = 0; i < namesForDisplay.size(); i++){

                                            Employee employee = new Employee(namesForDisplay.get(i),null, emailsForDisplay.get(i),null,null, "to be implemented");
                                            employeeArrayListForDisplay.add(employee);

                                        }

                                        if(getActivity() != null){
                                            listAdapter_display = new ListAdapter(getActivity(), employeeArrayListForDisplay);
                                            listViewShowEmployees_Manager.setAdapter(listAdapter_display);
                                            listViewShowEmployees_Manager.setEmptyView(emptyDisplayList_Manager);
                                            listAdapter_display.notifyDataSetChanged();
                                            listViewShowEmployees_Manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @SuppressLint("SetTextI18n")
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                    //TODO send Reminder
                                                    sendReminderTo.setText("Send Reminder to '"+namesForDisplay.get(i)+"'");
                                                    sendReminderDialog.show();
                                                    sendReminderDialog.findViewById(R.id.btn_okay).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                            HashMap<String, String> toBeAdded_Reminder = new HashMap<>();
                                                            String importantDate = "None";
                                                            String details = "None";
                                                            String formattedDate = date.format(Calendar.getInstance().getTime());

                                                            if(sendReminderDetails.getText().toString().isEmpty()){
                                                                sendReminderDetailsLayout.setError("Details cannot be Empty");
                                                            }
                                                            else if(sendReminderDetails.getText().toString().length() > 20){
                                                                sendReminderDetailsLayout.setError("Limit Details to 20 characters");
                                                            }
                                                            else{
                                                                importantDate = sendReminderDate.getText().toString().trim();
                                                                details = sendReminderDetails.getText().toString().trim();
                                                                toBeAdded_Reminder.put("Important Date", importantDate.isEmpty() ? "Indefinite" : importantDate);
                                                                toBeAdded_Reminder.put("Details", details);
                                                                toBeAdded_Reminder.put("Accomplished", "false");
                                                                toBeAdded_Reminder.put("Assigned By", managerName);
                                                                toBeAdded_Reminder.put("Assignee ID", managerID);
                                                                toBeAdded_Reminder.put("Reminder Type", "Team");
                                                                toBeAdded_Reminder.put("Reminder Type Context", teamName);


                                                                reference.child(IDsForDisplay.get(i))
                                                                         .child("Reminders")
                                                                         .child(formattedDate + " (" + Calendar.getInstance().getTimeInMillis() + ")")
                                                                         .setValue(toBeAdded_Reminder).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Toast.makeText(getActivity(), "Reminder added", Toast.LENGTH_LONG).show();
                                                                                toBeAdded_Reminder.clear();
                                                                                sendReminderDialog.dismiss();
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                                }

                                            });
                                            listAdapter_display.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } catch (Exception noTeamsYet){
                                Toast.makeText(getActivity(), "oops something went wrong " + teamName, Toast.LENGTH_LONG).show();
                            }
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

                EditText userTeamNameInput = createTeam.findViewById(R.id.newTeam_name_input);
                String newTeamName = userTeamNameInput.getText().toString().trim();
                if(newTeamName.isEmpty()) {
                    teamNameLayout.setError("New team name should not be empty!");
                }
                else if(newTeamName.contains(".") || newTeamName.contains("#")
                        || newTeamName.contains("$") || newTeamName.contains("[")
                        || newTeamName.contains("]"))
                {
                    teamNameLayout.setError("Please avoid using '.', '#', '$', '[' , or ']' in the team name");
                }
                else {
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
                if(listAdapter != null){
                    listAdapter.notifyDataSetChanged();
                    listAdapter_display.notifyDataSetChanged();
                }
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

        //
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){

                    String name = Objects.requireNonNull(ds.child("User Details").child("fullName").getValue()).toString();
                    String email = Objects.requireNonNull(ds.child("User Details").child("email").getValue()).toString();
                    String position = Objects.requireNonNull(ds.child("User Details").child("position").getValue()).toString();
                    String lastTimeIn = Objects.requireNonNull(ds.child("User Details").child("lastTimeIn").getValue()).toString();
                    String teams = Objects.requireNonNull(ds.child("User Details").child("teams").child("0").getValue()).toString();
                    String ID = ds.getKey();

                    if(!position.equals("Manager") && teams.equals("No team")){
                        names.add(name);
                        emails.add(email);
                        lastTimeInS.add(lastTimeIn);
                        IDs.add(ID);
                    }

                }

                for(int i = 0; i < names.size(); i++){

                    Employee employee = new Employee(names.get(i),null,emails.get(i),null,null, lastTimeInS.get(i));
                    employeeArrayList.add(employee);

                }

                if(getActivity() != null){
                    listAdapter = new ListAdapter(getActivity(), employeeArrayList);
                    listViewBrowse = browseMembers.findViewById(R.id.listView_lookForEmployees);
                    listViewBrowse.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                    listViewBrowse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            verifyAddingShow(IDs.get(i), names.get(i), userProfile.teams.get(0), emails.get(i), lastTimeInS.get(i), i);
                        }

                    });
                    listAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return root;
    }

    @SuppressLint("SetTextI18n")
    // show dialog and add team-less employees to manager's team
    private void verifyAddingShow(String toBeAdded_ID, String toBeAdded_name, String teamName, String toBeAddedEmail, String lastTimeIn,int indexToBeRemoved) {
        TextView changeTextView = verifyAdding.findViewById(R.id.textView_doubleCheck);
        changeTextView.setText("Do you wish to add "+toBeAdded_name+" to '"+teamName+"'?");
        verifyAdding.show();
        verifyAdding.findViewById(R.id.btn_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toBeAdded.put("Name", toBeAdded_name);
                toBeAdded.put("ID", toBeAdded_ID);
                toBeAdded.put("Email", toBeAddedEmail);
                toBeAdded.put("Last Time In", lastTimeIn);
                referenceForTeams.child(teamName).child("Team Member" + "(" + Calendar.getInstance().getTimeInMillis()+")").setValue(toBeAdded);
                toBeAdded.clear();

                reference.child(toBeAdded_ID).child("User Details").child("teams").child("0").setValue(teamName);
                if(getActivity() != null){
                    Toast.makeText(getActivity(), toBeAdded_name + " added to "+teamName + "!", Toast.LENGTH_LONG).show();
                }
                employeeArrayList.clear();
                names.clear();
                emails.clear();
                IDs.clear();
                lastTimeInS.clear();

                employeeArrayListForDisplay.clear();
                namesForDisplay.clear();
                emailsForDisplay.clear();
                IDsForDisplay.clear();
                lastTimeInSforDisplay.clear();

                listAdapter.notifyDataSetChanged();
                listAdapter_display.notifyDataSetChanged();

                verifyAdding.dismiss();
            }
        });
    }

    public void create_Team_Method(String newTeamNameParam){
        List<String> newTeamName = new ArrayList<>();
        String creatorName = userName + " (" + userID + ")";

        newTeamName.add(newTeamNameParam);
        reference.child(userID).child("User Details").child("teams").setValue(newTeamName);

        toBeAdded.put("Name", userName);
        toBeAdded.put("ID", userID);
        toBeAdded.put("Email", userEmail);
        referenceForTeams.child(newTeamNameParam).child("Team Leader").setValue(toBeAdded);
        toBeAdded.clear();


        employeeArrayList.clear();
        names.clear();
        emails.clear();
        IDs.clear();
        lastTimeInS.clear();
        listAdapter.notifyDataSetChanged();
        try{
            listAdapter_display.notifyDataSetChanged();
        } catch (Exception noListDisplay){
            // none for now
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}