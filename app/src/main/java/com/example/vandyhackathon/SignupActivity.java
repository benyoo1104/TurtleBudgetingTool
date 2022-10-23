package com.example.vandyhackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText email, emailVerification, password, passwordVerification;
    private Button signupBtn;
    private TextView signupQ;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email = findViewById(R.id.email);
        emailVerification = findViewById(R.id.emailVerification);
        password = findViewById(R.id.password);
        passwordVerification = findViewById(R.id.passwordVerification);
        signupBtn = findViewById(R.id.signupBtn);
        signupQ = findViewById(R.id.signupQ);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        signupQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    email.setError("Missing Email");
                } else if (TextUtils.isEmpty(password.getText().toString())) {
                    password.setError("Missing Password");
                } else if (TextUtils.isEmpty(emailVerification.getText().toString())) {
                    emailVerification.setError("Missing Email Verification");
                } else if (TextUtils.isEmpty(passwordVerification.getText().toString())) {
                    passwordVerification.setError("Missing Password Verification");
                } else if (!email.getText().toString().equals(emailVerification.getText().toString())) {
                    Toast.makeText(SignupActivity.this, "Email does not match", Toast.LENGTH_SHORT).show();
                } else if (!password.getText().toString().equals(passwordVerification.getText().toString())) {
                    Toast.makeText(SignupActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Creating Account...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(SignupActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}