package com.example.leodw.worldepth.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.signup.SignupViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static java.security.AccessController.getContext;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SignupViewModel mSignupViewModel;

    static private FirebaseWrapper fb;
    static private DataTransfer dt;

    private boolean mLoginState;

    private NotificationChannel mChannel;

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
        mSignupViewModel = ViewModelProviders.of(this).get(SignupViewModel.class);
        mLoginState = mPreferences.getBoolean("loginState", false);
        mSignupViewModel.setLoginState(mLoginState);
        mSignupViewModel.getLoginState().observe(this, state -> setLoginState(state));
        NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = hostFragment.getNavController();
        if (mLoginState) navController.navigate(R.id.cameraFragment);
        fb = new FirebaseWrapper();
        dt = new DataTransfer();
        createNotificationChannel();
        //listenForNotifications();
        loadFiles();
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

    private void notifyUser() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, mChannel.getId())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Fuck you Johann!!!")
                .setContentText("You have a friend request")
                .setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Worldepth channel";
            String description = "Worldepth messages";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel("Worldepth", name, importance);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    private boolean checkAndWriteFile(String filename){
        String externDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        File wdDir = new File(externDir + "/Worldepth");
        if(!wdDir.exists()){
            Log.i(TAG, "Worldepth folder not found, making it...");
            wdDir.mkdir();
            if(!wdDir.exists()){
                Log.e(TAG, "Unable to create worldepth folder");
            }
        }
        File targetFile = new File(externDir + "/Worldepth/" + filename);
        if(targetFile.exists()){
            Log.i(TAG, targetFile.getAbsolutePath() + " already exists");
            return false;
        }
        else {
            try {
                targetFile.createNewFile();
                if(!targetFile.exists()){
                    Log.e(TAG, "Could not make file!");
                }
                InputStream initialStream = this.getApplicationContext().getAssets().open(filename);
                byte[] buffer = new byte[initialStream.available()];
                initialStream.read(buffer);

                OutputStream outStream = new FileOutputStream(targetFile);
                outStream.write(buffer);

            }catch (IOException e){
                e.printStackTrace();
            }
            return true;
        }
    }

    private void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }

    }

    private void loadFiles() {
        boolean wasWritten = checkAndWriteFile("ORBvoc.txt.tar.gz");
        checkAndWriteFile("TUM1.yaml");
        if(wasWritten) {
            String externDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Worldepth";

            try {
                unzip(new File(externDir + "/ORBvoc.txt.tar.gz"), Environment.getExternalStorageDirectory());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

