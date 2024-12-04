package com.midterm.destined.Views.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

public interface MyProfileContract {
    interface view {
        void showProfileImage (Bitmap bitmap);
        void showMessage (String message);
        void navigateToSettings (View view);
        void navigateToLogin ();
        void launchImagePicker (Intent intent);
    }
    interface presenter {
        void chooseImage();
        void logout();
        void navigateToSettings(View view);
    }
}
