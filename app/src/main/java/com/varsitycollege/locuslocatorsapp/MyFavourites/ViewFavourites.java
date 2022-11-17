package com.varsitycollege.locuslocatorsapp.MyFavourites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.varsitycollege.locuslocatorsapp.R;
import com.varsitycollege.locuslocatorsapp.databinding.ActivityMyFavouritesBinding;
import com.varsitycollege.locuslocatorsapp.databinding.ActivityViewFavouritesBinding;

public class ViewFavourites extends FragmentActivity implements OnMapReadyCallback {

    private ActivityViewFavouritesBinding binding;

    GoogleMap map;
    SupportMapFragment mapFragment;

    private String locationName, latitude, longitude;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewFavouritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_maps);
        mapFragment.getMapAsync(this);

        //get data from intent
        Intent intent = getIntent();
        locationName = intent.getStringExtra("locationName");
        latitude = intent.getStringExtra("locationLat");
        longitude = intent.getStringExtra("locationLon");

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        //To indicate the location marker
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(locationName);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.heart));
        //To zoom into the map
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
        //Adding the location marker to the map
        marker = map.addMarker(markerOptions);
    }
}