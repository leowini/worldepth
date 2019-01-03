package com.example.leodw.worldepth.ui.profile;

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

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.ui.MainActivity;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.navigation.Navigation;

public class ProfileFragment extends Fragment {

    private ImageView mSettingsButton;
    private ImageView mBackButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_friend_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSettingsButton = view.findViewById(R.id.profileToSettingsBtn);
        mSettingsButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_settings));
        mBackButton = view.findViewById(R.id.profileToMapButton);
        mBackButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_addFriend_to_profileFragment));
        DatabaseReference dbRef = ((MainActivity) getActivity()).getFirebaseWrapper().getFirebaseDatabase().getReference();
        ArrayList<String> userList = new ArrayList<String>();

        }
    }
}
