package com.midterm.destined.chat;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.destined.R;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LEFT = 1;
    private static final int TYPE_RIGHT = 2;
    private List<ChatObject> chatList;
    private OnMessageClickListener listener;


    public ChatAdapter(List<ChatObject> chatList, OnMessageClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolder rcv = new ChatViewHolder(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.mMessage.setText(chatList.get(position).getMessage());
        if(chatList.get(position).getCurrentUser()){
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(20);
            shape.setCornerRadii(new float[]{25,25,3,25,25,25,25,25});
            shape.setColor(Color.parseColor("#5fc9f8"));
            holder.mContainer.setGravity(Gravity.END);
            holder.mMessage.setBackground(shape);
            holder.mMessage.setText(Color.parseColor("#FFFFFF"));
        }
        else{
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(20);
            shape.setCornerRadii(new float[]{25,25,3,25,25,25,25,25});
            shape.setColor(Color.parseColor("#53d769"));
            holder.mContainer.setGravity(Gravity.START);
            holder.mMessage.setBackground(shape);
            holder.mMessage.setText(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }

    public interface OnMessageClickListener {
        void onMessageClick(ChatObject message);
    }






//    @Override
//    public int getItemViewType(int position) {
//        ChatObject chatList = chatList.get(position);
//        return message.isSender() ? TYPE_RIGHT : TYPE_LEFT;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == TYPE_LEFT) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
//            return new LeftMessageViewHolder(view);
//        } else {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
//            return new RightMessageViewHolder(view);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Message message = messages.get(position);
//        if (holder instanceof LeftMessageViewHolder) {
//            ((LeftMessageViewHolder) holder).bind(message);
//        } else {
//            ((RightMessageViewHolder) holder).bind(message);
//        }
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onMessageClick(message);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return messages.size();
//    }
//
//    static class LeftMessageViewHolder extends RecyclerView.ViewHolder {
//        TextView messageText, timeText;
//
//        LeftMessageViewHolder(View itemView) {
//            super(itemView);
//            messageText = itemView.findViewById(R.id.tv_message_left);
//            timeText = itemView.findViewById(R.id.tv_time_left);
//        }
//
//        void bind(Message message) {
//            messageText.setText(message.getContent());
//            timeText.setText(message.getTime());
//        }
//    }
//
//    static class RightMessageViewHolder extends RecyclerView.ViewHolder {
//        TextView messageText, timeText;
//
//        RightMessageViewHolder(View itemView) {
//            super(itemView);
//            messageText = itemView.findViewById(R.id.tv_message_right);
//            timeText = itemView.findViewById(R.id.tv_time_right);
//        }
//
//        void bind(Message message) {
//            messageText.setText(message.getContent());
//            timeText.setText(message.getTime());
//        }
//    }
//
//    public void updateData(List<Message> newMessages) {
//        this.messages.clear();
//        this.messages.addAll(newMessages);
//        notifyDataSetChanged();
//    }
//
}