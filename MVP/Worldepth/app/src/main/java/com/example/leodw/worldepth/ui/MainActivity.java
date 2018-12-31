package com.example.leodw.worldepth.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.map.MapsActivity;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        mLoginState = mPreferences.getBoolean("loginState", false);
        String startFragment = (mLoginState) ? "Camera_Fragment" : "StartScreen_Fragment";
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

    public void switchToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
