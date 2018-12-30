package com.example.leodw.worldepth.ui.map;

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

public class MapFragment extends Fragment {

    private Button mProfileButton;
    private Button mCameraButton;
    private Button mMessageButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mProfileButton = view.findViewById(R.id.mapToProfileBtn);
        mMessageButton = view.findViewById(R.id.mapToMessageBtn);
        mCameraButton = view.findViewById(R.id.mapToCameraBtn);
        mProfileButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_profileFragment));
        mCameraButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_cameraFragment));
        mMessageButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_messageFragment));
    }
}
