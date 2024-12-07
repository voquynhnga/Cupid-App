package com.midterm.destined.Views.Profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.midterm.destined.Presenters.MyProfilePresenter;
import com.midterm.destined.Views.Authentication.GetStarted;
import com.midterm.destined.R;
import com.midterm.destined.Views.Profile.MyProfileContract;
import com.midterm.destined.databinding.FragmentMyProfileBinding;

import java.io.IOException;
import java.util.Objects;

public class MyProfileFragment extends Fragment implements MyProfileContract.view {

    private FragmentMyProfileBinding binding;
    private MyProfilePresenter presenter;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MyProfilePresenter(this);


        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImageUri = data.getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), selectedImageUri);

                                presenter.uploadImageToFirebaseStorage(bitmap);

                                showProfileImage(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                                showMessage("Failed to load image");
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.imSetting.setOnClickListener(v -> presenter.navigateToSettings(v));
        binding.btnAvatar.setOnClickListener(v -> presenter.chooseImage());
        binding.imLogout.setOnClickListener(v -> presenter.logout());

        presenter.loadAvatarFromDB();
        return view;
    }

    @Override
    public void showProfileImage(Bitmap bitmap) {
        binding.imAvatar.setImageBitmap(bitmap);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToSettings(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_MyProfileFragment_to_MySettingFragment);
    }

    @Override
    public void navigateToLogin() {
        Intent intent = new Intent(getActivity(), GetStarted.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void launchImagePicker(Intent intent) {
        imagePickerLauncher.launch(intent);
    }

    @Override
    public Activity getActivityContext() {
            return getActivity();
    }
}
