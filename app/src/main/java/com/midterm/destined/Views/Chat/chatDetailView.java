package com.midterm.destined.Views.Chat;

import com.midterm.destined.Models.LastMessage;

import java.util.List;

public interface chatDetailView {
    void displayMessages(List<LastMessage> messages);
    void showError(String error);
    void clearInputField();
}
