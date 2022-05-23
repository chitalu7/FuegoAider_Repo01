package com.murrays.aiderv1;
//this is our User class, it is the framework for a custom User object
//is based from the nodes on the database
// (we might not need to use this since we are pulling directly from the database
// as of now)
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

