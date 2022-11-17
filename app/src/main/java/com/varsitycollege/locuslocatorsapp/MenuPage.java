package com.varsitycollege.locuslocatorsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.varsitycollege.locuslocatorsapp.MyFavourites.MyFavourites;
import com.varsitycollege.locuslocatorsapp.MyLocation.MyLocation;
import com.varsitycollege.locuslocatorsapp.NearbyLandmarks.NearbyLandmarks;
import com.varsitycollege.locuslocatorsapp.RouteFinder.DirectionActivity;
import com.varsitycollege.locuslocatorsapp.Settings.ProfileActivity;
import com.varsitycollege.locuslocatorsapp.ShareLocation.ShareLocation;
import com.varsitycollege.locuslocatorsapp.databinding.ActivityMenuPageBinding;

//nicole started
public class MenuPage extends AppCompatActivity {

    //view binding
    private ActivityMenuPageBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        binding.routeFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPage.this, DirectionActivity.class));
            }
        });

        binding.myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPage.this, MyLocation.class));
            }
        });

        binding.nearbyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPage.this, NearbyLandmarks.class));
            }
        });

        binding.shareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPage.this, ShareLocation.class));
            }
        });

        binding.myFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPage.this, MyFavourites.class));
            }
        });

        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPage.this, ProfileActivity.class));
            }
        });

    }

    //method checks if user is logged in or not
    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            loadUserInfo();
        }
        else{
            //go to login screen if not logged in
            startActivity(new Intent(MenuPage.this, MainActivity.class));
            finish();
        }
    }

    //method loads user information in profile
    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info from users
                        String name = ""+snapshot.child("first_name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        //set the data
                        binding.nameTv.setText(name+"!");

                        //set up image using glide
                        Glide.with(MenuPage.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ic_profile)
                                .into(binding.profileImageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}
//nicole ended