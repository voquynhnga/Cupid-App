package com.midterm.destined.Models;

import com.google.firebase.auth.FirebaseAuth;

public class MyProfile implements MyProfileModel {

    @Override
    public void logout(LogoutCallback callback) {
        FirebaseAuth.getInstance().signOut();
        callback.onComplete(true);
    }


}

