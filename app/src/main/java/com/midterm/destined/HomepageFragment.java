package com.midterm.destined;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
    private ImageView btnLike;
    private ImageView btndisLike;
    private String currentUserId ;
    private CardFragment cf;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
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


        btnRefresh = view.findViewById(R.id.refreshButton);
        btnLike = view.findViewById(R.id.likeButton);
        btndisLike = view.findViewById(R.id.dislikeButton);

        //FIXXXX
        btnRefresh.setOnClickListener(v -> {
            cf = (CardFragment) getChildFragmentManager().findFragmentById(R.id.card_container);
            if (cf != null) {
               // cf.fetchAllUsersExceptCurrentAndFavorited();
            } else {
                Log.d("Refresh", "CardFragment not found in container");
            }
        });

        btnLike.setOnClickListener(v -> {
            cf = (CardFragment) getChildFragmentManager().findFragmentById(R.id.card_container);
            if (cf != null) {
                cf.getFlingContainer().getTopCardListener().selectRight();
            }

        });
        btndisLike.setOnClickListener(v -> {
            cf = (CardFragment) getChildFragmentManager().findFragmentById(R.id.card_container);
            if (cf != null) {
                cf.getFlingContainer().getTopCardListener().selectLeft();
            }

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
