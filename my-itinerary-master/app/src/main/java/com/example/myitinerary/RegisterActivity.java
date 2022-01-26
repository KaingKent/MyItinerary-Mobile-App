package com.example.myitinerary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextTextPersonName, editTextEmail, editTextPassword,
            editTextPasswordConfirm;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        TextView goToLoginBtn = findViewById(R.id.goToLoginBtn);
        goToLoginBtn.setOnClickListener(this);

        Button registerSubmitBtn = findViewById(R.id.registerSubmitBtn);
        registerSubmitBtn.setOnClickListener(this);

        editTextTextPersonName = findViewById(R.id.editTextPersonName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.goToLoginBtn) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (v.getId() == R.id.registerSubmitBtn) {
            registerUser();
        }
    }

    private void registerUser() {
        String fullName = editTextTextPersonName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String password_c = editTextPasswordConfirm.getText().toString().trim();

        // form validation
        if(fullName.isEmpty()) {
            editTextTextPersonName.setError("Please enter a name");
            editTextTextPersonName.requestFocus();
            return;
        }
        if(!fullName.contains(" "))
        {
            editTextTextPersonName.setError("Please enter a first and last name");
            editTextTextPersonName.requestFocus();
            return;
        }
        if(email.isEmpty()) {
            editTextEmail.setError("Please enter an email");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty() || password_c.isEmpty()) {
            editTextPassword.setError("Please enter a password and confirm");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 8)
        {
            editTextPassword.setError("Password must be at least 8 characters");
            editTextPassword.requestFocus();
            return;
        }
        if(!password.matches(password_c)) {
            editTextPasswordConfirm.setError("Passwords do not match");
            editTextPasswordConfirm.requestFocus();
            return;
        }

        //create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        //location and bio can be set later in settings
                        User user = new User(fullName, email, "", "");
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(Objects.requireNonNull(
                                        FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                .setValue(user).addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful())
                                    {
                                        FirebaseAuth.getInstance().getCurrentUser().
                                                sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this,
                                                "Email verification sent",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this,
                                                "Failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("itineraries")
                                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                addOnCompleteListener(task12 -> {
                                    if(task12.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this,
                                                "Registration successful, you can log in now",
                                                Toast.LENGTH_LONG).show();
                                       CreateItinerary.createItineraryEntry("My First Itinerary",
                                               "This is its brief description.",
                                               FirebaseAuth.getInstance().getCurrentUser().getUid(), "0", "0");
                                        startActivity(new Intent(
                                                RegisterActivity.this,
                                                LoginActivity.class));

                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this,
                                                "Failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
    }
}