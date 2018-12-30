package com.example.leodw.worldepth.ui.loading;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.ui.MainActivity;

import androidx.navigation.Navigation;


public class LoadingFragment extends Fragment {

    private static final String TAG = "LoadingFragment";

    private LoadingViewModel mLoadingViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.loading_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button loadingNextButton = view.findViewById(R.id.loadingNextButton);
        Button loadingBackButton = view.findViewById(R.id.loadingBackBtn);
        loadingNextButton.setOnClickListener((view1) -> {
            Navigation.findNavController(view1).navigate(R.id.action_loadingFragment_to_viewerFragment);
        });
        loadingBackButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loadingFragment_to_cameraFragment));
    }
}
