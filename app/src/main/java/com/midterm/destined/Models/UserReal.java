package com.midterm.destined.Models;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserReal implements Serializable {

    private String uid;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String dateOfBirth;
    private String gender;
    private String bio;
    private List<String> interests; // New field for interests
    private GPSAddress location;
    private String profilePicture;
    private List<String> imageUrls;
    private String userName;
    private String detailAddress;
    private boolean isMatched = false; // Trạng thái đã match
    private boolean isFavorited = false; // Sửa tên biến ở đây

    public UserReal() {
        this.interests = new ArrayList<>();
        this.location = new GPSAddress(0.0, 0.0);
        this.imageUrls = new ArrayList<>();
        this.profilePicture = "";
    }

    public UserReal(String uid, String email, String fullName, String phoneNumber, String dateOfBirth, String gender,String bio, List<String> interests, GPSAddress location, String detailAddress, List<String> imageUrls, String userName) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bio = bio;
        this.detailAddress = detailAddress;
        this.interests = interests != null ? interests : new ArrayList<>();
        this.location = location != null ? location : new GPSAddress(0.0, 0.0);
        this.profilePicture = imageUrls.get(0);
        this.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) :
                new ArrayList<>(Collections.singletonList("https://firebasestorage.googleapis.com/v0/b/cupid-app-ad700.appspot.com/o/avatar_default.jpg?alt=media&token=70caf3c1-ebd8-4151-ad82-bc70365d87cf"));

        if (!this.imageUrls.isEmpty() && this.imageUrls.get(0).equals("gs://cupid-app-ad700.appspot.com/avatar_default.jpg")) {
            this.imageUrls.set(0, "https://firebasestorage.googleapis.com/v0/b/cupid-app-ad700.appspot.com/o/avatar_default.jpg?alt=media&token=70caf3c1-ebd8-4151-ad82-bc70365d87cf");
        }

        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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


    @PropertyName("detailAddress")
    public String getDetailAddress() {
        return detailAddress;
    }

    @PropertyName("detailAddress")
    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }
}
