package com.midterm.destined.Models;

public class ChatObject {
    private String user1;
    private String user2;

    private String lastMessage;
    private String chatId;
    private String userName1;
    private String userName2;
    private String avatarUser1;
    private String avatarUser2;


    public ChatObject(){}

    public ChatObject(String user1, String user2, String lastMessage, String chatId, String userName1, String userName2, String avatarUser1, String avatarUser2) {
        this.user1 = user1;
        this.user2 = user2;
        this.lastMessage = lastMessage;
        this.chatId = chatId;
        this.userName1 = userName1;
        this.userName2 = userName2;
        this.avatarUser1 = avatarUser1;
        this.avatarUser2 = avatarUser2;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }


    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserName1() {
        return userName1;
    }

    public void setUserName1(String userName1) {
        this.userName1 = userName1;
    }

    public String getUserName2() {
        return userName2;
    }

    public void setUserName2(String userName2) {
        this.userName2 = userName2;
    }

    public String getAvatarUser1() {
        return avatarUser1;
    }

    public void setAvatarUser1(String avatarUser1) {
        this.avatarUser1 = avatarUser1;
    }

    public String getAvatarUser2() {
        return avatarUser2;
    }

    public void setAvatarUser2(String avatarUser2) {
        this.avatarUser2 = avatarUser2;
    }
}
