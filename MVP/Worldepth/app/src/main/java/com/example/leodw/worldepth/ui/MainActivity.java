package com.example.leodw.worldepth.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

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
    private String sharedPrefFile = "com.example.android.leodw.worldepth";

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
        FirebaseUser user = this.fb.getCurrentUser();
        updateUi(user);
    }

    private void updateUi(FirebaseUser user) {
        if (user != null) {
            //do some stuff;
        }
    }

    public void login(String email, String password) {
        fb.getFirebaseAuth().signInWithEmailAndPassword(email, password).
                addOnCompleteListener((OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = fb.getCurrentUser();
                        updateUi(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUi(null);
                    }
                });
    }
}
