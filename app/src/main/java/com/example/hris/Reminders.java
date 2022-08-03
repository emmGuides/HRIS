package com.example.hris;

public class Reminders {

    String accomplished, assignee_Name, assignee_ID, details, importantDate, reminderType, reminderContext;
    public Reminders(){

    }

    public Reminders(String accomplished, String assignee_Name, String assignee_ID, String details, String importantDate, String reminderType, String reminderContext) {
        this.accomplished = accomplished;
        this.assignee_Name = assignee_Name;
        this.assignee_ID = assignee_ID;
        this.details = details;
        this.importantDate = importantDate;
        this.reminderType = reminderType;
        this.reminderContext = reminderContext;
    }
}
