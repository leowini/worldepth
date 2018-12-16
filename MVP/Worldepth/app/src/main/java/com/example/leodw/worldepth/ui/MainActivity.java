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
        mPagerAdapter.addFragment(new StartScreenFragment(), "StartScreen_Fragment");   //0
        mPagerAdapter.addFragment(new LoginFragment(), "Login_Fragment");               //1
        mPagerAdapter.addFragment(new StartSignupFragment(), "StartSignup_Fragment");   //2
        mPagerAdapter.addFragment(new EmailFragment(), "Email_Fragment");               //3
        mPagerAdapter.addFragment(new PhoneFragment(), "Phone_Fragment");               //4
        mPagerAdapter.addFragment(new NameFragment(), "Name_Fragment");                 //5
        mPagerAdapter.addFragment(new BirthdayFragment(), "Birthday_Fragment");         //6
        mPagerAdapter.addFragment(new CameraFragment(), "Camera_Fragment");             //7
        mPagerAdapter.addFragment(new LoadingFragment(), "Loading_Fragment");           //8
        mPagerAdapter.addFragment(new ViewerFragment(), "Viewer_Fragment");             //9
        viewPager.setAdapter(mPagerAdapter);
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
