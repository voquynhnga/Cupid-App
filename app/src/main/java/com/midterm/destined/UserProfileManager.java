package com.midterm.destined;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;
import java.util.List;

public class UserProfileManager {
    private DatabaseReference databaseReference;
    private List<UserProfile> profiles;

    public UserProfileManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        profiles = new ArrayList<>();
    }

    public void fetchUserProfiles() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profiles.clear(); // Xóa danh sách trước khi thêm mới
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        profiles.add(userProfile);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public List<UserProfile> getProfiles() {
        return profiles;
    }
}
