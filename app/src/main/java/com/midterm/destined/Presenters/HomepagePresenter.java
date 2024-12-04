package com.midterm.destined.Presenters;

import com.midterm.destined.Views.Homepage.HomepageContract;

public class HomepagePresenter implements HomepageContract.Presenter {

    private final HomepageContract.View view;

    public HomepagePresenter(HomepageContract.View view) {
        this.view = view;
    }

    @Override
    public void onLikeButtonClicked() {
        view.performLikeAction(); // Yêu cầu view thực hiện hành động Like
    }

    @Override
    public void onDislikeButtonClicked() {
        view.performDislikeAction(); // Yêu cầu view thực hiện hành động Dislike
    }

    @Override
    public void onSearchButtonClicked() {
        view.navigateToSearch(); // Yêu cầu view điều hướng tới SearchFragment
    }
}
