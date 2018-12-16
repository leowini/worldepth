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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.start_screen_fragment, container, false);
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
        Log.d(TAG, "onCreateView: started");
        goToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Going to login page", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).setViewPager(1); //login page
            }
        });

        Button goToSignUp = view.findViewById(R.id.goToSignUp);
        Log.d(TAG, "onCreateView: started");
        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Going to sign up page", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).setViewPager(2); //sign up page
            }
        });
    }
}
