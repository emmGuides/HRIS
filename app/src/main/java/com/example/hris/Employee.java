package com.example.hris;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    public String fullName, age, email;
    public List< List<String> > vacationLeaves, sickLeaves;

    public Employee(){

    }

    public Employee(String fullName, String age, String email, List< List<String> > vacationLeaves, List< List<String> > sickLeaves){
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.vacationLeaves = vacationLeaves;
        this.sickLeaves = sickLeaves;
    }

}
