package com.example.leodw.worldepth.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static java.security.AccessController.getContext;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    static private FirebaseWrapper fb;
    static private DataTransfer dt;

    private boolean mLoginState;

    private SharedPreferences mPreferences;
    private static final String sharedPrefFile = "com.example.android.leodw.worldepth";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Lock the orientation to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        mLoginState = mPreferences.getBoolean("loginState", false);
        NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = hostFragment.getNavController();
        if (mLoginState) navController.navigate(R.id.cameraFragment);
        fb = new FirebaseWrapper();
        dt = new DataTransfer();
        listenForFriendRequests();
    }

    public FirebaseWrapper getFirebaseWrapper(){
        return this.fb;
    }

    public DataTransfer getDataTransfer() { return this.dt; }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean("loginState", mLoginState);
        preferencesEditor.apply();
    }

    public void setLoginState(boolean state) {
        mLoginState = state;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = fb.getFirebaseAuth().getCurrentUser();
        updateUi(user);
    }

    private void updateUi(FirebaseUser user) {
        if (user != null) {
            //do some stuff;
        }
    }

    private void listenForFriendRequests() {
        ValueEventListener requestListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String userId = dataSnapshot.getKey();
                notifyUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        String path = "users/" + fb.getUid() + "/invitations";
        DatabaseReference requestReference = fb.getFirebaseDatabase().getReference(path);
        requestReference.addValueEventListener(requestListener);
    }

    public void notifyUser() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "Worldepth")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Fuck you Johann!!!")
                .setContentText("You have a friend request")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, mBuilder.build());
    }

}

