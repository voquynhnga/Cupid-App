package com.midterm.destined.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.destined.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private ArrayList<Message> messages;
    private OnMessageClickListener listener;

    public ChatAdapter(ArrayList<Message> messages, OnMessageClickListener listener) {
        this.messages = messages;
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
        Message message = messages.get(position);
        holder.tvSender.setText(message.getSender());
        holder.tvContent.setText(message.getContent());

        holder.itemView.setOnClickListener(v -> listener.onMessageClick(message));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(ArrayList<Message> newMessages) {
        messages = newMessages;
        notifyDataSetChanged();
    }

    public interface OnMessageClickListener {
        void onMessageClick(Message message);
    }

    // ViewHolder class
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender;
        TextView tvContent;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tv_sender);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}
