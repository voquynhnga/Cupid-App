package com.midterm.destined.Presenters;

import static com.midterm.destined.Models.ChatRepository.getTimestampLastmessage;
import static com.midterm.destined.Utils.TextExtensions.removeVietnameseDiacritics;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.ChatObject;
import com.midterm.destined.Models.Message;
import com.midterm.destined.Views.Chat.ChatContract;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatPresenter implements ChatContract.Presenter {
    private final ChatContract.View view;
    private final FirebaseFirestore db;
    private final DatabaseReference chatsRef;
    private final String currentUserId;
    private final List<ChatObject> chatObjects = new ArrayList<>();
    private final List<String> matchedUserIds = new ArrayList<>();

    public ChatPresenter(ChatContract.View view) {
        this.view = view;
        this.db = FirebaseFirestore.getInstance();
        this.chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        this.currentUserId = Card.fetchCurrentUserID();
    }




    @Override
    public void loadChatFromMatches() {
        db.collection("matches")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String userId1 = document.getString("userId1");
                            String userId2 = document.getString("userId2");

                            if (currentUserId.equals(userId1) || currentUserId.equals(userId2)) {
                                String matchedUserId = currentUserId.equals(userId1) ? userId2 : userId1;
                                if (matchedUserId != null) {
                                    matchedUserIds.add(matchedUserId);
                                }
                            }
                        }
                        loadChatsToApp();
                    } else {
                        view.showError("Failed to load matches: " + task.getException().getMessage());
                    }
                });
    }

    private void loadChatsToApp() {
        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatObjects.clear();
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey();
                    Message lastMessage = chatSnapshot.child("lastMessage").getValue(Message.class);
                    String userId1 = chatSnapshot.child("userId1").getValue(String.class);
                    String userId2 = chatSnapshot.child("userId2").getValue(String.class);

                    if ((userId1 != null && userId1.equals(currentUserId)) || (userId2 != null && userId2.equals(currentUserId))) {
                        loadUserInfo(userId1, (userName1, avatarUser1) -> {
                            loadUserInfo(userId2, (userName2, avatarUser2) -> {
                                chatObjects.add(new ChatObject(
                                        userId1, userId2, lastMessage, chatId, userName1, userName2, avatarUser1, avatarUser2
                                ));
                                chatObjects.sort(Comparator.comparing(chatObject -> getTimestampLastmessage(((ChatObject) chatObject).getLastMessage())).reversed());
                                view.showChats(chatObjects);
                            });
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                view.showError("Failed to load chats: " + error.getMessage());
            }
        });
    }



    private void loadUserInfo(String userId, UserInfoCallback callback) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String userName = document.getString("fullName");
                    String avatarUser = document.getString("profilePicture");
                    callback.onUserInfoLoaded(userName, avatarUser);
                } else {
                    callback.onUserInfoLoaded("Unknown User", "");
                }
            } else {
                callback.onUserInfoLoaded("Unknown User", "");
            }
        });
    }

    @Override
    public void searchMessages(String query) {
        String normalizedQuery = removeVietnameseDiacritics(query.toLowerCase().trim());
        ArrayList<ChatObject> filteredChats = new ArrayList<>();

        for (ChatObject chatObject : chatObjects) {
            String userName1 = removeVietnameseDiacritics(chatObject.getUserName1().toLowerCase());
            String userName2 = removeVietnameseDiacritics(chatObject.getUserName2().toLowerCase());

            if (userName1.contains(normalizedQuery) || userName2.contains(normalizedQuery)) {
                filteredChats.add(chatObject);
            }
        }
        view.updateFilteredChats(filteredChats);
    }



    interface UserInfoCallback {
        void onUserInfoLoaded(String userName, String avatarUser);
    }
}

