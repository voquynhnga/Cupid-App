package com.midterm.destined.Models;

import com.midterm.destined.R;

public class Notification {
    private String timestamp;
    private String content;
    private int mod;
    private int imageResource;

    public Notification() {
        // Default constructor for Firebase or serialization
    }

    public Notification(String timestamp, String content, int mod) {
        this.timestamp = timestamp;
        this.content = content;
        this.mod = mod;
        if(mod == 1){
            this.imageResource = R.drawable.red_heart;
        }
        else{
            this.imageResource = R.drawable.message_notification;
        }
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

}
