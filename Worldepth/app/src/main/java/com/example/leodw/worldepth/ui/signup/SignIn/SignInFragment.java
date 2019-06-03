package com.example.leodw.worldepth.ui.signup.SignIn;

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
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;

import androidx.navigation.Navigation;

public class SignInFragment extends Fragment {
    private static final String TAG = "SignInFragment";

    private EditText mEmailInput;
    private EditText mPasswordInput;

    private SignInViewModel mViewModel;
    private FirebaseWrapper mFb;

    private Button continueButton;
    private ImageView goBack;

    private DataTransfer mDt;


    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_in_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SignInViewModel.class);
        mFb = ((MainActivity) this.getActivity()).getFirebaseWrapper();
        mDt = ((MainActivity) this.getActivity()).getDataTransfer();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mEmailInput = view.findViewById(R.id.emailInput);
        mPasswordInput = view.findViewById(R.id.signInPassword);
        continueButton = view.findViewById(R.id.signInContinue);

        ImageView goBack = view.findViewById(R.id.signInBackButton);
        goBack.setOnClickListener((view2) -> {
            Navigation.findNavController(view2).navigate(R.id.action_signInFragment_to_startScreenFragment);
        });

        continueButton.setOnClickListener((view1) -> {
            if ((!mEmailInput.getText().toString().equals("")) && (!mPasswordInput.getText().toString().equals("")))
            {
                login(mEmailInput.getText().toString(), mPasswordInput.getText().toString());
            } else {
                Toast.makeText(getContext(), "Enter your login info.", Toast.LENGTH_SHORT).show();
            }
        });
        mEmailInput.requestFocus();
    }

    private void login(String email, String password) {
        mFb.getFirebaseAuth().signInWithEmailAndPassword(email, password).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signed in");
                        ((MainActivity) getActivity()).setLoginState(true);
                        Navigation.findNavController(getView()).navigate(R.id.action_signInFragment_to_homeFragment);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validEmail() {
        return !(mEmailInput.getText().toString().equals(""));
    }

}
