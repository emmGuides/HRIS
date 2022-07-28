package com.example.hris;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    public String fullName, age, email, position;

    public Employee(){

    }

    public Employee(String fullName, String age, String email, String position){
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.position = position;
    }

}
