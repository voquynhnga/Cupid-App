package com.midterm.destined.chat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.R;
import com.midterm.destined.card.Card;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private ArrayList<ChatObject> chatObjects;
    private OnMessageClickListener listener;
    public String senderId;


    public ChatAdapter(ArrayList<ChatObject> chatObjects, OnMessageClickListener listener) {
        this.chatObjects = chatObjects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatObject chatObject = chatObjects.get(position);
        if (chatObject.getUser1().equals(Card.fetchCurrentUserID())) {
            senderId = chatObject.getUser2();


        } else {
            senderId = chatObject.getUser1();
        }

        fetchSenderName(senderId, holder.tvSender);
        fetchSenderAvatar(senderId, holder.avatarChat);


        holder.tvContent.setText(chatObject.getLastMessage());

        holder.itemView.setOnClickListener(v -> listener.onMessageClick(chatObject));
    }

    public void fetchSenderAvatar(String userId, ImageView avatar) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String profileImageUrl = document.getString("profilePicture");
                    if (profileImageUrl != null) {
                        Glide.with(avatar.getContext())
                                .load(profileImageUrl)
                                .placeholder(R.drawable.avatardefault)
                                .into(avatar);
                    } else {
                        avatar.setImageResource(R.drawable.avatardefault);
                    }
                }
            } else {
                Log.w("DEBUG", "Database Error: ", task.getException());
            }
        });
    }

    public void fetchSenderName(String userId, TextView tvSender) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String fullName = document.getString("fullName");
                    if (fullName != null) {
                        tvSender.setText(fullName);
                    }
                }
            } else {
                Log.w("DEBUG", "Database Error: ", task.getException());
            }
        });
    }


    @Override
    public int getItemCount() {
        return chatObjects.size();
    }

    public void updateMessages(ArrayList<ChatObject> newMessages) {
        chatObjects = newMessages;
        notifyDataSetChanged();
    }

    public interface OnMessageClickListener {
        void onMessageClick(ChatObject chatObject);
    }

    // ViewHolder class
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender;
        TextView tvContent;
        ImageView avatarChat;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tv_sender);
            tvContent = itemView.findViewById(R.id.tv_content);
            avatarChat = itemView.findViewById(R.id.avatar_chat);
        }
    }
}
