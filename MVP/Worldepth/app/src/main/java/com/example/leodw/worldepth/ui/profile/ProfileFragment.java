package com.example.leodw.worldepth.ui.profile;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.ui.MainActivity;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import androidx.navigation.Navigation;

public class ProfileFragment extends Fragment {

    private ImageView mSettingsButton;
    private ImageView mBackButton;
    private TextView mNameOfUser;
    private int mFriendNumber = 1;
    private TextView mFriendText;
    private FirebaseDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mDb = ((MainActivity) getActivity()).getFirebaseWrapper().getFirebaseDatabase();
        mSettingsButton = view.findViewById(R.id.profileToSettingsBtn);
        mSettingsButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_settings));
        mBackButton = view.findViewById(R.id.profileToMapButton);
        mBackButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_mapFragment));
        mNameOfUser = view.findViewById(R.id.profileName);
        mNameOfUser.setText(((MainActivity)Objects.requireNonNull(getActivity())).getFirebaseWrapper().getName());
        //mFriendNumber = ((MainActivity) getActivity()).getFirebaseWrapper().getFollowerNumber();
        mFriendText = view.findViewById(R.id.profileNumberOfFollowers);
        mFriendText.setText(Integer.toString(mFriendNumber));
    }
}
