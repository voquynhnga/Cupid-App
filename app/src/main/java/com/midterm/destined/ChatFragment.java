//package com.midterm.destined;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.SearchView;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.fragment.NavHostFragment;
//import com.midterm.destined.databinding.FragmentChatBinding;
//
//import java.util.ArrayList;
//
//
//public class ChatFragment extends Fragment {
//
//    private FragmentChatBinding binding;
//
//    @Override
//    public View onCreateView(
//            @NonNull LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState
//    ) {
//
//        binding = FragmentChatBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//
//    }
//
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
////        binding.buttonSecond.setOnClickListener(v ->
////                NavHostFragment.findNavController(ChatFragment.this)
////                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
////        );
//    }
//
//    private void setupConversationList() {
//        ArrayList<String> conversations = new ArrayList<>();
//        conversations.add("Tin nhắn 1");
//        conversations.add("Tin nhắn 2");
//        conversations.add("Tin nhắn 3");
//        conversations.add("Tin nhắn 4");
//
//
//        ChatAdapter adapter = new ChatAdapter(requireContext(), conversations);
//        listView.setAdapter(adapter);
//    }
//
//    private void setupSearchFunctionality() {
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                ChatAdapter adapter = (ChatAdapter) listView.getAdapter();
//                if (adapter != null) {
//                    adapter.getFilter().filter(newText);
//                }
//                return true;
//            }
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//}


package com.midterm.destined;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatFragment extends Fragment implements ChatAdapter.OnMessageClickListener {

    private ImageView btnBack;
    private SearchView searchView;
    private RecyclerView listViewConversations;
    private ChatAdapter chatAdapter;
    private List<Message> messages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        btnBack = view.findViewById(R.id.btn_back);
        searchView = view.findViewById(R.id.searchView);
        listViewConversations = view.findViewById(R.id.listViewConversations);
        listViewConversations.setLayoutManager(new LinearLayoutManager(requireContext()));




        messages = new ArrayList<>();
        messages.add(new Message("Alice", "Hey! How are you?", getTime(),"me"));
        messages.add(new Message("Bob", "I'm good. What about you?", getTime(),"me"));
        messages.add(new Message("Charlie", "Have you completed the assignment?", getTime(),"me"));
        messages.add(new Message("David", "Yes, I submitted it yesterday.", getTime(),"me"));
        messages.add(new Message("Eve", "Let's meet up this weekend!", getTime(),"me"));

        chatAdapter = new ChatAdapter(messages, this);
        listViewConversations.setAdapter(chatAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMessages(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchMessages(newText);
                return true;
            }
        });



        return view;
    }

    private void searchMessages(String query) {
        ArrayList<Message> filteredMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getSender().toLowerCase().contains(query.toLowerCase()) ||
                    message.getContent().toLowerCase().contains(query.toLowerCase())) {
                filteredMessages.add(message);
            }
        }
        chatAdapter = new ChatAdapter(filteredMessages, this);
        listViewConversations.setAdapter(chatAdapter);
    }
    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");  // Định dạng thời gian, ví dụ: 09:25
        return sdf.format(new Date());  // Trả về thời gian hiện tại
    }

    @Override
    public void onMessageClick(Message selectedMessage) {
        Bundle bundle = new Bundle();
        bundle.putString("sender", selectedMessage.getSender());
        bundle.putString("content", selectedMessage.getContent());

        Log.d("ChatFragment", "Item clicked: " + selectedMessage.getSender());

        Navigation.findNavController(requireView()).navigate(R.id.action_chatFragment_to_chatDetailFragment, bundle);
    }
}
