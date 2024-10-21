package com.midterm.destined.model;

import java.util.ArrayList;

public class User {

    private String uid;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String dateOfBirth;
    private String gender;
    private String bio;
    private String imageURL;
    private String location;
//    private String lastMessage;
//    private String lastTimeStamp, childID;

    private ArrayList<User> userArrayList = new ArrayList<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String email, String password, String fullName, String phoneNumber, String dateOfBirth, String gender, String bio, String imageURL, String location) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bio = bio;
        this.imageURL = imageURL;
        this.location = location;

        this.userArrayList = userArrayList;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public ArrayList<User> getUserArrayList() {
        return userArrayList;
    }
    public void addUser(User user) {
        userArrayList.add(user);
    }

    public void setUserArrayList(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }
}