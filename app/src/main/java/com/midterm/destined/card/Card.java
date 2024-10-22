package com.midterm.destined.card;

public class Card {

    private String name;
//    private String age;
    private String bio;
    //HASHMAP
//    private String location;
    private String profileImageUrl;

    public Card(){}

    public Card(String name, String bio, String profileImageUrl) {
        this.name = name;
//        this.age = age;
        this.bio = bio;
//        this.location = location;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getAge() {
//        return age;
//    }
//
//    public void setAge(String age) {
//        this.age = age;
//    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
