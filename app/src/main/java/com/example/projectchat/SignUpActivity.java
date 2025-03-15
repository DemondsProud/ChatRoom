package com.example.projectchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        TextInputEditText editTextFullName = findViewById(R.id.editTextFullName);
        TextInputEditText editTextEmail = findViewById(R.id.editTextloginEmail);
        TextInputEditText editTextPassword = findViewById(R.id.editTextloginPassword);
        TextInputEditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        TextView textViewSignIn = findViewById(R.id.textViewSignIn);

        // Sign Up Button Click Listener
        buttonSignUp.setOnClickListener(v -> {
            Log.d("SignUpActivity", "Sign Up button clicked"); // Debug log
            String fullName = editTextFullName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            // Debug logs for input values
            Log.d("SignUpActivity", "Full Name: " + fullName);
            Log.d("SignUpActivity", "Email: " + email);
            Log.d("SignUpActivity", "Password: " + password);
            Log.d("SignUpActivity", "Confirm Password: " + confirmPassword);

            // Validate inputs
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed with sign-up logic
                signUpUser(email, password);
            }
        });

        // Sign In Link Click Listener
        textViewSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish(); // Close the current activity
        });
    }

    private void signUpUser(String email, String password) {
        Log.d("SignUpActivity", "Attempting to sign up with email: " + email); // Debug log
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                        // Navigate to Sign-In Activity
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        finish(); // Close the current activity
                    } else {
                        // If sign up fails, display a message to the user.
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            Toast.makeText(SignUpActivity.this, "Weak password.", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthUserCollisionException e) {
                            Toast.makeText(SignUpActivity.this, "User with this email already exists.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(SignUpActivity.this, "Sign Up Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("SignUpActivity", "Sign Up Error: " + e.getMessage());
                        }
                    }
                });
    }
}
