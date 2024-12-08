package com.midterm.destined.Adapters;

import static java.lang.Boolean.TRUE;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.midterm.destined.Models.ChatObject;
import com.midterm.destined.Models.ChatRepository;
import com.midterm.destined.Models.LastMessage;
import com.midterm.destined.R;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Utils.TimeExtensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LEFT = 1;
    private static final int VIEW_TYPE_RIGHT = 2;
    private ArrayList<LastMessage> messageList;
    private FirebaseUser currentUser;

    public ChatDetailAdapter(ArrayList<LastMessage> messageList, FirebaseUser currentUser) {
        this.messageList = messageList;
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        LastMessage message = messageList.get(position);
        return message.getSender().equals(currentUser.getUid()) ? VIEW_TYPE_RIGHT : VIEW_TYPE_LEFT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LEFT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new LeftMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new RightMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LastMessage message = messageList.get(position);
        if (holder instanceof LeftMessageViewHolder) {
            ((LeftMessageViewHolder) holder).bind(message);
        } else {
            ((RightMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class LeftMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView avatar;
        ImageView isSeen;

        LeftMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message_left);
            timeText = itemView.findViewById(R.id.tv_time_left);
            avatar = itemView.findViewById(R.id.avatar_chat_left);
            isSeen = itemView.findViewById(R.id.isSeenLeft);
        }

        void bind(LastMessage message) {
            messageText.setText(message.getContent());
            timeText.setText(message.getTime());


            String senderId = message.getSender();
            String currentUserId = DB.getCurrentUser().getUid();
            String chatId = senderId.compareTo(currentUserId) < 0
                    ? currentUserId + "_" + senderId
                    : senderId + "_" + currentUserId;

            checkMessageUserId(senderId,chatId, new MessageCheckCallback() {
                @Override
                public void onUserCheck(int userType) {
                    DatabaseReference chatRef = DB.getChatsRef().child(chatId).child("messages");

                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (userType == 1) {
                                    Boolean isRead1 = snapshot.child("isRead1").getValue(Boolean.class);
                                    isSeen.setImageResource((isRead1 != null && isRead1) ? R.drawable.seen : R.drawable.sent);
                                } else if (userType == 2) {
                                    Boolean isRead2 = snapshot.child("isRead2").getValue(Boolean.class);
                                    isSeen.setImageResource((isRead2 != null && isRead2) ? R.drawable.seen : R.drawable.sent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("ChatDetail", "Error fetching isRead status: " + error.getMessage());
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e("ChatDetail", "Error: " + error);
                }
            });


            DB.getUserDocument(message.getSender())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String avatarUrl = documentSnapshot.getString("profilePicture");
                            Glide.with(avatar.getContext())
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.avatardefault)
                                    .into(avatar);
                        }
                    })
                    .addOnFailureListener(e -> {
                    });
        }
    }



    class RightMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView isSeen;

        RightMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message_right);
            timeText = itemView.findViewById(R.id.tv_time_right);
            isSeen = itemView.findViewById(R.id.isSeenRight);
        }

        void bind(LastMessage message) {
            messageText.setText(message.getContent());
            timeText.setText(message.getTime());

            String currentUserId = DB.getCurrentUser().getUid();
            DatabaseReference chatsRef = DB.getChatsRef();

            chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String chatId = null;
                        String messageId = null;

                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            DataSnapshot messagesSnapshot = chatSnapshot.child("messages");

                            for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                                String content = messageSnapshot.child("content").getValue(String.class);
                                String time = messageSnapshot.child("time").getValue(String.class);


                                if (message.getContent().equals(content) && message.getTime().equals(time)) {
                                    chatId = chatSnapshot.getKey();
                                    messageId = messageSnapshot.getKey();
                                    break;
                                }
                            }

                            if (chatId != null && messageId != null) {
                                break;
                            }
                        }

                        if (chatId != null && messageId != null) {
                            DatabaseReference chatRef = chatsRef.child(chatId).child("messages").child(messageId);

                            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot chatSnapshot) {
                                    if (chatSnapshot.exists()) {
                                        int userType = currentUserId.equals(chatSnapshot.child("userId1").getValue(String.class)) ? 1 : 2;

                                        if (userType == 1) {
                                            Boolean isRead1 = chatSnapshot.child("isRead1").getValue(Boolean.class);
                                            isSeen.setImageResource((isRead1 != null && isRead1) ? R.drawable.seen : R.drawable.sent);
                                        } else {
                                            Boolean isRead2 = chatSnapshot.child("isRead2").getValue(Boolean.class);
                                            isSeen.setImageResource((isRead2 != null && isRead2) ? R.drawable.seen : R.drawable.sent);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("ChatDetail", "Error fetching chat details: " + error.getMessage());
                                }
                            });
                        } else {
                            Log.e("ChatDetail", "Message not found in any chat");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ChatDetail", "Error fetching chats: " + error.getMessage());
                }
            });
        }



    }


    public interface MessageCheckCallback {
        void onUserCheck(int userType);
        void onError(String error);
    }

    public void checkMessageUserId(String senderId, String chatId, MessageCheckCallback callback) {

        DatabaseReference chatRef = DB.getChatsRef().child(chatId);
        Log.d("ChatDetail", "Chat ID: " + chatId + ", Sender ID: " + senderId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String user1Id = snapshot.child("userId1").getValue(String.class);
                    String user2Id = snapshot.child("userId2").getValue(String.class);

                    if (senderId.equals(user1Id)) {
                        Log.d("ChatDetail", "Sender is User1");
                        callback.onUserCheck(1);
                    } else if (senderId.equals(user2Id)) {
                        Log.d("ChatDetail", "Sender is User2");
                        callback.onUserCheck(2);
                    } else {
                        Log.d("ChatDetail", "Sender is neither User1 nor User2");
                        callback.onUserCheck(0);
                    }
                } else {
                    Log.e("ChatDetail", "Chat data not found");
                    callback.onError("Chat data not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatDetail", "Error fetching chat data: " + error.getMessage());
                callback.onError("Error fetching chat data: " + error.getMessage());
            }
        });
    }


}
