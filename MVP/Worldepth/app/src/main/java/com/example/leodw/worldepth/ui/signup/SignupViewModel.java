package com.example.leodw.worldepth.ui.signup;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.data.FirebaseWrapper;
import com.example.leodw.worldepth.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.navigation.Navigation;

public class SignupViewModel extends ViewModel {

    private static final String TAG = "SignupViewModel";

    private FirebaseWrapper mFb;
    private FirebaseDatabase mDb;

    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> lastName = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginState = new MutableLiveData<>();

    public SignupViewModel() {
        mFb = new FirebaseWrapper();
        mDb = mFb.getFirebaseDatabase();
    }

    public LiveData<Boolean> getLoginState() { return loginState; }

    public void setLoginState(boolean state) { this.loginState.setValue(state); }

    public void setFirstName(String name) {firstName.setValue(name);}

    public void setLastName(String name) { lastName.setValue(name); }

    public void setPassword(String password) { this.password.setValue(password); }

    public void setEmail(String email) { this.email.setValue(email); }

    public void createNewAccount() {
        FirebaseAuth _auth = mFb.getFirebaseAuth();
        FirebaseDatabase database = mFb.getFirebaseDatabase();
        _auth.createUserWithEmailAndPassword(email.getValue(), password.getValue()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFb.createNewAccount(firstName.getValue(), lastName.getValue(), email.getValue(), password.getValue());
                //set login state
                setLoginState(true);
            } else {
                Log.d(TAG, "createNewAccount:failed", task.getException());
            }
        });
    }

}
