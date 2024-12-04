package com.midterm.destined.Views.Homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.midterm.destined.R;
import com.midterm.destined.Views.Homepage.Card.CardFragment;
import com.midterm.destined.databinding.FragmentHomepageBinding;
import com.midterm.destined.Presenters.HomepagePresenter;

public class HomepageFragment extends Fragment implements HomepageContract.View {

    private FragmentHomepageBinding binding;
    private HomepagePresenter presenter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomepageBinding.inflate(inflater, container, false);
        presenter = new HomepagePresenter(this);

        if (savedInstanceState == null) {
            showCards();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.likeButton.setOnClickListener(v -> presenter.onLikeButtonClicked());
        binding.dislikeButton.setOnClickListener(v -> presenter.onDislikeButtonClicked());
        binding.filterhp.setOnClickListener(v -> presenter.onSearchButtonClicked());
    }

    @Override
    public void showCards() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.card_container, CardFragment.getInstance(), "CARD_FRAGMENT").commit();
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
        NavHostFragment.findNavController(this).navigate(R.id.action_global_SearchFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}