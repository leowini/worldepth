package com.example.leodw.worldepth.ui.signup.ContinueSignUp;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.navigation.Navigation;

public class ContinueSignUp extends Fragment {
    private static final String TAG = "ContinueSignUp";

    private ContinueSignUpViewModel mViewModel;
    private FirebaseWrapper mFb;
    private DataTransfer mDt;

    private ImageView SignUpBackButton2;

    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button registerButton;

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
        mDt = ((MainActivity) this.getActivity()).getDataTransfer();
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        registerButton = view.findViewById(R.id.signUpRegister);
        mPassword = view.findViewById(R.id.signUpPassword);
        mConfirmPassword = view.findViewById(R.id.signUpConfirmPW);
        registerButton.setOnClickListener((view1) -> {
            if (validPassword()) {
                String email = mDt.getDataPair(0).getData();
                String firstName = mDt.getDataPair(1).getData();
                String lastName = mDt.getDataPair(2).getData();
                String password = mPassword.getText().toString();
                Log.d(TAG, "email: " + email);
                Log.d(TAG, "firstName: " + firstName);
                Log.d(TAG, "lastName: " + lastName);
                Log.d(TAG, "password: " + password);
                createNewAccount(firstName, lastName, email, password);
                Navigation.findNavController(view1).navigate(R.id.action_continueSignUpFragment_to_cameraFragment);
            }

            SignUpBackButton2 = view.findViewById(R.id.signUpBackButton2);
            SignUpBackButton2.setOnClickListener((view2) -> {
                Navigation.findNavController(view2).popBackStack();
            });
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validPassword();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validPassword();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validPassword() {
        String password = mPassword.getText().toString();
        String confirmed = mConfirmPassword.getText().toString();
        boolean validity = true;

        if (!checkPasswordLength(password)) {
            getView().findViewById(R.id.atLeast8).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.over20).setVisibility(View.VISIBLE);
            validity = false;
        } else {
            getView().findViewById(R.id.atLeast8).setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.over20).setVisibility(View.INVISIBLE);
        }

        if (!containsNumbers(password)) {
            getView().findViewById(R.id.containNumbers).setVisibility(View.VISIBLE);
            validity = false;
        } else {
            getView().findViewById(R.id.containNumbers).setVisibility(View.INVISIBLE);
        }

        if (!containsUpperAndLower(password)) {
            getView().findViewById(R.id.upperLower).setVisibility(View.VISIBLE);
            validity = false;
        } else {
            getView().findViewById(R.id.upperLower).setVisibility(View.INVISIBLE);
        }
        if (containsIllegalChars(password)) {
            getView().findViewById(R.id.passwordIllegalChars).setVisibility(View.VISIBLE);
            validity = false;
        } else {
            getView().findViewById(R.id.passwordIllegalChars).setVisibility(View.INVISIBLE);
        }

        if (!doPasswordsMatch(password, confirmed)) {
            getView().findViewById(R.id.mustMatch).setVisibility(View.VISIBLE);
            validity = false;
        } else {
            getView().findViewById(R.id.mustMatch).setVisibility(View.INVISIBLE);
        }

        return validity;
    };

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

    public void createNewAccount(String firstName, String lastName, String email, String password) {
        FirebaseAuth _auth = mFb.getFirebaseAuth();
        FirebaseDatabase database = mFb.getFirebaseDatabase();
        _auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFb.createNewAccount(firstName, lastName, email, password);
                //set login state
                ((MainActivity) getActivity()).setLoginState(true);
                //go to camera fragment
                Navigation.findNavController(getView()).navigate(R.id.action_passwordFragment_to_loadingFragment);
            } else {
                Toast.makeText(getContext(), "Account creation failed.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "createNewAccount:failed", task.getException());
            }
        });
    }
}
