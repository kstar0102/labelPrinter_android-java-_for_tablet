package com.labelprintertest.android.Models;

public class User {
    private String id;
    private String name;
    private String password;
    private String roll;

    public User () {

    }

    public void setId(String val) {
        id = val;
    }
    public String getId() {
        return id;
    }

    public void setName(String val) {
        name = val;
    }
    public String getName() {
        return name;
    }

    public void setPassword(String val) {
        password = val;
    }
    public String getPassword() {
        return password;
    }

    public void setRoll(String val) {
        roll = val;
    }
    public String getRoll() {
        return roll;
    }

}
