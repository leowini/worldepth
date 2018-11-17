package com.example.leodw.worldepth.ui.camera;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;

import com.example.leodw.worldepth.MainActivity;
import com.example.leodw.worldepth.databinding.CameraFragmentBinding;

import com.example.leodw.worldepth.R;

public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CameraFragmentBinding cameraFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.camera_fragment, container, false);
        View view = cameraFragmentBinding.getRoot();
        cameraFragmentBinding.setViewModel(new CameraViewModel());
        cameraFragmentBinding.executePendingBindings();
        return view;
    }
}
