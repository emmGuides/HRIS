package com.example.hris;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Employee> implements Filterable {

    public ListAdapter(Context context, ArrayList<Employee> employeeArrayList){
        super(context, R.layout.list_item, employeeArrayList);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Employee employee = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView employeeName = convertView.findViewById(R.id.employee_name);
        TextView employeeEmail = convertView.findViewById(R.id.employee_email);
        TextView lastTimeIn = convertView.findViewById(R.id.last_timeIn);

        employeeName.setText(employee.fullName);
        employeeEmail.setText(employee.email);
        lastTimeIn.setText("Last timed in: "+employee.lastTimeIn);

        return convertView;
    }
}
