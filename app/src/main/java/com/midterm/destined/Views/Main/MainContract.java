package com.midterm.destined.Views.Main;

import androidx.navigation.NavController;

public interface MainContract {
    interface View {
        void setupNavigation(int currentDestinationId);
    }

    interface Presenter {
        void initToolbarNavigation(NavController navController);
        void navigateToHome(int currentDestinationId);
        void navigateToMessages(int currentDestinationId);
        void navigateToProfile(int currentDestinationId);
    }
}
