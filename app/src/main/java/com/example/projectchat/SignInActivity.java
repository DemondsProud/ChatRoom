package com.example.projectchat;


import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    // Firebase Authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        TextInputEditText editTextEmail = findViewById(R.id.editTextloginEmail);
        TextInputEditText editTextPassword = findViewById(R.id.editTextloginPassword);
        Button buttonSignIn = findViewById(R.id.buttonSignIn);
        TextView textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        TextView textViewSignUp = findViewById(R.id.textViewSignUp);

        // Sign In Button Click Listener
        buttonSignIn.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate inputs
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Sign in with Firebase Authentication
                signInWithEmailAndPassword(email, password);
            }
        });

        // Forgot Password Link Click Listener
        textViewForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            // Add logic to handle forgot password
        });

        // Sign Up Link Click Listener
        textViewSignUp.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish(); // Close the current activity
        });
    }

    // Sign in with email and password using Firebase Authentication
    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignInActivity.this, "Sign In Successful!", Toast.LENGTH_SHORT).show();
                        // Navigate to the main activity (e.g., ChatActivity)
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        finish(); // Close the current activity
                    } else {
                        // Sign in failed
                        Toast.makeText(SignInActivity.this, "Authentication failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if the user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, navigate to the main activity
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish(); // Close the current activity
        }
    }
}