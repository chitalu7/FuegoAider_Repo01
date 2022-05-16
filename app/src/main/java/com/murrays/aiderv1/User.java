package com.murrays.aiderv1;

import com.google.firebase.database.PropertyName;

public class User {
    public String FirstName;
    public String LastName;

    public User(String fname, String lname){
        this.FirstName = fname;
        this.LastName = lname;
    }

    @PropertyName("FirstName")
    public String GetFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstname) {
        this.FirstName = firstname;
    }
    @PropertyName("LastName")
    public String GetLastName() {
        return LastName;
    }

    public void setLastName(String lastname) {
        this.LastName = lastname;
    }


}
