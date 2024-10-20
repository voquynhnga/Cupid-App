//package com.midterm.destined.chat;
//
//import static android.content.Context.INPUT_METHOD_SERVICE;
//import static android.content.Context.LAYOUT_INFLATER_SERVICE;
//import static androidx.core.content.ContextCompat.getSystemService;
//
//import static java.security.AccessController.getContext;
//
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.midterm.destined.R;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ChatDetailFragment extends Fragment {
//
////    private TextView senderTextView;
////    private TextView contentTextView;
//
//    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mChatAdapter;
//    private RecyclerView.LayoutManager mChatLayoutManager;
//
//    private EditText mSendEditText;
//    private ImageView btnBack;
//
//    private ImageView mSendButton;
//    private String notification;
//    private String currentUserID, matchID, chatID;
//    private String matchName, matchGive, matchNeed, matchBudget, matchProfile;
//    private String lastMessage, lastTimeStamp;
//    private String message, createdByUser, isSeen, messageID, currentUserName;
//    private Boolean currentUserBoolean;
//    ValueEventListener seenListener;
//    DatabaseReference mDatabaseUser, mDatabaseChat;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//
//
//
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);
//
////        senderTextView = view.findViewById(R.id.senderTextView);
////        contentTextView = view.findViewById(R.id.contentTextView);
//        btnBack = view.findViewById(R.id.btn_back);
//
//        mRecyclerView = view.findViewById(R.id.rv_chat_messages);
//        mRecyclerView.setNestedScrollingEnabled(false);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setFocusable(false);
//        mChatLayoutManager = new LinearLayoutManager(ChatDetailFragment.this.getApplicationContext());
//        mRecyclerView.setLayoutManager(mChatLayoutManager);
//        mRecyclerView.setAdapter(mChatAdapter);
//
//        mSendEditText = view.findViewById(R.id.et_message);
//        mSendButton = view.findViewById(R.id.btn_send);
//
//        mSendButton.setOnClickListener(v -> {
//            sendMessage();
//
//        });
//
//        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//                if(bottom < oldBottom ){
//                    mRecyclerView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
//                        }
//                    }, 100);
//                }
//            }
//        });
//
//
//
//        if (getArguments() != null) {
//
//            matchID = getArguments().getString("matchId");
//            matchName = getArguments().getString("matchName");
//            matchGive = getArguments().getString("matchGive");
//            matchNeed = getArguments().getString("matchNeed");
//            matchBudget = getArguments().getString("matchBudget");
//            matchProfile = getArguments().getString("matchProfile");
//
//            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID)
//                    .child("connections").child("matches").child(matchID).child("ChatId");
//            mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
//
//            getChatId();
//
//
////            String sender = getArguments().getString("sender");
////            String content = getArguments().getString("content");
//
////            senderTextView.setText(sender);
////            contentTextView.setText(content);
//        }
//
//        btnBack.setOnClickListener(v -> {
//            Navigation.findNavController(requireView()).navigate(R.id.action_chatDetailFragment_to_chatFragment);
//
//        });
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserID);
//        Map onChat = new HashMap();
//        onChat.put("onChat", matchID);
//        reference.updateChildren(onChat);
//
//        DatabaseReference current = FirebaseDatabase.getInstance().getReference("users")
//                .child(matchID).child("connections").child("matches").child(currentUserID);
//
//        Map lastSeen = new HashMap();
//        lastSeen.put("lastSeen", "false");
//        current.updateChildren(lastSeen);
//
//
//
//        return view;
//    }
//
//    @Override
//    public void onPause(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserID);
//        Map onChat = new HashMap();
//        onChat.put("onChat", "None");
//        reference.updateChildren(onChat);
//        super.onPause();
//    }
//
//    @Override
//    public void onStop(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserID);
//        Map onChat = new HashMap();
//        onChat.put("onChat", "None");
//        reference.updateChildren(onChat);
//        super.onStop();
//    }
//
//    private void sendMessage(final String text){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(matchID);
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(dataSnapshot.exists()){
//                    if(dataSnapshot.child("onChat").exists()){
//                        if(dataSnapshot.child("notificationKey").exists()){
//                            notification = dataSnapshot.child("notificationKey").getValue().toString();
//                        }
//                        else{
//                            notification = "";
//                        }
//                        if(!dataSnapshot.child("onChat").getValue().toString().equals(currentUserID)){
//                            new SendNotification(text, "New message from: " + currentUserName, notification,
//                                    "activityToBeOpened", "MatchesActivity");
//                        }
//                        else{
//                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
//                                    .child(currentUserID).child("connections").child("matches").child(matchID);
//                            Map seenInfo = new HashMap();
//                            seenInfo.put("lastSend", "false");
//                            reference.updateChildren(seenInfo);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
////    @Override
////    public boolean OnCreateOptionsMenu(Menu menu){
////
////    }
//
//    private  void showProfile(View v){
//        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        View popupView = inflater.inflate(R.layout.item_profile, null);
//
//        TextView name = (TextView) popupView.findViewById(R.id.name);
//        ImageView image = (ImageView) popupView.findViewById(R.id.image);
//        TextView budget = (TextView) popupView.findViewById(R.id.budget);
//        ImageView mNeedImage = (ImageView) popupView.findViewById(R.id.needImage);
//        ImageView mGiveImage = (ImageView) popupView.findViewById(R.id.giveImage);
//
//        name.setText(matchName);
//        budget.setText(matchBudget);
//
////        if(matchNeed.equals("Netflix")) {
////            mNeedImage.setImageResource(R.drawable.netflix);
////
////        }
////        else if(matchNeed.equals("Amazon Prime")){
////            mNeedImage.setImageResource(R.drawable.amazon);
////        }
////        else if(matchNeed.equals("Hulu")) {
////            mNeedImage.setImageResource(R.drawable.hulu);
////        }
////        else if(matchNeed.equals("Vudu")){
////            mNeedImage.setImageResource(R.drawable.vudu);
////        }
////        else if(matchNeed.equals("HBO")){
////            mNeedImage.setImageResource(R.drawable.hbo);
////        }
////        else
////            mNeedImage.setImageResource(R.drawable.none);
////
////
////
////
////        if(matchNeed.equals("Netflix")){
////            mGiveImage.setImageResource(ContextCompat.getDrawable(getApplicationContext(), R.drawable.netflix));
////        }
////        else if(matchNeed.equals("Amazon Prime")){
////            mGiveImage.setImageResource(ContextCompat.getDrawable(getApplicationContext(), R.drawable.amazon));
////        }
////        else if(matchNeed.equals("Hulu")){
////            mGiveImage.setImageResource(ContextCompat.getDrawable(getApplicationContext(), R.drawable.hulu));
////
////        }
////        else if(matchNeed.equals("Vudu")){
////            mGiveImage.setImageResource(ContextCompat.getDrawable(getApplicationContext(), R.drawable.vudu));
////        }
////        else if(matchNeed.equals("HBO")){
////            mGiveImage.setImageResource(ContextCompat.getDrawable(getApplicationContext(), R.drawable.hbo));
////        }
////        else
////            mGiveImage.setImageResource(ContextCompat.getDrawable(getApplicationContext(), R.drawable.none));
//
//        switch (matchProfile){
//            case "default" :
//                Glide.with(popupView.getContext()).load(R.drawable.profile).into(image);
//                break;
//            default:
//                Glide.clear(image);
//                Glide.with(v.getContext()).load(matchProfile).into(image);
//        }
//
//        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
//        int height = ViewGroup.LayoutParams.MATCH_PARENT;
//
//        boolean focusable = true;
//        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
//
//        hideSoftKeyBoard();
//
//        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                popupWindow.dismiss();
//                return false;
//            }
//        });
//
//    }
//
//    private void hideSoftKeyBoard() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//
//        if(imm.isAcceptingText()){
//            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        }
//
//    }
//
//    //private void deleteMatch(String MatchID){}
//
//
//
//    private List<ChatObject> getDataSetChat(){
//        return null;
//    }
//
//    private void getChatId(){
//
//    }
//
//}

package com.midterm.destined.chat;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.destined.R;
import com.midterm.destined.chat.SendNotification;

import java.util.HashMap;
import java.util.Map;

public class ChatDetailFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private EditText mSendEditText;
    private ImageView mSendButton, btnBack;
    private String currentUserID, matchID, matchName, matchProfile, matchBudget;
    private DatabaseReference mDatabaseUser, mDatabaseChat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        // Khởi tạo các View
        btnBack = view.findViewById(R.id.btn_back);
        mRecyclerView = view.findViewById(R.id.rv_chat_messages);
        mSendEditText = view.findViewById(R.id.et_message);
        mSendButton = view.findViewById(R.id.btn_send);

        // Thiết lập RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Xử lý sự kiện khi nhấn nút Back
        btnBack.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_chatDetailFragment_to_chatFragment));

        // Lấy dữ liệu từ Bundle (arguments)
        if (getArguments() != null) {
            matchID = getArguments().getString("matchId");
            matchName = getArguments().getString("matchName");
            matchProfile = getArguments().getString("matchProfile");
            matchBudget = getArguments().getString("matchBudget");

            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID)
                    .child("connections").child("matches").child(matchID).child("ChatId");
            mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

            getChatId();
        }

        // Sự kiện gửi tin nhắn
        mSendButton.setOnClickListener(v -> sendMessage(mSendEditText.getText().toString()));

        // Xử lý cuộn RecyclerView khi bàn phím xuất hiện
        mRecyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                mRecyclerView.postDelayed(() -> mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1), 100);
            }
        });

        // Cập nhật trạng thái đang chat
        updateOnChatStatus(true);

        return view;
    }

    @Override
    public void onPause() {
        updateOnChatStatus(false);
        super.onPause();
    }

    @Override
    public void onStop() {
        updateOnChatStatus(false);
        super.onStop();
    }

    // Phương thức gửi tin nhắn
    private void sendMessage(final String text) {
        if (text.trim().isEmpty()) return;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(matchID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String notificationKey = "";
                    if (dataSnapshot.child("notificationKey").exists()) {
                        notificationKey = dataSnapshot.child("notificationKey").getValue().toString();
                    }

                    String onChatUserID = dataSnapshot.child("onChat").getValue(String.class);
                    if (!onChatUserID.equals(currentUserID)) {
                        new SendNotification(text, "New message from: " + matchName, notificationKey, "activityToBeOpened", "MatchesActivity");
                    }

                    // Lưu tin nhắn vào Firebase
                    saveMessageToFirebase(text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // Lưu tin nhắn vào Firebase
    private void saveMessageToFirebase(String text) {
        String messageID = mDatabaseChat.push().getKey();
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("messageID", messageID);
        messageData.put("text", text);
        messageData.put("createdByUser", currentUserID);
        messageData.put("isSeen", false);

        mDatabaseChat.child(messageID).setValue(messageData);
    }

    // Lấy chat ID từ Firebase
    private void getChatId() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String chatId = dataSnapshot.getValue(String.class);
                    // TODO: Sử dụng chatId cho việc lấy tin nhắn
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // Cập nhật trạng thái "onChat" trong Firebase
    private void updateOnChatStatus(boolean isOnChat) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserID);
        Map<String, Object> onChatData = new HashMap<>();
        onChatData.put("onChat", isOnChat ? matchID : "None");
        reference.updateChildren(onChatData);
    }

    // Hiển thị thông tin profile dưới dạng popup
//    private void showProfile(View v) {
//        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(LayoutInflater.class);
//        View popupView = inflater.inflate(R.layout.item_profile, null);
//
//        TextView name = popupView.findViewById(R.id.name);
//        ImageView image = popupView.findViewById(R.id.image);
//        TextView budget = popupView.findViewById(R.id.budget);
//
//        name.setText(matchName);
//        budget.setText(matchBudget);
//
//        switch (matchProfile) {
//            case "default":
//                Glide.with(popupView.getContext()).load(R.drawable.profile).into(image);
//                break;
//            default:
//                Glide.with(popupView.getContext()).load(matchProfile).into(image);
//                break;
//        }
//
//        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
//        hideSoftKeyboard();
//        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
//
//        popupView.setOnTouchListener((view, motionEvent) -> {
//            popupWindow.dismiss();
//            return false;
//        });
//    }

    // Ẩn bàn phím
//    private void hideSoftKeyboard() {
//        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
//        if (imm.isAcceptingText()) {
//            imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
//        }
//    }
}
