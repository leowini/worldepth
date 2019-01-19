package com.example.leodw.worldepth.ui.profile;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.data.User;
import com.example.leodw.worldepth.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.navigation.Navigation;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ProfileViewModel mProfileViewModel;

    private ImageView mSettingsButton;
    private ImageView mBackButton;
    private TextView mNameOfUser;
    private int mFriendNumber = 1;
    private TextView mFriendText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mProfileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
        mBackButton = view.findViewById(R.id.profileToMapButton);
        mBackButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_mapFragment));
        mSettingsButton = view.findViewById(R.id.profileToSettingsBtn);
        mSettingsButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_settings));
        mNameOfUser = view.findViewById(R.id.profileName);
        mProfileViewModel.getName().observe(this, name -> mNameOfUser.setText(name));
        mFriendText = view.findViewById(R.id.profileNumberOfFollowers);
        mProfileViewModel.getFriendCount().observe(this, count -> mFriendText.setText(Integer.toString(mFriendNumber)));
    }

}
