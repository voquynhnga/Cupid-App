package com.midterm.destined;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.card.Card;
import com.midterm.destined.card.CardFragment;
import com.midterm.destined.databinding.FragmentHomepageBinding;
import com.midterm.destined.model.UserReal;

import java.util.ArrayList;
import java.util.List;

public class HomepageFragment extends Fragment {

    private FragmentHomepageBinding binding;
    private ImageView btnRefresh;
    private String currentUserId ;


    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomepageBinding.inflate(inflater, container, false);
        currentUserId = Card.fetchCurrentUserID();

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.card_container, new CardFragment())
                    .commit();
        }
        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRefresh = view.findViewById(R.id.refreshButton);
        btnRefresh.setOnClickListener(v -> {
            CardFragment cf = (CardFragment) getChildFragmentManager().findFragmentById(R.id.card_container);
            cf.fetchAllUsersExceptCurrentAndFavorited(currentUserId);
        });

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
