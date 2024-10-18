package com.midterm.destined;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.midterm.destined.databinding.ActivityLookingForBinding;

public class LookingFor extends AppCompatActivity {
    private ActivityLookingForBinding binding;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityLookingForBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btn = binding.btnContinue;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LookingFor.this, Interests.class);
                startActivity(intent);
            }
        });

    }
}