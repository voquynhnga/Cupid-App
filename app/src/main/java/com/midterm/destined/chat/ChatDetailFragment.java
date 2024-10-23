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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.destined.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatDetailFragment extends Fragment {

    private TextView senderTextView;
    private ImageView btnBack;
    private RecyclerView recyclerView;
    private ArrayList<Message> messageList;
    private ImageView btnSend;
    private EditText editText;
    private DatabaseReference chatRef;
    private ChatDetailAdapter adapter;
    private String chatId; // Biến để lưu ID của chat

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        // Giấu ActionBar
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }

        // Khởi tạo các view
        senderTextView = view.findViewById(R.id.tv_nameChat);
        btnBack = view.findViewById(R.id.btn_back);
        btnSend = view.findViewById(R.id.btn_send);
        editText = view.findViewById(R.id.et_message);
        recyclerView = view.findViewById(R.id.rv_chat_messages);
        messageList = new ArrayList<>();

        // Lấy chatId từ arguments
        if (getArguments() != null) {
            chatId = getArguments().getString("chatId");
            chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);

            // Lấy tin nhắn từ Realtime Database
            chatRef.child("messages").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messageList.clear(); // Xóa danh sách hiện tại
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        Message message = messageSnapshot.getValue(Message.class);
                        messageList.add(message);
                    }
                    adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                    recyclerView.smoothScrollToPosition(messageList.size() - 1); // Cuộn xuống dưới cùng
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi
                }
            });
        }

        // Khởi tạo adapter và layout manager
        adapter = new ChatDetailAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Xử lý sự kiện nhấn nút quay lại
        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_chatDetailFragment_to_chatFragment);
        });

        // Xử lý sự kiện nhấn nút gửi
        btnSend.setOnClickListener(v -> {
            String messageContent = editText.getText() != null ? editText.getText().toString().trim() : "";
            if (!messageContent.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String currentTime = sdf.format(new Date());

                // Tạo tin nhắn mới
                String messageId = chatRef.child("messages").push().getKey();
                Message message = new Message("me", messageContent, currentTime, chatId);

                // Lưu tin nhắn vào Realtime Database
                chatRef.child("messages").child(messageId).setValue(message);

                // Cập nhật danh sách tin nhắn
                messageList.add(message);
                adapter.notifyDataSetChanged();
                editText.setText("");
                recyclerView.smoothScrollToPosition(messageList.size() - 1);
            }
        });

        // Xử lý sự kiện nhấn Enter để gửi tin nhắn
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
