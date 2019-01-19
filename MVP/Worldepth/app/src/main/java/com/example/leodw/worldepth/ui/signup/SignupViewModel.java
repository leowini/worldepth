package com.example.leodw.worldepth.ui.signup;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.navigation.Navigation;

public class SignupViewModel extends ViewModel {

    private static final String TAG = "SignupViewModel";

    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();

    public void setFirstName(String name) {firstName.setValue(name);}

    public void setLastName(String name) { lastName.setValue(name); }

    public void setPassword(String password) { this.password.setValue(password); }

    public void setEmail(String email) { this.email.setValue(email); }

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
