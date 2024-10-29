package com.midterm.destined;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midterm.destined.databinding.ActivityMyLocationBinding;
import com.midterm.destined.model.GPSAddress;
import com.midterm.destined.model.UserReal;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyLocation extends AppCompatActivity {
    private Button btnAllow;
    private ActivityMyLocationBinding binding;
    private FusedLocationProviderClient fusedLocationClient; // Khai báo FusedLocationProviderClient
    private UserReal user; // Thêm biến UserReal để lưu thông tin người dùng
    private FirebaseFirestore db; // Khai báo Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnAllow = binding.btnAllowLocation;

        // Nhận thông tin UserReal từ Intent
        user = (UserReal) getIntent().getSerializableExtra("user");

        // Khởi tạo Firestore và FusedLocationProviderClient
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnAllow.setOnClickListener(v -> {
            requestLocationPermission();
        });
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission is required to continue.", Toast.LENGTH_SHORT).show();
                }
            }
    );

//    private void getCurrentLocation() {
//        // Kiểm tra xem permission đã được cấp chưa
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(this, location -> {
//                        if (location != null) {
//                            // Lấy vị trí hiện tại và cập nhật vào GPSAddress
//                            GPSAddress gpsAddress = new GPSAddress(location.getLatitude(), location.getLongitude());
//                            user.setLocation(gpsAddress); // Cập nhật địa chỉ GPS vào đối tượng UserReal
//                           user.setDetailAdrress("Đà Nẵng"); // cập nhật lại
//                            Log.d("Location", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
//                            // Lưu UserReal vào Firestore
//                            saveUserToFirestore(user);
//                        } else {
//                            Toast.makeText(this, "Unable to get location.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            GPSAddress gpsAddress = new GPSAddress(location.getLatitude(), location.getLongitude());
                            user.setLocation(gpsAddress);

                            // Sử dụng Geocoder để lấy địa chỉ
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(user.getLocation().getLatitude(), user.getLocation().getLongitude(), 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    String district = address.getSubAdminArea(); // Quận/Huyện
                                    String city = address.getAdminArea(); // Thành phố/Tỉnh

                                    // Gán địa chỉ vào UserReal
                                    String detailAddress = (district != null ? district : "") + ", " +
                                            (city != null ? city : "");
                                    user.setDetailAddress(detailAddress);

                                    Log.d("Location", "Latitude: " + user.getLocation().getLatitude() + ", Longitude: " + user.getLocation().getLongitude());
                                    Log.d("Address", "Detail Address: " + detailAddress);

                                    // Lưu UserReal vào Firestore
                                    saveUserToFirestore(user);
                                } else {
                                    Toast.makeText(this, "Unable to get address.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Error getting address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Unable to get location.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }




    private void saveUserToFirestore(UserReal user) {
        db.collection("users").document(user.getUid())
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyLocation.this, "User location saved successfully.", Toast.LENGTH_SHORT).show();
                        goToMain(); // Chuyển đến Activity chính
                    } else {
                        Toast.makeText(MyLocation.this, "Error saving user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMain() {
        Intent intent = new Intent(MyLocation.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}