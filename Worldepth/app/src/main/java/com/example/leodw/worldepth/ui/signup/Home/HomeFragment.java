package com.example.leodw.worldepth.ui.signup.Home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;

import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private HomeViewModel mViewModel;
    private FirebaseWrapper mFb;
    private EditText mBirthdayInput;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        ImageView homeToProfileButton = view.findViewById(R.id.homeToProfile);
        homeToProfileButton.setOnClickListener((view1) -> {
            Navigation.findNavController(view1).navigate(R.id.action_homeFragment_to_profileFragment);
        });

        ImageView homeToCameraButton = view.findViewById(R.id.homeToCamera);
        homeToCameraButton.setOnClickListener((view1) -> {
            Navigation.findNavController(view1).navigate(R.id.action_homeFragment_to_cameraFragment);
        });

        ImageView homeToMapButton = view.findViewById(R.id.homeToMap);
        homeToMapButton.setOnClickListener((view1) -> {
            Navigation.findNavController(view1).navigate(R.id.action_homeFragment_to_mapFragment);
        });
        /*Button birthdayNextButton = view.findViewById(R.id.birthdayNextButton);
        birthdayNextButton.setOnClickListener((view1) -> {
            Navigation.findNavController(view1).navigate(R.id.action_birthdayFragment_to_passwordFragment);
        });*/

        /*ImageView birthdayBackButton = view.findViewById(R.id.birthdayBackButton);
        birthdayBackButton.setOnClickListener((view2) -> {
            Navigation.findNavController(view2).popBackStack();
        });*/
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mFb = ((MainActivity)this.getActivity()).getFirebaseWrapper();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
    }
}
