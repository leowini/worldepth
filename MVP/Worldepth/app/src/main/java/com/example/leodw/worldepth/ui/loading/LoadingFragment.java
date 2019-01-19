package com.example.leodw.worldepth.ui.loading;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;

import com.example.leodw.worldepth.R;

import androidx.navigation.Navigation;


public class LoadingFragment extends Fragment {

    private static final String TAG = "LoadingFragment";

    private AnimationDrawable mLoadingAnimation;
    private ImageView mLoadingImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.loading_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mLoadingImage = view.findViewById(R.id.loadingAnimation);
        startLoadingAnimation();
        Button loadingNextButton = view.findViewById(R.id.loadingNextButton);
        loadingNextButton.setOnClickListener((view1) -> {
            Navigation.findNavController(view1).navigate(R.id.action_loadingFragment_to_cameraFragment);
        });
    }

    private void startLoadingAnimation() {
        mLoadingImage.setImageResource(R.drawable.loading_animation);
        mLoadingAnimation = (AnimationDrawable) mLoadingImage.getDrawable();
        mLoadingAnimation.start();
    }

}
