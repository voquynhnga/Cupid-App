package com.midterm.destined;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Message> messages;

    // Constructor
    public ChatAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Sử dụng ViewHolder pattern để tối ưu hiệu năng
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
            holder = new ViewHolder();

            holder.tvSender = convertView.findViewById(R.id.tv_sender);
            holder.tvContent = convertView.findViewById(R.id.tv_content);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Thiết lập dữ liệu cho từng dòng tin nhắn
        Message message = messages.get(position);
        holder.tvSender.setText(message.getSender());
        holder.tvContent.setText(message.getContent());

        return convertView;
    }

    // ViewHolder pattern để tối ưu hiệu năng
    private static class ViewHolder {
        TextView tvSender;
        TextView tvContent;
    }
}