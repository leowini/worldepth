package com.example.leodw.worldepth.ui.profile;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.Objects;

import androidx.navigation.Navigation;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView mSettingsButton;
    private ImageView mBackButton;
    private TextView mNameOfUser;
    private int mFriendNumber = 1;
    private TextView mFriendText;
    private FirebaseWrapper mFb;
    private FirebaseDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFb = ((MainActivity) getActivity()).getFirebaseWrapper();
        mDb = mFb.getFirebaseDatabase();
        mBackButton = view.findViewById(R.id.profileToMapButton);
        mBackButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_mapFragment));
        mSettingsButton = view.findViewById(R.id.profileToSettingsBtn);
        mSettingsButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_settings));
        mNameOfUser = view.findViewById(R.id.profileName);
        setName();
        mFriendText = view.findViewById(R.id.profileNumberOfFollowers);
        mFriendText.setText(Integer.toString(mFriendNumber));
    }

    private void setName() {
        DatabaseReference dbRef = mDb.getReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("users").child(mFb.getUid()).getValue(User.class);
                String fullname = user.firstName + " " + user.lastName;
                mNameOfUser.setText(fullname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
