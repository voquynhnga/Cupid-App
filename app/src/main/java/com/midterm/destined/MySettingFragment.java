package com.midterm.destined;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.midterm.destined.databinding.FragmentMyProfileBinding;
import com.midterm.destined.databinding.FragmentMySettingBinding;
import com.midterm.destined.model.GPSAddress;
import com.midterm.destined.model.UserReal;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class MySettingFragment extends Fragment {

    private FragmentMySettingBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private ListenerRegistration userListener;
    private String myPasswordInternal;
    private List<String> myInterestInternal;
    private GPSAddress myLocationGPS;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_my_setting, container, false);

        binding = FragmentMySettingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();


        // Get current user ID
        String uid = mAuth.getCurrentUser().getUid();

        if (uid != null) {
            loadUserData(uid);
        }

//        binding.edFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if(hasFocus){
//                    binding.edFullName.setHint("");
//                }
//                else {
//                    binding.edFullName.setHint("Unset");
//                }
//            }
//        });
//
//        binding.edFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if(hasFocus){
//                    binding.edFullName.setHint("");
//                }
//                else {
//                    binding.edFullName.setHint("Unset");
//                }
//            }
//        });
//
//        binding.edDayOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if(hasFocus){
//                    binding.edDayOfBirth.setHint("");
//                }
//                else {
//                    binding.edDayOfBirth.setHint("Unset");
//                }
//            }
//        });

        binding.edDayOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });


        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            boolean genderPerson = true;
            boolean isSuccess = true;
            @Override
            public void onClick(View view) {
                String fullName = binding.edFullName.getText().toString().trim();
                String dayOfBirth = binding.edDayOfBirth.getText().toString().trim();
                String phoneNumber = binding.edPhoneNumber.getText().toString().trim();

                if(binding.rbMale.isChecked()) genderPerson = true;
                else genderPerson = false;


                if(fullName.isEmpty()) {
                    Toast.makeText(getContext(), "Full name is empty", Toast.LENGTH_SHORT).show();
                    isSuccess =false;
                }
                else if(phoneNumber.isEmpty()) {
                    Toast.makeText(getContext(), "Phone Number is empty", Toast.LENGTH_SHORT).show();
                    isSuccess =false;
                }
                else if(dayOfBirth.isEmpty()) {
                    Toast.makeText(getContext(), "Day Of Birth is empty", Toast.LENGTH_SHORT).show();
                    isSuccess =false;
                }
                else {
                    Toast.makeText(getContext(), "Save your new information", Toast.LENGTH_SHORT).show();
                    isSuccess =true;
                }

                if (isSuccess) {
                    // Cập nhật dữ liệu người dùng vào Firestore
                    String uid = mAuth.getCurrentUser().getUid();

                    UserReal updatedUser = new UserReal();

                    updatedUser.setUid(uid);
                    updatedUser.setEmail(mAuth.getCurrentUser().getEmail());
                    updatedUser.setPassword(myPasswordInternal);
                    updatedUser.setInterests(myInterestInternal);
                    updatedUser.setLocation(myLocationGPS);
                    updatedUser.setFullName(fullName);
                    updatedUser.setPhoneNumber(phoneNumber);
                    updatedUser.setDateOfBirth(dayOfBirth);
                    updatedUser.setGender(genderPerson ? "Male" : "Female");

                    firestore.collection("users").document(uid)
                            .set(updatedUser)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "User information updated successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to update user information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("MySettingFragment", "Error updating user", e);
                            });
                }

            }
        });

        return view;
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            binding.edDayOfBirth.setText(selectedDate);
        }, year, month, day
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void loadUserData(String uid) {
        DocumentReference userRef = firestore.collection("users").document(uid);
        userListener = userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w("MySettingFragment", "Listened failed", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                UserReal user = documentSnapshot.toObject(UserReal.class);
                if (user != null) {
                    myPasswordInternal = user.getPassword();
                    myInterestInternal = user.getInterests();
                    myLocationGPS = user.getLocation();
                    binding.edFullName.setText(user.getFullName());
                    binding.edPhoneNumber.setText(user.getPhoneNumber());
                    binding.edDayOfBirth.setText(user.getDateOfBirth());
                    // Cập nhật các trường khác nếu cần, ví dụ: giới tính, sở thích, v.v.
                    if(Objects.equals(user.getGender(), "Male")){
                        binding.rbMale.setChecked(true);
                    }
                    else binding.rbFemale.setChecked(true);

                }
            } else {
                Log.d("MySettingFragment", "No such document");
            }
        });
    }




}