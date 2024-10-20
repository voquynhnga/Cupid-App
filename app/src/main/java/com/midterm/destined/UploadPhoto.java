package com.midterm.destined;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.midterm.destined.databinding.ActivityUploadPhotoBinding;
import com.midterm.destined.model.UserReal;

import java.util.List;

public class UploadPhoto extends AppCompatActivity {
    private ImageView[] images = new ImageView[6];
    private int selectedIndex = -1;
    private ActivityUploadPhotoBinding binding;
    private Button btn;
    private List<String> selectedInterests; // Danh sách sở thích

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageUri = data.getData();
                        if (selectedIndex != -1) {
                            images[selectedIndex].setImageURI(imageUri);
                            images[selectedIndex].setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Toast.makeText(this, "Ảnh đã được chọn " + (selectedIndex + 1), Toast.LENGTH_SHORT).show();
                            selectedIndex = -1;
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserReal user = (UserReal) getIntent().getSerializableExtra("user");

        // Lấy danh sách sở thích từ Intent
        selectedInterests = getIntent().getStringArrayListExtra("selectedInterests");

        images[0] = binding.imageSlot1;
        images[1] = binding.imageSlot2;
        images[2] = binding.imageSlot3;
        images[3] = binding.imageSlot4;
        images[4] = binding.imageSlot5;
        images[5] = binding.imageSlot6;

        btn = binding.btnContinue;

        for (int i = 0; i < images.length; ++i) {
            final int index = i;
            images[i].setOnClickListener(v -> {
                selectedIndex = index;
                openImageChooser();
            });
        }

//        btn.setOnClickListener(v -> {
//            Intent intent = new Intent(UploadPhoto.this, MyLocation.class);
//            intent.putStringArrayListExtra("selectedInterests", (ArrayList<String>) selectedInterests);
//            startActivity(intent);
//        });
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(UploadPhoto.this, MyLocation.class);
            intent.putExtra("user", user); // Chuyển đối tượng UserReal đã cập nhật
            startActivity(intent);
        });

    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}