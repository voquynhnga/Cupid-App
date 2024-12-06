package com.midterm.destined.Models;

public class Message {

    private String sender;
    private String content;
    private String time;

    public Message(){}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public Message(String sender, String content, String time) {
        this.sender = sender;
        this.content = content;
        this.time = time;

    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }


    public void setContent(String content) {
        this.content = content;
    }




    public String getContent() {
        return content;
    }


}
