package com.example.leodw.worldepth.ui.signup.StartScreen;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.example.leodw.worldepth.ui.signup.Name.NameViewModel;
import com.example.leodw.worldepth.ui.signup.Phone.PhoneFragment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class StartScreenFragment extends Fragment {
    private static final String TAG = "StartScreenFragment";

    private StartScreenViewModel mViewModel;
    private FirebaseWrapper mFb;


    public static StartScreenFragment newInstance() {
        return new StartScreenFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_screen_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StartScreenViewModel.class);
        mFb = ((MainActivity)this.getActivity()).getFirebaseWrapper();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Button goToSignIn = view.findViewById(R.id.goToSignIn);
        goToSignIn.setOnClickListener((view1) -> {
            Toast.makeText(getActivity(), "Going to login page", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).setViewPager(1); //login page
        });

        Button goToSignUp = view.findViewById(R.id.goToSignUp);
        goToSignUp.setOnClickListener((view2) -> {
            Toast.makeText(getActivity(), "Going to sign up page", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).setViewPager(2); //sign up page
        });

        Button goToViewer = view.findViewById(R.id.goToViewer);
        goToViewer.setOnClickListener((view3) -> {
            Toast.makeText(getActivity(), "Going to viewer", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).setViewPager(10); //viewer page
        });

        Button goToCamera = view.findViewById(R.id.goToCamera);
        goToCamera.setOnClickListener((view4) -> {
            Toast.makeText(getActivity(), "Going to camera", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).setViewPager(8); //camera page
        });
    }
}
