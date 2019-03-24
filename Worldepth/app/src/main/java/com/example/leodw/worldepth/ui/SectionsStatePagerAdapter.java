package com.example.leodw.worldepth.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.leodw.worldepth.ui.signup.Email.EmailFragment;
import com.example.leodw.worldepth.ui.signup.Password.PasswordFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "SectionsStatePagerAdapter";

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames = new HashMap<>();

    public SectionsStatePagerAdapter (FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragments.put(fragment, mFragmentList.size()-1);
        mFragmentNumbers.put(title, mFragmentList.size()-1);
        mFragmentNames.put(mFragmentList.size()-1, title);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() { return mFragmentList.size(); }

    /**
     * returns the fragment with the name @param
     * @param fragmentName
     * @return
     */
    public Integer getFragmentNumber(String fragmentName){
        return mFragmentNumbers.getOrDefault(fragmentName, null);
    }

    /**
     * returns the fragment with the name @param
     * @param fragmentNumber
     * @return
     */
    public String getFragmentName(Integer fragmentNumber){
        return mFragmentNames.getOrDefault(fragmentNumber, null);
    }
}
