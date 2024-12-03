package com.midterm.destined.Views.Homepage;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.midterm.destined.R;
import com.midterm.destined.Views.Homepage.Card.CardFragment;
import com.midterm.destined.databinding.FragmentHomepageBinding;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Presenters.HomepagePresenter;

import java.util.List;

public class HomepageFragment extends Fragment implements HomepageContract.View {

    private FragmentHomepageBinding binding;
    private HomepageContract.Presenter presenter;
    private ImageView btnLike;
    private ImageView btnDislike;
    private CardFragment cf ;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomepageBinding.inflate(inflater, container, false);
        presenter = new HomepagePresenter(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLike = view.findViewById(R.id.likeButton);
        btnDislike = view.findViewById(R.id.dislikeButton);
        cf = (CardFragment) getChildFragmentManager().findFragmentById(R.id.card_container);


        // Gọi presenter để load dữ liệu
        presenter.fetchCards("currentUserId");

        btnLike.setOnClickListener(v -> {
            presenter.likeCard(cf);
        });

        btnDislike.setOnClickListener(v -> {
            // Xử lý dislike card
            presenter.dislikeCard(cf);
        });
    }

    @Override
    public void showCards(List<Card> cards) {
        // Hiển thị danh sách card trên giao diện
        getChildFragmentManager().beginTransaction()
                .replace(R.id.card_container, CardFragment.getInstance(), "CARD_FRAGMENT").commit();
    }

    @Override
    public void showLoading() {
        // Hiển thị loading

    }

    @Override
    public void hideLoading() {
        // Ẩn loading
    }

    @Override
    public void showError(String message) {
        // Hiển thị thông báo lỗi
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

