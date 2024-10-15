package com.midterm.destined;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class AddStoryFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_PERMISSIONS = 100;
    private Uri imageUri;
    private ConstraintLayout constraintLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_story, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        constraintLayout = view.findViewById(R.id.constraintLayout);
        ImageView chooseImageButton = view.findViewById(R.id.addImg);
        ImageView addStoryButton = view.findViewById(R.id.addStoryButton);

        chooseImageButton.setOnClickListener(v -> {
            checkPermissionsAndOpenGallery(); // Kiểm tra quyền trước khi mở thư viện
        });

        addStoryButton.setOnClickListener(v -> {
            if (imageUri != null) {
                // Tải ảnh lên thư viện
                uploadImageToGallery();
                Toast.makeText(getActivity(), "Story added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Please choose an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionsAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getActivity(), "Permission denied to read storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                constraintLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToGallery() {
        if (imageUri != null) {
            // Lấy tên ảnh từ URI
            String imageName = "MyImage_" + System.currentTimeMillis();
            ImageUploader.uploadImageToGallery(getActivity(), imageUri, imageName);
        }
    }
}
