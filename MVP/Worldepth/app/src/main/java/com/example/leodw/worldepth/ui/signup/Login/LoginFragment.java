package com.example.leodw.worldepth.ui.signup.Login;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.example.leodw.worldepth.ui.signup.Phone.PhoneFragment;
import com.example.leodw.worldepth.ui.signup.Phone.PhoneViewModel;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    private LoginViewModel mViewModel;
    private FirebaseWrapper mFb;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mFb = ((MainActivity) this.getActivity()).getFirebaseWrapper();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Button loginButton = view.findViewById(R.id.signInButton);
        loginButton.setOnClickListener((view1) -> {
                /*EditText username = view.findViewById(R.id.enterEmail);
                String usernameString = username.getText().toString();
                EditText password = view.findViewById(R.id.enterPassword);
                String passwordString = password.getText().toString();
                if (usernameString.length() > 0 && passwordString.length() > 0) { //if username and password not blank
                    Toast.makeText(getActivity(), "Logging in", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).setViewPager(7); //camera
                } else {
                    Toast.makeText(getActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }*/
            Toast.makeText(getActivity(), "Logging in", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).setLoginState(true);
            ((MainActivity) getActivity()).setViewPagerByTitle("Camera_Fragment"); //camera
        });

        ImageView loginBackButton = view.findViewById(R.id.loginBackButton);
        loginBackButton.setOnClickListener((view2) -> {
            Toast.makeText(getActivity(), "Going back", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).setViewPagerByTitle("StartScreen_Fragment"); //start screen fragment
        });
    }

}
