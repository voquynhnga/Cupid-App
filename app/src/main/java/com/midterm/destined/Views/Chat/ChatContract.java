package com.midterm.destined.Views.Chat;

import com.midterm.destined.Models.ChatObject;

import java.util.ArrayList;
import java.util.List;

public interface ChatContract {
    interface View {
        void showChats(List<ChatObject> chatObjects);
        void showError(String message);
        void updateFilteredChats(ArrayList<ChatObject> filteredChats);
    }

    interface Presenter {
        void loadChatFromMatches();
        void searchMessages(String query);
    }
}
