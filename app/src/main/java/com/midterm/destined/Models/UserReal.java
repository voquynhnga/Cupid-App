package com.midterm.destined.Models;

import android.util.Log;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.midterm.destined.Utils.DB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        //FIX-1
        this.profilePicture = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0): "https://firebasestorage.googleapis.com/v0/b/cupid-app-ad700.appspot.com/o/avatar_default.jpg?alt=media&token=70caf3c1-ebd8-4151-ad82-bc70365d87cf";
        this.imageUrls = imageUrls;
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
