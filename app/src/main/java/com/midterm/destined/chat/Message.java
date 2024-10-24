package com.midterm.destined.chat;

public class Message {

    private String sender;
    private String content;
    private String time;
    private String chatId;


    public Message(String sender, String content, String time, String chatId) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.chatId = chatId;

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



    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;  // Getter for time
    }

}
