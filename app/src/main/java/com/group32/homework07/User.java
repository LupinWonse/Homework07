package com.group32.homework07;

public class User {

    public static final String STORAGE_PROFILE_PICTURES_REFERENCE = "profilePictures";

    private String firstName;
    private String lastName;
    private String uid;
    private String email;
    private Boolean gender;

    public User() {
    }

    public User(String firstName, String lastName, String uid, String email, Boolean gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.uid = uid;
        this.email = email;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String fullName(){
        return firstName + " " + lastName;
    }
}
