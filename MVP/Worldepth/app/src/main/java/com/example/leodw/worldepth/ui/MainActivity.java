package com.example.leodw.worldepth.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
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

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
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
        mLoginState = mPreferences.getBoolean("loginState", false);
        NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = hostFragment.getNavController();
        if (mLoginState) navController.navigate(R.id.cameraFragment);
        fb = new FirebaseWrapper();
        dt = new DataTransfer();
        createNotificationChannel();
        //listenForNotifications();
        loadFiles();
    }

    public FirebaseWrapper getFirebaseWrapper() {
        return this.fb;
    }

    public DataTransfer getDataTransfer() {
        return this.dt;
    }

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

    private File checkAndWriteFile(String filename) {
        String externDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        File wdDir = new File(externDir + "/Worldepth");
        if (!wdDir.exists()) {
            Log.i(TAG, "Worldepth folder not found, making it...");
            wdDir.mkdir();
            if (!wdDir.exists()) {
                Log.e(TAG, "Unable to create worldepth folder");
            }
        }
        File targetFile = new File(externDir + "/Worldepth/" + filename);
//        if (targetFile.exists()) {
//            Log.i(TAG, targetFile.getAbsolutePath() + " already exists");
//            return false;
//        }

        try {
            targetFile.createNewFile();
            if (!targetFile.exists()) {
                Log.e(TAG, "Could not make file!");
            }
            String[] assetsRoot = getAssets().list("");
            String assetsFile;
            if (filename == "ORBvoc.txt.tar.gz") {
                assetsFile = "ORBvoc.txt.tar";
            } else {
                assetsFile = filename;
            }
            InputStream initialStream = getAssets().open(assetsFile);
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);

            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile;
    }

    private void loadFiles() {
        File wasWritten = checkAndWriteFile("ORBvoc.txt.tar");
        checkAndWriteFile("TUM1.yaml");
        if (true) {
            String externDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Worldepth";

//            try {
//                unzip(new File(externDir + "/ORBvoc.txt.tar.gz"), externDir);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            try {
                String outputFile = getFileName(wasWritten, externDir);
                File tarFile = wasWritten;
                //tarFile = decompressGZIP(wasWritten, tarFile);
                File unTarFile = new File(externDir);
                unTar(tarFile, unTarFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void unTar(File tarFile, File destFile) throws IOException {
        FileInputStream fis = new FileInputStream(tarFile);
        TarArchiveInputStream tis = new TarArchiveInputStream(fis);
        TarArchiveEntry tarEntry = null;

        // tarIn is a TarArchiveInputStream
        while ((tarEntry = tis.getNextTarEntry()) != null) {
            File outputFile = new File(destFile + File.separator + tarEntry.getName());

            if (tarEntry.isDirectory()) {

                System.out.println("outputFile Directory ---- "
                        + outputFile.getAbsolutePath());
                if (!outputFile.exists()) {
                    outputFile.mkdirs();
                }
            } else {
                //File outputFile = new File(destFile + File.separator + tarEntry.getName());
                System.out.println("outputFile File ---- " + outputFile.getAbsolutePath());
                outputFile.getParentFile().mkdirs();
                //outputFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(outputFile);
                IOUtils.copy(tis, fos);
                fos.close();
            }
        }
        tis.close();
    }

    /**
     * This method is used to get the tar file name from the gz file
     * by removing the .gz part from the input file
     *
     * @param inputFile
     * @param outputFolder
     * @return
     */
    private static String getFileName(File inputFile, String outputFolder) {
        return outputFolder + File.separator +
                inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
    }
}

