package com.midterm.destined.Presenters;

import com.midterm.destined.Models.ChatObject;
import com.midterm.destined.Models.LastMessage;
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
            public void onMessagesLoaded(List<LastMessage> messages) {
                view.displayMessages(messages);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
    }

    public void sendMessage(String chatId, LastMessage message) {
        repository.sendMessage(chatId, message, new ChatRepository.SendMessageCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String error) {
                view.showError(error);
            }
        });
    }
}

