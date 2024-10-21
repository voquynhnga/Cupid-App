package com.midterm.destined.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.destined.R;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LEFT = 1;
    private static final int VIEW_TYPE_RIGHT = 2;
    private ArrayList<Message> messageList;
    private String currentUser;

    public ChatDetailAdapter(ArrayList<Message> messageList) {
        this.messageList = messageList;
//        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSender().equals("me")) {
            return VIEW_TYPE_RIGHT;
        } else {
            return VIEW_TYPE_LEFT;
        }
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
        Message message = messageList.get(position);
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

        LeftMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message_left);
            timeText = itemView.findViewById(R.id.tv_time_left);
        }

        void bind(Message message) {
            messageText.setText(message.getContent());
            timeText.setText(message.getTime());
        }
    }

    class RightMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        RightMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message_right);
            timeText = itemView.findViewById(R.id.tv_time_right);
        }

        void bind(Message message) {
            messageText.setText(message.getContent());
            timeText.setText(message.getTime());
        }
    }

}
