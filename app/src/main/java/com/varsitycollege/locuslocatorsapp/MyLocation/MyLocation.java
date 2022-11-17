package com.varsitycollege.locuslocatorsapp.MyLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.varsitycollege.locuslocatorsapp.R;
import com.varsitycollege.locuslocatorsapp.databinding.ActivityMyLocationBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/*
 * Code Attribution
 * Name: Android Coding
 * Published: 17 April 2020
 * URL: https://youtu.be/p0PoKEPI65o
 * masibonge started
 */
public class MyLocation extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityMyLocationBinding binding;

    private GoogleMap map;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;

    String location;

    Marker marker;
    private static final int REQUEST_CODE = 0;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_maps);
        supportMapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        if (ActivityCompat.checkSelfPermission(MyLocation.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MyLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location = binding.inputLocationtxt.getText().toString();
                map.clear();
                if (location.equals("")) {
                    Toast.makeText(MyLocation.this, "Type in any location name", Toast.LENGTH_SHORT).show();
                } else {
                    Geocoder geocoder = new Geocoder(MyLocation.this, Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(location, 1);
                        if (addressList.size() > 0) {
                            LatLng latLng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                            //To zoom into the map
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                            //Adding the location marker to the map
                            marker = map.addMarker(markerOptions);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        binding.saveSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location.equals("")) {
                    Toast.makeText(MyLocation.this, "Type in any location name", Toast.LENGTH_SHORT).show();
                } else {
                    saveLandMark();
                }
            }
        });

        binding.goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                location = binding.inputLocationtxt.getText().toString();

                double longitude = 0, latitude = 0;

                Geocoder geocoder = new Geocoder(MyLocation.this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocationName(location, 1);
                    if (addressList.size() > 0) {
                        longitude = addressList.get(0).getLongitude();
                        latitude = addressList.get(0).getLatitude();

                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q="+latitude+","+longitude+"&mode=1"));
                        intent.setPackage("com.google.android.apps.maps");

                        if(intent.resolveActivity(getPackageManager()) != null){
                            startActivity(intent);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*
     * nicole started
     * Code Attribution
     * Name: Atif Pervaiz
     * Published: 5 September 2021
     * URL: https://youtu.be/x1Vh3GlF1ng
     */
    private void saveLandMark() {

        location = binding.inputLocationtxt.getText().toString();

        double longitude = 0, latitude = 0;

        Geocoder geocoder = new Geocoder(MyLocation.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(location, 1);
            if (addressList.size() > 0) {
                longitude = addressList.get(0).getLongitude();
                latitude = addressList.get(0).getLatitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //show progress dialog
        progressDialog.setMessage("Saving Landmark...");
        progressDialog.show();

        //get current user uid
        String uid = firebaseAuth.getUid();

        //timestamp
        long timestamp = System.currentTimeMillis();

        //get location
        location = binding.inputLocationtxt.getText().toString();

        //setup data to db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("location_name", location);
        hashMap.put("latitude", latitude);
        hashMap.put("longitude", longitude);
        hashMap.put("timestamp", ""+timestamp);

        //add to firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favourite Landmarks");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void unused){
                        //success
                        progressDialog.dismiss();
                        Toast.makeText(MyLocation.this, "Category Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e){
                        //failure
                        progressDialog.dismiss();
                        Toast.makeText(MyLocation.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    /*
     * Code Attribution Ends
     * nicole ended
     */

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            if (ActivityCompat.checkSelfPermission(MyLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            googleMap.setMyLocationEnabled(true);
                            if (marker != null) {
                                marker.remove();
                            }
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            //To indicate the location marker
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Current Position");
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                            //To zoom into the map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                            //Adding the location marker to the map
                            marker = map.addMarker(markerOptions);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                break;
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
    }
}
/*
 * masibonge ended
 * Code Attribution Ended*/