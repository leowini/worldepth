package com.example.leodw.worldepth.data;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Post {


    public String uid;
    public Double lat;
    public Double lng;
    public String time;
    public String key;

    //empty constructor for firebase
    public Post() {

    }

    //data-filled constructor
    public Post(String uid, Double lat, Double lng, String key){
        this.uid = uid;
        this.lat = lat;
        this.lng = lng;
        this.time = Calendar.getInstance().getTime().toString();
        this.key = key;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("time", time);
        result.put("key", key);

        return result;
    }
}
