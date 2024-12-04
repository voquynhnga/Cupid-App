package com.midterm.destined.Views.Chat;

import com.midterm.destined.Models.Message;
import java.util.List;

public interface chatDetailView {
    void displayMessages(List<Message> messages);
    void showError(String error);  // Hiển thị lỗi
    void clearInputField();  // Xóa trường nhập liệu sau khi gửi tin nhắn
}
