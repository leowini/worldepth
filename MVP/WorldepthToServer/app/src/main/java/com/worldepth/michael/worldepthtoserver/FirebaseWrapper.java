package com.worldepth.michael.worldepthtoserver;

import android.util.Log;

//Firebase imports needed
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Easy interaction with our database
public class FirebaseWrapper {
    //Debug TAG
    private static final String TAG = "worldepth";

    private FirebaseDatabase database; //Instance of database

    public FirebaseWrapper() { //Constructor
        database = FirebaseDatabase.getInstance();
    }

    //Get firebase database object
    public FirebaseDatabase getFirebaseDatabase() {
        return database;
    }

    //Write to the firebase database
    public void writeToDatabase(String location, Object message) {
        DatabaseReference myRef = database.getReference(location); //location for message
        myRef.setValue(message); //sending the "message" object
        Log.d(TAG,"Wrote to Database");
    }
}
