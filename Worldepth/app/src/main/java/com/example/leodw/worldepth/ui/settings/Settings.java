package com.example.leodw.worldepth.ui.settings;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.slam.ReconVM;
import com.example.leodw.worldepth.ui.MainActivity;

import androidx.navigation.Navigation;

public class Settings extends Fragment {

    private static final String TAG = "Settings";

    private ReconVM mReconVM;

    private ImageView mBackToProfile;
    private Button mSignOutButton;
    private Button mCalibrateButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mReconVM = ViewModelProviders.of(getActivity()).get(ReconVM.class);
        mBackToProfile = view.findViewById(R.id.settingsBackButton);
        mSignOutButton = view.findViewById(R.id.signOutButton);
        mCalibrateButton = view.findViewById(R.id.CalibButton);
        mBackToProfile.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_profileFragment));
        mSignOutButton.setOnClickListener(v -> {
            ((MainActivity) getActivity()).setLoginState(false);
            Navigation.findNavController(v).navigate(R.id.action_settings_to_startScreenFragment);
        });
        mCalibrateButton.setOnClickListener(v -> {
            mReconVM.setCalibration(true);
            Navigation.findNavController(v).navigate(R.id.action_settings_to_cameraFragment);
        });
    }

}
