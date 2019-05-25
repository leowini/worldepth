package com.example.leodw.worldepth.ui.signup.ContinueSignUp;

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
import android.widget.ImageView;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;

import androidx.navigation.Navigation;

public class ContinueSignUp extends Fragment {
    private static final String TAG = "ContinueSignUp";

    private ContinueSignUpViewModel mViewModel;
    private FirebaseWrapper mFb;
    private DataTransfer mDt;

    private ImageView SignUpBackButton2;

    private EditText mPhoneInput;

    public static ContinueSignUp newInstance() {
        return new ContinueSignUp();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.continue_signup, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ContinueSignUpViewModel.class);
        mFb = ((MainActivity)this.getActivity()).getFirebaseWrapper();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Button registerButton = view.findViewById(R.id.signUpRegister);
        registerButton.setOnClickListener((view1) -> {
            Navigation.findNavController(view1).navigate(R.id.action_continueSignUpFragment_to_cameraFragment);
        });

        SignUpBackButton2 = view.findViewById(R.id.signUpBackButton2);
        SignUpBackButton2.setOnClickListener((view2) -> {
            Navigation.findNavController(view2).popBackStack();
        });
    }
}
