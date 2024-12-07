package com.midterm.destined.Models;

import com.google.firebase.database.PropertyName;

public class LastMessage {
    private String sender;
    private String content;
    private String time;

    @PropertyName("isRead1")
    private Boolean isRead1;

    @PropertyName("isRead2")
    private Boolean isRead2;

    public LastMessage() {}

    public LastMessage(String sender, String content, String time, Boolean isRead1, Boolean isRead2) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.isRead1 = isRead1;
        this.isRead2 = isRead2;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @PropertyName("isRead1")
    public Boolean getRead1() {
        return isRead1;
    }

    @PropertyName("isRead1")
    public void setRead1(Boolean isRead1) {
        this.isRead1 = isRead1;
    }

    @PropertyName("isRead2")
    public Boolean getRead2() {
        return isRead2;
    }

    @PropertyName("isRead2")
    public void setRead2(Boolean isRead2) {
        this.isRead2 = isRead2;
    }
}
