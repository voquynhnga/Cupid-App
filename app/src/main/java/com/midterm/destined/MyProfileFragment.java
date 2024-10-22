package com.midterm.destined;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.midterm.destined.databinding.FragmentMyProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;


public class MyProfileFragment extends Fragment {

    private FirebaseStorage storage;
    private FragmentMyProfileBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private String mParam1;
    private String mParam2;
    private FirebaseFirestore firestore ;
    private String uid ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            try {
                                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                                Bitmap croppedBitmap = cropCenterSquare(originalBitmap);
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, 300, 300, true);

                                // Hiển thị ảnh lên ImageView
                                binding.imAvatar.setImageBitmap(scaledBitmap);

                                // Lưu ảnh vào Firebase Storage
                                uploadImageToFirebaseStorage(scaledBitmap);

                            } catch (IOException e) {
                                Toast.makeText(getActivity(), "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Image selection canceled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String profileUrl = documentSnapshot.getString("profilePicture");
                if (profileUrl != null) {
                    // Tải ảnh từ URL và chuyển đổi thành Bitmap để cắt
                    Glide.with(getContext())
                            .asBitmap()
                            .load(profileUrl)
                            .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    // Cắt ảnh tải về theo tỷ lệ 1:1
                                    Bitmap croppedBitmap = cropCenterSquare(resource);

                                    // Scale ảnh thành 300x300
                                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, 300, 300, true);

                                    // Hiển thị ảnh đã được scale lên ImageView
                                    binding.imAvatar.setImageBitmap(scaledBitmap);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Xử lý khi ảnh bị huỷ hoặc khi bị thay thế
                                }
                            });
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lỗi khi tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentMyProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

       // return inflater.inflate(R.layout.fragment_my_profile, container, false);
        binding.imSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_MyProfileFragment_to_MySettingFragment);
            }
        });

        binding.btnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // com.google.android.material.imageview.ShapeableImageView
                // chọn ảnh từ thư viện và gán ảnh vào imageview trên ( có id là im_Avatar)
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            }
        });

        return view;
    }



    private Bitmap cropCenterSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = Math.min(width, height);
        int newHeight = newWidth;

        // Tính toán điểm bắt đầu để cắt trung tâm
        int xOffset = (width - newWidth) / 2;
        int yOffset = (height - newHeight) / 2;

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, newWidth, newHeight);
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap) {
        // Chuyển Bitmap thành ByteArray
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Tạo một đường dẫn ngẫu nhiên cho ảnh trong Firebase Storage
        String fileName = "images/" + uid + "/" + UUID.randomUUID() + ".jpg";
        StorageReference storageRef = storage.getReference().child(fileName);

        // Upload ảnh lên Firebase Storage
        UploadTask uploadTask = storageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Lấy URL của ảnh vừa tải lên
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String profileUrl = uri.toString();

                // Lưu URL vào Firestore
                firestore.collection("users").document(uid)
                        .update("profilePicture", profileUrl)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getActivity(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Failed to update profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


}