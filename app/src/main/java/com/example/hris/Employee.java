package com.example.hris;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    public String fullName, age, email, position;
    public List<String> teams = new ArrayList<>();

    public Employee(){

    }

    public Employee(String fullName, String age, String email, String position, List<String> teams){
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.position = position;
        this.teams = teams;
    }

}
