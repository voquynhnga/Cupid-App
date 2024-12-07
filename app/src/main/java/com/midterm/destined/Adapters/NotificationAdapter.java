package com.midterm.destined.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.Models.Notification;
import com.midterm.destined.R;
import com.midterm.destined.Utils.DB;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private final Context context;
    private final List<Notification> notifications;

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
        holder.timestamp.setText(notification.getTimestamp());
        holder.content.setText(notification.getContent());
        holder.imageNotification.setImageResource(notification.getImageResource());

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateData(List<Notification> newNotifications) {
        this.notifications.clear();
        this.notifications.addAll(newNotifications);
        notifyDataSetChanged();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        public final TextView timestamp;
        public final TextView content;
        public final ImageView imageNotification;

        public NotificationViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            timestamp = itemView.findViewById(R.id.tv_notiTimestamp);
            content = itemView.findViewById(R.id.tv_notiContent);
            imageNotification = itemView.findViewById(R.id.im_Notification);

        }


    }

}
