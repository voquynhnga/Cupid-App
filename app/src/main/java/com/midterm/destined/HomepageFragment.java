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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.databinding.FragmentHomepageBinding;
import com.midterm.destined.model.User;

import java.util.ArrayList;
import java.util.List;

public class HomepageFragment extends Fragment {

    private FragmentHomepageBinding binding;
    private List<User> userProfiles = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomepageBinding.inflate(inflater, container, false);
//        displayCardFragment();
        return binding.getRoot();

    }
    private void displayCardFragment() {

        User user = new User("uid123"," ", " ", "Sara", "0949959999", "12/7/2000", "female", "HEHE", "", "Da Nang");
        CardFragment cardFragment = CardFragment.newInstance(user);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_card, cardFragment);
        transaction.commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.filterhp.setOnClickListener(v -> {

            NavHostFragment.findNavController(HomepageFragment.this)
                    .navigate(R.id.action_global_SearchFragment);
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
}
