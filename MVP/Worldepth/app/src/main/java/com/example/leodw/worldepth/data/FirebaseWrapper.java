package com.example.leodw.worldepth.data;

import android.util.Log;
import android.net.Uri;
import android.widget.Toast;

import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import com.example.leodw.worldepth.ui.MainActivity;
import com.example.leodw.worldepth.ui.signup.StartSignup.StartSignupFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

//Easy interaction with our database
public class FirebaseWrapper {
    //Debug TAG
    private static final String TAG = "FirebaseWrapper";

    private FirebaseDatabase mDatabase; //Instance of database
    private StorageReference mStorageRef; //Instace of storage reference
    private FirebaseAuth mAuth; //Instance of Authentication checker
    private FirebaseUser currentUser;
    //TODO: create list of database references by location

    public FirebaseWrapper() { //Constructor
        mDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    //Get firebase database object
    public FirebaseDatabase getFirebaseDatabase() {
        return mDatabase;
    }

    //Get firebase storageReference object
    public StorageReference getStorageReference() { return mStorageRef; }

    public FirebaseUser getFirebaseUser() {
        return currentUser;
    }

    public FirebaseAuth getFirebaseAuth() { return mAuth; }

    //Write to the firebase database with serializable data
    public void writeToDatabase(String location, Object message) {
        DatabaseReference myRef = mDatabase.getReference(location); //location for message
        myRef.setValue(message); //sending the "message" object
        attachReader(myRef);
        Log.d(TAG,"Wrote to Database");
    }



    private void attachReader(DatabaseReference dbRef) {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public FirebaseUser getCurrentUser() { return mAuth.getCurrentUser(); }

    public FirebaseUser login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(FirebaseWrapper.this, (OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        return user;
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText( , "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        return null;
                    }

                    // ...
                });
    }
}
