package com.midterm.destined.Presenters;

import com.midterm.destined.Models.Message;
import com.midterm.destined.Models.ChatRepository;
import com.midterm.destined.Views.Chat.chatDetailView;

import java.util.List;

public class ChatDetailPresenter {

    private final chatDetailView view;
    private final ChatRepository repository;

    public ChatDetailPresenter(chatDetailView view) {
        this.view = view;
        this.repository = new ChatRepository();
    }

    public void loadMessages(String chatId) {
        repository.loadMessages(chatId, new ChatRepository.ChatCallback() {
            @Override
            public void onMessagesLoaded(List<Message> messages) {
                view.displayMessages(messages);
            }

            @Override
            public void onError(String error) {
                view.showError(error);  // Hiển thị lỗi nếu có
            }
        });
    }

    // Gửi tin nhắn lên repository và thông báo kết quả về view
    public void sendMessage(String chatId, Message message) {
        repository.sendMessage(chatId, message, new ChatRepository.SendMessageCallback() {
            @Override
            public void onSuccess() {
                view.clearInputField();  // Làm sạch trường nhập liệu sau khi gửi
            }

            @Override
            public void onFailure(String error) {
                view.showError(error);  // Hiển thị lỗi nếu có
            }
        });
    }
}

