package com.midterm.destined;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.midterm.destined.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();


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



        if (binding.toolbar.findViewById(R.id.nav_heart) != null) {
            binding.toolbar.findViewById(R.id.nav_heart).setOnClickListener(view -> {
                // Xử lý sự kiện cho Heart
            });
        }

        if (binding.toolbar.findViewById(R.id.nav_profile) != null) {
            binding.toolbar.findViewById(R.id.nav_profile).setOnClickListener(view -> {
                // Xử lý sự kiện cho Profile
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
//@Override
//protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    EdgeToEdge.enable(this);
//    setContentView(R.layout.activity_main);
//
//    View mainView = findViewById(R.id.main);
//    ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
//        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//        return insets;
//    });
//
//    mainView.setOnClickListener(v -> {
//        Intent intent = new Intent(MainActivity.this, MainUIActivity.class);
//        startActivity(intent);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//    });
//}

