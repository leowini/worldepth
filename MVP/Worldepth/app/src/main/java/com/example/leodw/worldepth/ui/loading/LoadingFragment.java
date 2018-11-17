package com.example.leodw.worldepth.ui.loading;

import android.databinding.DataBindingUtil;
import android.databinding.BindingAdapter;
import com.example.leodw.worldepth.databinding.LoadingFragmentBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leodw.worldepth.R;


public class LoadingFragment extends Fragment {

    private static final String TAG = "LoadingFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LoadingFragmentBinding loadingFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.loading_fragment, container, false);
        View view = loadingFragmentBinding.getRoot();
        loadingFragmentBinding.setViewModel(new LoadingViewModel());
        loadingFragmentBinding.executePendingBindings();
        return view;
    }
}
