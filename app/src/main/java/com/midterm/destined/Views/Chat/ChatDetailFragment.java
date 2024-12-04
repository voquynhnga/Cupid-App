package com.midterm.destined.Views.Chat;



import static com.midterm.destined.Utils.TimeExtensions.getCurrentTime;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.midterm.destined.Adapters.ChatDetailAdapter;
import com.midterm.destined.Models.Message;
import com.midterm.destined.Presenters.ChatDetailPresenter;
import com.midterm.destined.R;

import java.util.ArrayList;
import java.util.List;



public class ChatDetailFragment extends Fragment implements chatDetailView {
    private TextView senderTextView;
    private ImageView btnBack;
    private RecyclerView recyclerView;
    private ArrayList<Message> messageList;
    private ImageView btnSend;
    private EditText editText;
    private DatabaseReference chatRef;
    private ChatDetailAdapter adapter;
    private String chatId;
    private FirebaseUser currentUser;
    LinearLayoutManager layoutManager;
    private String userName;
    private ChatDetailPresenter presenter;

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
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new ChatDetailAdapter(messageList, FirebaseAuth.getInstance().getCurrentUser());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        presenter = new ChatDetailPresenter(this);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            chatId = getArguments().getString("chatId", "");
            userName = getArguments().getString("userName", "Unknown");
            senderTextView.setText(userName);
        } else {
            Toast.makeText(getContext(), "Chat data is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!chatId.isEmpty()) {
            presenter.loadMessages(chatId);
        }

        btnBack.setOnClickListener(v -> Navigation.findNavController(requireView())
                .navigate(R.id.action_chatDetailFragment_to_chatFragment));

        btnSend.setOnClickListener(v -> {
            String messageContent = editText.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                Message message = new Message(currentUser.getUid(), messageContent, getCurrentTime());
                presenter.sendMessage(chatId, message);
                clearInputField();
            }
        });

        editText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                btnSend.performClick();
                return true;
            }
            return false;
        });
    }

    @Override
    public void displayMessages(List<Message> messages) {
        messageList.clear();
        messageList.addAll(messages);
        adapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (layoutManager != null && adapter.getItemCount() > 0) {
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }


    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearInputField() {
        editText.setText("");
    }
}
