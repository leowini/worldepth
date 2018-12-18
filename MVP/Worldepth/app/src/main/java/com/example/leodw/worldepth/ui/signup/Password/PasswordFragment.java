package com.example.leodw.worldepth.ui.signup.Password;

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
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.DataTransfer;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.example.leodw.worldepth.ui.signup.Email.EmailFragment;
import com.example.leodw.worldepth.ui.signup.Phone.PhoneFragment;
import com.example.leodw.worldepth.ui.signup.Phone.PhoneViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordFragment extends Fragment {
    private static final String TAG = "PasswordFragment";

    private PasswordViewModel mViewModel;
    private FirebaseWrapper mFb;
    private DataTransfer mDt;
    private EditText mPasswordInput, mConfirmPassword;
    private Button completeSignUp, goBack;
    private String mEmail;


    public static PasswordFragment newInstance() {
        return new PasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.password_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PasswordViewModel.class);
        mFb = ((MainActivity)this.getActivity()).getFirebaseWrapper();
        mDt = ((MainActivity)this.getActivity()).getDataTransfer();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mPasswordInput = view.findViewById(R.id.passwordInput);
        mConfirmPassword = view.findViewById(R.id.confirmPasswordInput);

        completeSignUp = view.findViewById(R.id.passwordNextButton);
        goBack = view.findViewById(R.id.passwordBackButton);

        completeSignUp.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) { //add null checks
                if (validPassword() && mPasswordInput.getText().toString().equals(mConfirmPassword.getText().toString())) {
                    for (int i = 0; i < mDt.size(); i++) {
                        if (mDt.getDataPair(i).getLocation() == 4) {
                            createNewAccount(mDt.getDataPair(i).getData(), mPasswordInput.getText().toString());
                            return true;
                        }
                    }
                }
                return true;
            }
            return false;
        });
    }

    private boolean validPassword() {
        return true;
    }

    public void createNewAccount(String email, String password) {
        FirebaseAuth _auth = mFb.getFirebaseAuth();
        _auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Account created!", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).setViewPager(6); //name fragment
            } else {
                Log.d(TAG, "createNewAccount:failed", task.getException());
            }
        });
    }
}
