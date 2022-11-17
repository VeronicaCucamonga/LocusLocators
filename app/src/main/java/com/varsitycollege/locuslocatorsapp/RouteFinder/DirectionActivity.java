package com.varsitycollege.locuslocatorsapp.RouteFinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.android.SphericalUtil;
import com.varsitycollege.locuslocatorsapp.R;
import com.varsitycollege.locuslocatorsapp.RouteFinder.directionhelpers.FetchURL;
import com.varsitycollege.locuslocatorsapp.RouteFinder.directionhelpers.TaskLoadedCallback;
import com.varsitycollege.locuslocatorsapp.Settings.ProfileActivity;
import com.varsitycollege.locuslocatorsapp.databinding.ActivityDirectionBinding;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

//nicole started
public class DirectionActivity extends FragmentActivity implements OnMapReadyCallback,  GeoTask.Geo, TaskLoadedCallback {

    private ActivityDirectionBinding binding;

    GoogleMap map;
    SupportMapFragment mapFragment;

    private MarkerOptions placeFrom, placeTo;
    String str_from,str_to, unit_measurement;
    private Polyline currentPolyline;
    String units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.getRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.searchFrom.getText().toString().equals("")) {
                    Toast.makeText(DirectionActivity.this, "Please Enter Your Place of Origin", Toast.LENGTH_SHORT).show();
                } else if (binding.searchTo.getText().toString().equals("")) {
                    Toast.makeText(DirectionActivity.this, "Please Enter Your Place of Destination", Toast.LENGTH_SHORT).show();
                } else {
                    map.clear();

                    goToSearchFrom();
                    goToSearchTo();

                    str_from = binding.searchFrom.getText().toString();
                    str_to = binding.searchTo.getText().toString();

                    String url = getUrl(placeFrom.getPosition(), placeTo.getPosition(), "driving");
                    new FetchURL(DirectionActivity.this).execute(url, "driving");

                    String urll = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + str_from + "&destinations=" + str_to + "&mode=driving&units=metric&key=AIzaSyDDWdzRiDLF6hftG5ACxOyesFw4Suk7kwU";
                    new GeoTask(DirectionActivity.this).execute(urll);

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(placeTo.getPosition(), 11));
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }

    /*
     * Code Attribution
     * Name: Android Coding
     * Published: 5 March 2019
     * URL: https://youtu.be/iWYsBDCGhGw
     *
     * search for a place method (from)*/
    private void goToSearchFrom() {
        String location = binding.searchFrom.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(DirectionActivity.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            placeFrom = new MarkerOptions().position(latLng).title(location);
            map.addMarker(placeFrom);
        }
    }

    //search for a place method (from)
    private void goToSearchTo() {
        String location = binding.searchTo.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(DirectionActivity.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            placeTo = new MarkerOptions().position(latLng).title(location);
            map.addMarker(placeTo);
        }
    }
    /*
     * Code Attribution Ended*/


    /*
     * Code Attribution
     * Name: Srivastava Chintakindi
     * Published: 8 November 2016
     * URL: https://youtu.be/tXPEOJaeFm8
     */
    @Override
    public void setDouble(String result) {
        String res[]=result.split(",");
        Double min=Double.parseDouble(res[0])/60;
        int dist=Integer.parseInt(res[1])/1000;
        double miles = dist / 1.609;

        Intent intent = getIntent();
        units = intent.getStringExtra("unit");

        String Distance = "0 km";

        if(units != null){
            if(units.equals("miles")){
                Distance = "Distance: " + String.format("%.2f", miles) + " miles";
                binding.distanceTxt.setText(Distance);
            }
            else if(units.equals("km")){
                Distance = "Distance: " + dist + " km";
                binding.distanceTxt.setText(Distance);
            }
        }
        else{
            Distance = "Distance: " + dist + " km";
            binding.distanceTxt.setText(Distance);
        }

        String Duration = "Duration: " + (int) (min / 60) + " hr " + (int) (min % 60) + " mins";
        binding.time.setText(Duration);
    }
    /*
     * Code Attribution Ended*/

    /*
     * Code Attribution
     * Name: The Code City
     * Published: 10 November 2018
     * URL: https://youtu.be/wRDLjUK8nyU
     */
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);

        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }
    /*
     * Code Attribution Ended*/
}
//nicole ended

