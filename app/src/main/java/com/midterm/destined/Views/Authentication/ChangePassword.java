package com.midterm.destined.Views.Authentication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.midterm.destined.R;
import com.midterm.destined.databinding.ActivityChangePasswordBinding;

public class ChangePassword extends AppCompatActivity {
    private Button ok, back;
    private EditText etCurr, etNew, etCon;
    private ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ok = binding.btnChangePassword;
        back = binding.btnBack;
        etCurr = binding.etCurrentPassword;
        etNew = binding.etNewPassword;
        etCon = binding.etConfirmPassword;

        ok.setOnClickListener(v -> {
            String currentPassword = etCurr.getText().toString().trim();
            String newPassword = etNew.getText().toString().trim();
            String confirmPassword = etCon.getText().toString().trim();

            if (newPassword.equals(confirmPassword)) {
                reauthenticateAndChangePassword(currentPassword, newPassword);
            } else {
                Toast.makeText(ChangePassword.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            onBackPressed();
        });

    }
    private void reauthenticateAndChangePassword(String currentPassword, String newPassword) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updatePassword(user, newPassword);
                        } else {
                            Toast.makeText(ChangePassword.this, "Re-authentication failed. Check your current password.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updatePassword(FirebaseUser user, String newPassword) {
        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePassword.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        onBackPressed();
                    } else {
                        Toast.makeText(ChangePassword.this, "Failed to change password.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}