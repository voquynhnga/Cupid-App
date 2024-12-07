package com.midterm.destined.Presenters;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.midterm.destined.Models.MyProfile;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Views.Profile.MyProfileContract;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MyProfilePresenter implements MyProfileContract.presenter {

    private MyProfileContract.view view;
    private MyProfile model;

    public MyProfilePresenter(MyProfileContract.view view) {
        this.view = view;
        this.model = new MyProfile();
    }

    @Override
    public void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        view.launchImagePicker(intent);
    }

    @Override
    public void logout() {
        model.logout(success -> {
            if (success) {
                view.showMessage("Logged out successfully");
                view.navigateToLogin();
            } else {
                view.showMessage("Failed to log out");
            }
        });
    }

    @Override
    public void navigateToSettings(View view) {
        this.view.navigateToSettings(view);
    }

    public void loadAvatarFromDB() {
        DB.getCurrentUserDocument().get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String profileUrl = documentSnapshot.getString("profilePicture");
                if (profileUrl != null) {
                    Glide.with(view.getActivityContext())
                            .asBitmap()
                            .load(profileUrl)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    Bitmap croppedBitmap = cropCenterSquare(resource);

                                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, 300, 300, true);

                                    view.showProfileImage(scaledBitmap);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Xử lý khi ảnh bị huỷ hoặc khi bị thay thế
                                }
                            });
                }
            }
        });
    }
    //FIX-3
    public void uploadImageToFirebaseStorage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        String fileName = "images/" + DB.getCurrentUser().getUid() + "/" + UUID.randomUUID() + ".jpg";
        StorageReference storageRef = DB.getStorageInstance().getReference().child(fileName);

        UploadTask uploadTask = storageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String profileUrl = uri.toString();

                    DB.getCurrentUserDocument()
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    List<String> imageUrls = (List<String>) documentSnapshot.get("imageUrls");
                                    if (imageUrls == null) {
                                        imageUrls = new ArrayList<>();
                                    }

                                    if (imageUrls.isEmpty()) {
                                        imageUrls.add(profileUrl);
                                    } else {
                                        imageUrls.set(0, profileUrl);
                                    }

                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("imageUrls", imageUrls);
                                    updates.put("profilePicture", profileUrl);

                                    DB.getCurrentUserDocument()
                                            .update(updates)
                                            .addOnSuccessListener(aVoid ->
                                                    Toast.makeText(view.getActivityContext(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
                                            )
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(view.getActivityContext(), "Failed to update profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                            );
                                }
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(view.getActivityContext(), "Failed to fetch user document: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
        ).addOnFailureListener(e ->
                Toast.makeText(view.getActivityContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
    private Bitmap cropCenterSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = Math.min(width, height);
        int newHeight = newWidth;

        int xOffset = (width - newWidth) / 2;
        int yOffset = (height - newHeight) / 2;

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, newWidth, newHeight);
    }

}

