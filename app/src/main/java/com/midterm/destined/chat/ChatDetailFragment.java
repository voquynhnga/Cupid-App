package com.midterm.destined.chat;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.destined.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDetailFragment extends Fragment {

//    private TextView senderTextView;
//    private TextView contentTextView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText mSendEditText;
    private ImageView btnBack;

    private ImageView mSendButton;
    private String notification;
    private String currentUserID, matchID, chatID;
    private String matchName, matchGive, matchNeed, matchBudget, matchProfile;
    private String lastMessage, lastTimeStamp;
    private String message, createdByUser, isSeen, messageID, currentUserName;
    private Boolean currentUserBoolean;
    ValueEventListener seenListener;
    DatabaseReference mDatabaseUser, mDatabaseChat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

//        senderTextView = view.findViewById(R.id.senderTextView);
//        contentTextView = view.findViewById(R.id.contentTextView);
        btnBack = view.findViewById(R.id.btn_back);

        mRecyclerView = view.findViewById(R.id.rv_chat_messages);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        mChatLayoutManager = new LinearLayoutManager(ChatDetailFragment.this.getContext());
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = view.findViewById(R.id.et_message);
        mSendButton = view.findViewById(R.id.btn_send);

        mSendButton.setOnClickListener(v -> {
            sendMessage();

        });

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if(bottom < oldBottom ){
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });



        if (getArguments() != null) {

            matchID = getArguments().getString("matchId");
            matchName = getArguments().getString("matchName");
            matchGive = getArguments().getString("matchGive");
            matchNeed = getArguments().getString("matchNeed");
            matchBudget = getArguments().getString("matchBudget");
            matchProfile = getArguments().getString("matchProfile");

            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID)
                    .child("connections").child("matches").child(matchID).child("ChatId");
            mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

            getChatId();


//            String sender = getArguments().getString("sender");
//            String content = getArguments().getString("content");

//            senderTextView.setText(sender);
//            contentTextView.setText(content);
        }

        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_chatDetailFragment_to_chatFragment);

        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserID);
        Map onChat = new HashMap();
        onChat.put("onChat", matchID);
        reference.updateChildren(onChat);

        DatabaseReference current = FirebaseDatabase.getInstance().getReference("users")
                .child(matchID).child("connections").child("matches").child(currentUserID);

        Map lastSeen = new HashMap();
        lastSeen.put("lastSeen", "false");
        current.updateChildren(lastSeen);



        return view;
    }

    @Override
    public void onPause(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserID);
        Map onChat = new HashMap();
        onChat.put("onChat", "None");
        reference.updateChildren(onChat);
        super.onPause();
    }

    @Override
    public void onStop(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserID);
        Map onChat = new HashMap();
        onChat.put("onChat", "None");
        reference.updateChildren(onChat);
        super.onStop();
    }

    private void sendMessage(final String text){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(matchID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("onChat").exists()){
                        if(dataSnapshot.child("notificationKey").exists()){
                            notification = dataSnapshot.child("notificationKey").getValue().toString();
                        }
                        else{
                            notification = "";
                        }
                        if(!dataSnapshot.child("onChat").getValue().toString().equals(currentUserID)){
                            new SendNotification(text, "New message from: " + currentUserName, notification,
                                    "activityToBeOpened", "MatchesActivity");
                        }
                        else{
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(currentUserID).child("connections").child("matches").child(matchID);
                            Map seenInfo = new HashMap();
                            seenInfo.put("lastSend", "false");
                            reference.updateChildren(seenInfo);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//    @Override
//    public boolean OnCreateOptionsMenu(Menu menu){
//
//    }

    private  void showProfile(View v){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.item_profile, null);

        TextView name = (TextView) popupView.findViewById(R.id.name);
        ImageView image = (ImageView) popupView.findViewById(R.id.image);
        TextView budget = (TextView) popupView.findViewById(R.id.budget);
        ImageView mNeedImage = (ImageView) popupView.findViewById(R.id.needImage);
        ImageView mGiveImage = (ImageView) popupView.findViewById(R.id.giveImage);

        name.setText(matchName);
        budget.setText(matchBudget);
    }

    private List<ChatObject> getDataSetChat(){
        return null;
    }

    private void getChatId()[
            ]
}
