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
        mFb = ((MainActivity) this.getActivity()).getFirebaseWrapper();
        mDt = ((MainActivity) this.getActivity()).getDataTransfer();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mPasswordInput = view.findViewById(R.id.passwordInput);
        mConfirmPassword = view.findViewById(R.id.confirmPasswordInput);

        completeSignUp = view.findViewById(R.id.passwordNextButton);
        goBack = view.findViewById(R.id.passwordBackButton);

        completeSignUp.setOnClickListener((v) -> {
            if (validPassword()) {
                for (int i = 0; i < mDt.size(); i++) {
                    if (mDt.getDataPair(i).getLocation() == 4) {
                        createNewAccount(mDt.getDataPair(i).getData(), mPasswordInput.getText().toString());
                    }
                }
            }
        });

        goBack.setOnClickListener((view2) -> {
            Toast.makeText(getActivity(), "Going back", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).setViewPagerByTitle("Name_Fragment"); //name
        });
    }

    private boolean validPassword() {
        String password = mPasswordInput.getText().toString();
        String confirmed = mConfirmPassword.getText().toString();
        if (!checkPasswordLength(password)) {
            Log.d(TAG, "Password must be between 8 and 20 characters long");
            Toast.makeText(getActivity(), "Password must be between 8 and 20 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!containsNumbers(password)) {
            Log.d(TAG, "Password must contain numbers");
            Toast.makeText(getActivity(), "Password must contain numbers", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!containsUpperAndLower(password)) {
            Log.d(TAG, "Password must contain uppercase and lowercase characters");
            Toast.makeText(getActivity(), "Password must contain uppercase and lowercase characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (containsIllegalChars(password)) {
            Log.d(TAG, "Password contains an illegal character");
            Toast.makeText(getActivity(), "Password contains an illegal character", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!doPasswordsMatch(password, confirmed)) {
            Log.d(TAG, "Passwords must match");
            Toast.makeText(getActivity(), "Passwords must match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /*checks for illegal characters. Only the following are permitted:
-Lower and uppercase English letters (A-Z)
-Digits of Hindu-Arabic number system (0-9)
-' '(space), '!', '#', '$', '%', '&', '(', ')', '*', '+', '-', '.', '/', '[', ']', '^', '_', '`' */
    private boolean containsIllegalChars(String password) {
        for (int i = 0; i < password.length(); i++) {
            int test = (int) (password.charAt(i)); //casting char into an int to check against ASCII
            if (!((test >= 32 && test <= 126) && (test != 34 && test != 39 && test != 44 && test != 92)
                    && !(test >= 58 && test <= 64))) {
                return true;
            }
        }
        return false;
    }

    //checks if password is acceptable length (between 8 and 20 chars)
    private boolean checkPasswordLength(String password) {
        return (password.length() >= 8 && password.length() <= 20);
    }

    //checks if passwords and confirmed password match
    private boolean doPasswordsMatch(String password, String confirm) {
        return password.equals(confirm);
    }

    //checks if the password contains numbers
    private boolean containsNumbers(String password) {
        for (int i = 0; i < password.length(); i++) {
            int test = (int) (password.charAt(i)); //casting char into an int to check against ASCII
            if (test >= 48 && test <= 57)
                return true;
        }
        return false;
    }

    //checks if the password contains both uppercase and lowercase letters
    private boolean containsUpperAndLower(String password) {
        boolean upper = false;
        boolean lower = false;
        for (int i = 0; i < password.length(); i++) {
            int test = (int) (password.charAt(i)); //casting char into an int to check against ASCII
            if (test >= 65 && test <= 90) {
                upper = true;
            } else if (test >= 97 && test <= 122) {
                lower = true;
            }
            if (upper && lower) {
                return true;
            }
        }
        return false;
    }

    public void createNewAccount(String email, String password) {
        FirebaseAuth _auth = mFb.getFirebaseAuth();
        _auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //set login state
                ((MainActivity) getActivity()).setLoginState(true);
                //go to camera fragment
                ((MainActivity) getActivity()).setViewPagerByTitle("Camera_Fragment");
            } else {
                Log.d(TAG, "createNewAccount:failed", task.getException());
            }
        });
    }
}
