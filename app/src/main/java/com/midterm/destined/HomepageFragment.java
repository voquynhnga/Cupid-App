package com.midterm.destined;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.midterm.destined.databinding.FragmentHomepageBinding;


public class HomepageFragment extends Fragment {

    private FragmentHomepageBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomepageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sự kiện khi bấm vào nút filter để chuyển sang SearchFragment
        binding.filter.setOnClickListener(v -> {
            NavHostFragment.findNavController(HomepageFragment.this)
                    .navigate(R.id.action_global_SearchFragment);
        });

        // Sự kiện khi bấm vào nút story để chuyển sang AddStoryFragment
        binding.story.setOnClickListener(v -> {
            NavHostFragment.findNavController(HomepageFragment.this)
                    .navigate(R.id.action_global_AddStoryFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
