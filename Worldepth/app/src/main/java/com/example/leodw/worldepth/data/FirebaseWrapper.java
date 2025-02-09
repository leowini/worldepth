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
import com.google.firebase.auth.GetTokenResult;
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
    private StorageReference mStorageRef; //Instance of storage reference
    private FirebaseAuth mAuth; //Instance of Authentication checker
    private FirebaseUser currentUser;
    private String name;
    private String email;
    private Uri photoUrl;
    private boolean emailVerified;
    private String uid;

    //TODO: create list of database references by location

    public FirebaseWrapper() { //Constructor
        mDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            this.name = currentUser.getDisplayName();
            this.email = currentUser.getEmail();
            this.photoUrl = currentUser.getPhotoUrl();

            // Check if user's email is verified
            this.emailVerified = currentUser.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            this.uid = currentUser.getUid();
        }
    }

    public void getName() {
        //String name = mDatabase.getReference().child("users").child(uid);
        DatabaseReference dbRef = mDatabase.getReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("users").child(uid).getValue(User.class);
                Log.d(TAG, "The retrieved username is: " + user.firstName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public String getEmail() {
        return this.email;
    }

    public Uri getPhotoUrl() {
        return this.photoUrl;
    }

    public boolean getEmailVerified() {
        return this.emailVerified;
    }

    public String getUid() {
        return this.uid;
    }

    public void friendRequest(String Name) {

    }

    //Get firebase database object
    public FirebaseDatabase getFirebaseDatabase() {
        return mDatabase;
    }

    //Get firebase storageReference object
    public StorageReference getStorageReference() {
        return mStorageRef;
    }

    public FirebaseUser getFirebaseUser() {
        return currentUser;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    //Write to the firebase database with serializable data
    public void writeToDatabase(String location, Object message) {
        DatabaseReference myRef = mDatabase.getReference(location); //location for message
        myRef.setValue(message); //sending the "message" object
        attachReader(myRef);
        Log.d(TAG, "Wrote to Database");
    }

    public void createNewAccount(String firstName, String lastName, String email, String password) {
        User user = new User(firstName, lastName, email, password);
        FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = mDatabase.getReference();
        assert authUser != null;
        ref.child("users").child(authUser.getUid()).setValue(user);
//               authUser.getIdToken(true).addOnSuccessListener(result -> {
//            String idToken = result.getToken();
//            ref.child("users").child(idToken).setValue(user);
//        });
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
}
