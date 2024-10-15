package com.midterm.destined;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class CardFragment extends Fragment {

    private static final String ARG_PROFILE = "userProfile";

    public static CardFragment newInstance(UserProfile userProfile) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROFILE, (Parcelable) userProfile);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView profileImage = view.findViewById(R.id.profileImage);
        TextView userName = view.findViewById(R.id.userName);
        TextView location = view.findViewById(R.id.location);
        TextView bio = view.findViewById(R.id.bio);

        if (getArguments() != null) {
            UserProfile profile = getArguments().getParcelable(ARG_PROFILE);
            if (profile != null) {
                userName.setText(profile.getName() + ", " + profile.getAge());
                location.setText(profile.getLocation());
                bio.setText(profile.getBio());

                Glide.with(this)
                        .load(profile.getProfileImageUrl())
                        .into(profileImage);
            }
        }
    }
}
