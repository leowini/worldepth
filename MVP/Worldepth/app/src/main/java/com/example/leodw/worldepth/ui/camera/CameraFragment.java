package com.example.leodw.worldepth.ui.camera;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.databinding.DataBindingUtil;
import android.widget.Toast;

import com.example.leodw.worldepth.databinding.CameraFragmentBinding;

import com.example.leodw.worldepth.R;

public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private CameraViewModel mCameraViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCameraViewModel = ViewModelProviders.of(this).get(CameraViewModel.class);
        CameraFragmentBinding cameraFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.camera_fragment, container, false);
        View view = cameraFragmentBinding.getRoot();
        cameraFragmentBinding.setViewModel(mCameraViewModel);
        cameraFragmentBinding.executePendingBindings();
        return view;
    }

    /**
     * When the CameraViewModel sets its CaptureButtonOnTouchListener, this method sets the actual CaptureButton's OnTouchListener
     * to viewModel.CaptureButtonOnTouchListener.
     * @param view
     * @param onTouchListener
     */
    @BindingAdapter("onTouchListener")
    public static void setOnTouchListener(View view, View.OnTouchListener onTouchListener) {
        if (onTouchListener != null)
            view.setOnTouchListener(onTouchListener);
    }

    @BindingAdapter("surfaceTextureListener")
    public static void setSurfaceTextureListener(View view, TextureView.SurfaceTextureListener surfaceTextureListener) {
        if (surfaceTextureListener != null) {
            TextureView st = (TextureView) view;
            st.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraViewModel.onResume();
    }

    @Override
    public void onPause() {
        mCameraViewModel.onPause();
        super.onPause();
    }
}
