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
//        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        // Lấy dữ liệu từ Firestore
        fetchUserProfiles();

        return binding.getRoot();
    }

    private void fetchUserProfiles() {
        db.collection("users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            userProfiles.add(user);
                        }
                        if (!userProfiles.isEmpty()) {
                            displayCardFragment(userProfiles.get(0));
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                    Log.d("DEBUG", "ton tai nguoi dung");

                });
    }

    private void displayCardFragment(User user) {
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
