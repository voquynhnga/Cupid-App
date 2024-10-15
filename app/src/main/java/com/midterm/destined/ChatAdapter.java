package com.midterm.destined;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

//public class ChatAdapter extends BaseAdapter {
//
//    private Context context;
//    private ArrayList<Message> messages;
//
//    // Constructor
//    public ChatAdapter(Context context, ArrayList<Message> messages) {
//        this.context = context;
//        this.messages = messages;
//    }
//
//    @Override
//    public int getCount() {
//        return messages.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return messages.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // Sử dụng ViewHolder pattern để tối ưu hiệu năng
//        ViewHolder holder;
//
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
//            holder = new ViewHolder();
//
//            holder.tvSender = convertView.findViewById(R.id.tv_sender);
//            holder.tvContent = convertView.findViewById(R.id.tv_content);
//
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        // Thiết lập dữ liệu cho từng dòng tin nhắn
//        Message message = messages.get(position);
//        holder.tvSender.setText(message.getSender());
//        holder.tvContent.setText(message.getContent());
//
//        return convertView;
//    }
//
//    // ViewHolder pattern để tối ưu hiệu năng
//    private static class ViewHolder {
//        TextView tvSender;
//        TextView tvContent;
//    }
//}

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LEFT = 1;
    private static final int TYPE_RIGHT = 2;
    private List<Message> messages;
    private OnMessageClickListener listener;

    public interface OnMessageClickListener {
        void onMessageClick(Message message);
    }


    public ChatAdapter(List<Message> messages, OnMessageClickListener listener) {
        this.messages = messages;
        this.listener = listener;

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.isSender() ? TYPE_RIGHT : TYPE_LEFT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LEFT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new LeftMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new RightMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof LeftMessageViewHolder) {
            ((LeftMessageViewHolder) holder).bind(message);
        } else {
            ((RightMessageViewHolder) holder).bind(message);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMessageClick(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class LeftMessageViewHolder extends RecyclerView.ViewHolder {
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

    static class RightMessageViewHolder extends RecyclerView.ViewHolder {
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
