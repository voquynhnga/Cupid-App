package com.midterm.destined.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.Models.ChatObject;
import com.midterm.destined.Models.Notification;
import com.midterm.destined.Models.OnChatIdCheckListener;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.R;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Utils.TimeExtensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private final Context context;
    private final List<Notification> notifications;
    private  UserReal user;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;

    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.notification_item, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        TimeExtensions.setChatTimestamp(holder.timestamp,notification.getTimestamp());
        holder.content.setText(notification.getContent());
        holder.imageNotification.setImageResource(notification.getImageResource());


        user = notification.getSender();

        if (notification.getSender() != null) {
            holder.itemView.setOnClickListener(v -> {
                ChatObject.checkChatId(user.getUid(), new OnChatIdCheckListener() {
                    @Override
                    public void onChatIdFound(String chatId) {
                        Bundle bundle = new Bundle();
                        bundle.putString("chatId", chatId);
                        bundle.putString("userId", (notification.getSender()).getUid());
                        bundle.putString("userName", user.getFullName());
                        ChatObject.checkChatUserId(chatId);

                        NavController navController = Navigation.findNavController((Activity) v.getContext(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.action_global_ChatDetailFragment, bundle);
                    }

                    @Override
                    public void onChatIdNotFound() {
                        Log.e("checkChatId", "Chat ID not found.");
                    }
                });
            });
        }

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateData(List<Notification> newNotifications) {
        this.notifications.clear();
        TimeExtensions.sortNotificationsByTimestampDescending(newNotifications);
        this.notifications.addAll(newNotifications);
        notifyDataSetChanged();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        public final TextView timestamp;
        public final TextView content;
        public final ImageView imageNotification;


        public NotificationViewHolder(View view) {
            super(view);

            timestamp = itemView.findViewById(R.id.tv_notiTimestamp);
            content = itemView.findViewById(R.id.tv_notiContent);
            imageNotification = itemView.findViewById(R.id.im_Notification);

        }


    }

}
