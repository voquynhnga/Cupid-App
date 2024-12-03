package com.midterm.destined.Views.Authentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.midterm.destined.databinding.ActivityUploadPhotoBinding;
import com.midterm.destined.Models.UserReal;

import java.util.ArrayList;
import java.util.List;

public class UploadPhoto extends AppCompatActivity {
    private ImageView[] images = new ImageView[6];
    private int selectedIndex = -1;
    private ActivityUploadPhotoBinding binding;
    private Button btn;
    private List<String> selectedInterests;
    private StorageReference ref;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Uri[] imageUris = new Uri[6];
    private UserReal user;
    private FirebaseStorage storage;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imageUri = data.getData();
                        if (selectedIndex != -1) {
                            images[selectedIndex].setImageURI(imageUri);
                            images[selectedIndex].setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageUris[selectedIndex] = imageUri;
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

        user = (UserReal) getIntent().getSerializableExtra("user");

        selectedInterests = getIntent().getStringArrayListExtra("selectedInterests");
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        images[0] = binding.imageSlot1;
        images[1] = binding.imageSlot2;
        images[2] = binding.imageSlot3;
        images[3] = binding.imageSlot4;
        images[4] = binding.imageSlot5;
        images[5] = binding.imageSlot6;

        btn = binding.btnContinue;
        ref = storage.getReference();

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
//        btn.setOnClickListener(v -> {
//            Intent intent = new Intent(UploadPhoto.this, MyLocation.class);
//            intent.putExtra("user", user); // Chuyển đối tượng UserReal đã cập nhật
//            startActivity(intent);
//        });

        btn.setOnClickListener(v -> {
            if(noImageSelected()) {
                user.setProfilePicture("https://firebasestorage.googleapis.com/v0/b/cupid-app-ad700.appspot.com/o/avatar_def.jpg?alt=media&token=a96937d6-84c3-4ef3-b0d2-aba2f7affc26");
                saveUserDataAndProceed();
            }
            else {
                uploadImagesAndProceed();
            }
        });

    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private boolean noImageSelected() {
        for (Uri uri : imageUris) {
            if (uri != null) {
                return false;
            }
        }
        return true;
    }

    private void proceedToNextActivity() {
        Intent intent = new Intent(UploadPhoto.this, MyLocation.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private int countSelectedImages() {
        int count = 0;
        for (Uri uri : imageUris) {
            if (uri != null) {
                count++;
            }
        }
        return count;
    }

    private void uploadImagesAndProceed() {
        List<String> imageUrls = new ArrayList<>();
        int totalSelectedImages = countSelectedImages();  // Tổng số ảnh đã chọn

        for (int i = 0; i < imageUris.length; ++i) {
            Uri imageUri = imageUris[i];
            if (imageUri != null) {
                StorageReference imageRef = ref.child("images/" + user.getUid() + "/image_" + i);

                int finalI = i;
                imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());


                            if (finalI == 0) {
                                user.setProfilePicture(uri.toString());
                            }


                            if (imageUrls.size() == totalSelectedImages) {
                                user.setImageUrls(imageUrls);
                                saveUserDataAndProceed();
                            }
                        })
                ).addOnFailureListener(e -> {
                    Toast.makeText(UploadPhoto.this, "Lỗi upload ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }
    }


    private void saveUserDataAndProceed() {
        firestore.collection("users")
                .document(user.getUid())
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        proceedToNextActivity();
                    } else {
                        Toast.makeText(UploadPhoto.this, "Lỗi khi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}