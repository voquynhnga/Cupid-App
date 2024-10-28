package com.midterm.destined.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midterm.destined.R;
import com.midterm.destined.card.Card;
import com.midterm.destined.card.CardFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment implements ChatAdapter.OnMessageClickListener {

    private SearchView searchView;
    private RecyclerView listViewConversations;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatObject> chatObjects;
    private String chatId;
    private String userId1;
    private String userId2;
    private String lastMessage;
    private FirebaseFirestore db;
    private String userName;
    private String avatarUser;
    List<String> matchedUserIds = new ArrayList<>();
    private String currentUser = Card.fetchCurrentUserID();
    private DatabaseReference chatsRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }

        searchView = view.findViewById(R.id.searchView);
        listViewConversations = view.findViewById(R.id.listViewConversations);
        listViewConversations.setLayoutManager(new LinearLayoutManager(requireContext()));

        chatObjects = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatObjects, this);
        listViewConversations.setAdapter(chatAdapter);
        db = FirebaseFirestore.getInstance();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMessages(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchMessages(newText);
                return true;
            }
        });

        loadChatfromMatches();

        return view;
    }

    private void searchMessages(String query) {
        ArrayList<ChatObject> filteredMessages = new ArrayList<>();
        for (ChatObject chatObject : chatObjects) {
            if (chatObject.getUserName1().toLowerCase().contains(query.toLowerCase().trim()) || chatObject.getUserName2().toLowerCase().contains(query.toLowerCase().trim())) {
                filteredMessages.add(chatObject);
            }
        }
        chatAdapter.updateMessages(filteredMessages);
    }

    @Override
    public void onMessageClick(ChatObject selectedChat) {
        Bundle bundle = new Bundle();
        if (selectedChat.getUser1().equals(currentUser)) {
            bundle.putString("chatId", selectedChat.getChatId());
            bundle.putString("userId", selectedChat.getUser2());
            bundle.putString("userName", selectedChat.getUserName2());
        } else {
            bundle.putString("chatId", selectedChat.getChatId());
            bundle.putString("userId", selectedChat.getUser1());
            bundle.putString("userName", selectedChat.getUserName1());

        }
        Navigation.findNavController(requireView()).navigate(R.id.action_chatFragment_to_chatDetailFragment, bundle);
    }

    public void loadChatfromMatches() {

        db.collection("matches")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DEBUG", "ok");
                        for (DocumentSnapshot document : task.getResult()) {
                            userId1 = document.getString("userId1");
                            userId2 = document.getString("userId2");
                            Log.d("DEBUG", userId1 + " " + userId2);
                            Log.d("DEBUG", currentUser);

                            if (currentUser.equals(userId1) || currentUser.equals(userId2)) {
                                if (currentUser.equals(userId1) && userId2 != null) {
                                    matchedUserIds.add(userId2);
                                }
                                if (currentUser.equals(userId2) && userId1 != null) {
                                    matchedUserIds.add(userId1);
                                }
                            }
                        }

                        chatsRef = FirebaseDatabase.getInstance().getReference("chats");
                        for (String matchedUserId : matchedUserIds) {
                            Log.d("DEBUG", "matched " + matchedUserId);
                            String chatId = generateChatId(currentUser, matchedUserId);
                            chatsRef.child(chatId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        Map<String, Object> chatData = new HashMap<>();
                                        chatData.put("userID1", currentUser);
                                        chatData.put("userID2", matchedUserId);
                                        chatData.put("lastMessage", "Let's start chat with " + matchedUserId + "!");

                                        chatsRef.child(chatId).setValue(chatData)
                                                .addOnSuccessListener(aVoid -> Log.d("Chat", "Added chat between " + currentUser + " and " + matchedUserId))
                                                .addOnFailureListener(e -> Log.e("Chat", "Failed to add chat", e));
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.w("ChatFragment1", "loadChatfromMatches:onCancelled", error.toException());
                                }
                            });

                        }
                        loadChatsToApp(currentUser);

                    } else {
                        Log.e("Firestore", "Error getting matches: ", task.getException());
                    }
                });
    }

    private void loadChatsToApp(String currentUserID) {
        chatObjects.clear();
        chatsRef.addValueEventListener(new ValueEventListener() {
            private String userName1;
            private String userName2;
            private String avatarUser1;
            private String avatarUser2;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatObjects.clear();
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    chatId = chatSnapshot.getKey();
                    lastMessage = chatSnapshot.child("lastMessage").getValue(String.class);
                    userId1 = chatSnapshot.child("userID1").getValue(String.class);
                    userId2 = chatSnapshot.child("userID2").getValue(String.class);

                    if ((userId1 != null && userId1.equals(currentUserID)) || (userId2 != null && userId2.equals(currentUserID))) {
                        if (chatId != null && userId1 != null && userId2 != null) {
                            loadUserInfo(userId1, (userName1, avatarUser1) -> {
                                this.userName1 = userName1;
                                this.avatarUser1 = avatarUser1;

                                loadUserInfo(userId2, (userName2, avatarUser2) -> {
                                    this.userName2 = userName2;
                                    this.avatarUser2 = avatarUser2;

                                    ChatObject chatObject = new ChatObject(userId1, userId2, lastMessage, chatId, userName1, userName2, avatarUser1, avatarUser2);
                                    chatObjects.add(chatObject);
                                    chatAdapter.notifyDataSetChanged();
                                });
                            });
                        } else {
                            Log.w("ChatFragment", "Chat data is missing some values, skipping this chat entry.");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ChatFragment", "loadChatsToApp:onCancelled", error.toException());
            }
        });
    }


    private String generateChatId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }


    private void loadUserInfo(String userId, UserInfoCallback callback) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userName = document.getString("fullName");
                     avatarUser = document.getString("profilePicture");
                    callback.onUserInfoLoaded(userName, avatarUser);
                } else {
                    Log.w("ChatFragment", "User không tồn tại trong Firestore.");
                    callback.onUserInfoLoaded("Unknown User", ""); // Giá trị mặc định
                }
            } else {
                Log.w("ChatFragment", "Error getting user info", task.getException());
                callback.onUserInfoLoaded("Unknown User", ""); // Giá trị mặc định
            }
        });
    }

    interface UserInfoCallback {
        void onUserInfoLoaded(String userName, String avatarUser);
    }
}
