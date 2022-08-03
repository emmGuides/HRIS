package com.example.hris;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListAdapter_Reminders extends ArrayAdapter<Reminders> {

    public ListAdapter_Reminders(Context context, ArrayList<Reminders> remindersArrayList){
        super(context, R.layout.list_item_reminders, remindersArrayList);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Reminders reminder = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_reminders, parent, false);
        }

        /*
        TextView employeeName = convertView.findViewById(R.id.employee_name);
        TextView employeeEmail = convertView.findViewById(R.id.employee_email);
        TextView lastTimeIn = convertView.findViewById(R.id.last_timeIn);

        employeeName.setText(employee.fullName);
        employeeEmail.setText(employee.email);
        lastTimeIn.setText("Last timed in: "+employee.lastTimeIn);
        */

        TextView assigneeName = convertView.findViewById(R.id.assignee_name);
        TextView importantDate = convertView.findViewById(R.id.important_date);
        TextView reminderContext = convertView.findViewById(R.id.reminder_context);
        TextView reminderType = convertView.findViewById(R.id.reminder_type);
        TextView details = convertView.findViewById(R.id.details);

        assigneeName.setText(reminder.assignee_Name);
        importantDate.setText(reminder.importantDate);
        reminderContext.setText(reminder.reminderContext);
        reminderType.setText(reminder.reminderType);
        details.setText(reminder.details);

        return convertView;
    }

}
