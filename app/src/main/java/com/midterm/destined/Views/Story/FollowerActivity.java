//package com.midterm.destined.Views.Story;
//
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.appcompat.widget.Toolbar;
//
//import android.view.View;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.midterm.destined.Adapters.UserAdapter;
//import com.midterm.destined.Models.UserReal;
//import com.midterm.destined.R;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//public class FollowersActivity extends AppCompatActivity {
//
//    String id;
//    private List<String> idList;
//
//    RecyclerView recyclerView;
//    UserAdapter userAdapter;
//    List<UserReal> userList;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_followers);
//
//        Intent intent = getIntent();
//        id = intent.getStringExtra("id");
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Views");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//        recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        userList = new ArrayList<>();
//        userAdapter = new UserAdapter(this, userList, false);
//        recyclerView.setAdapter(userAdapter);
//
//        idList = new ArrayList<>();
//
//        // Gọi hàm lấy danh sách người đã xem story
//        getViews();
//    }
//
//    private void getViews() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
//                .child(id).child(Objects.requireNonNull(getIntent().getStringExtra("storyid"))).child("views");
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                idList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    idList.add(snapshot.getKey());
//                }
//                showUsers();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void showUsers() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    UserReal user = snapshot.getValue(UserReal.class);
//                    for (String id : idList) {
//                        assert user != null;
//                        if (user.getUid().equals(id)) {
//                            userList.add(user);
//                        }
//                    }
//                }
//                userAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//}
