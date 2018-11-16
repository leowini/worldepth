package com.example.leodw.worldepth;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.leodw.worldepth.ui.camera.CameraFragment;
import com.example.leodw.worldepth.ui.loading.LoadingFragment;
import com.example.leodw.worldepth.ui.viewer.ViewerFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

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
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment(), "Camera_Fragment");
        adapter.addFragment(new LoadingFragment(), "Loading_Fragment");
        adapter.addFragment(new ViewerFragment(), "Viewer_Fragment");
        viewPager.setAdapter(adapter);
    }

    public void setmViewPager(int fragmentIndex) {
        mViewPager.setCurrentItem(fragmentIndex);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
