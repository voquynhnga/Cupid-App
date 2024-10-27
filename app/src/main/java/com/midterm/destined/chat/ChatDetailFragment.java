package com.midterm.destined.chat;

import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midterm.destined.R;
import com.midterm.destined.model.UserReal;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private String chatId;
    private FirebaseUser currentUser;


    private ImageView senderAvatar;
    private String userName;
    private String userAvatar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }


        senderTextView = view.findViewById(R.id.tv_nameChat);
        senderAvatar = view.findViewById(R.id.img_avatarChat);

        btnBack = view.findViewById(R.id.btn_back);
        btnSend = view.findViewById(R.id.btn_send);
        editText = view.findViewById(R.id.et_message);
        recyclerView = view.findViewById(R.id.rv_chat_messages);
        messageList = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (getArguments() != null ) {
            chatId = requireArguments().getString("chatId");
            userName = getArguments().getString("userName");
            userAvatar = getArguments().getString("avatar");

            senderTextView.setText(userName);
            Picasso.get().load(userAvatar).into(senderAvatar);
            chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
            loadMessage();

        }

        adapter = new ChatDetailAdapter(messageList, currentUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_chatDetailFragment_to_chatFragment);
        });

        btnSend.setOnClickListener(v -> {
            String messageContent = editText.getText() != null ? editText.getText().toString().trim() : "";
            if (!messageContent.isEmpty()) {
                Message message = new Message(currentUser.getUid(), messageContent, getTime(), chatId);
                addMessageToChat(chatId, message);
                editText.setText("");
//                recyclerView.smoothScrollToPosition(messageList.size() - 1);
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


    private void loadMessage() {
        chatRef.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
              //  recyclerView.smoothScrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ChatDetailFragment", "loadMessages:onCancelled", error.toException());
            }
        });
    }

    private void addMessageToChat(String chatId, Message message) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("chats")
                .child(chatId)
                .child("messages");

        String messageId = messagesRef.push().getKey();
        if (messageId != null) {
            messagesRef.child(messageId).setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        updateLastMessage(chatId, message.getContent());
                    })
                    .addOnFailureListener(e -> Log.w("RealtimeDB", "Error adding message", e));
        }
    }

    private void updateLastMessage(String chatId, String lastMessage) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("lastMessage", lastMessage);
        updateData.put("timestamp", ServerValue.TIMESTAMP);

        chatRef.updateChildren(updateData)
                .addOnSuccessListener(aVoid -> Log.d("RealtimeDB", "Last message updated"))
                .addOnFailureListener(e -> Log.w("RealtimeDB", "Error updating last message", e));
    }
    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }
}
