package com.midterm.destined.Views.Homepage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.midterm.destined.Models.Notification;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.Presenters.NotificationPresenter;
import com.midterm.destined.R;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Views.Homepage.Card.CardFragment;
import com.midterm.destined.Views.Homepage.Search.SearchFragment;
import com.midterm.destined.databinding.FragmentHomepageBinding;
import com.midterm.destined.Presenters.HomepagePresenter;

import java.util.HashMap;
import java.util.Map;

public class HomepageFragment extends Fragment implements HomepageContract.View {

    private FragmentHomepageBinding binding;
    private HomepagePresenter presenter;
    private static HomepageFragment homepageFragment;

    public static HomepageFragment getInstance(){
        if(homepageFragment == null){
            return new HomepageFragment();
        }
        else return homepageFragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomepageBinding.inflate(inflater, container, false);
        presenter = new HomepagePresenter(this);

        if (savedInstanceState != null) {
            String value = getArguments().getString("CHECK");
            if ("1".equals(value)) {
                showCards();
            }
        } else {
            showCards();
        }
        if (getArguments() != null) {
            boolean value2 = getArguments().getBoolean("New notification", false);
            if (value2) {
                binding.notifications.setImageResource(R.drawable.notification_new);
            }
            else{
                binding.notifications.setImageResource(R.drawable.notification);
            }
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.likeButton.setOnClickListener(v -> presenter.onLikeButtonClicked());
        binding.dislikeButton.setOnClickListener(v -> presenter.onDislikeButtonClicked());
        binding.filterhp.setOnClickListener(v -> presenter.onSearchButtonClicked());
        binding.notifications.setOnClickListener(v -> presenter.onNotificationsClicked());

    }

    @Override
    public void showCards() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.card_container, new CardFragment(), "CARD_FRAGMENT").commit();
    }

    @Override
    public void performLikeAction() {
        CardFragment cardFragment = (CardFragment) getChildFragmentManager().findFragmentByTag("CARD_FRAGMENT");
        if (cardFragment != null) {
            cardFragment.getFlingContainer().getTopCardListener().selectRight();
        }
    }

    @Override
    public void performDislikeAction() {
        CardFragment cardFragment = (CardFragment) getChildFragmentManager().findFragmentByTag("CARD_FRAGMENT");
        if (cardFragment != null) {
            cardFragment.getFlingContainer().getTopCardListener().selectLeft();
        }
    }

    @Override
    public void navigateToSearch() {
        NavHostFragment.findNavController(this).navigate(R.id.action_HomepageFragment_to_SearchFragment);


    }

    @Override
    public void navigateToNotifications() {
        NavHostFragment.findNavController(this).navigate(R.id.action_global_NotificationsFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
