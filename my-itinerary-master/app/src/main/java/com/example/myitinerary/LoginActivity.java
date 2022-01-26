package com.example.myitinerary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private TextView goToRegisterBtn, forgotPasswordBtn;
    private Button loginSubmitBtn;
    private EditText editTextEmail, editTextPassword, editTextPasswordConfirm;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        // check if logged in first
        if(mAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }


        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        loginSubmitBtn = (Button) findViewById(R.id.loginSubmitBtn);
        loginSubmitBtn.setOnClickListener(this);
        goToRegisterBtn = (TextView) findViewById(R.id.goToRegisterBtn);
        goToRegisterBtn.setOnClickListener(this);
        forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);
        forgotPasswordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.goToRegisterBtn:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.loginSubmitBtn:
                userLogin();
                break;
            case R.id.forgotPasswordBtn:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }

    }

    private void userLogin()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //form validation
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
        if(password.isEmpty()) {
            editTextPassword.setError("Please enter a password");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else
                {
                    Toast.makeText(LoginActivity.this,
                            "Bad credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}