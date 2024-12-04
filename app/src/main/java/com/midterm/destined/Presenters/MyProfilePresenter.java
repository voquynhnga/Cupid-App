package com.midterm.destined.Presenters;

import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;

import com.midterm.destined.Models.MyProfile;
import com.midterm.destined.Views.Profile.MyProfileContract;

public class MyProfilePresenter implements MyProfileContract.presenter {

    private MyProfileContract.view view;
    private MyProfile model;

    public MyProfilePresenter(MyProfileContract.view view) {
        this.view = view;
        this.model = new MyProfile();
    }

    @Override
    public void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        view.launchImagePicker(intent);
    }

    @Override
    public void logout() {
        model.logout(success -> {
            if (success) {
                view.showMessage("Logged out successfully");
                view.navigateToLogin();
            } else {
                view.showMessage("Failed to log out");
            }
        });
    }

    @Override
    public void navigateToSettings(View view) {
        this.view.navigateToSettings(view);
    }
}

