package com.example.leodw.worldepth.data;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.net.Uri;
import android.widget.Toast;

//Firebase imports needed
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

//Easy interaction with our database
public class FirebaseWrapper {
    //Debug TAG
    private static final String TAG = "worldepth";

    private FirebaseDatabase mDatabase; //Instance of database
    private StorageReference mStorageRef; //Instace of storage reference
    private FirebaseAuth mAuth; //Instance of Authentication checker
    private FirebaseUser currentUser;

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

    //Write to the firebase database with serializable data
    public void writeToDatabase(String location, Object message) {
        DatabaseReference myRef = mDatabase.getReference(location); //location for message
        myRef.setValue(message); //sending the "message" object
        Log.d(TAG,"Wrote to Database");
    }

    public FirebaseUser getFirebaseUser() {
        return currentUser;
    }

    //upload a file object to our Firebase Cloud Storage
    public void uploadFile(Uri file) {
        final StorageReference fileRef = mStorageRef.child("Bursts"); //Path to where files are placed

        fileRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        com.google.android.gms.tasks.Task<android.net.Uri> downloadUrl = fileRef.getDownloadUrl();
                        Log.d(TAG, downloadUrl.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d(TAG, "Error: " + exception.getMessage());
                    }
                });
    }

    /* mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                updateUI(null);
            }

            // ...
        }
    }); */
}
