package com.midterm.destined;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.destined.databinding.FragmentHomepageBinding;
import com.midterm.destined.model.UserReal;

import java.util.ArrayList;
import java.util.List;

public class HomepageFragment extends Fragment {

    private FragmentHomepageBinding binding;
    private ViewPager2 viewPager;
    private List<UserReal> userProfiles;

    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomepageBinding.inflate(inflater, container, false);

        //viewPager = binding.viewPager;
        userProfiles = new ArrayList<>();

        recyclerView_story = binding.recyclerView; // Sử dụng binding để lấy RecyclerView
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);

        //fetchUserProfiles(); // Chuyển dòng này xuống dưới để đảm bảo RecyclerView đã được thiết lập

        return binding.getRoot(); // Đảm bảo trả về root view
    }
//    private void displayCardFragment() {
//
//        CardFragment cardFragment = new CardFragment();
//
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_card, cardFragment);
//        transaction.commit();
//    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.filterhp.setOnClickListener(v -> {
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

//    private void fetchUserProfiles() {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userProfiles");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userProfiles.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    UserProfile profile = snapshot.getValue(UserProfile.class);
//                    if (profile != null) {
//                        userProfiles.add(profile);
//                    }
//                }
//                showCards(userProfiles);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý lỗi
//            }
//        });
//    }

//    private void showCards(List<UserProfile> profiles) {
//        CardAdapter adapter = new CardAdapter(this, profiles);
//        viewPager.setAdapter(adapter);
//    }

    private void readStory() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", 0, 0, "",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));

                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }
}
