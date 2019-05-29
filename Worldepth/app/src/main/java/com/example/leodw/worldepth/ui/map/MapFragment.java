package com.example.leodw.worldepth.ui.map;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.Post;
import com.example.leodw.worldepth.ui.MainActivity;
import com.example.leodw.worldepth.ui.preview.PreviewFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.navigation.Navigation;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapFragment";

    private GoogleMap mMap;

    private MapView mMapView;

    private FirebaseDatabase mDatabase; //Instance of database
    private DatabaseReference mDataRef;
    private StorageReference mStorageRef; //Instance of storage reference
    private FirebaseAuth mAuth; //Instance of Authentication checker
    private FirebaseUser currentUser;

    private Map<String, Map<String, Object>> postList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mMapView = view.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Button mHomeButton = view.findViewById(R.id.mapToHomeBtn);
        mHomeButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_homeFragment));
        //Button mProfileButton = view.findViewById(R.id.mapToProfileBtn);
        //Button mMessageButton = view.findViewById(R.id.mapToMessageBtn);
        //Button mCameraButton = view.findViewById(R.id.mapToCameraBtn);
        //mProfileButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_profileFragment));
        //mCameraButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_cameraFragment));
        //mMessageButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_messageFragment));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot postsSnap = dataSnapshot.child("posts");
                postList = (Map<String, Map<String, Object>>) postsSnap.getValue();
                for (Map<String, Object> post : postList.values()) {
                    LatLng loc = new LatLng((Double)post.get("lat"), (Double)post.get("lng"));
                    mMap.addMarker(new MarkerOptions().position(loc).title((String)post.get("key")));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String key = marker.getTitle();
        StorageReference modelRef = mStorageRef.child("Models/" + key);
        File localFile = new File(getContext().getFilesDir().getAbsolutePath() + "/download.ply");
        modelRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.setLocal(false);
                Navigation.findNavController(getView()).navigate(R.id.action_mapFragment_to_viewerFragment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                exception.printStackTrace();
            }
        });
        return true;
    }

}
