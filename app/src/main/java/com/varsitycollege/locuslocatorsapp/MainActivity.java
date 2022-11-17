package com.varsitycollege.locuslocatorsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.varsitycollege.locuslocatorsapp.databinding.ActivityMainBinding;

/*
 * Code Attribution
 * Name: Atif Pervaiz
 * Published: 9 May 2021
 * URL: https://youtu.be/SbUtFAu9O7k
 * alieya started
 */
public class MainActivity extends AppCompatActivity {

    //view binding
    ActivityMainBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //handles sign in button
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        //handles button to register activity
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });
    }

    //data validation before logging in user
    private String email = "", password = "";
    private void validateData(){
        //get data
        email = binding.email.getText().toString().trim();
        password = binding.password.getText().toString().trim();

        //validate data
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
        }
        else if(password.length() < 6){
            Toast.makeText(this, "Password should be more than 6 characters", Toast.LENGTH_SHORT).show();
        }
        else{
            loginUser();
        }
    }

    //login user method called after validation is correct
    private void loginUser() {
        progressDialog.setMessage("Logging In...");

        //login user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //successful login
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, MenuPage.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failure to login
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
/*
 * alieya ended
 * Code Attribution Ended*/