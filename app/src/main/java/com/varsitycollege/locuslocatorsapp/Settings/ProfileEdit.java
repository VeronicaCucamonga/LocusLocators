package com.varsitycollege.locuslocatorsapp.Settings;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.varsitycollege.locuslocatorsapp.R;
import com.varsitycollege.locuslocatorsapp.databinding.ActivityProfileEditBinding;

import java.util.HashMap;

/*
 * Code Attribution
 * Name: Atif Pervaiz
 * Published: 1 August 2021
 * URL: https://youtu.be/F8L-gnxDJ-o
 * nicole started
 */
public class ProfileEdit extends AppCompatActivity {

    //view binding
    private ActivityProfileEditBinding binding;

    //progress dialog
    private ProgressDialog progressDialog;
    //firebase auth
    private FirebaseAuth firebaseAuth;

    private Uri ImageUri = null;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //set up firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        //handle click to go back
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle when profile image is clicked
        binding.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
            }
        });

        //handle the update button
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void verifyPermissions(String permission, int requestCode) {
        //check if permission is granted
        if(ContextCompat.checkSelfPermission(ProfileEdit.this, permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(ProfileEdit.this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImageAttachMenu();
            }
        }
    }
    //----------CODE ATTRIBUTION ENDS----------
    //nicole ends

    //method validates data before editing
    String name = "", lastName="";
    private void validateData() {
        name = binding.firstNameEdit.getText().toString().trim();
        lastName = binding.lastNameEdit.getText().toString().trim();

        //validate data
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter a first name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(lastName)){
            Toast.makeText(this, "Please enter a last name", Toast.LENGTH_SHORT).show();
        }
        else{
            if(ImageUri == null){
                //update without image
                updateProfile("");
            }
            else{
                uploadImage();
            }
        }
    }

    //method uploads image user inputs
    private void uploadImage() {
        progressDialog.setMessage("Updating profile image");
        progressDialog.show();

        //image path and name, using uid to replace previous
        String filePathAndName = "ProfileImage/"+firebaseAuth.getUid();

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(ImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedImageUrl = ""+uriTask.getResult();

                        updateProfile(uploadedImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEdit.this, "Failed to Upload Image:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //method updates user entire profile
    private void updateProfile(String imageUrl){
        progressDialog.setMessage("Updating User Profile");
        progressDialog.show();

        //set up data to update firebase
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("first_name", ""+name);
        if(ImageUri != null){
            hashMap.put("profileImage", ""+imageUrl);
        }

        //update data to firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEdit.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEdit.this, "Failed to update database:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //shows pop up of select image
    private void showImageAttachMenu() {
        //init and set up popup menu
        PopupMenu popupMenu = new PopupMenu(this, binding.profileImageView);
        popupMenu.getMenu().add(Menu.NONE, 0, 0,"Camera");
        popupMenu.getMenu().add(Menu.NONE, 1, 1,"Gallery");

        popupMenu.show();

        //handle menu item clicked
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //get id of item clicked
                int which = menuItem.getItemId();
                if (which==0){
                    //camera clicked
                    pickImageCamera();
                }
                else if (which==1){
                    //gallery clicked
                    pickImageGallery();
                }
                return false;
            }
        });
    }

    //intent if user clicks gallery
    private void pickImageGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    //intent if user picks camera
    private void pickImageCamera() {
        //intent to pick image from camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        ImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of camera intent
                    //get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        binding.profileImageView.setImageURI(ImageUri);
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of gallery intent
                    //get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        ImageUri = data.getData();
                        binding.profileImageView.setImageURI(ImageUri);
                    }
                }
            }
    );

    //loads user information in the edit activity
    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info from users
                        String name = ""+snapshot.child("first_name").getValue();
                        String lastName = ""+snapshot.child("last_name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        //set the data
                        binding.firstNameEdit.setText(name);
                        binding.lastNameEdit.setText(lastName);

                        //set up image using glide
                        Glide.with(ProfileEdit.this)
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
/*
 * nicole ended
 * Code Attribution Ended*/