package com.example.hris.ui.calendar;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hris.R;
import com.example.hris.databinding.FragmentCalendarBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CalendarFragment extends Fragment {


    private FragmentCalendarBinding binding;

    private FirebaseUser user;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private String userID;
    ListView timeInOutLog, vacationLeaveLog, sickLeaveLog, overtimeLog, offsetLog;
    String ret_timeInOut, ret_vacation, ret_sick, ret_overtime, ret_offset;
    Dialog timeInOutLogDialog, vacationLeaveLogDialog, sickLeaveLogDialog, overtimeLogDialog, offsetLogDialog, downloadFileDialog;
    Button timeInOutLog_BUTTON, vacationLeaveLog_BUTTON, sickLeaveLog_BUTTON, overtimeLog_BUTTON, offsetLog_BUTTON;

    Animation scaleUpSlow, scaleDownSlow;
    ArrayList<String> timeInOutList = new ArrayList<>();
    ArrayList<String> vacationLeavesList = new ArrayList<>();
    ArrayList<String> sickLeavesList = new ArrayList<>();
    ArrayList<String> overtimeList = new ArrayList<>();
    ArrayList<String> offsetList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter_timeInOut, arrayAdapter_vacation, arrayAdapter_sick, arrayAdapter_overtime, arrayAdapter_offset;
    HashMap<Integer, String> certificates = new HashMap<>();
    HashMap<Integer, String> certificateKeyName = new HashMap<>();
    String childName, fileNameS, fileLoc;
    int counter = 0;
    ImageView calendarDummy;

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendarDummy = binding.calendarPlaceholderImage;

        // Authenticated user ID and Firebase Reference
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://hris-c2ba2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Employees");
        userID = user.getUid();

        // Animation
        scaleUpSlow = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_slow);
        scaleDownSlow = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_slow);

        calendarDummy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    calendarDummy.startAnimation(scaleDownSlow);
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    calendarDummy.startAnimation(scaleUpSlow);
                }

                try{
                    Snackbar.make(getView(), "This is a placeholder for a working Calendar Widget", Snackbar.LENGTH_LONG).show();
                } catch (Exception s) {
                    Toast.makeText(requireActivity(), "This is a placeholder for a working Calendar Widget", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });


        // overtime Dialog
        overtimeLog_BUTTON = binding.overtimeLogBUTTON;
        overtimeLogDialog = new Dialog(getContext());
        overtimeLogDialog.setContentView(R.layout.custom_dialog_log_overtime);
        overtimeLogDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        overtimeLogDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        overtimeLogDialog.setCancelable(true);
        overtimeLogDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        // overtime ListView
        overtimeLog = overtimeLogDialog.findViewById(R.id.overtimeLOG_inDialog);
        // set up and adapter for overtimes
        arrayAdapter_overtime = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, overtimeList);
        overtimeLog.setAdapter(arrayAdapter_overtime);
        overtimeLog.setEmptyView(overtimeLogDialog.findViewById(R.id.emptyListOvertime_TextView));
        // confirm button in overtime dialog
        overtimeLogDialog.findViewById(R.id.close_BUTTON).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overtimeLogDialog.dismiss();
            }
        });

        overtimeLog_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overtimeLogDialog.show();
            }
        });

        // download file Dialog
        downloadFileDialog = new Dialog(getContext());
        downloadFileDialog.setContentView(R.layout.custom_dialog_downloadfile);
        downloadFileDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        downloadFileDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        downloadFileDialog.setCancelable(false);
        downloadFileDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        downloadFileDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFileDialog.dismiss();
            }
        });
        downloadFileDialog.findViewById(R.id.btn_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    downloadFile();
                    Toast.makeText(getActivity(), "File being downloaded", Toast.LENGTH_LONG).show();
                }catch (Exception d){
                    Toast.makeText(getActivity(), "Download error", Toast.LENGTH_SHORT).show();
                }
                downloadFileDialog.dismiss();
            }
        });
        downloadFileDialog.findViewById(R.id.close_BUTTON).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFileDialog.dismiss();
            }
        });

        // offset Dialog
        offsetLog_BUTTON = binding.offsetLogBUTTON;
        offsetLogDialog = new Dialog(getContext());
        offsetLogDialog.setContentView(R.layout.custom_dialog_log_offset);
        offsetLogDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        offsetLogDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        offsetLogDialog.setCancelable(true);
        offsetLogDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        // offset ListView
        offsetLog = offsetLogDialog.findViewById(R.id.offsetLOG_inDialog);
        // set up and adapter for offsets
        arrayAdapter_offset = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, offsetList);
        offsetLog.setAdapter(arrayAdapter_offset);
        offsetLog.setEmptyView(offsetLogDialog.findViewById(R.id.emptyListOffset_TextView));
        // confirm button in offset dialog

        offsetLogDialog.findViewById(R.id.close_BUTTON).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offsetLogDialog.dismiss();
            }
        });

        offsetLog_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offsetLogDialog.show();
            }
        });


        // timeInOutLog Dialog
        timeInOutLog_BUTTON = binding.timeInOutLogBUTTON;
        timeInOutLogDialog = new Dialog(getContext());
        timeInOutLogDialog.setContentView(R.layout.custom_dialog_log_time_in_out);
        timeInOutLogDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        timeInOutLogDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeInOutLogDialog.setCancelable(true);
        timeInOutLogDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        //ListView
        timeInOutLog = timeInOutLogDialog.findViewById(R.id.timeInsOutsLOG_inDialog);
        // set up and adapter for time ins and outs
        arrayAdapter_timeInOut = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, timeInOutList);
        timeInOutLog.setAdapter(arrayAdapter_timeInOut);
        timeInOutLog.setEmptyView(timeInOutLogDialog.findViewById(R.id.emptyListTimeInOut_TextView));
        // Close
        timeInOutLogDialog.findViewById(R.id.close_BUTTON).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeInOutLogDialog.dismiss();
            }
        });

        // show dialog button on click time in and out
        timeInOutLog_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeInOutLogDialog.show();
            }
        });


        // Vacation Leave log Dialog
        vacationLeaveLog_BUTTON = binding.VacationLogBUTTON;
        vacationLeaveLogDialog = new Dialog(getContext());
        vacationLeaveLogDialog.setContentView(R.layout.custom_dialog_log_vacation_leave);
        vacationLeaveLogDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        vacationLeaveLogDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vacationLeaveLogDialog.setCancelable(true);
        vacationLeaveLogDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        //List View
        vacationLeaveLog = vacationLeaveLogDialog.findViewById(R.id.vacationLeaveLOG_inDialog);
        // Set up adapter for vacation leave log
        arrayAdapter_vacation = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, vacationLeavesList);
        vacationLeaveLog.setAdapter(arrayAdapter_vacation);
        vacationLeaveLog.setEmptyView(vacationLeaveLogDialog.findViewById(R.id.emptyListVacation_TextView));
        // Close
        vacationLeaveLogDialog.findViewById(R.id.close_BUTTON).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vacationLeaveLogDialog.dismiss();
            }
        });

        vacationLeaveLog_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vacationLeaveLogDialog.show();
            }
        });

        // Sick Leave log Dialog
        sickLeaveLog_BUTTON = binding.SickLogBUTTON;
        sickLeaveLogDialog = new Dialog(getContext());
        sickLeaveLogDialog.setContentView(R.layout.custom_dialog_log_sick_leave);
        sickLeaveLogDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_backgroud));
        sickLeaveLogDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sickLeaveLogDialog.setCancelable(true);
        sickLeaveLogDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        //List View
        sickLeaveLog = sickLeaveLogDialog.findViewById(R.id.sickLeaveLOG_inDialog);
        // Set up adapter for vacation leave log
        arrayAdapter_sick = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sickLeavesList);
        sickLeaveLog.setAdapter(arrayAdapter_sick);
        sickLeaveLog.setEmptyView(sickLeaveLogDialog.findViewById(R.id.emptyListSick_TextView));
        // Close
        sickLeaveLogDialog.findViewById(R.id.close_BUTTON).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sickLeaveLogDialog.dismiss();
            }
        });

        // TODO: testo
        sickLeaveLog.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView fileName = (TextView) downloadFileDialog.findViewById(R.id.download_filename);
                fileName.setText(certificates.get(i));
                if(!fileName.getText().toString().trim().equals("No Certificate")){
                    downloadFileDialog.show();
                }else{
                    Toast.makeText(getActivity(), "No certificate uploaded", Toast.LENGTH_SHORT).show();
                }

                childName = certificateKeyName.get(i);
                fileNameS = certificates.get(i);

                return false;
            }
        });

        sickLeaveLog_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sickLeaveLogDialog.show();
            }
        });


        // offset list view reference
        reference.child(userID).child("Offsets").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                try{
                    ret_offset = "\nDate of request: " + snapshot.getKey()
                            + "\n\n\t\t\tDate of Offset: " + ((HashMap<?, ?>) snapshot.getValue()).get("Date of Offset")
                            + "\n\t\t\tHours: " + ((HashMap<?, ?>) snapshot.getValue()).get("Hours")
                            + "\n\t\t\tReason: " + ((HashMap<?, ?>) snapshot.getValue()).get("Reason")
                            + "\n\t\t\tTeam Name: " + ((HashMap<?, ?>) snapshot.getValue()).get("Team Name")
                            + "\n\t\t\tTeam Leader: " + ((HashMap<?, ?>) snapshot.getValue()).get("Team Leader")
                            + "\n";
                } catch (Exception e) {
                    ret_offset = "Retrieval Error";
                }
                offsetList.add(ret_offset);
                arrayAdapter_offset.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayAdapter_offset.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        // overtime list view reference
        reference.child(userID).child("Overtimes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                try{
                    ret_overtime = "\nDate of request: " + snapshot.getKey()
                            + "\n\n\t\t\tDate of Overtime: " + ((HashMap<?, ?>) snapshot.getValue()).get("Date of Overtime")
                            + "\n\t\t\tHours: " + ((HashMap<?, ?>) snapshot.getValue()).get("Hours")
                            + "\n\t\t\tReason: " + ((HashMap<?, ?>) snapshot.getValue()).get("Reason")
                            + "\n\t\t\tTitle/Position: " + ((HashMap<?, ?>) snapshot.getValue()).get("Title")
                            + "\n\t\t\tTeam: " + ((HashMap<?, ?>) snapshot.getValue()).get("Team")
                            + "\n";
                } catch (Exception e) {
                    ret_overtime = "Retrieval Error";
                }


                overtimeList.add(ret_overtime);
                arrayAdapter_overtime.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayAdapter_overtime.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // time ins and outs list view reference
        reference.child(userID).child("Time Ins and Outs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ArrayList<String> master = (ArrayList<String>) snapshot.getValue();
                // String dateAdded = TextUtils.join(", ", (Iterable) snapshot.getValue());

                try{
                    ret_timeInOut = "\nDate: " + snapshot.getKey()
                                + "\n\n\t\t\tTime In: " + master.get(0)
                                + "\n\t\t\tTime Out: " + master.get(2)
                                + "\n\t\t\tDuration: " + master.get(3)
                                + "\n";
                } catch (Exception e) {
                    ret_timeInOut = "\nDate: " + snapshot.getKey()
                                + "\n\n\t\t\tTime In: " + master.get(0)
                                + "\n\t\t\tTime Out: Not Timed out.\n";
                }


                timeInOutList.add(ret_timeInOut);
                arrayAdapter_timeInOut.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    arrayAdapter_timeInOut.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // fix scrolling of time ins and outs list view
        timeInOutLog.setOnTouchListener(new ListView.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        // vacation leaves
        reference.child(userID).child("Vacation Leaves").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                try{
                    ret_vacation = "\nDate Filed: " + Objects.requireNonNull(snapshot.getKey()).replaceAll("\\(.*\\)", "")
                            + "\n\n\n\t\t\tFrom: " + ((HashMap<?, ?>) snapshot.getValue()).get("Start Date")
                            + "\n\t\t\tTo: " + ((HashMap<?, ?>) snapshot.getValue()).get("End Date")
                            + "\n\t\t\tNumber of Days: " + ((HashMap<?, ?>) snapshot.getValue()).get("Leave Duration")
                            + "\n\n\t\t\tAdditional Details: \n\t\t\t" + ((HashMap<?, ?>) snapshot.getValue()).get("Additional Details")
                            + "\n";
                } catch (Exception e) {
                    ret_vacation = "Something wrong happened.";
                }


                vacationLeavesList.add(ret_vacation);
                arrayAdapter_vacation.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayAdapter_vacation.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // fix scrolling of vacation leave list view
        vacationLeaveLog.setOnTouchListener(new ListView.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        // Sick Leaves
        reference.child(userID).child("Sick Leaves").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String cirtName, dispCirtName;
                try{
                    cirtName = (String) ((HashMap<?, ?>) Objects.requireNonNull(snapshot.getValue())).get("Certificate Name");
                } catch (Exception c){
                    cirtName = "No Certificate";
                }

                if(Objects.requireNonNull(cirtName).length() > 14){
                    dispCirtName = cirtName.substring(0, 10) + ".." + cirtName.substring(cirtName.length() - 4);
                } else {
                    dispCirtName = cirtName;
                }

                try{
                    ret_sick = "\n\nDate Filed: " + Objects.requireNonNull(snapshot.getKey()).replaceAll("\\(.*\\)", "")
                            + "\n\n\t\t\tFrom: " + ((HashMap<?, ?>) snapshot.getValue()).get("Start Date")
                            + "\n\t\t\tTo: " + ((HashMap<?, ?>) snapshot.getValue()).get("End Date")
                            + "\n\t\t\tNumber of Days: " + ((HashMap<?, ?>) snapshot.getValue()).get("Leave Duration")
                            + "\n\t\t\tAvailment: " + ((HashMap<?, ?>) snapshot.getValue()).get("Availment")
                            + "\n\n\t\t\tMedical Certificate: \n\t\t\t" + dispCirtName
                            + "\n\n\t\t\tAdditional Details: " + ((HashMap<?, ?>) snapshot.getValue()).get("Details")
                            + "\n";
                } catch (Exception e) {
                    ret_sick = "Something wrong happened.";
                }
                certificates.put(counter, cirtName);
                certificateKeyName.put(counter, (String) snapshot.getKey());
                counter++;
                sickLeavesList.add(ret_sick);
                arrayAdapter_sick.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayAdapter_sick.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // fix scrolling of Sick leave list view
        sickLeaveLog.setOnTouchListener(new ListView.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        return root;

    }

    private void downloadFile() {

        fileLoc = "Medical Certificates/" + userID + "/" + childName + "/" + fileNameS;
        storageReference = FirebaseStorage.getInstance("gs://hris-c2ba2.appspot.com").getReference()
                .child(fileLoc);

        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadNow(requireActivity(),fileNameS, DIRECTORY_DOWNLOADS, uri.toString());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try{
                    Toast.makeText(getActivity(), "Download Failed", Toast.LENGTH_LONG).show();
                } catch (Error f){
                    // not needed as of now
                }
            }
        });
    }

    private void downloadNow(Context context, String completeFileName, String destinationDirectory, String url) {

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, completeFileName);

        downloadManager.enqueue(request);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}