package com.varsitycollege.locuslocatorsapp.NearbyLandmarks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.varsitycollege.locuslocatorsapp.R;
import com.varsitycollege.locuslocatorsapp.databinding.ActivityNearbyLandmarksBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/*
* Code Attribution
* Name: Android Coding
* Published: 31 May 2020
* URL: https://youtu.be/pjFcJ6EB8Dg
* alieya started
*/
public class NearbyLandmarks extends FragmentActivity implements OnMapReadyCallback {

    private ActivityNearbyLandmarksBinding binding;

    GoogleMap map;
    SupportMapFragment mapFragment;

    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNearbyLandmarksBinding.inflate(getLayoutInflater());
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

        //Initialize array of place type
        String[] placeTypeList = {"atm", "bank", "hospital", "movie_theater", "restaurant"};

        //initialize array of place name
        String[] placeNameList = {"ATM", "Bank", "Hospital", "Movie Theater", "Restaurant"};

        //set adapter on spinner
        binding.spType.setAdapter(new ArrayAdapter<>(NearbyLandmarks.this
                , R.layout.selected_item, placeNameList));


        //initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check permission
        if (ActivityCompat.checkSelfPermission(NearbyLandmarks.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //when permission granted, call method

            getCurrentLocation();
        } else {
            //when permission denied, request permission

            ActivityCompat.requestPermissions(NearbyLandmarks.this
                    ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        binding.btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get selected position of spinner
                int i = binding.spType.getSelectedItemPosition();
                //initialize url
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + //url
                        "?location=" + currentLat + "," + currentLong + //location lat and long
                        "&radius=5000" + //nearby radius
                        "&types=" + placeTypeList[i] + //place type
                        "&sensor=true" + //sensor
                        "&key=" + getResources().getString(R.string.map_key); //google map key

                //execute place task method to download json data
                new PlaceTask().execute(url);
            }
        });
    }

    private void getCurrentLocation() {

        //initialize task location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                //when success
                if (location != null){

                    //when location is not = to null,get current lat

                    currentLat = location.getLatitude();

                    //get long
                    currentLong = location.getLongitude();

                    //sync map

                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {

                            //when map is ready
                            map = googleMap;
                            //zoom current location on map
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat,currentLong),10
                            ));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //when permission granted
                //call method
                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                //initialize data
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //execute parser task
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        //initialize url
        URL url = new URL(string);
        //initialize connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //connect connection
        connection.connect();

        //initialize input stream
        InputStream stream = connection.getInputStream();
        //initialize buffer reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        //initialize string variable
        String line = "";
        //use while loop
        while ((line = reader.readLine()) != null){
            //append line
            builder.append(line);
        }
        //get append data
        String data = builder.toString();
        //close reader
        reader.close();
        //return data
        return data;
    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>>{
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //create json parser class
            JsonParser jsonParser = new JsonParser();
            //initialize hash map list

            List<HashMap<String,String>> mapList = null;
            JSONObject object = null;

            try {
                //initialize json obj
                object = new JSONObject(strings[0]);
                //parse json obj
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //return map list
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            //clear map
            map.clear();
            //use for loop
            for(int i=0; i<hashMaps.size(); i++){
                //initialize hash map
                HashMap<String,String> hashMapList = hashMaps.get(i);

                //get lat
                double lat = Double.parseDouble(hashMapList.get("lat"));

                //get long
                double lng = Double.parseDouble(hashMapList.get("lng"));

                //get name
                String name = hashMapList.get("name");

                //concat lat and long
                LatLng latLng = new LatLng(lat,lng);
                //initialize marker options
                MarkerOptions options = new MarkerOptions();
                //set position
                options.position(latLng);
                //set title
                options.title(name);
                //add marker on map
                map.addMarker(options);
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }
}
/*
 * alieya ended
 * Code Attribution Ended*/