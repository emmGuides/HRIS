package com.example.hris;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    public String fullName, age, email;
    public List< List<String> > vacationLeaves, sickLeaves;

    public Employee(){

    }

    public Employee(String fullName, String age, String email){
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }

}
