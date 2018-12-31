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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import androidx.navigation.Navigation;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.getMapAsync(this);

        Button mProfileButton = view.findViewById(R.id.mapToProfileBtn);
        Button mMessageButton = view.findViewById(R.id.mapToMessageBtn);
        Button mCameraButton = view.findViewById(R.id.mapToCameraBtn);
        mProfileButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_profileFragment));
        mCameraButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_cameraFragment));
        mMessageButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_messageFragment));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
