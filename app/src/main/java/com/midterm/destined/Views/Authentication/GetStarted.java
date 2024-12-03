//package com.midterm.destined;
//
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class GetStarted extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_get_started);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//}

package com.midterm.destined.Views.Authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.midterm.destined.databinding.ActivityGetStartedBinding;

import java.io.File;

public class GetStarted extends AppCompatActivity {
    private Button btLogin;
    private TextView tvSignUp;
    private ActivityGetStartedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetStartedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btLogin = binding.loginWithPhone;
        tvSignUp = binding.signupText;
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetStarted.this, Login.class);
                startActivity(intent);
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetStarted.this, SignUp.class);
                startActivity(intent);
            }
        });
    }


    public void downloadImageToInternalStorage(Context context, String fileName, String storagePath) {
        // Firebase Storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(storagePath);

        // Đường dẫn nơi file sẽ được lưu trong bộ nhớ cục bộ của thiết bị
        File localFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

        // Bắt đầu tải xuống
        storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            // Tải xuống thành công
            Toast.makeText(context, "Tải ảnh thành công!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Xử lý lỗi
            Toast.makeText(context, "Lỗi khi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
