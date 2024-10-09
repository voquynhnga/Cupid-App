package com.midterm.destined;

import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class AddStoryFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private ImageView storyImageView;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflating layout for the fragment
        return inflater.inflate(R.layout.add_story, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storyImageView = view.findViewById(R.id.storyImageView);
        ImageView chooseImageButton = view.findViewById(R.id.addImg);
        ImageView addStoryButton = view.findViewById(R.id.addStoryButton);

        // Chọn ảnh từ thư viện
        chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        addStoryButton.setOnClickListener(v -> {
            if (imageUri != null) {
                // Xử lý thêm story
                Toast.makeText(getActivity(), "Story added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Please choose an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                storyImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
