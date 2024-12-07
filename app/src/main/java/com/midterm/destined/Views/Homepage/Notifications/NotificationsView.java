package com.midterm.destined.Views.Homepage.Notifications;

import com.midterm.destined.Models.Notification;

import java.util.List;

public interface NotificationsView {
    void updateNotifications(List<Notification> notifications);
    void showError(String message);
}
