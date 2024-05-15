package com.aemstore.core.models;

public class User{
    public User(int ID, String firstname, String lastname, String email) {
        this.ID = ID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    private int ID;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getID() {
        return ID;
    }

}
