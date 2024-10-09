package com.midterm.destined;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class AddStoryActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ImageView storyImageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_story);

        storyImageView = findViewById(R.id.storyImageView);
        ImageView chooseImageButton = findViewById(R.id.addImg);
        ImageView addStoryButton = findViewById(R.id.addStoryButton);

        // Chọn ảnh từ thư viện
        chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });


        addStoryButton.setOnClickListener(v -> {
            if (imageUri != null) {
                // Xử lý thêm story
                Toast.makeText(this, "Story added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                storyImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
