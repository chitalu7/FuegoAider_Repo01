package com.murrays.aiderv1;

public class User {
    public String FirstName;
    public String LastName;

    public User() {
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String name) {
        FirstName = name;
    }
    public String getLastName() {
        return LastName;
    }

    public void setLastName(String name) {
        LastName = name;
    }




    public User(String fname, String lname) {
        FirstName = fname;
        LastName = lname;

    }

}

