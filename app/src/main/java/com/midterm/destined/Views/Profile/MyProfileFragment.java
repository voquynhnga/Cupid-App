package com.midterm.destined.Views.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.midterm.destined.Presenters.MyProfilePresenter;
import com.midterm.destined.Views.Authentication.GetStarted;
import com.midterm.destined.R;
import com.midterm.destined.databinding.FragmentMyProfileBinding;


public class MyProfileFragment extends Fragment implements MyProfileContract.view {

    private FragmentMyProfileBinding binding;
    private MyProfilePresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MyProfilePresenter(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.imSetting.setOnClickListener(v -> presenter.navigateToSettings(v));
        binding.btnAvatar.setOnClickListener(v -> presenter.chooseImage());
        binding.imLogout.setOnClickListener(v -> presenter.logout());

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
        NavController navController = Navigation.findNavController( view);
        navController.navigate(R.id.action_MyProfileFragment_to_MySettingFragment);
    }

    @Override
    public void navigateToLogin() {
        Intent intent = new Intent(getActivity(), GetStarted.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void launchImagePicker(Intent intent) {
        startActivityForResult(intent, 100);
    }
}
