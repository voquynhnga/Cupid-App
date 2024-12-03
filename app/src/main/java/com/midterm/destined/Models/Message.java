package com.midterm.destined.Models;

public class Message {

    private String sender;
    private String content;
    private String time;
    private String chatId;

    public Message(){}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Message(String sender, String content, String time, String chatId) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.chatId = chatId;

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
