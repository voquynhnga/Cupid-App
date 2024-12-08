package com.midterm.destined.Models;

public interface MyProfileModel {
    void logout(LogoutCallback callback);

    interface LogoutCallback {
        void onComplete(boolean success);
    }


}
