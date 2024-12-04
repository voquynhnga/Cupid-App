package com.midterm.destined.Views.Main;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.midterm.destined.Presenters.MainPresenter;
import com.midterm.destined.R;
import com.midterm.destined.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private MainContract.Presenter presenter;
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        presenter = new MainPresenter(this, navController);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        presenter.initToolbarNavigation(navController);

    }

    @Override
    public void setupNavigation(int currentDestinationId) {
        if (binding.toolbar.findViewById(R.id.nav_home) != null) {
            binding.toolbar.findViewById(R.id.nav_home).setOnClickListener(view -> {
                if (navController.getCurrentDestination() != null) {
                    if (navController.getCurrentDestination().getId() == R.id.fragment_chat) {
                        navController.navigate(R.id.action_ChatFragment_to_HomepageFragment);
                    } else if (navController.getCurrentDestination().getId() != R.id.fragment_homepage) {
                        navController.navigate(R.id.action_global_HomepageFragment);
                    } else {
                        Log.d("Navigation", "Already on HomepageFragment");
                    }
                }
            });
        }

        if (binding.toolbar.findViewById(R.id.nav_message) != null) {
            binding.toolbar.findViewById(R.id.nav_message).setOnClickListener(view -> {
                if (navController.getCurrentDestination() != null) {
                    if (navController.getCurrentDestination().getId() == R.id.fragment_homepage) {
                        navController.navigate(R.id.action_HomepageFragment_to_ChatFragment);
                    } else if (navController.getCurrentDestination().getId() != R.id.fragment_chat) {
                        navController.navigate(R.id.action_global_ChatFragment); // Đảm bảo bạn đã định nghĩa action này trong nav_graph.xml
                    } else {
                        Log.d("Navigation", "Already on ChatFragment");
                    }
                }
            });
        }



        if (binding.toolbar.findViewById(R.id.nav_profile) != null) {
            binding.toolbar.findViewById(R.id.nav_profile).setOnClickListener(view -> {
                if (navController.getCurrentDestination() != null) {
                    if (navController.getCurrentDestination().getId() == R.id.fragment_homepage) {
                        navController.navigate(R.id.action_MyProfileFragment_to_HomepageFragment);
                    } else if (navController.getCurrentDestination().getId() != R.id.MyProfileFragment) {
                        navController.navigate(R.id.action_global_MyProfileFragment);
                    } else {
                        Log.d("Navigation", "Already on MyProfileFragment");
                    }
                }
            });
        }

    }
}
