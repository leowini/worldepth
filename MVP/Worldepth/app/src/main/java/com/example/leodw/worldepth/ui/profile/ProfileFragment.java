package com.example.leodw.worldepth.ui.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.leodw.worldepth.R;

import androidx.navigation.Navigation;

public class ProfileFragment extends Fragment {

    private Button mSettingsButton;
    private Button mBackButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSettingsButton = view.findViewById(R.id.profileToSettingsBtn);
        mSettingsButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_settings));
        mBackButton = view.findViewById(R.id.profileToMapButton);
        mBackButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_mapFragment));
    }
}
