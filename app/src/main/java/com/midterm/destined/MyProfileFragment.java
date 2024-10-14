package com.midterm.destined;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.midterm.destined.databinding.FragmentMyProfileBinding;

import java.io.IOException;


public class MyProfileFragment extends Fragment {


    private FragmentMyProfileBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private String mParam1;
    private String mParam2;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//
//        }
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            try {
                                // Chuyển đổi URI thành Bitmap
                                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);

                                // Cắt ảnh theo tỷ lệ 1:1
                                Bitmap croppedBitmap = cropCenterSquare(originalBitmap);

                                // Scale ảnh thành 300x300
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, 300, 300, true);

                                // Hiển thị ảnh đã được scale lên ImageView
                                binding.imAvatar.setImageBitmap(scaledBitmap);

                            } catch (IOException e) {
                                Toast.makeText(getActivity(), "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Image selection canceled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
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

}