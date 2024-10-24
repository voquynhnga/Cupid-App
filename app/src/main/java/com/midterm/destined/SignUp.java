package com.midterm.destined;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midterm.destined.databinding.ActivitySignUpBinding;
import com.midterm.destined.model.GPSAddress;
import com.midterm.destined.model.UserReal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SignUp extends AppCompatActivity {

    private EditText dateOfBirth, password, confirmPassword, email, fullName, phoneNumber, userName;
    private ActivitySignUpBinding binding;
    private TextView textViewLogin, tvErr;
    private Button btSignUp;
    private RadioGroup genderGroup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        dateOfBirth = binding.etDob;
        password = binding.etPassword;
        confirmPassword = binding.etConfirmPassword;
        email = binding.etEmail;
        fullName = binding.etFullName;
        phoneNumber = binding.etPhone;
        textViewLogin = binding.alreadyHaveAccount;
        btSignUp = binding.btnSignUp;
        genderGroup = binding.rgGender;
        userName = binding.etUserName;
        tvErr = binding.tvUsernameError;

        dateOfBirth.setOnClickListener(v -> showDatePicker());

        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        });

        btSignUp.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString();
            String confirmPasswordInput = confirmPassword.getText().toString();
            String fullNameInput = fullName.getText().toString().trim();
            String phoneInput = phoneNumber.getText().toString().trim();
            String dobInput = dateOfBirth.getText().toString().trim();
            String genderInput = getSelectedGender();
            String userNameInput = userName.getText().toString().trim();

            if (passwordInput.equals(confirmPasswordInput)) {
                checkUsernameExists(userNameInput, emailInput, passwordInput, fullNameInput, phoneInput, dobInput, genderInput);
            } else {
                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignUp.this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            dateOfBirth.setText(selectedDate);
        }, year, month, day
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private String getSelectedGender() {
        int selectedId = genderGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        return radioButton == null ? "" : radioButton.getText().toString();
    }

    private void checkUsernameExists(String username, String email, String password, String fullName, String phone, String dob, String gender) {
        db.collection("users").whereEqualTo("username", username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        tvErr.setText("Username already exists, please choose another username.");
                    } else {
                        registerUser(email, password, fullName, phone, dob, gender, username);
                    }
                });
    }

    private void registerUser(String email, String password, String fullName, String phone, String dob, String gender, String userName) {
        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty() || dob.isEmpty() || gender.isEmpty()) {
            tvErr.setText("Please fill in all the information.");
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            List<String> interests = new ArrayList<>();
                            GPSAddress location = new GPSAddress(0.0, 0.0);
                            String pic = "";
                            List<String> url = new ArrayList<>();


                            UserReal user = new UserReal(uid, email, fullName, phone, dob, gender, interests, location, pic, url , userName);

                            Intent intent = new Intent(SignUp.this, Interests.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(SignUp.this, "Đăng nhập không thành công: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(UserReal user) {
        db.collection("users").document(user.getUid())
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, LookingFor.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUp.this, "Error saving user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}