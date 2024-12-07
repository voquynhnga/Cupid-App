package com.midterm.destined.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midterm.destined.R;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.ChatObject;
import com.midterm.destined.Utils.ChatDiffCallback;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Utils.TimeExtensions;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private ArrayList<ChatObject> chatObjects;
    private OnMessageClickListener listener;



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

        String senderName, senderAvatar;
        if (chatObject.getUser1().equals(DB.getCurrentUser().getUid())) {
            senderName = chatObject.getUserName2();
            senderAvatar = chatObject.getAvatarUser2();
        } else {
            senderName = chatObject.getUserName1();
            senderAvatar = chatObject.getAvatarUser1();
        }

        holder.tvSender.setText(senderName);
        if (senderAvatar != null) {
            Glide.with(holder.avatarChat.getContext())
                    .load(senderAvatar)
                    .placeholder(R.drawable.avatardefault)
                    .into(holder.avatarChat);
        }

        holder.tvContent.setText((chatObject.getLastMessage()).getContent());

        holder.itemView.setOnClickListener(v -> listener.onMessageClick(chatObject));

        TimeExtensions.setChatTimestamp(holder.tvTimestamp, chatObject.getLastMessage().getTime());
    }




    @Override
    public int getItemCount() {
        return chatObjects.size();
    }


    public interface OnMessageClickListener {
        void onMessageClick(ChatObject chatObject);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender;
        TextView tvContent;
        ImageView avatarChat;
        TextView tvTimestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tv_sender);
            tvContent = itemView.findViewById(R.id.tv_content);
            avatarChat = itemView.findViewById(R.id.avatar_chat);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }
    }
    public void updateMessages(ArrayList<ChatObject> newMessages) {
        ChatDiffCallback diffCallback = new ChatDiffCallback(this.chatObjects, newMessages);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.chatObjects.clear();
        this.chatObjects.addAll(newMessages);

        diffResult.dispatchUpdatesTo(this);
    }
}
