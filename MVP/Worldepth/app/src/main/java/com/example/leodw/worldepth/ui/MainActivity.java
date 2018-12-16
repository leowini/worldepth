package com.example.leodw.worldepth.ui;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.camera.CameraFragment;
import com.example.leodw.worldepth.ui.loading.LoadingFragment;
import com.example.leodw.worldepth.ui.login.LoginFragment;
import com.example.leodw.worldepth.ui.signup.Birthday.BirthdayFragment;
import com.example.leodw.worldepth.ui.signup.Email.EmailFragment;
import com.example.leodw.worldepth.ui.signup.Name.NameFragment;
import com.example.leodw.worldepth.ui.signup.Phone.PhoneFragment;
import com.example.leodw.worldepth.ui.signup.SignUpFragment;
import com.example.leodw.worldepth.ui.signup.StartScreen.StartScreenFragment;
import com.example.leodw.worldepth.ui.signup.StartSignup.StartSignupFragment;
import com.example.leodw.worldepth.ui.viewer.ViewerFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    
    private SectionsStatePagerAdapter mPagerAdapter;
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

        mPagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        fb = new FirebaseWrapper();
        //updateUI(fb.getFirebaseUser());
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StartScreenFragment(), "StartScreen_Fragment");   //0
        adapter.addFragment(new LoginFragment(), "Login_Fragment");               //1
        adapter.addFragment(new StartSignupFragment(), "StartSignup_Fragment");   //2
        adapter.addFragment(new EmailFragment(), "Email_Fragment");               //3
        adapter.addFragment(new PhoneFragment(), "Phone_Fragment");               //4
        adapter.addFragment(new NameFragment(), "Name_Fragment");                 //5
        adapter.addFragment(new BirthdayFragment(), "Birthday_Fragment");         //6
        adapter.addFragment(new CameraFragment(), "Camera_Fragment");             //7
        adapter.addFragment(new LoadingFragment(), "Loading_Fragment");           //8
        adapter.addFragment(new ViewerFragment(), "Viewer_Fragment");             //9
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentIndex) {
        mViewPager.setCurrentItem(fragmentIndex);
    }

    public void setViewPagerByTitle(String fragmentTitle) {
        int fragmentIndex = mPagerAdapter.getFragmentNumber(fragmentTitle);
        setViewPager(fragmentIndex);
    }

    public FirebaseWrapper getFirebaseWrapper(){
        return this.fb;
    }
}
