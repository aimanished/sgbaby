package com.example.a16031940.sgbaby;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    Button btnReg, btnLogin;
    EditText email,cfmPass,password;
    private FirebaseAuth mAuth;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnLogin = findViewById(R.id.reg_login);
        btnReg = findViewById(R.id.reg_btn);
        email = findViewById(R.id.reg_email);
        cfmPass = findViewById(R.id.reg_cfmpassword);
        password = findViewById(R.id.reg_password);
        progressBar = findViewById(R.id.progressBar2);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String emailText = email.getText().toString();
                String passText = password.getText().toString();
                String cfmPassText = cfmPass.getText().toString();
                if (email.getText().toString().equals("") || password.getText().toString().equals("")) {
                    Toast.makeText(Register.this, "No empty fields, please.", Toast.LENGTH_SHORT).show();
                }else if(passText.equals(cfmPassText)){
                    // TODO: implement Firebase Authentication - register
                    mAuth.createUserWithEmailAndPassword(emailText, passText)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Create user", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Log.i("Email", "Email sent.");
                                                            Toast.makeText(Register.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(Register.this,SetUpActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Log.e("Email", "sendEmailVerification", task.getException());
                                                            Toast.makeText(Register.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        String reason = task.getException().getMessage();

                                        Log.e("Create Email", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register.this, "Authentication failed: " + reason,
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                } else {

                  Toast.makeText(Register.this,"Password mismatch",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            sendToMain();

        }

    }

    private void sendToMain() {

        Intent mainIntent = new Intent(Register.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

}
