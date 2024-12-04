package com.midterm.destined.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChatObject {
    private String user1;
    private String user2;

    private Message lastMessage;
    private String chatId;
    private String userName1;
    private String userName2;
    private String avatarUser1;
    private String avatarUser2;




    public ChatObject(){}

    public ChatObject(String user1, String user2, Message lastMessage, String chatId, String userName1, String userName2, String avatarUser1, String avatarUser2) {
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


    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatObject that = (ChatObject) o;
        return chatId.equals(that.chatId) &&
                lastMessage.equals(that.lastMessage) &&
                user1.equals(that.user1) &&
                user2.equals(that.user2);
    }

    public static void checkAndAddChatList(String userID1, String userID2, String timestamp) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");
        String chatID = userID1.compareTo(userID2) < 0 ? userID1 + "_" + userID2 : userID2 + "_" + userID1;

        chatRef.child(chatID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Map<String, Object> chatData = new HashMap<>();
                    chatData.put("userId1", userID1);
                    chatData.put("userId2", userID2);
                    Message lastMessage = new Message(userID1, "Let's start chat!", timestamp);
                    chatData.put("lastMessage", lastMessage);


                    chatRef.child(chatID).setValue(chatData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("RealtimeDB", "Chat created successfully between " + userID1 + " and " + userID2);
                        } else {
                            Log.e("RealtimeDB", "Failed to create chat", task.getException());
                        }
                    });
                } else {
                    Log.d("RealtimeDB", "Chat between " + userID1 + " and " + userID2 + " already exists, skipping creation.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RealtimeDB", "Failed to check for existing chat", error.toException());
            }
        });
    }

}
