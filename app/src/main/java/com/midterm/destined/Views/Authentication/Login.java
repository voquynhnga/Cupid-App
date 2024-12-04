package com.midterm.destined.Views.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Views.Main.MainActivity;
import com.midterm.destined.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {
    private EditText userEmail, password;
    private Button btLogin;
    private TextView tvSignUp;
    private ActivityLoginBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userEmail = binding.mail;
        password = binding.pass;
        btLogin = binding.continueButton;
        tvSignUp = binding.signupText;

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userEmail.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(user,pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                                else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });



            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = DB.getCurrentUser();

    }
}