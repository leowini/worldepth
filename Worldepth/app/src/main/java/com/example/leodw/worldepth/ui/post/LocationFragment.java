package com.example.leodw.worldepth.ui.post;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;

    private MapView mMapView;

    private final String TAG = "LocationFragment";

    private FirebaseDatabase mDatabase; //Instance of database
    private DatabaseReference mDataRef;
    private StorageReference mStorageRef; //Instance of storage reference
    private FirebaseAuth mAuth; //Instance of Authentication checker
    private FirebaseUser currentUser;

    private OnFragmentInteractionListener mListener;

    private ImageView mbackButton;
    private Button mPostButton;

    private LatLng currentLoc;

    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment LocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mMapView = view.findViewById(R.id.map2);
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mbackButton = view.findViewById(R.id.postBackButton);
        mPostButton = view.findViewById(R.id.postButton);
        mbackButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_locationFragment_to_viewerFragment));
        mPostButton.setOnClickListener(v -> {
            if(currentLoc == null){
                Toast.makeText(getActivity().getApplicationContext(), R.string.no_location, Toast.LENGTH_SHORT).show();
            } else{
                postModel();
            }
        });
    }

    public void postModel(){
        String key = mDataRef.child("posts").push().getKey();
        Uri model = Uri.fromFile(new File(getContext().getFilesDir().getAbsolutePath() + "/SLAM.ply"));
        StorageReference modelRef = mStorageRef.child("Models/" + key);
        UploadTask task = modelRef.putFile(model);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.model_upload_failure, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Post post = new Post(currentUser.getUid(), currentLoc.latitude, currentLoc.longitude, key);
                Map<String, Object> postValues = post.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/posts/" + key, postValues);
                childUpdates.put("/users/" + currentUser.getUid() + "/" + key, postValues);

                mDataRef.updateChildren(childUpdates);
                Navigation.findNavController(getView()).navigate(R.id.action_locationFragment_to_mapFragment);
            }
        });
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this.getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        mMap = googleMap;

        googleMap.setOnMapClickListener((this));
    }

    @Override
    public void onMapClick(LatLng loc){
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(loc).title("post location").
                icon(BitmapDescriptorFactory.defaultMarker(208)));
        currentLoc = loc;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
