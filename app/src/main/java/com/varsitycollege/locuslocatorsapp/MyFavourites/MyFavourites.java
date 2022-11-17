package com.varsitycollege.locuslocatorsapp.MyFavourites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.varsitycollege.locuslocatorsapp.databinding.ActivityMyFavouritesBinding;

import java.util.ArrayList;

/*
 * Code Attribution
 * Name: Atif Pervaiz
 * Published: 23 May 2021
 * URL: https://youtu.be/j6GrP2MdFos
 * nicole started
 */
public class MyFavourites extends AppCompatActivity {

    //view binding
    private ActivityMyFavouritesBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //arraylist to store landmarks
    private ArrayList<ModelLandmark> landmarkArrayList;

    //adapter
    private AdapterLandmark adapterLandmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyFavouritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadLandmarks();

        //back button pressed
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //alieya started
        //egg button clicked
        binding.eggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEggPressed();
            }
            private void onEggPressed() {
                startActivity(new Intent(MyFavourites.this, GameActivity.class));
            }
        });
        //alieya ended
    }

    //method to load landmarks from firebase
    private void loadLandmarks() {
        //method loads all landmarks from the firebase database
        //init arraylist
        landmarkArrayList = new ArrayList<>();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();

        //get all landmarks from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favourite Landmarks");
        ref.orderByChild("uid").equalTo(uid).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear arraylist before adding data
                        landmarkArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get data

                            ModelLandmark model = ds.getValue(ModelLandmark.class);

                            //add to arraylist
                            landmarkArrayList.add(model);
                        }
                        //setup adapter
                        adapterLandmark = new AdapterLandmark(MyFavourites.this, landmarkArrayList);
                        //setup adapter to recyclerview
                        binding.landmarkRv.setAdapter(adapterLandmark);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}
/*
 * nicole ended
 * Code Attribution Ended*/