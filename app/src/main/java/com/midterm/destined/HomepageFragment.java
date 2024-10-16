package com.midterm.destined;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.destined.databinding.FragmentHomepageBinding;

import java.util.ArrayList;
import java.util.List;


public class HomepageFragment extends Fragment {

    private FragmentHomepageBinding binding;
    private ViewPager2 viewPager;
    private List<UserProfile> userProfiles;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomepageBinding.inflate(inflater, container, false);

        viewPager = binding.viewPager;
        userProfiles = new ArrayList<>();

        fetchUserProfiles();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.filter.setOnClickListener(v -> {
            Log.d("DEBUG", "an zo search");
            NavHostFragment.findNavController(HomepageFragment.this)
                    .navigate(R.id.action_HomepageFragment_to_SearchFragment);
        });

        binding.story.setOnClickListener(v -> {
            NavHostFragment.findNavController(HomepageFragment.this)
                    .navigate(R.id.action_global_AddStoryFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void fetchUserProfiles() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userProfiles");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfiles.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserProfile profile = snapshot.getValue(UserProfile.class);
                    if (profile != null) {
                        userProfiles.add(profile);
                    }
                }
                showCards(userProfiles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }

    private void showCards(List<UserProfile> profiles) {
        CardAdapter adapter = new CardAdapter(this, profiles);
        viewPager.setAdapter(adapter);
    }
}
