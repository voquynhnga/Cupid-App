package com.midterm.destined;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.midterm.destined.databinding.ActivityMyLocationBinding;

public class MyLocation extends AppCompatActivity {
    private Button btnAllow;
    private ActivityMyLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnAllow = binding.btnAllowLocation;
        btnAllow.setOnClickListener(v -> {
            requestLocationPermission();
        });
    }

    private void requestLocationPermission() {
        if(ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            goToMain();
        }
        else{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if(isGranted) {
                    goToMain();
                }
                else {
                    Toast.makeText(this, "Location permission is required to continue.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void goToMain() {
        Intent intent = new Intent(MyLocation.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}