package com.midterm.destined.Presenters;

import com.midterm.destined.Views.Homepage.HomepageContract;

public class HomepagePresenter implements HomepageContract.Presenter {

    private final HomepageContract.View view;

    public HomepagePresenter(HomepageContract.View view) {
        this.view = view;
    }

    @Override
    public void onLikeButtonClicked() {
        view.performLikeAction();
    }

    @Override
    public void onDislikeButtonClicked() {
        view.performDislikeAction();
    }

    @Override
    public void onSearchButtonClicked() {
        view.navigateToSearch();
    }

    @Override
    public void onNotificationsClicked() {
        view.navigateToNotifications();
    }
}
