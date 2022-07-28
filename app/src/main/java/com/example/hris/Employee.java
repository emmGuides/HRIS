package com.example.hris;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    public String fullName, age, email, position, teams;

    public Employee(){

    }

    public Employee(String fullName, String age, String email, String position, String teams){
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.position = position;
        this.teams = teams;
    }

}
