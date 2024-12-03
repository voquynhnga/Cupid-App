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
        // Logic to set up toolbar navigation
        if (binding.toolbar.findViewById(R.id.nav_home) != null) {
            binding.toolbar.findViewById(R.id.nav_home).setOnClickListener(view -> presenter.navigateToHome(currentDestinationId));
        }

        if (binding.toolbar.findViewById(R.id.nav_message) != null) {
            binding.toolbar.findViewById(R.id.nav_message).setOnClickListener(view -> presenter.navigateToMessages(currentDestinationId));
        }

        if (binding.toolbar.findViewById(R.id.nav_profile) != null) {
            binding.toolbar.findViewById(R.id.nav_profile).setOnClickListener(view -> presenter.navigateToProfile(currentDestinationId));
        }
    }
}
