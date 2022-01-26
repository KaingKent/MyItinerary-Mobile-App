package com.example.myitinerary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private Button resetPSubmitBtn;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        editTextEmail = findViewById(R.id.editTextEmailAddress);
        resetPSubmitBtn = findViewById(R.id.resetPSubmitBtn);
        resetPSubmitBtn.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.resetPSubmitBtn) {
            resetPassword();
        }
    }

    private void resetPassword() {

        String email = editTextEmail.getText().toString().trim();
        if(email.isEmpty()) {
            editTextEmail.setError("Enter an email please");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Enter a valid email please");
            editTextEmail.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "We'll send you an email",
                                    Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Error. Make sure email is correct",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}