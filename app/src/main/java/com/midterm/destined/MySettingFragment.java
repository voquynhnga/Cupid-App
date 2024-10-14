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


        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Save your new information",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}