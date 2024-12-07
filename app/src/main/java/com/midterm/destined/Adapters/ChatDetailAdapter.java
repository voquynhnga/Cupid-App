package com.midterm.destined.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.midterm.destined.Models.LastMessage;
import com.midterm.destined.R;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Utils.TimeExtensions;

import java.util.ArrayList;

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

        LeftMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message_left);
            timeText = itemView.findViewById(R.id.tv_time_left);
            avatar = itemView.findViewById(R.id.avatar_chat_left);
        }

        void bind(LastMessage message) {
            messageText.setText(message.getContent());
            timeText.setText(message.getTime());
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

        RightMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message_right);
            timeText = itemView.findViewById(R.id.tv_time_right);
        }

        void bind(LastMessage message) {
            messageText.setText(message.getContent());
            timeText.setText(message.getTime());
        }
    }
}
