package com.midterm.destined;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.midterm.destined.databinding.FragmentMyProfileBinding;
import com.midterm.destined.databinding.FragmentMySettingBinding;


public class MySettingFragment extends Fragment {

    private FragmentMySettingBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_my_setting, container, false);

        binding = FragmentMySettingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        binding.edFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    binding.edFullName.setHint("");
                }
                else {
                    binding.edFullName.setHint("Unset");
                }
            }
        });

        binding.edUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    binding.edUserName.setHint("");
                }
                else {
                    binding.edUserName.setHint("Unset");
                }
            }
        });

        binding.edAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    binding.edAddress.setHint("");
                }
                else {
                    binding.edAddress.setHint("Unset");
                }
            }
        });


        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = binding.edFullName.getText().toString().trim();
                String userName = binding.edUserName.getText().toString().trim();
                String address = binding.edAddress.getText().toString().trim();


                if(userName.length() < 8){
                    Toast.makeText(getContext(), "User name must be longer than 8 characters ", Toast.LENGTH_SHORT).show();
                }
                else if(fullName.isEmpty()) {
                    Toast.makeText(getContext(), "Full name is empty", Toast.LENGTH_SHORT).show();
                }
                else if(address.isEmpty()) {
                    Toast.makeText(getContext(), "Address is empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Save your new information", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}