package com.varsitycollege.locuslocatorsapp.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.varsitycollege.locuslocatorsapp.MainActivity;
import com.varsitycollege.locuslocatorsapp.R;
import com.varsitycollege.locuslocatorsapp.RouteFinder.DirectionActivity;
import com.varsitycollege.locuslocatorsapp.databinding.ActivityProfileBinding;

import java.util.Calendar;
import java.util.Locale;

/*
 * Code Attribution
 * Name: Atif Pervaiz
 * Published: 1 August 2021
 * URL: https://youtu.be/F8L-gnxDJ-o
 * nicole started
 */
public class ProfileActivity extends AppCompatActivity {

    //view binding
    private ActivityProfileBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    public static final String EXTRA_TEXT = "com.varsitycollege.locuslocatorsapp.EXTRA_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setup firebase
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        binding.switchMeasurement.setChecked(sharedPreferences.getBoolean("value", true));

        /*
         * Code Attribution
         * Name: Android Coding
         * Published: 29 July 2019
         * URL: https://youtu.be/RyiTx8lWdx0
         */
        binding.switchMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.switchMeasurement.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    binding.switchMeasurement.setChecked(true);

                    Toast.makeText(ProfileActivity.this, "Changed to Miles", Toast.LENGTH_SHORT).show();
                    String unit = "miles";
                    Intent intent = new Intent(ProfileActivity.this, DirectionActivity.class);
                    intent.putExtra("unit", unit);
                    startActivity(intent);
                }
                else{
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    binding.switchMeasurement.setChecked(false);

                    Toast.makeText(ProfileActivity.this, "Changed to Kilometers", Toast.LENGTH_SHORT).show();
                    String unit = "km";
                    Intent intent = new Intent(ProfileActivity.this, DirectionActivity.class);
                    intent.putExtra("unit", unit);
                    startActivity(intent);
                }
            }
        });
        /*
         * Code Attribution Ended*/

        //handles log out button clicked
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        });

        //handles back button clicked
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handles edit button clicked
        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ProfileEdit.class));
            }
        });
    }

    //method loads user information in profile
    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info from users
                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("first_name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();

                        //format date
                        String formatDate = formatTimestamp(Long.parseLong(timestamp));

                        //set the data
                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);
                        binding.joinedTv.setText(formatDate);

                        //set up image using glide
                        Glide.with(ProfileActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ic_profile)
                                .into(binding.profileImageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    //method formats date to dd--mm--yyyy
    public static final String formatTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();

        return date;
    }
}
/*
 * nicole ended
 * Code Attribution Ended*/