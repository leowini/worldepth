package com.example.leodw.worldepth.ui.viewer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import com.example.leodw.worldepth.databinding.ViewerFragmentBinding;

import com.example.leodw.worldepth.R;

public class ViewerFragment extends Fragment {

    private static final String TAG = "ViewerFragment";

    private ViewerViewModel mViewerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewerFragmentBinding viewerFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.viewer_fragment, container, false);
        View view = inflater.inflate(R.layout.viewer_fragment, container, false);
        return view;
    }
}
