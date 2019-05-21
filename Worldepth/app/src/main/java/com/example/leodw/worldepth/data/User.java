package com.example.leodw.worldepth.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    private static final String TAG = "User";

    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public Map<String, Object> posts;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.posts = new HashMap<String, Object>();
    }

}