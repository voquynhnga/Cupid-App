package com.midterm.destined.chat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.destined.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatDetailFragment extends Fragment {

    private TextView senderTextView;
    private TextView contentTextView;
    private ImageView btnBack;
    private RecyclerView recyclerView;
    private ArrayList<Message> messageList;
    private ImageView btnSend;
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }

        senderTextView = view.findViewById(R.id.tv_nameChat);
        btnBack = view.findViewById(R.id.btn_back);
        btnSend = view.findViewById(R.id.btn_send);
        editText = view.findViewById(R.id.et_message);
        recyclerView = view.findViewById(R.id.rv_chat_messages);



        messageList = new ArrayList<>();
        messageList.add(new Message("me", "Hello!", "9:25 AM"));
        messageList.add(new Message("2", "How are you?", "10 AM"));
        messageList.add(new Message("me", "I'm fine, thanks!", "2 AM"));
        ChatDetailAdapter adapter = new ChatDetailAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        if (getArguments() != null) {
            String sender = getArguments().getString("sender");
            String content = getArguments().getString("content");

            senderTextView.setText(sender);
            //contentTextView.setText(content);
            messageList.add(new Message(sender,content,"6:40 AM"));
        }


        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_chatDetailFragment_to_chatFragment);

        });

        btnSend.setOnClickListener(v -> {
            String messageContent = editText.getText() != null ? editText.getText().toString().trim() : "";

            if (!messageContent.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String currentTime = sdf.format(new Date());

                messageList.add(new Message("me", messageContent, currentTime));

                adapter.notifyDataSetChanged();
                editText.setText("");
                recyclerView.smoothScrollToPosition(messageList.size() - 1);

            }
        });

        editText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                btnSend.performClick();
                return true;
            }
            return false;
        });





        return view;
    }
}