package com.varsitycollege.locuslocatorsapp.NearbyLandmarks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Code Attribution
 * Name: Android Coding
 * Published: 31 May 2020
 * URL: https://youtu.be/pjFcJ6EB8Dg
 * alieya started
 */
public class JsonParser {

    private HashMap<String,String> parseJsonObject(JSONObject object){
        //initialize hash map
        HashMap<String,String> dataList = new HashMap<>();

        try {
            String name = object.getString("name");
            //get lat from obj
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");

            //get long from obj

            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");


            //pull all values in hash map
            dataList.put("name",name);
            dataList.put("lat",latitude);
            dataList.put("lng",longitude);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return hash map
        return dataList;


    }

    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray) {
        //initialize hash map list
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                //initialize hash map
                HashMap<String, String> data = parseJsonObject((JSONObject) jsonArray.get(i));

                // add data in hash map list

                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // return hash map list
        return dataList;
    }

    public List<HashMap<String,String>> parseResult(JSONObject object){
        //initialize jsonArray

        JSONArray jsonArray = null;

        //get result array

            try {
                jsonArray = object.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //return array
            return parseJsonArray(jsonArray);
        }
    }

/*
 * alieya ended
 * Code Attribution Ended*/







