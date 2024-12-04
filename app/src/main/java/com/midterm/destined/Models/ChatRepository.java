package com.midterm.destined.Models;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatRepository {

    private final DatabaseReference chatRef;

    public ChatRepository() {
        chatRef = FirebaseDatabase.getInstance().getReference("chats");
    }

    public interface ChatCallback {
        void onMessagesLoaded(List<Message> messages);
        void onError(String error);
    }

    public interface SendMessageCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void loadMessages(String chatId, ChatCallback callback) {
        chatRef.child(chatId).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
                callback.onMessagesLoaded(messages);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }


    public void sendMessage(String chatId, Message newMessage, SendMessageCallback sendMessageCallback) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);

        chatRef.child("messages").push().setValue(newMessage)
                .addOnSuccessListener(aVoid -> {
                    chatRef.child("lastMessage").setValue(newMessage)
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d("RealtimeDB", "Last message updated successfully");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("RealtimeDB", "Failed to update last message", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("RealtimeDB", "Failed to send message", e);
                });
    }


    public static long getTimestampLastmessage(Message lastMessage) {
        String timeString = lastMessage.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy HH:mm");

        try {
            Date date = sdf.parse(timeString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}

