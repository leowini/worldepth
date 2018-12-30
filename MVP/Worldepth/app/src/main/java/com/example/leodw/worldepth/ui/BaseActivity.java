package com.example.leodw.worldepth.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.navigation.NavigationManager;
public class BaseActivity extends AppCompatActivity {

    public Toolbar mToolbar;

    public FrameLayout mContentLayout;

    @Inject
    public NavigationManager mNavigationManager;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        com.exarlabs.android.fragmentnavigationdemo.business.dagger.DaggerManager.component().inject(this);
    }



    @Override

    public void setContentView(int layoutResID) {
        // We set a toolbar layout as the default and inflate into the content layout
        super.setContentView(R.layout.toolbar_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mContentLayout = (FrameLayout) findViewById(R.id.content);

        // use the new toolbar
        setSupportActionBar(mToolbar);

        // Get an inflate
        getLayoutInflater().inflate(layoutResID, mContentLayout);
        ButterKnife.bind(this);
        TypefaceHelper.typeface(this);
    }


    /*
     * Sets the toolbars visibility
     * @param visibility
     */
    public void setToolbarVisibility(int visibility) {
        mToolbar.setVisibility(visibility);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mNavigationManager.navigateBack(this);
    }
}
