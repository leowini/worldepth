package com.example.leodw.worldepth.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.signup.Name.NameFragment;
import com.example.leodw.worldepth.ui.signup.Name.NameViewModel;

public class SignupOrLogin extends Fragment {
    private static final String TAG = "SignupOrLogin";

    private SignupOrLoginViewModel mViewModel;
    private FirebaseWrapper mFb;
    private Button mSignUpButton;
    private Button mLoginButton;


    public static SignupOrLogin newInstance() {
        return new SignupOrLogin();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signuporlogin_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SignupOrLoginViewModel.class);
        mFb = ((MainActivity)this.getActivity()).getFirebaseWrapper();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mSignUpButton = (Button) view.findViewById(R.id.goToSignup);
        mLoginButton = (Button) view.findViewById(R.id.goToLogin);
        mSignUpButton.setOnClickListener(view1 -> goToSignup());
        mLoginButton.setOnClickListener(view2 -> goToLogin());
    }

    private void goToSignup() {
        ((MainActivity) getActivity()).setViewPagerByTitle("Signup_Fragment");
    }

    private void goToLogin() {
        ((MainActivity) getActivity()).setViewPagerByTitle("Login_Fragment");
    }
}
