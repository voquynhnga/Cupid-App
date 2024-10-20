package com.midterm.destined.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserReal implements Serializable {

    private String uid;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String dateOfBirth;
    private String gender;
    private List<String> interests; // New field for interests
    private GPSAddress location; // New field for location

    public UserReal() {
        // Default constructor required for calls to DataSnapshot.getValue(UserReal.class)
        this.interests = new ArrayList<>(); // Initialize interests list
        this.location = new GPSAddress(0.0, 0.0); // Initialize location with default values
    }

    public UserReal(String uid, String email, String password, String fullName, String phoneNumber, String dateOfBirth, String gender, List<String> interests, GPSAddress location) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.interests = interests != null ? interests : new ArrayList<>();
        this.location = location != null ? location : new GPSAddress(0.0, 0.0);
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public GPSAddress getLocation() {
        return location;
    }

    public void setLocation(GPSAddress location) {
        this.location = location;
    }

}