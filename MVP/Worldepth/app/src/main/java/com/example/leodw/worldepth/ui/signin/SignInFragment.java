package com.example.leodw.worldepth.ui.signin;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";

    private SignInViewModel mViewModel;
    private Button signIn;
    private FirebaseWrapper mFb;


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
        mFb = ((MainActivity)this.getActivity()).getFirebaseWrapper();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        signIn = (Button) view.findViewById(R.id.signInButton);
        signIn.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) { //add null checks
                EditText emailInput = (EditText) view.findViewById(R.id.emailInput);
                String email = emailInput.getText().toString();
                EditText passwordInput = (EditText) view.findViewById(R.id.passwordInput);
                String password = passwordInput.getText().toString();
                createNewAccount(email, password);
                return true;
            }
            return false;
        });
    }

    public void createNewAccount(String email, String password) {
        FirebaseAuth _auth = mFb.getFirebaseAuth();
        _auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    ((MainActivity) getActivity()).setViewPager(1);
                } else {
                    Log.d(TAG, "createNewAccount:failed", task.getException());
                }
            }
        });
    }
}
