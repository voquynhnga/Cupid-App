package com.midterm.destined.Views.Chat;

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
import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.ChatObject;
import com.midterm.destined.Adapters.ChatAdapter;

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
        String normalizedQuery = removeVietnameseDiacritics(query.toLowerCase().trim());
        ArrayList<ChatObject> filteredMessages = new ArrayList<>();

        for (ChatObject chatObject : chatObjects) {
            String userName1 = removeVietnameseDiacritics(chatObject.getUserName1().toLowerCase());
            String userName2 = removeVietnameseDiacritics(chatObject.getUserName2().toLowerCase());

            if (userName1.contains(normalizedQuery) || userName2.contains(normalizedQuery)) {
                filteredMessages.add(chatObject);
            }
        }
        chatAdapter.updateMessages(filteredMessages);
    }

    private String removeVietnameseDiacritics(String input) {
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
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
                        Log.d("DEBUG", "Matches retrieved successfully");

                        for (DocumentSnapshot document : task.getResult()) {
                            userId1 = document.getString("userId1");
                            userId2 = document.getString("userId2");

                            if (currentUser.equals(userId1) || currentUser.equals(userId2)) {
                                String matchedUserId = currentUser.equals(userId1) ? userId2 : userId1;
                                if (matchedUserId != null) {
                                    matchedUserIds.add(matchedUserId);
                                }
                            }
                        }

                        chatsRef = FirebaseDatabase.getInstance().getReference("chats");

                        for (String matchedUserId : matchedUserIds) {
                            String uniqueChatId = currentUser.compareTo(matchedUserId) < 0 ? currentUser + "_" + matchedUserId : matchedUserId + "_" + currentUser;

                            DatabaseReference chatReference = chatsRef.child(uniqueChatId);
                            chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        Map<String, Object> chatData = new HashMap<>();
                                        chatData.put("userID1", currentUser);
                                        chatData.put("userID2", matchedUserId);
                                        chatData.put("lastMessage", "Let's start chat!");
                                        chatReference.setValue(chatData)
                                                .addOnSuccessListener(aVoid -> Log.d("Chat", "Chat created between " + currentUser + " and " + matchedUserId))
                                                .addOnFailureListener(e -> Log.e("Chat", "Failed to create chat", e));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.w("ChatFragment", "loadChatfromMatches:onCancelled", error.toException());
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

        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatObjects.clear();
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey();
                    String lastMessage = chatSnapshot.child("lastMessage").getValue(String.class);
                    String userId1 = chatSnapshot.child("userID1").getValue(String.class);
                    String userId2 = chatSnapshot.child("userID2").getValue(String.class);

                    if ((userId1 != null && userId1.equals(currentUserID)) || (userId2 != null && userId2.equals(currentUserID))) {
                        if (chatId != null && userId1 != null && userId2 != null) {
                            loadUserInfo(userId1, (userName1, avatarUser1) -> {
                                loadUserInfo(userId2, (userName2, avatarUser2) -> {
                                    ChatObject chatObject = new ChatObject(
                                            userId1, userId2, lastMessage, chatId, userName1, userName2, avatarUser1, avatarUser2
                                    );
                                    chatObjects.add(chatObject);
                                    Log.d("DEBUG", "chato" + chatObject);
                                    chatAdapter.notifyDataSetChanged();

                                });
                            });
                        } else {
                            Log.w("ChatFragment", "Some chat data is missing, skipping entry.");
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
