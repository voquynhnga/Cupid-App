package com.midterm.destined.Views.Chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.destined.Presenters.ChatPresenter;
import com.midterm.destined.R;
import com.midterm.destined.Models.ChatObject;
import com.midterm.destined.Adapters.ChatAdapter;
import com.midterm.destined.Utils.DB;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment implements ChatContract.View, ChatAdapter.OnMessageClickListener {

    private SearchView searchView;
    private RecyclerView listViewConversations;
    private ChatAdapter chatAdapter;
    private ChatPresenter presenter;
    private ArrayList<ChatObject> chatObjects = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }

        searchView = view.findViewById(R.id.searchView);
        listViewConversations = view.findViewById(R.id.listViewConversations);
        listViewConversations.setLayoutManager(new LinearLayoutManager(requireContext()));

        chatAdapter = new ChatAdapter(chatObjects, this);
        listViewConversations.setAdapter(chatAdapter);

        presenter = new ChatPresenter(this);
        presenter.loadChat();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.searchMessages(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.searchMessages(newText);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String chatId = getArguments().getString("chatId");
            String userId = getArguments().getString("userId");
            String userName = getArguments().getString("userName");

            Bundle bundle = new Bundle();
            bundle.putString("chatId", chatId);
            bundle.putString("userId", userId);
            bundle.putString("userName", userName);
            Navigation.findNavController(requireView()).navigate(R.id.action_chatFragment_to_chatDetailFragment, bundle);
        }
    }

    @Override
    public void showChats(List<ChatObject> chatObjects) {
        this.chatObjects.clear();
        this.chatObjects.addAll(chatObjects);
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Log.e("ChatFragment", message);
    }

    @Override
    public void updateFilteredChats(ArrayList<ChatObject> filteredChats) {
        chatAdapter.updateMessages(filteredChats);
    }


    @Override
    public void onMessageClick(ChatObject selectedChat) {
        Bundle bundle = new Bundle();
        bundle.putString("chatId", selectedChat.getChatId());
        if (selectedChat.getUser1().equals(DB.getCurrentUser().getUid())) {
            bundle.putString("userId", selectedChat.getUser2());
            bundle.putString("userName", selectedChat.getUserName2());
        } else {
            bundle.putString("userId", selectedChat.getUser1());
            bundle.putString("userName", selectedChat.getUserName1());
        }
        Navigation.findNavController(requireView()).navigate(R.id.action_chatFragment_to_chatDetailFragment, bundle);
    }
}
