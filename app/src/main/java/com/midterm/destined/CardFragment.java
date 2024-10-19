package com.midterm.destined;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midterm.destined.model.User;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class CardFragment extends Fragment {

    private static final String ARG_PROFILE = "userProfile";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static CardFragment newInstance(User user) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROFILE, (Parcelable) user);
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

//        ImageView profileImage = view.findViewById(R.id.profileImage);
        TextView userName = view.findViewById(R.id.userName);
        TextView location = view.findViewById(R.id.location);
        TextView bio = view.findViewById(R.id.bio);


        if (getArguments() != null) {
            User profile = getArguments().getParcelable(ARG_PROFILE);
            if (profile != null) {
                Log.d("DEBUG", "UID: " + profile.getUid());
                fetchUserDataFromFirestore(profile.getUid(), userName, location, bio);
            }
        }

    }

    private void fetchUserDataFromFirestore(String uid, TextView userName, TextView location, TextView bio) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("Firestore", "Document snapshot: " + documentSnapshot.getData()); // Kiểm tra dữ liệu
                    if (documentSnapshot.exists()) {
                        User userProfile = documentSnapshot.toObject(User.class);
                        if (userProfile != null) {
                            int age = calculateAge(userProfile.getDateOfBirth());
                            userName.setText(userProfile.getFullName() + ", " + age);
                            location.setText(userProfile.getLocation());
                            bio.setText(userProfile.getBio());

//                            Glide.with(this)
//                                    .load(userProfile.getImageURL())
//                                    .into(profileImage);
                        }
                        else {
                            Log.e("Firestore", "Document does not exist");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching user data", e);
                });
    }

    public static int calculateAge(String dateOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter);
        LocalDate currentDate = LocalDate.now();
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();

        }
        else{
            return 0;
        }
    }
}
