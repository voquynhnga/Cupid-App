package com.midterm.destined;

public class Message {

    private String sender;
    private String content;
    private String time;
    private String currentUser;

    public Message(String sender, String content, String time, String currentUser) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.currentUser = currentUser;

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;  // Getter for time
    }
    public boolean isSender() {
        return sender.equals(currentUser);
    }
}
