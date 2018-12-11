package com.example.leodw.worldepth.ui;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.camera.CameraFragment;
import com.example.leodw.worldepth.ui.loading.LoadingFragment;
import com.example.leodw.worldepth.ui.login.LoginFragment;
import com.example.leodw.worldepth.ui.signup.SignUpFragment;
import com.example.leodw.worldepth.ui.viewer.ViewerFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;
    static private FirebaseWrapper fb;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        fb = new FirebaseWrapper();
        //updateUI(fb.getFirebaseUser());
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SignUpFragment(), "SignUp_Fragment");
        adapter.addFragment(new LoginFragment(), "Login_Fragment");
        adapter.addFragment(new CameraFragment(), "Camera_Fragment");
        adapter.addFragment(new LoadingFragment(), "Loading_Fragment");
        adapter.addFragment(new ViewerFragment(), "Viewer_Fragment");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentIndex) {
        mViewPager.setCurrentItem(fragmentIndex);
    }

    public FirebaseWrapper getFirebaseWrapper(){
        return this.fb;
    }
}
