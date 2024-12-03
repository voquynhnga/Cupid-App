package com.midterm.destined.Presenters;

import android.util.Log;
import androidx.navigation.NavController;
import com.midterm.destined.R;
import com.midterm.destined.Views.Main.MainContract;


public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View view;
    private final NavController navController;

    public MainPresenter(MainContract.View view, NavController navController) {
        this.view = view;
        this.navController = navController;
    }

    @Override
    public void initToolbarNavigation(NavController navController) {
        int currentDestinationId = navController.getCurrentDestination() != null ? navController.getCurrentDestination().getId() : -1;
        view.setupNavigation(currentDestinationId);
    }

    @Override
    public void navigateToHome(int currentDestinationId) {
        if (currentDestinationId == R.id.fragment_chat) {
            navController.navigate(R.id.action_ChatFragment_to_HomepageFragment);
        } else if (currentDestinationId != R.id.fragment_homepage) {
            navController.navigate(R.id.action_global_HomepageFragment);
        } else {
            Log.d("Navigation", "Already on HomepageFragment");
        }
    }

    @Override
    public void navigateToMessages(int currentDestinationId) {
        if (currentDestinationId == R.id.fragment_homepage) {
            navController.navigate(R.id.action_HomepageFragment_to_ChatFragment);
        } else if (currentDestinationId != R.id.fragment_chat) {
            navController.navigate(R.id.action_global_ChatFragment);
        } else {
            Log.d("Navigation", "Already on ChatFragment");
        }
    }

    @Override
    public void navigateToProfile(int currentDestinationId) {
        if (currentDestinationId == R.id.fragment_homepage) {
            navController.navigate(R.id.action_MyProfileFragment_to_HomepageFragment);
        } else if (currentDestinationId != R.id.MyProfileFragment) {
            navController.navigate(R.id.action_global_MyProfileFragment);
        } else {
            Log.d("Navigation", "Already on MyProfileFragment");
        }
    }
}
