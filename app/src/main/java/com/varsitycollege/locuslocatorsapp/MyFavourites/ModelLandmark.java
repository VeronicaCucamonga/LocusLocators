package com.varsitycollege.locuslocatorsapp.MyFavourites;

/*
 * Code Attribution
 * Name: Atif Pervaiz
 * Published: 23 May 2021
 * URL: https://youtu.be/j6GrP2MdFos
 * nicole started
 */
public class ModelLandmark {

    //variables
    String timestamp, location_name;
    Double latitude, longitude;

    //constructor with no parameters
    public ModelLandmark() {
    }

    //constructor with parameters
    public ModelLandmark(String timestamp, String location_name, Double latitude, Double longitude) {
        this.timestamp = timestamp;
        this.location_name = location_name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //Getters and Setters
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
/*
 * nicole ended
 * Code Attribution Ended*/
